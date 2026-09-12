package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Vanilla furnace surface and native items, with original explanatory overlays. */
final class LiveSmeltingRenderer {
    private static final ResourceLocation GUI = ResourceLocation.parse("minecraft:textures/gui/container/furnace.png");
    private static final ResourceLocation FIRE = ResourceLocation.parse("minecraft:textures/gui/sprites/container/furnace/lit_progress.png");
    private static final ResourceLocation ARROW = ResourceLocation.parse("minecraft:textures/gui/sprites/container/furnace/burn_progress.png");

    private LiveSmeltingRenderer() {}

    static void draw(GuiGraphics graphics, FirstTorchLayout.Rect bounds, LiveSmeltingCatalog.Recipe recipe) {
        int width = GuideImageLayout.pairedCellWidth(bounds.width());
        for (int step = 0; step < 2; step++) {
            graphics.pose().pushPose();
            graphics.pose().translate(step == 0 ? bounds.x() : bounds.right() - width, bounds.y(), 0);
            graphics.pose().scale(width / 176F, bounds.height() / 166F, 1F);
            LiveRenderCompat.blit(graphics, GUI, 0, 0, 176, 166, 0F, 176F / 256F, 0F, 166F / 256F);
            LiveRenderCompat.blit(graphics, FIRE, 56, 36, 70, 50, 0F, 1F, 0F, 1F);
            LiveRenderCompat.blit(graphics, ARROW, 79, 34, 103, 50, 0F, 1F, 0F, 1F);
            graphics.renderItem(QuestIcons.resolveItem(recipe.input()), 56, 17);
            graphics.renderItem(QuestIcons.resolveItem(recipe.fuel()), 56, 53);
            graphics.renderItem(QuestIcons.resolveItem(recipe.result()), 116, 35);
            graphics.fill(6, 5, 23, 18, FirstTorchTheme.BACKGROUND);
            LiveRenderCompat.text(graphics, Component.literal(Integer.toString(step + 1)), 6, 23, 5, 18);
            if (step == 1) {
                graphics.renderOutline(114, 33, 20, 20, FirstTorchTheme.GOLD);
                // Result-to-inventory teaching arrow; not an interactive slot or real inventory state.
                graphics.fill(123, 55, 125, 79, FirstTorchTheme.GOLD);
                graphics.fill(105, 77, 125, 79, FirstTorchTheme.GOLD);
                graphics.fill(105, 77, 107, 97, FirstTorchTheme.GOLD);
                for (int row = 0; row < 5; row++) {
                    graphics.fill(101 + row, 93 + row, 111 - row, 94 + row, FirstTorchTheme.GOLD);
                }
                graphics.renderOutline(97, 101, 18, 18, FirstTorchTheme.GOLD);
                graphics.renderItem(QuestIcons.resolveItem(recipe.result()), 98, 102);
            }
            graphics.pose().popPose();
        }
    }
}
