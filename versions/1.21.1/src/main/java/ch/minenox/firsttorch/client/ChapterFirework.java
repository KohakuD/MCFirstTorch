package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.ArrayDeque;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

/** A bounded, non-interactive screen overlay. No world particles, entities or server writes. */
final class ChapterFirework {
    private static final ChapterCompletionTracker TRACKER = new ChapterCompletionTracker();
    private static final ArrayDeque<ChapterDefinition> PENDING = new ArrayDeque<>();
    private static ChapterDefinition active;
    private static long started;
    private static final double DURATION = 2.2;

    static void observe(GuideSnapshot guides, ProgressPayload progress) {
        for (var chapter : TRACKER.observe(guides, progress)) {
            if (PENDING.size() < 16) PENDING.addLast(chapter);
        }
    }

    static void clear() { TRACKER.clear(); PENDING.clear(); active = null; }

    static void draw(GuiGraphics graphics, Font font, int width, int height) {
        var minecraft = Minecraft.getInstance();
        if (!FirstTorchClientConfig.CHAPTER_FIREWORKS.get() || minecraft.options.hideLightningFlash().get()) {
            PENDING.clear(); active = null; return;
        }
        long now = System.nanoTime();
        if (active != null && (now - started) / 1e9 >= DURATION) active = null;
        if (active == null) {
            active = PENDING.pollFirst();
            if (active == null) return;
            started = now;
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.FIREWORK_ROCKET_BLAST, 1.2F, 0.25F));
        }
        double age = (now - started) / 1e9;
        int cx = width / 2, cy = height / 2;
        graphics.flush();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        double backdropFade = Math.max(0, Math.min(1, Math.min(age / 0.18, (DURATION - age) / 0.45)));
        graphics.fill(0, 0, width, height, ((int) (190 * backdropFade) << 24) | 0x06090E);
        graphics.flush();
        // Three staggered warm bursts; no full-screen flash or rapid flicker.
        for (int burst = 0; burst < 3; burst++) {
            double t = age - burst * 0.18;
            if (t < 0 || t > 1.8) continue;
            int bx = cx + (burst == 1 ? -27 : burst == 2 ? 27 : 0);
            int by = cy - 20 + (burst == 0 ? -12 : 4);
            for (int i = 0; i < 28; i++) {
                double angle = i * Math.PI * 2 / 28 + burst * 0.37;
                double speed = 26 + (i % 4) * 9;
                double distance = speed * (1 - Math.exp(-t * 2));
                int px = bx + (int) (Math.cos(angle) * distance);
                int py = by + (int) (Math.sin(angle) * distance + 17 * t * t);
                int alpha = (int) (255 * Math.max(0, 1 - t / 1.8));
                int rgb = i % 3 == 0 ? 0xFFF1B0 : i % 3 == 1 ? 0xFFC34D : 0xF18A2A;
                graphics.fill(px, py, px + 2, py + 2, (alpha << 24) | rgb);
                if (t > 0.15) graphics.fill(px, py - 3, px + 1, py, ((alpha / 3) << 24) | rgb);
            }
        }
        int fade = (int) (255 * Math.min(1, (DURATION - age) / 0.45));
        Component caption = Component.translatable("screen.firsttorch.chapter.celebration");
        var lines = font.split(caption, Math.max(1, width - 30));
        int textY = cy + 49;
        for (var line : lines) {
            graphics.drawCenteredString(font, line, cx, textY, (fade << 24) | 0xFFD174);
            textY += 10;
        }
        for (var line : font.split(Component.translatable(active.titleKey()), Math.max(1, Math.min(220, width - 30)))) {
            graphics.drawCenteredString(font, line, cx, textY, (fade << 24) | 0xFFF1CA);
            textY += 10;
        }
        graphics.flush();
        graphics.pose().popPose();
    }

    private ChapterFirework() {}
}
