package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import java.util.concurrent.atomic.AtomicReference;

public final class ClientGuideCache {
    private static final AtomicReference<GuideSnapshot> SNAPSHOT =
            new AtomicReference<>(GuideSnapshot.EMPTY);

    private ClientGuideCache() {
    }

    public static GuideSnapshot snapshot() {
        return SNAPSHOT.get();
    }

    public static void install(GuideSnapshot snapshot) {
        SNAPSHOT.set(new GuideSnapshot(snapshot.guides()));
    }

    public static void clear() {
        SNAPSHOT.set(GuideSnapshot.EMPTY);
    }
}
