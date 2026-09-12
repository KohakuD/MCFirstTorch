package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** Installed block-item models supply the exact unbrushed textures and orientation. */
final class LiveBlockComparisonRenderer {
    private LiveBlockComparisonRenderer() {}

    static void draw(GuiGraphics graphics, FirstTorchLayout.Rect bounds) {
        graphics.pose().pushPose();
        graphics.pose().translate(bounds.x(), bounds.y(), 0);
        graphics.pose().scale(bounds.width() / 300F, bounds.height() / 200F, 1F);
        for (int index = 0; index < LiveBlockComparison.BLOCKS.size(); index++) {
            var panel = LiveBlockComparison.panel(index);
            FirstTorchTheme.frame(graphics, panel, false);
            LiveRenderCompat.text(graphics,
                    Component.literal(Integer.toString(index + 1)), panel.x() + 5, panel.x() + 22,
                    panel.y() + 5, panel.y() + 20);
            graphics.pose().pushPose();
            graphics.pose().translate(panel.x() + 34, panel.y() + 9, 0);
            graphics.pose().scale(5F, 5F, 1F);
            graphics.renderItem(QuestIcons.resolveItem(LiveBlockComparison.BLOCKS.get(index)), 0, 0);
            graphics.pose().popPose();
        }
        graphics.pose().popPose();
    }
}
