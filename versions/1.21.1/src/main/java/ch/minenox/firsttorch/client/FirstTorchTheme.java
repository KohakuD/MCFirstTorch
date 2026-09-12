package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import net.minecraft.client.gui.GuiGraphics;

/** Original gunmetal and brass interface surfaces, drawn without game texture imitations. */
final class FirstTorchTheme {
    static final int BACKGROUND = 0xB80C0D0D;
    static final int GOLD_DARK = 0xFF815923;
    static final int GOLD = 0xFFE6AE53;
    static final int AMBER = 0xFFFFC258;
    static final int TEXT = 0xFFF4E9D7;
    static final int MUTED = 0xFFC0B4A4;
    static final int CONNECTION = 0xFFAD9367;

    private FirstTorchTheme() {}

    // Eight separated teeth, a continuous rim and an open central bore.
    private static final String[] SETTINGS_GEAR = {
            ".....###.....",
            ".....###.....",
            "..##.###.##..",
            "..#########..",
            "...##...##...",
            "####.....####",
            "####.....####",
            "####.....####",
            "...##...##...",
            "..#########..",
            "..##.###.##..",
            ".....###.....",
            ".....###....."
    };

    /** Pixel-aligned UI symbols; these do not imitate Minecraft items. */
    static void headerIcon(GuiGraphics graphics, int x, int y, FirstTorchButton.Kind kind) {
        if (kind == FirstTorchButton.Kind.SEARCH) {
            graphics.renderOutline(x + 1, y, 7, 8, TEXT);
            graphics.fill(x, y + 2, x + 1, y + 6, TEXT);
            graphics.fill(x + 8, y + 2, x + 9, y + 6, TEXT);
            for (int i = 0; i < 5; i++) graphics.fill(x + 7 + i, y + 7 + i, x + 9 + i, y + 9 + i, TEXT);
        } else if (kind == FirstTorchButton.Kind.ACCESSIBILITY) {
            graphics.fill(x + 5, y, x + 8, y + 3, TEXT);
            graphics.fill(x + 1, y + 4, x + 12, y + 6, TEXT);
            graphics.fill(x + 5, y + 5, x + 8, y + 9, TEXT);
            for (int i = 0; i < 5; i++) {
                graphics.fill(x + 5 - i / 2, y + 8 + i, x + 7 - i / 2, y + 9 + i, TEXT);
                graphics.fill(x + 6 + i / 2, y + 8 + i, x + 8 + i / 2, y + 9 + i, TEXT);
            }
        } else {
            for (int row = 0; row < SETTINGS_GEAR.length; row++) {
                for (int column = 0; column < SETTINGS_GEAR[row].length(); column++) {
                    if (SETTINGS_GEAR[row].charAt(column) == '#') {
                        graphics.fill(x + column, y + row, x + column + 1, y + row + 1,
                                row + column < 12 ? TEXT : MUTED);
                    }
                }
            }
        }
    }

    /** Original UI cup symbol, not an in-world Minecraft item. */
    static void trophyIcon(GuiGraphics graphics, int x, int y) {
        graphics.fill(x + 3, y + 1, x + 10, y + 6, GOLD);
        graphics.fill(x + 4, y + 6, x + 9, y + 8, GOLD);
        graphics.renderOutline(x, y + 2, 4, 4, GOLD);
        graphics.renderOutline(x + 9, y + 2, 4, 4, GOLD);
        graphics.fill(x + 6, y + 8, x + 7, y + 11, GOLD);
        graphics.fill(x + 3, y + 11, x + 10, y + 13, GOLD);
        graphics.fill(x + 3, y + 1, x + 10, y + 2, AMBER);
    }

    /** Shared completion badge for quest nodes and their task cards. */
    static void completionBadge(GuiGraphics graphics, int left, int top) {
        medallion(graphics, new Rect(left, top, 16, 16), true, false);
        int x = left + 3, y = top + 7;
        for (int i = 0; i < 4; i++) graphics.fill(x + i, y + i, x + i + 2, y + i + 2, TEXT);
        for (int i = 0; i < 7; i++) graphics.fill(x + 3 + i, y + 3 - i, x + 5 + i, y + 5 - i, TEXT);
    }

