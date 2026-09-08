package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Test;

final class IronEssentialsChapterTest {
    private static final List<String> QUESTS = List.of("27A9D4E60B835CF1", "49CBF6082DA57E13",
            "6BED182A4FC79035", "1D0F3A4C61E9B257", "50A42D7E18C6B39F", "3F215C6E03ABD479");
    private static final List<String> TASKS = List.of("38BAE5F71C946D02", "5ADC07193EB68F24",
            "7CFE293B50D8A146", "2E104B5D72FAC368", "61B53E8F29D7C4A0", "40326D7F14BCE58A");

    @Test void preservesSourceIdentifiersGraphAndReward() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(8);
        assertEquals("4C86EA031D9F5B72", chapter.id());
        assertEquals(8, chapter.order());
        assertEquals("minecraft:water_bucket", chapter.iconItemId());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASKS, chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        assertEquals(List.of("3C16B9E50A724DF8"), chapter.quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.getFirst()), chapter.quests().get(1).prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.getFirst()), chapter.quests().get(2).prerequisiteQuestIds());
        for (int i = 3; i < 5; i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.get(1), QUESTS.get(4)), chapter.quests().getLast().prerequisiteQuestIds());
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY,
                TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL),
                chapter.quests().stream().map(q -> q.tasks().getFirst().type()).toList());
        var reward = chapter.quests().getLast().rewards().getFirst();
        assertEquals("51337E8025CDF69B", reward.id());
        assertEquals(5, reward.amount());
    }

    @Test void sixIngotsAreRequiredAndManualWaterLessonsCannotBeBypassed() throws Exception {
        var snapshot = snapshot();
        var unlocked = new ProgressState(Set.of(), Set.of("3C16B9E50A724DF8"));
        ToIntFunction<String> five = key -> key.equals("minecraft:iron_ingot") ? 5 : 0;
        assertFalse(TaskEvaluator.evaluate(snapshot, unlocked, five).completedQuestIds().contains(QUESTS.getFirst()));
        ToIntFunction<String> six = key -> key.equals("minecraft:iron_ingot") ? 6 : 0;
        var ingots = TaskEvaluator.evaluate(snapshot, unlocked, six);
        assertTrue(ingots.completedQuestIds().contains(QUESTS.getFirst()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, ingots, QUESTS.get(4), TASKS.get(4), six));
        var pickaxe = TaskEvaluator.evaluate(snapshot, ingots, key -> key.equals("minecraft:iron_pickaxe") ? 1 : 0);
        var bucket = TaskEvaluator.evaluate(snapshot, ingots, key -> key.equals("minecraft:bucket") ? 1 : 0);
        assertTrue(pickaxe.completedQuestIds().contains(QUESTS.get(1)));
        assertTrue(bucket.completedQuestIds().contains(QUESTS.get(2)));
        var water = TaskEvaluator.evaluate(snapshot, bucket, key -> key.equals("minecraft:water_bucket") ? 1 : 0);
        assertTrue(water.completedQuestIds().contains(QUESTS.get(3)));
        var source = TaskEvaluator.confirm(snapshot, water, QUESTS.get(4), TASKS.get(4), key -> 0);
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, source, QUESTS.getLast(), TASKS.getLast(), key -> 0));
        var ready = new ProgressState(source.completedTaskIds(), Set.of("3C16B9E50A724DF8", QUESTS.getFirst(), QUESTS.get(1), QUESTS.get(2), QUESTS.get(3), QUESTS.get(4)));
        assertTrue(TaskEvaluator.confirm(snapshot, ready, QUESTS.getLast(), TASKS.getLast(), key -> 0).completedQuestIds().contains(QUESTS.getLast()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
