package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import ch.minenox.firsttorch.guide.progress.ClaimableRewards;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class StrongholdSearchBatchTest {
    private static final List<String> FIRE = List.of("1D9468F4A713C5E0", "3FB68A16C935E702", "51D8AC38EB570924", "73FACD5A0D792B46");
    private static final List<String> EYES = List.of("0C7539E5BF936D1A", "2E975B07D1B58F3C", "40B97D29F3D7A15E", "62DB9F4B15F9C370", "14FDB16D371BE592", "361FD38F593D07B4");
    private static final List<String> SEARCH = List.of("5A31E5B17B5F29D6", "7C5307D39D714BF8", "1E7529F5BF936D1A", "30974B17D1B58F3C", "52B96D39F3D7A15E", "74DB8F5B15F9C370");

    @Test void preservesSourceIdsBranchesAndOriginalRewardBatches() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters().subList(31, 34);
        assertEquals(List.of("788FB257E40C913E", "746B17D3AE825CF0", "7990C368F51DA24F"), chapters.stream().map(c -> c.id()).toList());
        assertEquals(List.of(FIRE, EYES, SEARCH), chapters.stream().map(c -> c.quests().stream().map(q -> q.id()).toList()).toList());
        for (int c : List.of(0, 1)) assertEquals(List.of("5620D39F7A4E18B5"), chapters.get(c).quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of(EYES.getLast()), chapters.getLast().quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < FIRE.size(); i++) assertEquals(List.of(FIRE.get(i - 1)), chapters.getFirst().quests().get(i).prerequisiteQuestIds());
        for (int i = 1; i < SEARCH.size(); i++) assertEquals(List.of(SEARCH.get(i - 1)), chapters.getLast().quests().get(i).prerequisiteQuestIds());
        var eyes = chapters.get(1).quests();
        assertEquals(List.of(EYES.getFirst()), eyes.get(1).prerequisiteQuestIds());
        for (int i : List.of(2, 3)) assertEquals(List.of(EYES.get(1)), eyes.get(i).prerequisiteQuestIds());
        assertEquals(List.of(EYES.get(2), EYES.get(3)), eyes.get(4).prerequisiteQuestIds());
        assertEquals(List.of(EYES.get(4)), eyes.getLast().prerequisiteQuestIds());
        var tasks = chapters.stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.tasks().stream()).toList();
        assertEquals(List.of("2EA57905B824D6F1", "40C79B27DA46F813", "62E9BD49FC681A35", "04ABDE6B1E8A3C57", "1D864AF6C0A47E2B", "3FA86C18E2C6904D", "51CA8E3A04E8B26F", "73ECA05C260AD481", "250EC27E482CF6A3", "4720E4A06A4E18C5", "6B42F6C28C603AE7", "0D6418E4AE825C09", "2F863A06C0A47E2B", "41A85C28E2C6904D", "63CA7E4A04E8B26F", "05EC906C260AD481"), tasks.stream().map(t -> t.id()).toList());
        var rewards = chapters.stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("15BCEF7C2F9B4D68", "5801F5B17B5F29D6", "691206C28C603AE7", "16FDA17D371BE592", "270EB28E482CF6A3"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(5, 2, 10, 16, 10), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:ender_eye", rewards.get(1).itemId());
        assertEquals("minecraft:torch", rewards.get(3).itemId());
    }

    @Test void automaticThresholdsCountBeforeUnlockWithoutCompletingManualExercises() throws Exception {
        var snapshot = snapshot();
        var automatic = snapshot.guides().getFirst().chapters().subList(31, 34).stream()
                .flatMap(c -> c.quests().stream()).flatMap(q -> q.tasks().stream()).filter(TaskDefinition::automatic).toList();
        assertEquals(5, automatic.size());
        assertEquals(List.of(1, 16, 16, 16, 1), automatic.stream().map(t -> t.count()).toList());
        for (var task : automatic) {
            var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(task.inventoryKey()) ? task.count() - 1 : 0);
            assertFalse(low.completedTaskIds().contains(task.id()));
            var enough = TaskEvaluator.evaluate(snapshot, low, key -> key.equals(task.inventoryKey()) ? task.count() : 0);
            assertTrue(enough.completedTaskIds().contains(task.id()));
            assertTrue(enough.completedQuestIds().isEmpty());
        }
    }

    @Test void eyeCraftingRequiresBothMaterialBranchesButNeverFireResistance() throws Exception {
        var snapshot = snapshot();
        var before = new ProgressState(Set.of("250EC27E482CF6A3"), Set.of(EYES.get(1), EYES.get(2)));
        assertFalse(TaskEvaluator.evaluate(snapshot, before, key -> 0).completedQuestIds().contains(EYES.get(4)));
        var ready = new ProgressState(before.completedTaskIds(), Set.of(EYES.get(1), EYES.get(2), EYES.get(3)));
        var crafted = TaskEvaluator.evaluate(snapshot, ready, key -> 0);
        assertTrue(crafted.completedQuestIds().contains(EYES.get(4)));
        assertFalse(crafted.completedQuestIds().contains(EYES.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, crafted, Set.of(), Set.of()).contains(EYES.getLast()));
        var stored = TaskEvaluator.confirm(snapshot, crafted, EYES.getLast(), "4720E4A06A4E18C5", key -> 0);
        assertTrue(ClaimableRewards.ids(snapshot, stored, Set.of(), Set.of()).contains(EYES.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, stored, Set.of(EYES.getLast()), Set.of()).contains(EYES.getLast()));
        assertTrue(FIRE.stream().noneMatch(stored.completedQuestIds()::contains));
    }

    @Test void strongholdAdvancementNeverProvesSecuredEntranceOrSurfaceReturn() throws Exception {
        var snapshot = snapshot();
        var task = snapshot.guides().getFirst().chapters().get(33).quests().get(4).tasks().getFirst();
        assertEquals(TaskDefinition.Type.ADVANCEMENT, task.type());
        assertEquals("minecraft:story/follow_ender_eye", task.advancementId());
        assertEquals("in_stronghold", task.criterion());
        var prior = new ProgressState(Set.of(), Set.of(SEARCH.get(3)));
        var arrived = TaskEvaluator.evaluate(snapshot, prior, key -> key.equals("@minecraft:story/follow_ender_eye|in_stronghold") ? 1 : 0);
        assertTrue(arrived.completedQuestIds().contains(SEARCH.get(4)));
        assertFalse(arrived.completedQuestIds().contains(SEARCH.getLast()));
        var returned = TaskEvaluator.confirm(snapshot, arrived, SEARCH.getLast(), "05EC906C260AD481", key -> 0);
        assertTrue(ClaimableRewards.ids(snapshot, returned, Set.of(), Set.of()).contains(SEARCH.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, returned, Set.of(SEARCH.getLast()), Set.of()).contains(SEARCH.getLast()));
        assertEquals(returned, TaskEvaluator.evaluate(snapshot, returned, key -> 0));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
