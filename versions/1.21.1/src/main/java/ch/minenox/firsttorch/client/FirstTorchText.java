package ch.minenox.firsttorch.client;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Immediate native text drawing, including clipped scrolling labels and optional shadows. */
final class FirstTorchText {
    private final GuiGraphics graphics;
    private final Font font = Minecraft.getInstance().font;
    private final boolean shadow;

    FirstTorchText(GuiGraphics graphics) { this(graphics, true); }
    FirstTorchText(GuiGraphics graphics, boolean shadow) {
        this.graphics = graphics;
        this.shadow = shadow;
    }

    void accept(int x, int y, Component text) {
        graphics.drawString(font, text, x, y, 0xFFFFFFFF, shadow);
    }

    void accept(int x, int y, FormattedCharSequence text) {
        graphics.drawString(font, text, x, y, 0xFFFFFFFF, shadow);
    }

    void acceptScrollingWithDefaultCenter(Component text, int left, int right, int top, int bottom) {
        if (right <= left || bottom <= top) return;
        int width = font.width(text);
        int available = right - left;
        int y = (top + bottom - font.lineHeight) / 2 + 1;
        if (width <= available) {
            accept(left + (available - width) / 2, y, text);
            return;
        }
        int overflow = width - available;
        double period = Math.max(3.0, overflow * 0.5);
        double phase = Util.getMillis() / 1000.0 * Math.PI * 2.0 / period;
        double fraction = 0.5 + Math.sin(Math.cos(phase) * Math.PI / 2.0) / 2.0;
        FirstTorchGui.enableScissor(graphics, left, top, right, bottom);
        accept(left - (int) (overflow * fraction), y, text);
        graphics.disableScissor();
    }
}
