package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class InventoryTagTaskEvaluatorTest {
    private static final String QUEST = "2000000000000001";
    private static final String TASK = "3000000000000001";

    @Test
    void tagUsesASeparateObservationKeyAndCompletionIsSticky() {
        TaskDefinition tag = new TaskDefinition(TASK, TaskDefinition.Type.INVENTORY_TAG, "minecraft:planks", 8);
        assertEquals("#minecraft:planks", tag.inventoryKey());
        GuideSnapshot guide = snapshot(tag);
        ProgressState incomplete = TaskEvaluator.evaluate(guide, ProgressState.EMPTY,
                key -> Map.of("minecraft:planks", 64, "#minecraft:planks", 7).getOrDefault(key, 0));
        assertEquals(ProgressState.EMPTY, incomplete);
        ProgressState complete = TaskEvaluator.evaluate(guide, ProgressState.EMPTY,
                key -> Map.of("minecraft:planks", 0, "#minecraft:planks", 8).getOrDefault(key, 0));
        assertEquals(Set.of(TASK), complete.completedTaskIds());
        assertEquals(complete, TaskEvaluator.evaluate(guide, complete, key -> 0));
    }

    @Test
    void confirmationRejectsTagTasks() {
        GuideSnapshot guide = snapshot(new TaskDefinition(TASK, TaskDefinition.Type.INVENTORY_TAG, "minecraft:planks", 1));
        assertThrows(IllegalArgumentException.class,
                () -> TaskEvaluator.confirm(guide, ProgressState.EMPTY, QUEST, TASK, key -> 64));
    }

    private static GuideSnapshot snapshot(TaskDefinition task) {
        QuestDefinition quest = new QuestDefinition(QUEST, 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of(), List.of(task), List.of());
        return new GuideSnapshot(List.of(new GuideDefinition(1, "0000000000000001", "guide.test.title",
                "guide.test.description", List.of(new ChapterDefinition("1000000000000001", 0,
                "chapter.test.title", "chapter.test.description", List.of(quest))))));
    }
}
