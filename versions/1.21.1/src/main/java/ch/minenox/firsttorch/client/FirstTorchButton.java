package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Custom appearance with native button input and narration, without hover tooltips. */
final class FirstTorchButton extends Button {
    enum Kind { CARD, PAUSE_ENTRY, MEDALLION, FOOTER, NAVIGATION, TROPHY, ARCHIVE, SEARCH, ACCESSIBILITY, SETTINGS, CLAIM_ALL, REFERENCE_INDEX }

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
        // Keep the whole First Torch interface free of popup tooltips, including navigation controls.
        setTooltip(null);
    }

    FirstTorchButton preview(ItemStack icon, boolean completed, boolean locked) {
        this.lessonIcon = icon;
        this.completed = completed;
        this.locked = locked;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Rect bounds = new Rect(getX(), getY(), getWidth(), getHeight());
        if (kind == Kind.MEDALLION) {
            FirstTorchTheme.medallion(graphics, bounds, selected, isHoveredOrFocused());
        } else {
            FirstTorchTheme.buttonPlate(graphics, bounds, selected || isHoveredOrFocused(), kind == Kind.FOOTER);
        }
        FirstTorchText text = new FirstTorchText(graphics, kind != Kind.FOOTER);
        int color = !active ? FirstTorchTheme.MUTED
                : kind == Kind.FOOTER ? 0xFF21170C
                : selected ? FirstTorchTheme.AMBER : FirstTorchTheme.TEXT;
        Component label = getMessage().copy().withStyle(style -> style.withColor(color));
        if (kind == Kind.FOOTER) {
            // A dark drop shadow on dark lettering made the gold action buttons look doubled.
            label = label.copy().withStyle(style -> style.withBold(false));
        }
        if (kind == Kind.SEARCH || kind == Kind.ACCESSIBILITY || kind == Kind.SETTINGS) {
            FirstTorchTheme.headerIcon(graphics, bounds.centerX() - 6, bounds.centerY() - 6, kind);
        } else if (kind == Kind.REFERENCE_INDEX) {
            graphics.renderItem(lessonIcon, bounds.centerX() - 8, bounds.centerY() - 8);
        } else if (kind == Kind.ARCHIVE) {
            FirstTorchTheme.completionBadge(graphics, getX() + 5, bounds.centerY() - 8);
            if (getWidth() >= 100) text.acceptScrollingWithDefaultCenter(label,
                    getX() + 26, getRight() - 5, getY() + 4, getBottom() - 4);
            else text.accept(getRight() - 10, bounds.centerY() - 4,
                    Component.literal(selected ? "-" : "+").withStyle(s -> s.withColor(FirstTorchTheme.TEXT)));
        } else if (kind == Kind.CLAIM_ALL) {
            int x = bounds.centerX() - 6, y = bounds.centerY() - 5;
            graphics.fill(x, y, x + 12, y + 10, FirstTorchTheme.GOLD);
            graphics.renderOutline(x, y, 12, 10, FirstTorchTheme.AMBER);
            graphics.fill(x, y + 3, x + 12, y + 4, FirstTorchTheme.BACKGROUND);
            graphics.fill(x + 5, y + 2, x + 7, y + 6, FirstTorchTheme.TEXT);
        } else if (kind == Kind.TROPHY) {
            FirstTorchTheme.trophyIcon(graphics, bounds.centerX() - 6, bounds.centerY() - 6);
        } else if (kind == Kind.MEDALLION) {
            int iconSize = FirstTorchLayout.nodeIconSize(bounds.width());
            graphics.pose().pushPose();
            graphics.pose().translate(bounds.centerX() - iconSize / 2F, bounds.centerY() - iconSize / 2F, 0);
            graphics.pose().scale(iconSize / 16F, iconSize / 16F, 1);
            graphics.renderItem(lessonIcon, 0, 0);
            graphics.pose().popPose();
            if (completed) {
                Rect badge = FirstTorchLayout.nodeCompletionBadge(bounds);
                graphics.pose().pushPose();
                graphics.pose().translate(badge.x(), badge.y(), 0);
                graphics.pose().scale(badge.width() / 16F, badge.height() / 16F, 1);
                FirstTorchTheme.completionBadge(graphics, 0, 0);
                graphics.pose().popPose();
            } else if (locked) {
                int x = bounds.right() - 11, y = bounds.y() + 4;
                graphics.renderOutline(x + 1, y, 5, 5, FirstTorchTheme.MUTED);
                graphics.fill(x, y + 3, x + 7, y + 8, FirstTorchTheme.MUTED);
                graphics.fill(x + 3, y + 5, x + 4, y + 7, FirstTorchTheme.BACKGROUND);
            }
        } else if (kind == Kind.PAUSE_ENTRY) {
            graphics.renderItem(lessonIcon, getX() + 9, bounds.centerY() - 8);
            // Symmetric bounds centre the label on the button, not the space after its icon.
            text.acceptScrollingWithDefaultCenter(label, getX() + 32, getRight() - 32, getY() + 5, getBottom() - 5);
        } else if (kind == Kind.CARD && getWidth() < 100) {
            graphics.renderItem(lessonIcon, bounds.centerX() - 8, bounds.centerY() - 8);
        } else if (kind == Kind.CARD && getWidth() >= 100) {
            graphics.renderItem(lessonIcon, getX() + 9, bounds.centerY() - 8);
            text.acceptScrollingWithDefaultCenter(label, getX() + 32, getRight() - 8, getY() + 5, getBottom() - 5);
        } else {
            text.acceptScrollingWithDefaultCenter(label, getX() + 5, getRight() - 5, getY(), getBottom());
        }
    }
}
