package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Language-neutral recipe illustration using the installed game's item renderer. */
final class LiveRecipeRenderer {
    private LiveRecipeRenderer() {}

    static void draw(GuiGraphicsExtractor graphics, FirstTorchLayout.Rect bounds, LiveRecipeCatalog.Recipe recipe) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(bounds.x(), bounds.y());
        graphics.pose().scale(bounds.width() / 300F, bounds.height() / 169F);
        FirstTorchTheme.frame(graphics, new FirstTorchLayout.Rect(0, 0, 300, 169), false);
        for (int index = 0; index < 9; index++) {
            slot(graphics, 22 + index % 3 * 44, 18 + index / 3 * 44, recipe.ingredients().get(index));
        }
        // Own geometric arrow, not a copied Minecraft GUI sprite.
        graphics.fill(165, 78, 202, 87, FirstTorchTheme.MUTED);
        for (int step = 0; step < 13; step++) {
            graphics.fill(195 + step, 70 + step, 196 + step, 95 - step, FirstTorchTheme.MUTED);
        }
        slot(graphics, 230, 62, recipe.result());
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
