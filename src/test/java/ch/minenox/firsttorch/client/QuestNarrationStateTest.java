package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

final class QuestNarrationStateTest {
    @Test void missingProgressTakesPriority() {
        assertEquals("screen.firsttorch.narration.unavailable", QuestNarrationState.key(false, true, true, true));
    }

    @Test void lockedCompletionDoesNotOfferRewards() {
        assertEquals("screen.firsttorch.narration.locked", QuestNarrationState.key(true, true, false, false));
        assertEquals("screen.firsttorch.narration.completed_locked", QuestNarrationState.key(true, true, true, true));
    }

    @Test void distinguishesOpenCompletedAndClaimable() {
        assertEquals("screen.firsttorch.narration.open", QuestNarrationState.key(true, false, false, false));
        assertEquals("screen.firsttorch.narration.completed", QuestNarrationState.key(true, false, true, false));
        assertEquals("screen.firsttorch.narration.reward", QuestNarrationState.key(true, false, true, true));
    }
}
