package ch.minenox.firsttorch.client;

import java.util.List;

/** Reading order matches the existing bilingual numbered caption. */
final class LiveBlockComparison {
    static final String RESOURCE = "firsttorch:textures/questpics/archaeology_comparison.png";
    static final List<String> BLOCKS = List.of("minecraft:sand", "minecraft:suspicious_sand",
            "minecraft:gravel", "minecraft:suspicious_gravel");

    private LiveBlockComparison() {}
    static boolean supports(String resource) { return RESOURCE.equals(resource); }
    static FirstTorchLayout.Rect bounds(int x, int y, int width) {
        return new FirstTorchLayout.Rect(x, y, Math.max(1, width), GuideImageLayout.height(width, 300, 200));
    }
    static FirstTorchLayout.Rect panel(int index) {
        if (index < 0 || index >= BLOCKS.size()) throw new IllegalArgumentException("Unknown comparison panel");
        return new FirstTorchLayout.Rect(index % 2 * 152, index / 2 * 102, 148, 98);
    }
}
