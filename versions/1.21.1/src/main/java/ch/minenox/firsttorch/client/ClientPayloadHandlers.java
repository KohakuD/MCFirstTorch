package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.network.GuideSnapshotPayload;
import ch.minenox.firsttorch.network.ProgressPayload;

/** Only entered by client-bound handlers, on the client main thread. */
public final class ClientPayloadHandlers {
    private ClientPayloadHandlers() {}

    public static void installGuide(GuideSnapshotPayload payload) {
        ClientProgressCache.clear();
        ClientGuideCache.install(payload.snapshot());
    }

    public static void installProgress(ProgressPayload payload) {
        ClientProgressCache.install(payload);
    }

    public static void requestWelcome() {
        FirstTorchWelcome.request();
    }
}