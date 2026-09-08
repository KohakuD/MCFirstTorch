package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class StorageChapterTest {
    private static final List<String> QUESTS = List.of("6519B084E62C3A7D", "073BD2A6084E5C9F",
            "295DF4C82A607EB1", "4B7F16EA4C8290D3", "6D91380C6EA4B2F5");

    @Test void preservesSourceIdsChainThresholdsAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(15);
        assertEquals("2CEA40698DF5173B", chapter.id());
        assertEquals(15, chapter.order());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("42F68D51B39E074C"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL, TaskDefinition.Type.INVENTORY_TAG,
                TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL), chapter.quests().stream().map(q -> q.tasks().getFirst().type()).toList());
        assertEquals(3, chapter.quests().getFirst().tasks().getFirst().count());
        assertEquals(3, chapter.quests().get(2).tasks().getFirst().count());
        assertEquals(3, chapter.quests().get(1).rewards().getFirst().amount());
        assertEquals(1, chapter.quests().getLast().rewards().getFirst().amount());
        assertEquals(5, chapter.quests().getLast().rewards().get(1).amount());
    }

    @Test void inventoryThresholdsAreAutomaticButStoragePracticeIsNot() throws Exception {
        var snapshot = snapshot();
        var unlocked = new ProgressState(Set.of(), Set.of("42F68D51B39E074C"));
        assertFalse(TaskEvaluator.evaluate(snapshot, unlocked, key -> key.equals("minecraft:chest") ? 2 : 0).completedQuestIds().contains(QUESTS.getFirst()));
        var chests = TaskEvaluator.evaluate(snapshot, unlocked, key -> key.equals("minecraft:chest") ? 3 : 0);
        assertTrue(chests.completedQuestIds().contains(QUESTS.getFirst()));
        assertFalse(TaskEvaluator.evaluate(snapshot, chests, key -> key.equals("#minecraft:signs") ? 2 : 0).completedQuestIds().contains(QUESTS.get(2)));
        assertFalse(chests.completedQuestIds().contains(QUESTS.get(1)));
        var placed = TaskEvaluator.confirm(snapshot, chests, QUESTS.get(1), "184CE3B7195F6DA0", key -> 0);
        assertTrue(placed.completedQuestIds().contains(QUESTS.get(1)));
        assertFalse(TaskEvaluator.evaluate(snapshot, placed, key -> key.equals("#minecraft:signs") ? 2 : 0)
                .completedQuestIds().contains(QUESTS.get(2)));
        var signs = TaskEvaluator.evaluate(snapshot, placed, key -> key.equals("#minecraft:signs") ? 3 : 0);
        assertTrue(signs.completedQuestIds().contains(QUESTS.get(2)));
        assertFalse(signs.completedQuestIds().contains(QUESTS.get(3)));
        assertFalse(signs.completedQuestIds().contains(QUESTS.getLast()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
