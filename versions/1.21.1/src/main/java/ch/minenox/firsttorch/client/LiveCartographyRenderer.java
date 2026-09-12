package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** Native item models with bilingual labels distinguishing the three map results. */
final class LiveCartographyRenderer {
    private LiveCartographyRenderer() {}
    static void draw(GuiGraphics graphics, FirstTorchLayout.Rect bounds) {
        LiveRecipeRenderer.drawPanels(graphics, bounds, LiveCartographyLayout.PANELS);
        int width = GuideImageLayout.pairedCellWidth(bounds.width());
        int height = GuideImageLayout.height(width, 300, 169);
        for (int index = 0; index < 4; index++) {
            graphics.pose().pushPose();
            graphics.pose().translate(index % 2 == 0 ? bounds.x() : bounds.right() - width,
                    bounds.y() + index / 2 * (height + 4), 0);
            graphics.pose().scale(width / 300F, height / 169F, 1F);
            LiveRenderCompat.text(graphics,
                    Component.translatable("image.firsttorch.cartography." + LiveCartographyLayout.LABELS.get(index)),
                    8, 292, 151, 165);
            graphics.pose().popPose();
        }
    }
}
