package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

final class ClientProgressCacheTest {
    @AfterEach
    void clear() { ClientProgressCache.clear(); }

    @Test
    void distinguishesWaitingAvailableUnavailableAndDisconnect() {
        ClientProgressCache.clear();
        assertNull(ClientProgressCache.snapshot());
        ProgressPayload observation = new ProgressPayload(ProgressState.EMPTY, Map.of("3000000000000001", 7), true);
        ClientProgressCache.install(observation);
        assertEquals(observation, ClientProgressCache.snapshot());
        ClientProgressCache.install(ProgressPayload.UNAVAILABLE);
        assertFalse(ClientProgressCache.snapshot().available());
        assertTrue(ClientProgressCache.snapshot().taskCounts().isEmpty());
        ClientProgressCache.clear();
        assertNull(ClientProgressCache.snapshot());
    }
}
