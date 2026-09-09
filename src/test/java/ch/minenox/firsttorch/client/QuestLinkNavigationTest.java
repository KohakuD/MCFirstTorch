package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.data.GuideJson;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class QuestLinkNavigationTest {
    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
    private ProgressPayload complete(GuideSnapshot snapshot) {
        return new ProgressPayload(new ProgressState(Set.of(), snapshot.guides().getFirst().chapters().stream()
                .flatMap(c -> c.quests().stream()).map(q -> q.id()).collect(Collectors.toSet())), Map.of(), true);
    }

    @Test void destinationsIncludeCompletedChaptersButNeverRevealHiddenChapters() throws Exception {
        var snapshot = snapshot();
        var target = snapshot.guides().getFirst().chapters().get(58).quests().getFirst().id();
        assertTrue(QuestLinkNavigation.destination(snapshot, complete(snapshot), target).isPresent());
        var initial = new ProgressPayload(new ProgressState(Set.of(), Set.of()), Map.of(), true);
        assertTrue(QuestLinkNavigation.destination(snapshot, initial, target).isEmpty());
        assertTrue(QuestLinkNavigation.destination(snapshot, ProgressPayload.UNAVAILABLE, target).isEmpty());
        assertTrue(QuestLinkNavigation.destination(snapshot, complete(snapshot), "missing").isEmpty());
    }

    @Test void nestedBackRestoresExactReadingLocationsWithoutChangingProgress() throws Exception {
        var snapshot = snapshot();
        var progress = complete(snapshot);
        var chapters = snapshot.guides().getFirst().chapters();
        var a = QuestLinkNavigation.destination(snapshot, progress, chapters.get(58).quests().getFirst().id()).orElseThrow();
        var b = QuestLinkNavigation.destination(snapshot, progress, chapters.get(59).quests().getFirst().id()).orElseThrow();
        var first = new QuestLinkNavigation.Location(a, 187, false, true, 8);
        var second = new QuestLinkNavigation.Location(b, 54, true, false, 2);
        var history = new QuestLinkNavigation();
        history.push(first);
        history.push(second);
        assertEquals(second, history.back(snapshot, progress).orElseThrow());
        assertEquals(first, history.back(snapshot, progress).orElseThrow());
        assertFalse(history.hasBack());
        assertEquals(complete(snapshot), progress);
    }

    @Test void missingTargetsAreSkippedAndHistoryCanBeCleared() throws Exception {
        var snapshot = snapshot();
        var progress = complete(snapshot);
        var first = snapshot.guides().getFirst().chapters().getFirst().quests().getFirst().id();
        var location = new QuestLinkNavigation.Location(QuestLinkNavigation.destination(snapshot, progress, first).orElseThrow(), 10, true, true, 0);
        var history = new QuestLinkNavigation();
        history.push(location);
        assertTrue(history.back(GuideSnapshot.EMPTY, progress).isEmpty());
        for (int i = 0; i < 40; i++) history.push(location);
        int count = 0;
        while (history.back(snapshot, progress).isPresent()) count++;
        assertEquals(32, count);
        history.push(location);
        history.clear();
        assertFalse(history.hasBack());
    }
}
