package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Locks the native Ender Dragon victory lessons, including the optional Dragon Egg branch. */
final class DragonVictoryBatchTest {
    private static final List<String> QUESTS = List.of(
            "12D739F5C0A48F4E", "34F95B17E2C6B170", "561B7D3904E8D392",
            "013E8F5B26A0F5B5", "2350AB7D48C217D7", "4572CD9F6AE439F9");
    private static final List<String> TASKS = List.of(
            "23E84A06D1B5A05F", "450A6C28F3D7C281", "672C8E4A15F9E4A3",
            "124F9A6C37B106C6", "3461BC8E59D328E8", "5683DEA07BF54A0A");

    @Test
    void preservesSourceOrderTasksAdvancementsAndNoRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(39);
        assertEquals("7DD407AC3951E683", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("71C628E4BF937E3D"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < 4; i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.get(3)), chapter.quests().get(4).prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.get(3)), chapter.quests().get(5).prerequisiteQuestIds());
        assertEquals(TASKS, chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        assertEquals(List.of(TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL, TaskDefinition.Type.ADVANCEMENT,
                TaskDefinition.Type.MANUAL, TaskDefinition.Type.ADVANCEMENT, TaskDefinition.Type.MANUAL),
                chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(TaskDefinition::type).toList());
        var dragon = chapter.quests().get(2).tasks().getFirst();
        assertEquals("minecraft:end/kill_dragon", dragon.advancementId());
        assertEquals("killed_dragon", dragon.criterion());
        var egg = chapter.quests().get(4).tasks().getFirst();
        assertEquals("minecraft:end/dragon_egg", egg.advancementId());
        assertEquals("dragon_egg", egg.criterion());
        assertTrue(chapter.quests().stream().allMatch(q -> q.rewards().isEmpty()));
    }

    @Test
    void automaticAdvancementsStickBeforeTheirGatesButFightPracticeCannotBeBypassed() throws Exception {
        var snapshot = snapshot();
        var observed = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key ->
                Set.of("@minecraft:end/kill_dragon|killed_dragon", "@minecraft:end/dragon_egg|dragon_egg").contains(key) ? 1 : 0);
        assertTrue(observed.completedTaskIds().containsAll(Set.of(TASKS.get(2), TASKS.get(4))));
        assertFalse(observed.completedQuestIds().contains(QUESTS.get(2)));
        assertFalse(observed.completedQuestIds().contains(QUESTS.get(4)));
        assertEquals(observed, TaskEvaluator.evaluate(snapshot, observed, key -> 0));
        for (int index : List.of(0, 1, 3, 5)) assertEquals(TaskDefinition.Type.MANUAL, task(snapshot, index).type());
        var readyForFight = new ProgressState(observed.completedTaskIds(), Set.of("71C628E4BF937E3D"));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, readyForFight,
                QUESTS.get(1), TASKS.get(1), key -> 0));
        var flightPractised = TaskEvaluator.confirm(snapshot, readyForFight, QUESTS.get(0), TASKS.get(0), key -> 0);
        assertFalse(flightPractised.completedQuestIds().contains(QUESTS.get(1)));
        var perchedPractised = TaskEvaluator.confirm(snapshot, flightPractised, QUESTS.get(1), TASKS.get(1), key -> 0);
        assertTrue(perchedPractised.completedQuestIds().contains(QUESTS.get(2)));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, observed,
                QUESTS.get(2), TASKS.get(2), key -> 0));
    }

    @Test
    void returnRemainsAvailableWithoutTheOptionalEgg() throws Exception {
        var snapshot = snapshot();
        var returned = TaskEvaluator.confirm(snapshot, new ProgressState(Set.of(), Set.of(QUESTS.get(3))),
                QUESTS.get(5), TASKS.get(5), key -> 0);
        assertTrue(returned.completedQuestIds().contains(QUESTS.get(5)));
        assertFalse(returned.completedQuestIds().contains(QUESTS.get(4)));
    }

    private static TaskDefinition task(GuideSnapshot snapshot, int index) {
        return snapshot.guides().getFirst().chapters().get(39).quests().get(index).tasks().getFirst();
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = DragonVictoryBatchTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
