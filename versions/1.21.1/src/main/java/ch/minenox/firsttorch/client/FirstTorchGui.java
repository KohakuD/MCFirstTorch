package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3f;

/** Coordinate conversion for the scaled browser; native 1.21.1 scissors ignore the pose. */
final class FirstTorchGui {
    private FirstTorchGui() { }

    static void enableScissor(GuiGraphics graphics, int left, int top, int right, int bottom) {
        var matrix = graphics.pose().last().pose();
        var start = matrix.transformPosition(new Vector3f(left, top, 0));
        var end = matrix.transformPosition(new Vector3f(right, bottom, 0));
        graphics.enableScissor((int) Math.floor(start.x), (int) Math.floor(start.y),
                (int) Math.ceil(end.x), (int) Math.ceil(end.y));
    }
}
