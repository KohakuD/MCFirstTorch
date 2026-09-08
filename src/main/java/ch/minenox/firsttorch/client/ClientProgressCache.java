package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Session-local cache; null means that this connection has not supplied progress yet. */
public final class ClientProgressCache {
    private static final AtomicReference<ProgressPayload> SNAPSHOT = new AtomicReference<>();

    private ClientProgressCache() {}

    public static ProgressPayload snapshot() { return SNAPSHOT.get(); }

    public static void install(ProgressPayload payload) {
        ProgressPayload previous = SNAPSHOT.getAndSet(Objects.requireNonNull(payload));
        if (ExperienceRewardSound.shouldPlay(ClientGuideCache.snapshot(), previous, payload)) {
            net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                    net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                            net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 0.25F));
        }
        ChapterFirework.observe(ClientGuideCache.snapshot(), payload);
        AutomaticQuestToast.observe(ClientGuideCache.snapshot(), previous, payload);
    }

    public static void clear() { SNAPSHOT.set(null); ChapterFirework.clear(); AutomaticQuestToast.clear(); }
}
