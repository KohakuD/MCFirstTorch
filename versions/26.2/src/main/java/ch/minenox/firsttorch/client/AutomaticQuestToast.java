package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.ArrayDeque;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
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
    private static boolean enqueued;
    private static long generation;

    private final AutomaticQuestCompletionTracker.Completion completion;
    private final List<FormattedCharSequence> titleLines;
    private final boolean rewardAvailable;
    private final long toastGeneration;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

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
        enqueued = false;
        generation++;
    }

    private static void enqueueNext() {
        if (enqueued || PENDING.isEmpty()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return;
        enqueued = true;
        minecraft.gui.toastManager().addToast(new AutomaticQuestToast(PENDING.removeFirst(), minecraft.font));
    }

    @Override public Object getToken() { return completion.questId(); }
    @Override public int width() { return WIDTH; }

    @Override
    public int height() {
        return 12 + titleLines.size() * 9 + (rewardAvailable ? 12 : 0) + 7;
    }

    @Override
    public int occcupiedSlotCount() {
        return net.minecraft.util.Mth.positiveCeilDiv(height(), SLOT_HEIGHT);
    }

    @Override
    public float yPos(int firstSlotIndex) {
        // 26.1.2 exposes yPos directly: reserve the entire native five-slot area, then retain
        // the manager slot index so First Torch notices stack independently of vanilla toasts.
        return VANILLA_TOAST_AREA + firstSlotIndex * SLOT_HEIGHT;
    }

    @Override public Toast.Visibility getWantedVisibility() { return wantedVisibility; }

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs) {
        wantedVisibility = toastGeneration != generation || fullyVisibleForMs >= DISPLAY_TIME * manager.getNotificationDisplayTimeMultiplier()
                ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
        graphics.fill(0, 0, width(), height(), 0xF20B0D0E);
        graphics.fill(0, 0, width(), 2, FirstTorchTheme.GOLD);
        graphics.fill(0, height() - 2, width(), height(), FirstTorchTheme.GOLD_DARK);
        graphics.fill(0, 0, 2, height(), FirstTorchTheme.GOLD_DARK);
        graphics.fill(width() - 2, 0, width(), height(), FirstTorchTheme.GOLD);
        graphics.text(font, Component.translatable("notification.firsttorch.quest_complete"), 8, 5,
                FirstTorchTheme.AMBER, false);
        int y = 16;
        for (FormattedCharSequence titleLine : titleLines) {
            graphics.text(font, titleLine, 8, y, FirstTorchTheme.TEXT, false);
            y += 9;
        }
        if (rewardAvailable) graphics.text(font, Component.translatable("notification.firsttorch.reward_available"),
                8, y + 1, FirstTorchTheme.MUTED, false);
    }

    @Override
    public void onFinishedRendering() {
        if (toastGeneration == generation) {
            enqueued = false;
            enqueueNext();
        }
    }
}