    static void frame(GuiGraphics graphics, Rect rect, boolean selected) {
        int l = rect.x(), t = rect.y(), r = rect.right(), b = rect.bottom();
        graphics.fill(l, t, r, b, 0xFF070809);
        outline(graphics, rect, 1, 0xFF303337);
        bevel(graphics, l + 2, t + 2, r - 2, b - 2,
                selected ? 0xFFFFD98A : 0xFF9C9C91, 0xFF141617);
        bevel(graphics, l + 3, t + 3, r - 3, b - 3, 0xFF606466, 0xFF303335);
        bevel(graphics, l + 4, t + 4, r - 4, b - 4, 0xFF3B3F42, 0xFF080A0B);
        outline(graphics, rect, 5, 0xFF0B0D0E);
        if (r - l <= 14 || b - t <= 14) return;
        surface(graphics, l + 6, t + 6, r - 6, b - 6, 0xFF24292C, 0xFF151819);
        // Recessed seams isolate the steel rail from the darker face plate.
        graphics.fill(l + 7, t + 6, r - 7, t + 7, 0xFF080A0B);
        graphics.fill(l + 7, b - 7, r - 7, b - 6, 0xFF444746);
        bracket(graphics, l + 3, t + 3, 1, 1);
        bracket(graphics, r - 4, t + 3, -1, 1);
        bracket(graphics, l + 3, b - 4, 1, -1);
        bracket(graphics, r - 4, b - 4, -1, -1);
    }

