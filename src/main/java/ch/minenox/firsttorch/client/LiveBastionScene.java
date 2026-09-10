package ch.minenox.firsttorch.client;

import java.util.ArrayList;
import java.util.Comparator;

/** The existing explanatory broken-tower composition, using installed block items. */
final class LiveBastionScene {
    private record Block(int x, int y, int z, String item) {}
    private LiveBastionScene() {}
    static LiveScene create() {
        var blocks = new ArrayList<Block>();
        for (int x = -6; x <= 6; x++) for (int z = -3; z <= 3; z++) {
            if (!(Math.abs(x) == 6 && Math.abs(z) == 3)) blocks.add(new Block(x, 0, z, "blackstone"));
        }
        for (int y = 1; y < 6; y++) for (int x = -5; x < -1; x++) for (int z = -2; z < 3; z++) {
            boolean edge = x == -5 || x == -2 || z == -2 || z == 2;
            boolean broken = (x + 2 * z + y) % 11 == 0 || (y > 3 && x == -2 && z == 2);
            if (edge && !broken) blocks.add(new Block(x, y, z,
                    (x + z + y) % 7 == 0 ? "cracked_polished_blackstone_bricks" : "polished_blackstone_bricks"));
        }
        for (int y = 1; y < 8; y++) for (int x = 2; x < 6; x++) for (int z = -3; z < 2; z++) {
            boolean edge = x == 2 || x == 5 || z == -3 || z == 1;
            boolean broken = (2 * x + z + y) % 13 == 0 || (y > 5 && x == 2 && z == -3);
            if (edge && !broken) blocks.add(new Block(x, y, z,
                    (x - z + y) % 6 == 0 ? "cracked_polished_blackstone_bricks" : "polished_blackstone_bricks"));
        }
        for (int x = -1; x < 3; x++) for (int z = -1; z < 2; z++) blocks.add(new Block(x, 4, z, "polished_blackstone_bricks"));
        blocks.add(new Block(3, 2, 1, "gilded_blackstone"));
        blocks.add(new Block(4, 3, 1, "gilded_blackstone"));
        blocks.sort(Comparator.comparingInt((Block block) -> block.x() + block.z()).thenComparingInt(Block::y).thenComparingInt(Block::x));
        var scene = new LiveScene.Builder(600, 420, false);
        for (var block : blocks) scene.item("minecraft:" + block.item(),
                276 + (block.x() - block.z()) * 19, 278 + (block.x() + block.z()) * 10 - block.y() * 24, 48);
        return scene.build();
    }
}
