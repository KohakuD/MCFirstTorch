package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.ArrayDeque;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** A bounded First Torch toast stream, placed below the native five-slot toast area. */
final class AutomaticQuestToast implements Toast {
    private static final int WIDTH = 220;
    private static final int VANILLA_TOAST_AREA = SLOT_HEIGHT * 5;
    private static final int MAX_PENDING = 16;
    private static final long DISPLAY_TIME = 5000L;
    private static final AutomaticQuestCompletionTracker TRACKER = new AutomaticQuestCompletionTracker();
    private static final ArrayDeque<AutomaticQuestCompletionTracker.Completion> PENDING = new ArrayDeque<>();
    private static AutomaticQuestToast enqueued;
    private static long generation;

    private final AutomaticQuestCompletionTracker.Completion completion;
    private final List<FormattedCharSequence> titleLines;
    private final boolean rewardAvailable;
    private final long toastGeneration;

    private AutomaticQuestToast(AutomaticQuestCompletionTracker.Completion completion, Font font) {
        this.completion = completion;
        this.titleLines = font.split(Component.translatable(completion.titleKey()), WIDTH - 16).stream().limit(3).toList();
        this.rewardAvailable = completion.rewardAvailable();
        this.toastGeneration = generation;
    }

    static void observe(GuideSnapshot guides, ProgressPayload previous, ProgressPayload current) {
        for (var completion : TRACKER.observe(guides, current)) {
            if (PENDING.size() < MAX_PENDING) PENDING.addLast(completion);
        }
        enqueueNext();
    }

    static void clear() {
        TRACKER.clear();
        PENDING.clear();
        enqueued = null;
        generation++;
    }

    private static void enqueueNext() {
        if (enqueued != null || PENDING.isEmpty()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return;
        enqueued = new AutomaticQuestToast(PENDING.removeFirst(), minecraft.font);
        minecraft.getToasts().addToast(enqueued);
    }

    @Override public Object getToken() { return new Token(completion.questId(), toastGeneration); }
    @Override public int width() { return WIDTH; }

    @Override
    public int height() {
        return 12 + titleLines.size() * 9 + (rewardAvailable ? 12 : 0) + 7;
    }

    @Override
    public int slotCount() {
        return net.minecraft.util.Mth.positiveCeilDiv(height(), SLOT_HEIGHT);
    }

    /** Runs outside ToastComponent rendering, after its native slide-out has removed the toast. */
    static void tick() {
        var manager = Minecraft.getInstance().getToasts();
        if (enqueued != null && manager.getToast(AutomaticQuestToast.class, enqueued.getToken()) != enqueued) {
            enqueued = null;
        }
        enqueueNext();
    }

    @Override
    public Toast.Visibility render(GuiGraphics graphics, ToastComponent manager, long fullyVisibleForMs) {
        if (toastGeneration != generation) return Toast.Visibility.HIDE;
        Font font = manager.getMinecraft().font;
        graphics.pose().pushPose();
        // Retain the native slot offset while placing First Torch below vanilla's five-slot area.
        graphics.pose().translate(0, VANILLA_TOAST_AREA, 0);
        graphics.fill(0, 0, width(), height(), 0xF20B0D0E);
        graphics.fill(0, 0, width(), 2, FirstTorchTheme.GOLD);
        graphics.fill(0, height() - 2, width(), height(), FirstTorchTheme.GOLD_DARK);
        graphics.fill(0, 0, 2, height(), FirstTorchTheme.GOLD_DARK);
        graphics.fill(width() - 2, 0, width(), height(), FirstTorchTheme.GOLD);
        graphics.drawString(font, Component.translatable("notification.firsttorch.quest_complete"), 8, 5,
                FirstTorchTheme.AMBER, false);
        int y = 16;
        for (FormattedCharSequence titleLine : titleLines) {
            graphics.drawString(font, titleLine, 8, y, FirstTorchTheme.TEXT, false);
            y += 9;
        }
        if (rewardAvailable) graphics.drawString(font, Component.translatable("notification.firsttorch.reward_available"),
                8, y + 1, FirstTorchTheme.MUTED, false);
        graphics.flush();
        graphics.pose().popPose();
        return fullyVisibleForMs >= DISPLAY_TIME * manager.getNotificationDisplayTimeMultiplier()
                ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    private record Token(String questId, long generation) { }

}
