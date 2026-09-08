package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

final class ServerWelcomeServiceTest {
    @Test void onlyCompletedQuestsOrManualTasksSuppressANewWelcome() {
        Set<String> manual = Set.of("3000000000000001");
        assertFalse(ServerWelcomeService.hasGenuinelyStarted(Set.of(), Set.of(), manual));
        assertFalse(ServerWelcomeService.hasGenuinelyStarted(Set.of(), Set.of("3000000000000002"), manual));
        assertTrue(ServerWelcomeService.hasGenuinelyStarted(Set.of("2000000000000001"), Set.of(), manual));
        assertTrue(ServerWelcomeService.hasGenuinelyStarted(Set.of(), Set.of("3000000000000001"), manual));
    }
}
