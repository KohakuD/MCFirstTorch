package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Installed block-item models supply the exact unbrushed textures and orientation. */
final class LiveBlockComparisonRenderer {
    private LiveBlockComparisonRenderer() {}

    static void draw(GuiGraphicsExtractor graphics, FirstTorchLayout.Rect bounds) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(bounds.x(), bounds.y());
        graphics.pose().scale(bounds.width() / 300F, bounds.height() / 200F);
        for (int index = 0; index < LiveBlockComparison.BLOCKS.size(); index++) {
            var panel = LiveBlockComparison.panel(index);
            FirstTorchTheme.frame(graphics, panel, false);
            graphics.textRenderer().acceptScrollingWithDefaultCenter(
                    Component.literal(Integer.toString(index + 1)), panel.x() + 5, panel.x() + 22,
                    panel.y() + 5, panel.y() + 20);
            graphics.pose().pushMatrix();
            graphics.pose().translate(panel.x() + 34, panel.y() + 9);
            graphics.pose().scale(5F, 5F);
            graphics.item(QuestIcons.resolveItem(LiveBlockComparison.BLOCKS.get(index)), 0, 0);
            graphics.pose().popMatrix();
        }
        graphics.pose().popMatrix();
    }
}
