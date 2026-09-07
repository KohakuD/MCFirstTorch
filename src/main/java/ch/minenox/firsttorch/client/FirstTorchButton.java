package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Custom appearance with native button input, tooltip and narration behaviour. */
final class FirstTorchButton extends Button {
    enum Kind { CARD, MEDALLION, FOOTER, NAVIGATION }

    private ItemStack lessonIcon = new ItemStack(Items.BOOK);
    private boolean completed;
    private boolean locked;
    private final Kind kind;
    private final boolean selected;

    FirstTorchButton(int x, int y, int width, int height, Component message, OnPress onPress,
            CreateNarration narration, Tooltip tooltip, Kind kind, boolean selected) {
        super(x, y, width, height, message, onPress, narration);
        this.kind = kind;
        this.selected = selected;
        setTooltip(tooltip);
    }

    FirstTorchButton preview(ItemStack icon, boolean completed, boolean locked) {
        this.lessonIcon = icon;
        this.completed = completed;
        this.locked = locked;
        return this;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Rect bounds = new Rect(getX(), getY(), getWidth(), getHeight());
        if (kind == Kind.MEDALLION) {
            FirstTorchTheme.medallion(graphics, bounds, selected, isHoveredOrFocused());
        } else {
            FirstTorchTheme.buttonPlate(graphics, bounds, selected || isHoveredOrFocused(), kind == Kind.FOOTER);
        }
        ActiveTextCollector text = graphics.textRenderer();
        int color = !active ? FirstTorchTheme.MUTED
                : kind == Kind.FOOTER ? 0xFF21170C
                : selected ? FirstTorchTheme.AMBER : FirstTorchTheme.TEXT;
        Component label = getMessage().copy().withStyle(style -> style.withColor(color));
        if (kind == Kind.MEDALLION) {
            graphics.item(lessonIcon, bounds.centerX() - 8, bounds.centerY() - 8);
            if (completed) {
                Rect badge = new Rect(bounds.right() - 16, bounds.bottom() - 16, 16, 16);
                FirstTorchTheme.medallion(graphics, badge, true, false);
                int x = badge.x() + 3, y = badge.y() + 7;
                for (int i = 0; i < 4; i++) graphics.fill(x + i, y + i, x + i + 2, y + i + 2, FirstTorchTheme.TEXT);
                for (int i = 0; i < 7; i++) graphics.fill(x + 3 + i, y + 3 - i, x + 5 + i, y + 5 - i, FirstTorchTheme.TEXT);
            } else if (locked) {
                int x = bounds.right() - 11, y = bounds.y() + 4;
                graphics.outline(x + 1, y, 5, 5, FirstTorchTheme.MUTED);
                graphics.fill(x, y + 3, x + 7, y + 8, FirstTorchTheme.MUTED);
                graphics.fill(x + 3, y + 5, x + 4, y + 7, FirstTorchTheme.BACKGROUND);
            }
        } else if (kind == Kind.CARD && getWidth() >= 100) {
            graphics.item(lessonIcon, getX() + 9, bounds.centerY() - 8);
            text.acceptScrollingWithDefaultCenter(label, getX() + 32, getRight() - 8, getY() + 5, getBottom() - 5);
        } else {
            extractScrollingStringOverContents(text, label, 5);
        }
    }
}
