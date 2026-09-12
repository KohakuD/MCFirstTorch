package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** Original explanatory slots and arrows with native Minecraft block models. */
final class LiveHandCraftingRenderer {
    private LiveHandCraftingRenderer() {}

    static void draw(GuiGraphics graphics, FirstTorchLayout.Rect bounds) {
        int width = GuideImageLayout.pairedCellWidth(bounds.width());
        for (int step = 0; step < 2; step++) {
            graphics.pose().pushPose();
            graphics.pose().translate(step == 0 ? bounds.x() : bounds.right() - width, bounds.y(), 0);
            graphics.pose().scale(width / 300F, bounds.height() / 240F, 1F);
            FirstTorchTheme.frame(graphics, new FirstTorchLayout.Rect(0, 0, 300, 240), false);
            LiveRenderCompat.text(graphics, Component.literal(Integer.toString(step + 1)), 8, 30, 8, 24);
            for (int index = 0; index < 4; index++) {
                var slot = LiveHandCraftingLayout.gridSlot(index);
                LiveRecipeRenderer.slot(graphics, slot.x(), slot.y(), index == 0 ? LiveHandCraftingLayout.INPUT : "");
            }
            graphics.fill(165, 72, 202, 81, FirstTorchTheme.MUTED);
            for (int offset = 0; offset < 13; offset++) {
                graphics.fill(195 + offset, 64 + offset, 196 + offset, 89 - offset, FirstTorchTheme.MUTED);
            }
            LiveRecipeRenderer.slot(graphics, 230, 54, LiveHandCraftingLayout.OUTPUT);
            LiveRenderCompat.text(graphics,
                    Component.literal("× " + LiveHandCraftingLayout.OUTPUT_COUNT), 226, 276, 101, 115);
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 9; col++) {
                    int x = 24 + col * 28, y = 136 + row * 22 + (row == 3 ? 6 : 0);
                    graphics.fill(x, y, x + 26, y + 20, FirstTorchTheme.BACKGROUND);
                    graphics.renderOutline(x, y, 26, 20, FirstTorchTheme.MUTED);
                }
            }
            if (step == 1) {
                graphics.renderOutline(228, 52, 46, 46, FirstTorchTheme.GOLD);
                graphics.fill(249, 116, 251, 128, FirstTorchTheme.GOLD);
                graphics.fill(177, 126, 251, 128, FirstTorchTheme.GOLD);
                graphics.fill(177, 126, 179, 153, FirstTorchTheme.GOLD);
                for (int row = 0; row < 5; row++) {
                    graphics.fill(173 + row, 149 + row, 183 - row, 150 + row, FirstTorchTheme.GOLD);
                }
                graphics.renderOutline(164, 158, 26, 20, FirstTorchTheme.GOLD);
                graphics.renderItem(QuestIcons.resolveItem(LiveHandCraftingLayout.OUTPUT), 165, 160);
                LiveRenderCompat.text(graphics,
                        Component.literal(Integer.toString(LiveHandCraftingLayout.OUTPUT_COUNT)), 180, 190, 165, 177);
            }
            graphics.pose().popPose();
        }
    }
}