    static void inset(GuiGraphics graphics, Rect rect) {
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), 0xFF080A0B);
        bevel(graphics, rect.x() + 1, rect.y() + 1, rect.right() - 1, rect.bottom() - 1,
                0xFF575B5C, 0xFF101213);
        surface(graphics, rect.x() + 3, rect.y() + 3, rect.right() - 3, rect.bottom() - 3,
                0xFF25292A, 0xFF151718);
    }

    static void buttonPlate(GuiGraphics graphics, Rect rect, boolean highlighted, boolean brass) {
        inset(graphics, rect);
        if (brass) {
            if (FirstTorchClientConfig.QUIET_SURFACES.get()) {
                // Action buttons retain a bright fill behind their dark labels.
                graphics.fill(rect.x() + 3, rect.y() + 3, rect.right() - 3, rect.bottom() - 3,
                        highlighted ? 0xFFFFD17A : 0xFFDBAF5D);
            } else {
                surface(graphics, rect.x() + 3, rect.y() + 3, rect.right() - 3, rect.bottom() - 3,
                        highlighted ? 0xFFFFD17A : 0xFFDBAF5D, 0xFF9B671F);
            }
            bevel(graphics, rect.x() + 1, rect.y() + 1, rect.right() - 1, rect.bottom() - 1,
                    0xFFFFE2A0, 0xFF5F3C14);
        } else if (highlighted) {
            surface(graphics, rect.x() + 3, rect.y() + 3, rect.right() - 3, rect.bottom() - 3,
                    0xFF35332B, 0xFF1D1F1E);
            bevel(graphics, rect.x(), rect.y(), rect.right(), rect.bottom(), 0xFFFFD582, GOLD_DARK);
            outline(graphics, rect, 2, GOLD_DARK);
        }
        if (rect.width() >= 80 && rect.height() >= 20) {
            rivet(graphics, rect.x() + 5, rect.y() + 5, brass);
            rivet(graphics, rect.right() - 6, rect.bottom() - 6, brass);
        }
    }

    static void outline(GuiGraphics graphics, Rect rect, int inset, int color) {
        bevel(graphics, rect.x() + inset, rect.y() + inset, rect.right() - inset, rect.bottom() - inset, color, color);
    }

    private static void bevel(GuiGraphics graphics, int l, int t, int r, int b, int light, int shadow) {
        if (r <= l || b <= t) return;
        graphics.fill(l, t, r, t + 1, light);
        graphics.fill(l, t, l + 1, b, light);
        graphics.fill(l, b - 1, r, b, shadow);
        graphics.fill(r - 1, t, r, b, shadow);
    }

    private static void bracket(GuiGraphics graphics, int x, int y, int sx, int sy) {
        for (int i = 0; i < 8; i++) {
            int px = x + i * sx, py = y + i * sy;
            graphics.fill(px, y, px + 1, y + 1, 0xFF717572);
            graphics.fill(x, py, x + 1, py + 1, 0xFF717572);
        }
        rivet(graphics, x + sx * 2, y + sy * 2, false);
    }

    private static void rivet(GuiGraphics graphics, int x, int y, boolean brass) {
        graphics.fill(x - 1, y - 1, x + 3, y + 3, 0xFF090B0C);
        graphics.fill(x - 1, y - 1, x + 2, y + 2, brass ? 0xFFB68A43 : 0xFF676D6E);
        graphics.fill(x - 1, y - 1, x + 1, y, brass ? 0xFFFFE4AA : 0xFFB0B4AE);
        graphics.fill(x, y, x + 2, y + 1, brass ? 0xFF65461F : 0xFF24282A);
    }

    private static void surface(GuiGraphics graphics, int l, int t, int r, int b, int light, int dark) {
        if (r <= l || b <= t) return;
        if (FirstTorchClientConfig.QUIET_SURFACES.get()) {
            graphics.fill(l, t, r, b, 0xFF111416);
            return;
        }
        for (int y = t; y < b; y += 3) {
            graphics.fill(l, y, r, Math.min(b, y + 3), blend(light, dark, (float) (y - t) / Math.max(1, b - t)));
        }
        // Sparse horizontal machining marks; no repeated stone tiles or dot pattern.
        for (int y = t + 5; y < b - 2; y += 13) {
            int span = Math.max(1, r - l);
            int offset = Math.floorMod(y * 37, span);
            int start = l + offset;
            graphics.fill(start, y, Math.min(r, start + 9 + Math.floorMod(y * 11, 23)), y + 1, 0x125F696B);
        }
    }

    private static int blend(int a, int b, float amount) {
        int red = (int) (((a >> 16) & 255) * (1 - amount) + ((b >> 16) & 255) * amount);
        int green = (int) (((a >> 8) & 255) * (1 - amount) + ((b >> 8) & 255) * amount);
        int blue = (int) ((a & 255) * (1 - amount) + (b & 255) * amount);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    static void medallion(GuiGraphics graphics, Rect rect, boolean selected, boolean hovered) {
        int radius = Math.max(4, Math.min(rect.width(), rect.height()) / 2 - 1);
        int cx = rect.centerX(), cy = rect.centerY();
        if (selected || hovered) {
            circle(graphics, cx, cy, radius + 4, 0x23FFB343, 0x23FFB343);
            circle(graphics, cx, cy, radius + 1, GOLD, GOLD_DARK);
        }
        circle(graphics, cx, cy, radius, selected ? 0xFFFFE4A3 : 0xFFB1B3AA,
                selected ? 0xFF976027 : 0xFF3E4548);
        circle(graphics, cx, cy, radius - 2, 0xFF080B0D, 0xFF080B0D);
        circle(graphics, cx, cy, radius - 3, selected ? 0xFF8A7145 : 0xFF626A6B, 0xFF191D20);
        circle(graphics, cx, cy, radius - 5, 0xFF2B3032, 0xFF111619);
    }

    private static void circle(GuiGraphics graphics, int cx, int cy, int radius, int light, int dark) {
        if (radius <= 0) return;
        for (int y = -radius; y <= radius; y++) {
            int extent = (int) Math.sqrt(radius * radius - y * y);
            int color = light == dark ? light : blend(light, dark, (float) (y + radius) / (2 * radius));
            graphics.fill(cx - extent, cy + y, cx + extent + 1, cy + y + 1, color);
        }
    }
}
