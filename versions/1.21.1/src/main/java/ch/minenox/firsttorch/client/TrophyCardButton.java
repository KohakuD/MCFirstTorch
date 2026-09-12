package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/** A selectable collection card; completion is read-only and never grants a reward. */
final class TrophyCardButton extends Button {
    private final TrophyCatalog.Entry entry;
    private final boolean selected;

    TrophyCardButton(Rect bounds, TrophyCatalog.Entry entry, boolean selected, OnPress press) {
        super(bounds.x(), bounds.y(), bounds.width(), bounds.height(),
                Component.translatable(entry.titleKey()), press,
                ignored -> Component.translatable(entry.titleKey()).append(". ")
                        .append(Component.translatable(entry.earned()
                                ? "screen.firsttorch.trophies.earned" : "screen.firsttorch.trophies.locked")));
        this.entry = entry;
        this.selected = selected;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Rect bounds = new Rect(getX(), getY(), getWidth(), getHeight());
        FirstTorchTheme.buttonPlate(graphics, bounds, selected || isHoveredOrFocused(), false);
        int size = Math.min(36, Math.max(16, getHeight() - 47));
        TrophyCollectionRenderer.badge(graphics,
                new Rect(bounds.centerX() - size / 2, getY() + 8, size, size), entry);
        var text = new FirstTorchText(graphics);
        var font = Minecraft.getInstance().font;
        int titleY = getY() + size + 16;
        var lines = font.split(Component.translatable(entry.titleKey()), Math.max(1, getWidth() - 12));
        int rows = Math.min(lines.size(), Math.max(1, (getBottom() - titleY - 17) / 10));
        FirstTorchGui.enableScissor(graphics, getX() + 4, getY() + 4, getRight() - 4, getBottom() - 4);
        if (lines.size() > rows) {
            text.acceptScrollingWithDefaultCenter(Component.translatable(entry.titleKey()),
                    getX() + 5, getRight() - 5, titleY, titleY + 11);
        } else {
            for (int i = 0; i < rows; i++) text.accept(bounds.centerX() - font.width(lines.get(i)) / 2, titleY + i * 10, lines.get(i));
        }
        text.acceptScrollingWithDefaultCenter(Component.translatable(entry.earned()
                        ? "screen.firsttorch.trophies.earned" : "screen.firsttorch.trophies.locked")
                        .withStyle(s -> s.withColor(entry.earned() ? FirstTorchTheme.GOLD : FirstTorchTheme.MUTED)),
                getX() + 5, getRight() - 5, getBottom() - 16, getBottom() - 5);
        graphics.disableScissor();
    }
}
