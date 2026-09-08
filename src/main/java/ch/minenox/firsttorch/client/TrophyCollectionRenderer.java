package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Industrial collection detail pane, using the game's original item renderer. */
final class TrophyCollectionRenderer {
    private TrophyCollectionRenderer() {}

    static void badge(GuiGraphicsExtractor graphics, Rect bounds, TrophyCatalog.Entry entry) {
        FirstTorchTheme.medallion(graphics, bounds, entry.earned(), false);
        int size = Math.max(8, bounds.width() / 2);
        graphics.pose().pushMatrix();
        graphics.pose().translate(bounds.centerX() - size / 2F, bounds.centerY() - size / 2F);
        graphics.pose().scale(size / 16F, size / 16F);
        graphics.item(QuestIcons.resolveItem(entry.iconItemId()), 0, 0);
        graphics.pose().popMatrix();
        if (entry.earned()) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(bounds.right() - 8, bounds.bottom() - 8);
            graphics.pose().scale(0.625F, 0.625F);
            FirstTorchTheme.completionBadge(graphics, 0, 0);
            graphics.pose().popMatrix();
        } else {
            // The item remains recognisable beneath the locked-state veil.
            graphics.fill(bounds.x() + 6, bounds.y() + 6, bounds.right() - 6, bounds.bottom() - 6, 0x99141819);
            int x = bounds.centerX() - 3, y = bounds.centerY() - 4;
            graphics.outline(x + 1, y, 5, 5, FirstTorchTheme.MUTED);
            graphics.fill(x, y + 3, x + 7, y + 9, FirstTorchTheme.MUTED);
            graphics.fill(x + 3, y + 5, x + 4, y + 8, FirstTorchTheme.BACKGROUND);
        }
    }

    static int draw(GuiGraphicsExtractor graphics, Font font, Rect panel,
            TrophyCatalog.Entry entry, boolean available, int scroll) {
        int x = panel.x() + 10, width = Math.max(1, panel.width() - 20);
        int top = panel.y() + 10, bottom = panel.bottom() - 10;
        int y = top - scroll;
        graphics.enableScissor(x, top, x + width, bottom);
        if (entry == null) {
            text(graphics, font, Component.translatable("screen.firsttorch.trophies.empty"), x, y, width, FirstTorchTheme.MUTED);
            graphics.disableScissor();
            return 0;
        }
        int size = Math.min(48, width);
        badge(graphics, new Rect(x + 1, y + 2, size, size), entry);
        Component title = Component.translatable(entry.titleKey()).withStyle(s -> s.withBold(true));
        if (width >= 150) y = Math.max(y + 62, text(graphics, font, title, x + 60, y + 10, width - 60, FirstTorchTheme.TEXT) + 12);
        else y = text(graphics, font, title, x, y + size + 12, width, FirstTorchTheme.TEXT) + 12;
        y = text(graphics, font, Component.translatable(entry.descriptionKey()), x, y, width, FirstTorchTheme.MUTED) + 18;
        String stateKey = !available ? "screen.firsttorch.trophies.unavailable"
                : entry.earned() ? "screen.firsttorch.trophies.chapter_complete" : "screen.firsttorch.trophies.locked";
        Component state = Component.translatable(stateKey);
        Component chapter = Component.translatable(entry.chapterTitleKey());
        int insetHeight = (font.split(state, Math.max(1, width - 16)).size()
                + font.split(chapter, Math.max(1, width - 16)).size()) * 10 + 25;
        FirstTorchTheme.inset(graphics, new Rect(x, y, width, insetHeight));
        int inside = text(graphics, font, state, x + 8, y + 9, width - 16, FirstTorchTheme.GOLD) + 7;
        text(graphics, font, chapter, x + 8, inside, width - 16, FirstTorchTheme.TEXT);
        y += insetHeight + 24;
        if (entry.earned()) y = text(graphics, font, Component.translatable("screen.firsttorch.trophies.permanent"), x, y, width, FirstTorchTheme.GOLD) + 8;
        y = text(graphics, font, Component.translatable("screen.firsttorch.trophies.no_item"), x, y, width, FirstTorchTheme.MUTED);
        graphics.disableScissor();
        return Math.max(0, y + scroll - bottom);
    }

    private static int text(GuiGraphicsExtractor graphics, Font font, Component label, int x, int y, int width, int color) {
        for (var line : font.split(label.copy().withStyle(s -> s.withColor(color)), Math.max(1, width))) {
            graphics.textRenderer().accept(x, y, line);
            y += 10;
        }
        return y;
    }
}
