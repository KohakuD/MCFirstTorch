package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.util.List;
import org.junit.jupiter.api.Test;

final class DeepMiningChapterTest {
    private static final List<String> QUESTS = List.of("1C4EA0627FB38D59", "72A406C8D519E3BF",
            "14C628EAF73B05D1", "36E840AC195D27F3", "580A62CE3B7F4915", "7A2C84E05D916B37");

    @Test void preservesDeepMiningSourceChainThresholdsAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(19);
        assertEquals("6B3D9F215E8C4A70", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("6D91380C6EA4B2F5", "3F215C6E03ABD479"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(10, chapter.quests().stream().mapToInt(q -> q.tasks().size()).sum());
        var preparation = chapter.quests().getFirst().tasks();
        assertEquals(List.of(2, 1, 1, 32, 8), preparation.stream().map(t -> t.count()).toList());
        assertEquals("firsttorch:iron_or_better_pickaxes", preparation.getFirst().itemId());
        assertEquals("firsttorch:cooked_food", preparation.getLast().itemId());
        assertEquals(TaskDefinition.Type.MANUAL, chapter.quests().get(1).tasks().getFirst().type());
        assertEquals(TaskDefinition.Type.MANUAL, chapter.quests().get(3).tasks().getFirst().type());
        assertEquals(16, chapter.quests().get(2).tasks().getFirst().count());
        assertEquals(3, chapter.quests().get(4).tasks().getFirst().count());
        assertEquals(4, chapter.quests().getFirst().rewards().getFirst().amount());
        assertEquals(8, chapter.quests().get(2).rewards().getFirst().amount());
        assertEquals(5, chapter.quests().get(4).rewards().getFirst().amount());
        assertEquals(1, chapter.quests().getLast().rewards().getFirst().amount());
        assertEquals(5, chapter.quests().getLast().rewards().get(1).amount());
    }

    @Test void automaticThresholdsDoNotCompletePracticalLessons() throws Exception {
        var snapshot = snapshot();
        var chapter = snapshot.guides().getFirst().chapters().get(19);
        for (var quest : chapter.quests()) {
            for (var task : quest.tasks().stream().filter(TaskDefinition::automatic).toList()) {
                var state = ch.minenox.firsttorch.guide.progress.ProgressState.EMPTY;
                var shortCount = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, state,
                        key -> key.equals(task.inventoryKey()) ? task.count() - 1 : 0);
                assertFalse(shortCount.completedTaskIds().contains(task.id()));
                var enough = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, state,
                        key -> key.equals(task.inventoryKey()) ? task.count() : 0);
                assertTrue(enough.completedTaskIds().contains(task.id()));
                assertFalse(enough.completedQuestIds().contains(quest.id()));
            }
        }
        var allItems = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot,
                ch.minenox.firsttorch.guide.progress.ProgressState.EMPTY, key -> 4096);
        assertFalse(allItems.completedTaskIds().contains("03B517D9E62AF4C0"));
        assertFalse(allItems.completedTaskIds().contains("47F951BD2A6E3804"));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
