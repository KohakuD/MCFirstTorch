package ch.minenox.firsttorch.guide.edition;

import static org.junit.jupiter.api.Assertions.*;
import static ch.minenox.firsttorch.guide.edition.EditionHistory.Kind.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.*;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class EditionHistoryTest {
    private static final String A = "1000000000000001", B = "1000000000000002", C = "1000000000000003";
    private static EditionHistory.Change change(String id, String version, EditionHistory.Kind kind) {
        return new EditionHistory.Change(id, version, kind);
    }
    @Test void cumulativeChangesAreDeduplicatedAndBaselineIsExclusive() {
        var history = new EditionHistory(List.of("1.21.1", "26.1.2", "26.2"), List.of(
                change(A, "26.1.2", NEW), change(A, "26.2", REVISED), change(B, "26.2", NEW)));
        assertEquals(Set.of(A, B), history.compare("1.21.1", "26.2", snapshot()).changedQuestIds());
        assertEquals(Set.of(A), history.compare("1.21.1", "26.1.2", snapshot()).changedQuestIds());
        assertEquals(Set.of(A, B), history.compare("26.1.2", "26.2", snapshot()).changedQuestIds());
    }
    @Test void contextIsSeparateAndOriginalDefinitionsArePreserved() {
        var snapshot = snapshot();
        var history = new EditionHistory(List.of("old", "new"), List.of(change(C, "new", REVISED)));
        var result = history.compare("old", "new", snapshot);
        assertEquals(Set.of(C), result.changedQuestIds());
        assertEquals(Set.of(A, B), result.contextQuestIds());
        assertEquals(List.of(B), snapshot.guides().getFirst().chapters().getFirst().quests().getLast().prerequisiteQuestIds());
        assertThrows(UnsupportedOperationException.class, () -> result.changedQuestIds().clear());
    }
    @Test void absentLessonsAndContextChangesAreNotReportedAsNew() {
        var history = new EditionHistory(List.of("old", "new"), List.of(
                change("1000000000000004", "new", NEW), change(B, "new", CONTEXT)));
        var result = history.compare("old", "new", snapshot());
        assertTrue(result.changedQuestIds().isEmpty());
        assertEquals(Set.of(A, B), result.contextQuestIds());
    }
    @Test void orderedPublishedBaselinesExcludeCurrentAndFutureTargets() {
        var history = new EditionHistory(List.of("1.21.1", "26.1.2", "26.2", "26.3"), List.of());
        assertEquals(List.of("1.21.1", "26.1.2"), history.baselines("26.2", Set.of("26.3", "26.1.2", "1.21.1")));
        assertEquals(List.of("1.21.1"), history.baselines("26.2", Set.of("1.21.1")));
        assertTrue(history.baselines("1.21.1", Set.of("1.21.1")).isEmpty());
        assertTrue(history.compare("26.2", "26.3", snapshot()).changedQuestIds().isEmpty());
    }
    @Test void rejectsAmbiguousHistoriesAndInvalidComparisons() {
        assertThrows(IllegalArgumentException.class, () -> new EditionHistory(List.of("a", "a"), List.of()));
        assertThrows(IllegalArgumentException.class, () -> new EditionHistory(List.of("a"), List.of(change(A, "b", NEW))));
        assertThrows(IllegalArgumentException.class, () -> new EditionHistory(List.of("a"), List.of(change(A, "a", NEW), change(A, "a", REVISED))));
        var history = new EditionHistory(List.of("a", "b"), List.of());
        assertThrows(IllegalArgumentException.class, () -> history.compare("b", "a", snapshot()));
        assertThrows(IllegalArgumentException.class, () -> history.compare("a", "a", snapshot()));
        assertThrows(IllegalArgumentException.class, () -> history.compare("unknown", "b", snapshot()));
    }
    private static GuideSnapshot snapshot() {
        var quests = List.of(new QuestDefinition(A, 0, "q.a", "q.a.desc", new QuestPosition(0, 0), List.of()),
                new QuestDefinition(B, 1, "q.b", "q.b.desc", new QuestPosition(1, 0), List.of(A)),
                new QuestDefinition(C, 2, "q.c", "q.c.desc", new QuestPosition(2, 0), List.of(B)));
        return new GuideSnapshot(List.of(new GuideDefinition(1, "2000000000000001", "guide.title", "guide.desc",
                List.of(new ChapterDefinition("3000000000000001", 0, "chapter.title", "chapter.desc", quests)))));
    }
}
