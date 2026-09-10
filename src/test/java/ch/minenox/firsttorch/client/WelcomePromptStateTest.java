package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class WelcomePromptStateTest {
    @Test void pendingWelcomeWaitsThroughDeathAndRespawnUntilDismissed() {
        var state = new WelcomePromptState();
        state.request();
        assertTrue(state.ready(true, true, 100));
        assertFalse(state.ready(false, false, 100));
        assertTrue(state.pending());
        assertFalse(state.ready(true, true, 0));
        assertFalse(state.ready(true, false, 40));
        assertTrue(state.ready(true, true, 40));
        state.handled();
        assertFalse(state.ready(true, true, 100));
        state.request();
        assertFalse(state.pending());
    }

    @Test void waitsForWorldAndFreeScreenAndInitialTicks() {
        var state = new WelcomePromptState();
        assertFalse(state.ready(true, true, 100));
        state.request();
        assertFalse(state.ready(false, true, 100));
        assertFalse(state.ready(true, false, 100));
        assertFalse(state.ready(true, true, 39));
        assertTrue(state.ready(true, true, 40));
    }

    @Test void dismissalPreventsDuplicatesButDisconnectResetsSession() {
        var state = new WelcomePromptState();
        state.request();
        state.handled();
        state.request();
        assertFalse(state.ready(true, true, 100));
        state.clear();
        assertFalse(state.pending());
        state.request();
        assertTrue(state.ready(true, true, 100));
    }
}
