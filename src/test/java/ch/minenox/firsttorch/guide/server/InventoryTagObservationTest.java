package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class InventoryTagObservationTest {
    private static final String QUEST = "2000000000000001";
    private static final String TASK = "3000000000000001";

    @Test
    void mixedStacksCountExactItemsAndOnlyReferencedMatchingTags() {
        record Stack(String item, int count, Set<String> tags) {}
        Map<String, Integer> counts = InventoryCounts.collect(List.of(
                new Stack("minecraft:oak_planks", 3, Set.of("minecraft:planks", "minecraft:wooden")),
                new Stack("minecraft:birch_planks", 6, Set.of("minecraft:planks")),
                new Stack("minecraft:stone", 5000, Set.of("minecraft:blocks"))),
                Stack::item, Stack::count, Set.of("minecraft:planks"), (stack, tag) -> stack.tags().contains(tag));
        assertEquals(Map.of("minecraft:oak_planks", 3, "minecraft:birch_planks", 6, "minecraft:stone", 4096,
                "#minecraft:planks", 9), counts);
    }

    @Test
    void tagObservationUsesItsPrefixedKeyAndStaysComplete() {
        TaskDefinition tag = new TaskDefinition(TASK, TaskDefinition.Type.INVENTORY_TAG, "minecraft:planks", 8);
        GuideSnapshot guide = snapshot(tag);
        assertEquals(7, ProgressObservation.create(guide, ProgressState.EMPTY,
                Map.of("minecraft:planks", 64, "#minecraft:planks", 7)).taskCounts().get(TASK));
        ProgressState complete = new ProgressState(Set.of(TASK), Set.of(QUEST));
        assertEquals(8, ProgressObservation.create(guide, complete, Map.of()).taskCounts().get(TASK));
    }

    private static GuideSnapshot snapshot(TaskDefinition task) {
        QuestDefinition quest = new QuestDefinition(QUEST, 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of(), List.of(task), List.of());
        return new GuideSnapshot(List.of(new GuideDefinition(1, "0000000000000001", "guide.test.title",
                "guide.test.description", List.of(new ChapterDefinition("1000000000000001", 0,
                "chapter.test.title", "chapter.test.description", List.of(quest))))));
    }
}
