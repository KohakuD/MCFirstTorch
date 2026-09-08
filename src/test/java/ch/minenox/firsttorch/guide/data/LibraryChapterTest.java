package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class LibraryChapterTest {
    private static final List<String> QUESTS = List.of("2E71A5C9D304B86F", "3F82B6DAE415C970", "4093C7EBF526DA81");

    @Test void preservesSourceChainTasksAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(21);
        assertEquals("607E84ADB1395B7F", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("6A2C84E05D916B37"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < 3; i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of("51A4D8FC0637EB92", "62B5E90D1748FCA3", "73C6FA1E28590DB4"),
                chapter.quests().stream().map(q -> q.tasks().getFirst().id()).toList());
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL),
                chapter.quests().stream().map(q -> q.tasks().getFirst().type()).toList());
        assertEquals(List.of(1, 15, 1), chapter.quests().stream().map(q -> q.tasks().getFirst().count()).toList());
        assertTrue(chapter.quests().stream().limit(2).allMatch(q -> q.tasks().getFirst().itemId().equals("minecraft:bookshelf")));
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("04D70B2F396A1EC5", "15E81C304A7B2FD6", "26F92D415B8C30E7"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(3, 3, 10), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:book", rewards.getFirst().itemId());
        assertEquals("minecraft:lapis_lazuli", rewards.get(1).itemId());
    }

    @Test void fifteenBookshelvesAreObservedEarlyWithoutUnlockingOrConfirmingLayout() throws Exception {
        var snapshot = snapshot();
        var fourteen = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:bookshelf") ? 14 : 0);
        assertTrue(fourteen.completedTaskIds().contains("51A4D8FC0637EB92"));
        assertFalse(fourteen.completedTaskIds().contains("62B5E90D1748FCA3"));
        var fifteen = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:bookshelf") ? 15 : 0);
        assertTrue(fifteen.completedTaskIds().contains("62B5E90D1748FCA3"));
        assertTrue(fifteen.completedQuestIds().stream().noneMatch(QUESTS::contains));
        assertFalse(fifteen.completedTaskIds().contains("73C6FA1E28590DB4"));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, fifteen, QUESTS.getLast(), "73C6FA1E28590DB4", key -> 0));
    }

    @Test void storedItemsUnlockAfterPrerequisiteButLayoutStillNeedsConfirmation() throws Exception {
        var snapshot = snapshot();
        var prepared = new ProgressState(Set.of("51A4D8FC0637EB92", "62B5E90D1748FCA3"), Set.of("6A2C84E05D916B37"));
        var ready = TaskEvaluator.evaluate(snapshot, prepared, key -> 0);
        assertTrue(ready.completedQuestIds().containsAll(QUESTS.subList(0, 2)));
        assertFalse(ready.completedQuestIds().contains(QUESTS.getLast()));
        var finished = TaskEvaluator.confirm(snapshot, ready, QUESTS.getLast(), "73C6FA1E28590DB4", key -> 0);
        assertTrue(finished.completedQuestIds().containsAll(QUESTS));
        assertEquals(finished, TaskEvaluator.evaluate(snapshot, finished, key -> 0));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
