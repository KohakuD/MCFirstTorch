package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

final class TaskEvaluatorTest {
    @Test
    void requiresThresholdAndKeepsCompletionAfterInventoryDisappears() {
        GuideSnapshot guide = snapshot(quest(1, List.of(), inventory(1, 4)));
        for (int count : new int[]{-1, 0, 3}) {
            assertEquals(ProgressState.EMPTY, TaskEvaluator.evaluate(guide, ProgressState.EMPTY, item -> count));
        }
        ProgressState completed = TaskEvaluator.evaluate(guide, ProgressState.EMPTY, item -> 4);
        assertEquals(Set.of(taskId(1)), completed.completedTaskIds());
        assertEquals(Set.of(questId(1)), completed.completedQuestIds());
        assertEquals(completed, TaskEvaluator.evaluate(guide, completed, item -> 0));
    }

    @Test
    void sharesNonconsumingCountsAndResolvesReverseOrderedChains() {
        GuideSnapshot guide = snapshot(
                quest(3, List.of(questId(2)), inventory(3, 4)),
                quest(2, List.of(questId(1)), inventory(2, 4)),
                quest(1, List.of(), inventory(1, 4)));
        AtomicInteger observations = new AtomicInteger();
        ProgressState completed = TaskEvaluator.evaluate(guide, ProgressState.EMPTY, item -> {
            assertEquals("minecraft:torch", item);
            observations.incrementAndGet();
            return 4;
        });
        assertEquals(Set.of(questId(1), questId(2), questId(3)), completed.completedQuestIds());
        assertEquals(3, completed.completedTaskIds().size());
        assertEquals(1, observations.get());
    }

    @Test
    void lockedAutomaticTasksPrecompleteButManualTasksAndQuestCompletionStayGated() {
        GuideSnapshot guide = snapshot(quest(1, List.of(), manual(1)),
                quest(2, List.of(questId(1)), inventory(2, 1), manual(3)));
        ProgressState precompleted = TaskEvaluator.evaluate(guide, ProgressState.EMPTY, item -> 64);
        assertEquals(Set.of(taskId(2)), precompleted.completedTaskIds());
        assertEquals(Set.of(), precompleted.completedQuestIds());
        assertEquals(precompleted, TaskEvaluator.evaluate(guide, precompleted, item -> 0));
        assertThrows(IllegalArgumentException.class, () ->
                TaskEvaluator.confirm(guide, ProgressState.EMPTY, questId(2), taskId(3), item -> 64));
        ProgressState unlocked = TaskEvaluator.confirm(guide, precompleted, questId(1), taskId(1), item -> 0);
        assertEquals(Set.of(taskId(1), taskId(2)), unlocked.completedTaskIds());
        assertEquals(Set.of(questId(1)), unlocked.completedQuestIds());
        ProgressState completed = TaskEvaluator.confirm(guide, unlocked, questId(2), taskId(3), item -> 0);
        assertTrue(completed.completedQuestIds().contains(questId(2)));
        assertEquals(completed, TaskEvaluator.confirm(guide, completed, questId(2), taskId(3), item -> 0));
    }

    @Test
    void confirmationEvaluatesInventoryFirstAndConfirmsOnlyRequestedManualTask() {
        GuideSnapshot guide = snapshot(quest(1, List.of(), inventory(1, 4)),
                quest(2, List.of(questId(1)), manual(2), manual(3)));
        ProgressState state = TaskEvaluator.confirm(guide, ProgressState.EMPTY, questId(2), taskId(2), item -> 4);
        assertEquals(Set.of(taskId(1), taskId(2)), state.completedTaskIds());
        assertEquals(Set.of(questId(1)), state.completedQuestIds());
    }

    @Test
    void rejectsUnknownQuestUnknownTaskCrossQuestTaskAndInventorySpoof() {
        GuideSnapshot guide = snapshot(quest(1, List.of(), manual(1)), quest(2, List.of(), inventory(2, 1)));
        for (String[] request : List.of(new String[]{questId(99), taskId(1)},
                new String[]{questId(1), taskId(99)}, new String[]{questId(2), taskId(1)},
                new String[]{questId(2), taskId(2)})) {
            assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(
                    guide, ProgressState.EMPTY, request[0], request[1], item -> 64));
        }
    }

    @Test
    void emptyTaskQuestsDoNotCompleteOrUnlockDependents() {
        GuideSnapshot guide = snapshot(quest(1, List.of()), quest(2, List.of(questId(1)), inventory(2, 1)));
        assertEquals(new ProgressState(Set.of(taskId(2)), Set.of()), TaskEvaluator.evaluate(guide, ProgressState.EMPTY, item -> 64));
        assertEquals(ProgressState.EMPTY, TaskEvaluator.evaluate(GuideSnapshot.EMPTY, ProgressState.EMPTY, item -> 64));
    }

    @Test
    void sanitizesUnknownIdsButKeepsKnownHistoricalCompletion() {
        GuideSnapshot guide = snapshot(quest(1, List.of(), manual(1)),
                quest(2, List.of(questId(1)), inventory(2, 1)));
        ProgressState old = new ProgressState(Set.of(taskId(1), taskId(99)), Set.of(questId(1), questId(99)));
        ProgressState state = TaskEvaluator.evaluate(guide, old, item -> 0);
        assertEquals(Set.of(taskId(1)), state.completedTaskIds());
        assertEquals(Set.of(questId(1)), state.completedQuestIds());
        assertFalse(state.completedTaskIds().contains(taskId(2)));
        assertEquals(ProgressState.EMPTY, TaskEvaluator.evaluate(GuideSnapshot.EMPTY, old, item -> 64));
    }

    @Test
    void defensivelyCopiesAndExposesImmutableSets() {
        Set<String> tasks = new HashSet<>(Set.of(taskId(1)));
        Set<String> quests = new HashSet<>(Set.of(questId(1)));
        ProgressState state = new ProgressState(tasks, quests);
        tasks.clear();
        quests.clear();
        assertEquals(Set.of(taskId(1)), state.completedTaskIds());
        assertEquals(Set.of(questId(1)), state.completedQuestIds());
        assertThrows(UnsupportedOperationException.class, () -> state.completedTaskIds().clear());
        assertThrows(UnsupportedOperationException.class, () -> state.completedQuestIds().clear());
        assertThrows(UnsupportedOperationException.class, () -> ProgressState.EMPTY.completedTaskIds().add(taskId(1)));
    }

    private static String questId(int number) {
        return String.format("2A13F17C%08X", number);
    }

    private static String taskId(int number) {
        return String.format("3A13F17C%08X", number);
    }

    private static TaskDefinition manual(int number) {
        return new TaskDefinition(taskId(number), TaskDefinition.Type.MANUAL, null, 1);
    }

    private static TaskDefinition inventory(int number, int count) {
        return new TaskDefinition(taskId(number), TaskDefinition.Type.INVENTORY, "minecraft:torch", count);
    }

    private static QuestDefinition quest(int number, List<String> prerequisites, TaskDefinition... tasks) {
        return new QuestDefinition(questId(number), number, "quest.test.title", "quest.test.description",
                new QuestPosition(number, 0), prerequisites, List.of(tasks), List.of());
    }

    private static GuideSnapshot snapshot(QuestDefinition... quests) {
        return new GuideSnapshot(List.of(new GuideDefinition(1, "0A13F17C00000001", "guide.test.title",
                "guide.test.description", List.of(new ChapterDefinition("1A13F17C00000001", 0,
                "chapter.test.title", "chapter.test.description", List.of(quests))))));
    }
}
