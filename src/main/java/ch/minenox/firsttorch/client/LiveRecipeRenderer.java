package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Language-neutral recipe illustration using the installed game's item renderer. */
final class LiveRecipeRenderer {
    private LiveRecipeRenderer() {}

    static void drawPanels(GuiGraphicsExtractor graphics, FirstTorchLayout.Rect bounds,
            java.util.List<LiveRecipeCatalog.Recipe> recipes) {
        int cellWidth = GuideImageLayout.pairedCellWidth(bounds.width());
        int cellHeight = GuideImageLayout.height(cellWidth, 300, 169);
        for (int index = 0; index < recipes.size(); index++) {
            int x = index % 2 == 0 ? bounds.x() : bounds.right() - cellWidth;
            int y = bounds.y() + index / 2 * (cellHeight + 4);
            draw(graphics, new FirstTorchLayout.Rect(x, y, cellWidth, cellHeight), recipes.get(index));
        }
    }

    static void draw(GuiGraphicsExtractor graphics, FirstTorchLayout.Rect bounds, LiveRecipeCatalog.Recipe recipe) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(bounds.x(), bounds.y());
        graphics.pose().scale(bounds.width() / 300F, bounds.height() / 169F);
        FirstTorchTheme.frame(graphics, new FirstTorchLayout.Rect(0, 0, 300, 169), false);
        if (recipe.shapeless()) {
            var items = recipe.groupedIngredients();
            for (int index = 0; index < items.size(); index++) {
                int x = items.size() == 1 ? 66 : 32 + index * 74;
                slot(graphics, x, 62, items.get(index).item());
                if (items.get(index).count() > 1) {
                    graphics.textRenderer().acceptScrollingWithDefaultCenter(
                            net.minecraft.network.chat.Component.literal("× " + items.get(index).count()), x - 4, x + 46, 111, 125);
                }
                if (index > 0) {
                    graphics.fill(x - 23, 80, x - 9, 83, FirstTorchTheme.MUTED);
                    graphics.fill(x - 18, 75, x - 15, 89, FirstTorchTheme.MUTED);
                }
            }
        } else {
            for (int index = 0; index < 9; index++) {
                slot(graphics, 22 + index % 3 * 44, 18 + index / 3 * 44, recipe.ingredients().get(index));
            }
        }
        // Own geometric arrow, not a copied Minecraft GUI sprite.
        graphics.fill(165, 78, 202, 87, FirstTorchTheme.MUTED);
        for (int step = 0; step < 13; step++) {
            graphics.fill(195 + step, 70 + step, 196 + step, 95 - step, FirstTorchTheme.MUTED);
        }
        slot(graphics, 230, 62, recipe.result());
        if (recipe.count() > 1) {
            graphics.textRenderer().acceptScrollingWithDefaultCenter(
                    net.minecraft.network.chat.Component.literal("× " + recipe.count()), 226, 276, 111, 125);
        }
        graphics.pose().popMatrix();
    }

    private static void slot(GuiGraphicsExtractor graphics, int x, int y, String item) {
        graphics.fill(x, y, x + 42, y + 42, FirstTorchTheme.BACKGROUND);
        graphics.outline(x, y, 42, 42, FirstTorchTheme.MUTED);
        if (item.isEmpty()) return;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + 5, y + 5);
        graphics.pose().scale(2F, 2F);
        graphics.item(QuestIcons.resolveItem(item), 0, 0);
        graphics.pose().popMatrix();
    }
}
