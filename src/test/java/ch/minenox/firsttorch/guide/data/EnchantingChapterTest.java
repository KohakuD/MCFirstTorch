package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class EnchantingChapterTest {
    private static final List<String> QUESTS = List.of("1D5FB17380C49E6A", "4082E4A6B3F7C19D",
            "73B517D9E62AF4C0", "15D739FB084C16E2", "480A62CE3B7F4915", "6A2C84E05D916B37");

    @Test void keepsSourceBranchesQuantitiesAndRewardIds() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(20);
        assertEquals("5F1D739CB0284A6E", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        for (int index : List.of(0, 1, 3)) {
            assertEquals(List.of("7A2C84E05D916B37"), chapter.quests().get(index).prerequisiteQuestIds());
        }
        assertEquals(List.of(QUESTS.get(1)), chapter.quests().get(2).prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.get(0), QUESTS.get(2), QUESTS.get(3)), chapter.quests().get(4).prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.get(4)), chapter.quests().getLast().prerequisiteQuestIds());
        assertEquals(List.of(4, 3, 1, 3, 1, 1), chapter.quests().stream().map(q -> q.tasks().getFirst().count()).toList());
        assertEquals(List.of("minecraft:obsidian", "minecraft:sugar_cane", "minecraft:book", "minecraft:lapis_lazuli", "minecraft:enchanting_table"),
                chapter.quests().stream().limit(5).map(q -> q.tasks().getFirst().itemId()).toList());
        assertTrue(chapter.quests().stream().limit(5).allMatch(q -> q.tasks().size() == 1 && q.tasks().getFirst().type() == TaskDefinition.Type.INVENTORY));
        var enchanted = chapter.quests().getLast().tasks().getFirst();
        assertEquals(TaskDefinition.Type.ADVANCEMENT, enchanted.type());
        assertEquals("minecraft:story/enchant_item", enchanted.advancementId());
        assertEquals("enchanted_item", enchanted.criterion());
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("3F71D395A2E6B08C", "62A406C8D519E3BF", "37F951BD2A6E3804", "2E60C82491D5AF7B", "0C4EA6027FB38D59", "1D5FB71380C49E6A"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(5, 1, 5, 5, 3, 10), rewards.stream().map(r -> r.amount()).toList());
    }

    @Test void observesItemsEarlyWithoutSkippingPrerequisites() throws Exception {
        var snapshot = snapshot();
        var chapter = snapshot.guides().getFirst().chapters().get(20);
        for (var quest : chapter.quests()) {
            var task = quest.tasks().getFirst();
            var shortCount = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                    key -> key.equals(task.inventoryKey()) ? task.count() - 1 : 0);
            assertFalse(shortCount.completedTaskIds().contains(task.id()));
            var enough = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                    key -> key.equals(task.inventoryKey()) ? task.count() : 0);
            assertTrue(enough.completedTaskIds().contains(task.id()));
            assertFalse(enough.completedQuestIds().contains(quest.id()));
            assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, enough, quest.id(), task.id(), key -> 4096));
        }
    }

    @Test void firstEnchantmentRequiresRealAdvancementAndRetainsCompletion() throws Exception {
        var snapshot = snapshot();
        var chapter = snapshot.guides().getFirst().chapters().get(20);
        var tasks = chapter.quests().stream().limit(5).map(q -> q.tasks().getFirst().id()).collect(java.util.stream.Collectors.toSet());
        var quests = new java.util.HashSet<>(QUESTS.subList(0, 5));
        quests.add("7A2C84E05D916B37");
        var ready = new ProgressState(tasks, quests);
        assertFalse(TaskEvaluator.evaluate(snapshot, ready, key -> 0).completedQuestIds().contains(QUESTS.getLast()));
        var done = TaskEvaluator.evaluate(snapshot, ready, key -> key.equals("@minecraft:story/enchant_item|enchanted_item") ? 1 : 0);
        assertTrue(done.completedQuestIds().contains(QUESTS.getLast()));
        assertTrue(TaskEvaluator.evaluate(snapshot, done, key -> 0).completedQuestIds().contains(QUESTS.getLast()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
