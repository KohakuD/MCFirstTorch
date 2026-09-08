package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Locks the gateway-arrival safety sequence separately from the optional Dragon Egg. */
final class GatewayArrivalBatchTest {
    private static final List<String> QUESTS = List.of(
            "6794EFB18C065B1B", "3BD823F5C04A9F5F", "5DFA4517E26CB171",
            "7F1C6739048ED393", "213E895B26A0F5B5");
    private static final List<String> TASKS = List.of(
            "08A5F0C29D176C2C", "19B601D3AE287D3D", "30D823F5C04A9F60", "2AC712E4BF398E4E",
            "4CE93406D15BA060", "6E0B5628F37DC282", "102D784A159FE4A4",
            "324F9A6C37B106C6");

    @Test
    void preservesTheFiveSourceQuestsTasksDependenciesAndNoRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(40);
        assertEquals("78AF5B17E2C6904D", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASKS, chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        assertEquals(List.of("4572CD9F6AE439F9"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int index = 1; index < QUESTS.size(); index++) {
            assertEquals(List.of(QUESTS.get(index - 1)), chapter.quests().get(index).prerequisiteQuestIds());
        }
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL,
                        TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL, TaskDefinition.Type.ADVANCEMENT,
                        TaskDefinition.Type.MANUAL),
                chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(TaskDefinition::type).toList());
        assertTrue(chapter.quests().stream().allMatch(quest -> quest.rewards().isEmpty()));
    }

    @Test
    void suppliesStickEarlyButPackingAndReadinessStaySeparateManualSteps() throws Exception {
        var snapshot = snapshot();
        var arrival = snapshot.guides().getFirst().chapters().get(40).quests().getFirst();
        var pearls = arrival.tasks().get(0);
        var endStone = arrival.tasks().get(1);
        var milk = arrival.tasks().get(2);
        assertEquals("minecraft:ender_pearl", pearls.itemId());
        assertEquals(4, pearls.count());
        assertEquals("minecraft:end_stone", endStone.itemId());
        assertEquals(64, endStone.count());
        assertEquals("minecraft:milk_bucket", milk.itemId());
        assertEquals(1, milk.count());
        var early = TaskEvaluator.evaluate(snapshot, new ProgressState(Set.of(), Set.of("4572CD9F6AE439F9")), key ->
                key.equals(pearls.inventoryKey()) ? 3 : key.equals(endStone.inventoryKey()) ? 63 : 0);
        assertFalse(early.completedTaskIds().contains(pearls.id()));
        assertFalse(early.completedTaskIds().contains(endStone.id()));
        assertFalse(early.completedTaskIds().contains(milk.id()));
        var supplied = TaskEvaluator.evaluate(snapshot, early, key ->
                key.equals(pearls.inventoryKey()) ? 4 : key.equals(endStone.inventoryKey()) ? 64 : 0);
        assertTrue(supplied.completedTaskIds().containsAll(Set.of(pearls.id(), endStone.id())));
        assertFalse(supplied.completedTaskIds().contains(milk.id()));
        assertEquals(supplied, TaskEvaluator.evaluate(snapshot, supplied, key -> 0));
        assertFalse(supplied.completedQuestIds().contains(arrival.id()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, supplied, arrival.id(), pearls.id(), key -> 4));
        var manuallyReady = TaskEvaluator.confirm(snapshot, supplied, arrival.id(), TASKS.get(3), key -> 0);
        assertFalse(manuallyReady.completedQuestIds().contains(arrival.id()));
        var complete = TaskEvaluator.evaluate(snapshot, manuallyReady, key -> key.equals(milk.inventoryKey()) ? 1 : 0);
        assertTrue(complete.completedTaskIds().contains(milk.id()));
        assertTrue(complete.completedQuestIds().contains(arrival.id()));
    }

    @Test
    void existingCompletedPreparationSurvivesTheNewMilkObjective() throws Exception {
        var snapshot = snapshot();
        var arrival = snapshot.guides().getFirst().chapters().get(40).quests().getFirst();
        var persisted = new ProgressState(Set.of("08A5F0C29D176C2C", "19B601D3AE287D3D", "2AC712E4BF398E4E"),
                Set.of(arrival.id()));
        var evaluated = TaskEvaluator.evaluate(snapshot, persisted, key -> 0);
        assertTrue(evaluated.completedQuestIds().contains(arrival.id()));
        assertTrue(evaluated.completedTaskIds().contains("2AC712E4BF398E4E"));
        assertFalse(evaluated.completedTaskIds().contains("30D823F5C04A9F60"));
    }

    @Test
    void onlyTheGatewayAdvancementCountsAndItCannotApproveArrivalSafety() throws Exception {
        var snapshot = snapshot();
        var gateway = snapshot.guides().getFirst().chapters().get(40).quests().get(3).tasks().getFirst();
        assertEquals("minecraft:end/enter_end_gateway", gateway.advancementId());
        assertEquals("entered_end_gateway", gateway.criterion());
        var oldEnd = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                key -> key.equals("@minecraft:end/root|entered_end") ? 1 : 0);
        assertFalse(oldEnd.completedTaskIds().contains(gateway.id()));
        var observed = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                key -> key.equals(gateway.inventoryKey()) ? 1 : 0);
        assertTrue(observed.completedTaskIds().contains(gateway.id()));
        assertFalse(observed.completedQuestIds().contains(QUESTS.get(3)));
        var suppliedArrival = TaskEvaluator.evaluate(snapshot,
                new ProgressState(observed.completedTaskIds(), Set.of("4572CD9F6AE439F9")), key ->
                        key.equals("minecraft:ender_pearl") ? 4 : key.equals("minecraft:end_stone") ? 64 : 0);
        assertTrue(suppliedArrival.completedTaskIds().contains(gateway.id()));
        assertFalse(suppliedArrival.completedQuestIds().contains(QUESTS.get(0)));
        assertFalse(suppliedArrival.completedQuestIds().contains(QUESTS.get(3)));
        var transferred = TaskEvaluator.evaluate(snapshot,
                new ProgressState(observed.completedTaskIds(), Set.of(QUESTS.get(2))), key -> 0);
        assertTrue(transferred.completedQuestIds().contains(QUESTS.get(3)));
        assertFalse(transferred.completedTaskIds().contains(TASKS.getLast()));
        assertFalse(transferred.completedQuestIds().contains(QUESTS.getLast()));
        assertTrue(TaskEvaluator.confirm(snapshot, transferred, QUESTS.getLast(), TASKS.getLast(), key -> 0)
                .completedQuestIds().contains(QUESTS.getLast()));
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = GatewayArrivalBatchTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
