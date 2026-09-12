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
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class DevelopmentQuestCompletionTest {
    @Test void alternativePathOnlyCompletesOneBranch() {
        var first = quest(1, List.of(), manual(1));
        var second = quest(2, List.of(), manual(2));
        var selected = new QuestDefinition(id(3), 3, "quest.test.title", "quest.test.description",
                new QuestPosition(3, 0), List.of(id(1), id(2)), List.of(manual(3)), List.of(), null, null,
                QuestDefinition.PrerequisiteMode.ANY);
        var snapshot = snapshot(List.of(first, second, selected));
        assertEquals(Set.of(id(1), id(3)), DevelopmentQuestCompletion.complete(snapshot, ProgressState.EMPTY, id(3)).completedQuestIds());
        var existing = new ProgressState(Set.of(taskId(2)), Set.of(id(2)));
        assertEquals(Set.of(id(2), id(3)), DevelopmentQuestCompletion.complete(snapshot, existing, id(3)).completedQuestIds());
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, ProgressState.EMPTY, id(3), taskId(3), key -> 0));
        assertTrue(TaskEvaluator.confirm(snapshot, existing, id(3), taskId(3), key -> 0).completedQuestIds().contains(id(3)));
    }

    @Test
    void completesExactTransitiveClosureAcrossChaptersAndEveryTaskType() {
        QuestDefinition root = quest(1, List.of(), manual(1));
        QuestDefinition middle = quest(2, List.of(id(1)), inventory(2));
        QuestDefinition selected = quest(3, List.of(id(2)), advancement(3));
        GuideSnapshot snapshot = snapshot(List.of(root), List.of(middle, selected));

        ProgressState result = DevelopmentQuestCompletion.complete(snapshot, ProgressState.EMPTY, id(3));

        assertEquals(Set.of(id(1), id(2), id(3)), result.completedQuestIds());
        assertEquals(Set.of(taskId(1), taskId(2), taskId(3)), result.completedTaskIds());
    }

    @Test
    void leavesOptionalBranchesUntouchedAndPreservesHistoricalState() {
        QuestDefinition root = quest(1, List.of(), manual(1));
        QuestDefinition selected = quest(2, List.of(id(1)), inventory(2));
        QuestDefinition optional = quest(3, List.of(id(1)), manual(3));
        GuideSnapshot snapshot = snapshot(List.of(root, selected, optional));
        ProgressState current = new ProgressState(Set.of("3A13F17CDEADBEEF"), Set.of("2A13F17CDEADBEEF"));

        ProgressState result = DevelopmentQuestCompletion.complete(snapshot, current, id(2));

        assertEquals(Set.of(taskId(1), taskId(2), "3A13F17CDEADBEEF"), result.completedTaskIds());
        assertEquals(Set.of(id(1), id(2), "2A13F17CDEADBEEF"), result.completedQuestIds());
        assertFalse(result.completedQuestIds().contains(id(3)));
        assertFalse(result.completedTaskIds().contains(taskId(3)));
        assertEquals(result, DevelopmentQuestCompletion.complete(snapshot, result, id(2)));
    }

    @Test
    void rejectsUnknownMalformedAndNullIdsBeforeChangingState() {
        GuideSnapshot snapshot = snapshot(List.of(quest(1, List.of(), manual(1))));
        ProgressState current = new ProgressState(Set.of("3A13F17CDEADBEEF"), Set.of("2A13F17CDEADBEEF"));

        for (String questId : List.of("2A13F17C00000099", "2a13f17c00000001", "too-short")) {
            assertThrows(IllegalArgumentException.class,
                    () -> DevelopmentQuestCompletion.complete(snapshot, current, questId));
            assertEquals(Set.of("3A13F17CDEADBEEF"), current.completedTaskIds());
            assertEquals(Set.of("2A13F17CDEADBEEF"), current.completedQuestIds());
        }
        assertThrows(IllegalArgumentException.class,
                () -> DevelopmentQuestCompletion.complete(snapshot, current, null));
        assertTrue(current.completedTaskIds().contains("3A13F17CDEADBEEF"));
    }

    private static String id(int number) {
        return String.format("2A13F17C%08X", number);
    }

    private static String taskId(int number) {
        return String.format("3A13F17C%08X", number);
    }

    private static TaskDefinition manual(int number) {
        return new TaskDefinition(taskId(number), TaskDefinition.Type.MANUAL, null, 1);
    }

    private static TaskDefinition inventory(int number) {
        return new TaskDefinition(taskId(number), TaskDefinition.Type.INVENTORY, "minecraft:torch", 1);
    }

    private static TaskDefinition advancement(int number) {
        return new TaskDefinition(taskId(number), TaskDefinition.Type.ADVANCEMENT, null, 1, null,
                "minecraft:story/mine_stone", null);
    }

    private static QuestDefinition quest(int number, List<String> prerequisites, TaskDefinition... tasks) {
        return new QuestDefinition(id(number), number, "quest.test.title", "quest.test.description",
                new QuestPosition(number, 0), prerequisites, List.of(tasks), List.of());
    }

    @SafeVarargs
    private static GuideSnapshot snapshot(List<QuestDefinition>... chapters) {
        List<ChapterDefinition> definitions = new java.util.ArrayList<>();
        for (int index = 0; index < chapters.length; index++) {
            definitions.add(new ChapterDefinition(String.format("1A13F17C%08X", index + 1), index,
                    "chapter.test.title", "chapter.test.description", chapters[index]));
        }
        return new GuideSnapshot(List.of(new GuideDefinition(1, "0A13F17C00000001", "guide.test.title",
                "guide.test.description", definitions)));
    }
}
