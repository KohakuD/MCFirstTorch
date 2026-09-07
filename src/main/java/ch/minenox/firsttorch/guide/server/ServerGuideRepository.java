package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import java.util.concurrent.atomic.AtomicReference;

public final class ServerGuideRepository {
    public static final ServerGuideRepository INSTANCE = new ServerGuideRepository();

    private final AtomicReference<GuideSnapshot> snapshot = new AtomicReference<>(GuideSnapshot.EMPTY);

    public GuideSnapshot snapshot() {
        return snapshot.get();
    }

    public void replace(GuideSnapshot replacement) {
        snapshot.set(replacement);
    }
}
