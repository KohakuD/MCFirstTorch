package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class DevelopmentQuestAccessTest {
    @Test void launchPropertyCannotEnableRetiredTestCompletion() {
        String previous = System.getProperty(DevelopmentQuestAccess.PROPERTY);
        try {
            System.setProperty(DevelopmentQuestAccess.PROPERTY, "true");
            assertFalse(DevelopmentQuestAccess.enabled());
        } finally {
            if (previous == null) System.clearProperty(DevelopmentQuestAccess.PROPERTY);
            else System.setProperty(DevelopmentQuestAccess.PROPERTY, previous);
        }
    }
    @Test void requiresExplicitOptInAndTheIntegratedWorldOwner() {
        UUID owner = new UUID(0, 1), guest = new UUID(0, 2);
        assertTrue(DevelopmentQuestAccess.allowed(true, true, owner, owner));
        assertFalse(DevelopmentQuestAccess.allowed(false, true, owner, owner));
        assertFalse(DevelopmentQuestAccess.allowed(true, false, owner, owner));
        assertFalse(DevelopmentQuestAccess.allowed(true, true, owner, guest));
        assertFalse(DevelopmentQuestAccess.allowed(true, true, null, owner));
        assertFalse(DevelopmentQuestAccess.allowed(true, true, owner, null));
    }
}
