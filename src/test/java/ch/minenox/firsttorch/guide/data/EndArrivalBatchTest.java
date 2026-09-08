package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ClaimableRewards;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Locks the source identities and deliberate safety sequence for the first End arrival lessons. */
final class EndArrivalBatchTest {
    private static final List<String> ARRIVAL = List.of(
            "0A6FB17D482C07C5", "1B70C28E593D18D6", "2C81D39F6A4E29E7",
            "3D92E4A07B5F3AF8", "0CB72D9F6A4E29E8", "4EA3F5B18C604B09");
    private static final List<String> DEFENCE = List.of(
            "5FA406C29D715C1B", "60B517D3AE826D2C", "71C628E4BF937E3D");

    @Test
    void preservesSourceIdsDependenciesTasksAndRewardsAcrossTwoSmallChapters() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters().subList(37, 39);
        assertEquals(List.of("779E4A06D1B58F4E", "7CC3F69B2840D572"), chapters.stream().map(c -> c.id()).toList());
        assertEquals(List.of(ARRIVAL, DEFENCE), chapters.stream().map(c -> c.quests().stream().map(q -> q.id()).toList()).toList());
        assertEquals(List.of("6F54A06C371BF6A3"), chapters.getFirst().quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < 4; i++) assertEquals(List.of(ARRIVAL.get(i - 1)), chapters.getFirst().quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of(ARRIVAL.get(3)), chapters.getFirst().quests().get(4).prerequisiteQuestIds());
        assertEquals(List.of(ARRIVAL.get(3)), chapters.getFirst().quests().get(5).prerequisiteQuestIds());
        assertEquals(List.of(ARRIVAL.get(5)), chapters.getLast().quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < DEFENCE.size(); i++) assertEquals(List.of(DEFENCE.get(i - 1)), chapters.getLast().quests().get(i).prerequisiteQuestIds());

        var tasks = chapters.stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.tasks().stream()).toList();
        assertEquals(List.of("5FB406C29D715C1A", "60C517D3AE826D2B", "71D628E4BF937E3C", "02E739F5C0A48F4D", "1DC83EA07B5F3AF9", "13F84A06D1B5A05E", "24B95B17E2C6B160", "35CA6C28F3D7C271", "46DB7D3904E8D382", "57EC8E4A15F9E493"), tasks.stream().map(t -> t.id()).toList());
        var rewards = chapters.stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("24A95B17E2C6B16F", "35BA6C28F3D7C270", "68FD9F5B260AF5A4", "09AE0A6C371B06B5"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(16, 5, 16, 10), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:cobblestone", rewards.getFirst().itemId());
        assertEquals("minecraft:arrow", rewards.get(2).itemId());
    }

    @Test
    void enteringTheEndIsAutomaticBeforeUnlockButItsRewardStaysGated() throws Exception {
        var snapshot = snapshot();
        var task = snapshot.guides().getFirst().chapters().get(37).quests().get(1).tasks().getFirst();
        assertEquals(TaskDefinition.Type.ADVANCEMENT, task.type());
        assertEquals("minecraft:end/root", task.advancementId());
        assertEquals("entered_end", task.criterion());
        var entered = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                key -> key.equals("@minecraft:end/root|entered_end") ? 1 : 0);
        assertTrue(entered.completedTaskIds().contains(task.id()));
        assertFalse(entered.completedQuestIds().contains(ARRIVAL.get(1)));
        assertTrue(ClaimableRewards.ids(snapshot, entered, Set.of(), Set.of()).isEmpty());
    }

    @Test
    void endStoneInventoryIsStickyAndCagePracticeRemainsManual() throws Exception {
        var snapshot = snapshot();
        var cage = snapshot.guides().getFirst().chapters().get(38).quests().get(1);
        var endStone = cage.tasks().getFirst();
        assertEquals(TaskDefinition.Type.INVENTORY, endStone.type());
        assertEquals("minecraft:end_stone", endStone.itemId());
        assertEquals(64, endStone.count());
        assertEquals(TaskDefinition.Type.MANUAL, cage.tasks().get(1).type());
        var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(endStone.inventoryKey()) ? 63 : 0);
        assertFalse(low.completedTaskIds().contains(endStone.id()));
        var enough = TaskEvaluator.evaluate(snapshot, low, key -> key.equals(endStone.inventoryKey()) ? 64 : 0);
        assertTrue(enough.completedTaskIds().contains(endStone.id()));
        assertFalse(enough.completedQuestIds().contains(cage.id()));
        assertTrue(TaskEvaluator.evaluate(snapshot, enough, key -> 0).completedTaskIds().contains(endStone.id()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, enough, cage.id(), endStone.id(), key -> 64));
    }

    @Test
    void optionalShelterNeverBlocksCrystalOrTheNextLesson() throws Exception {
        var snapshot = snapshot();
        assertEquals(List.of(ARRIVAL.get(3)), snapshot.guides().getFirst().chapters().get(37).quests().get(5).prerequisiteQuestIds());
        var ready = new ProgressState(Set.of(), Set.of(ARRIVAL.get(3)));
        var crystal = TaskEvaluator.confirm(snapshot, ready, ARRIVAL.get(5), "13F84A06D1B5A05E", key -> 0);
        assertTrue(crystal.completedQuestIds().contains(ARRIVAL.get(5)));
        assertFalse(crystal.completedQuestIds().contains(ARRIVAL.get(4)));
        var next = TaskEvaluator.confirm(snapshot, crystal, DEFENCE.getFirst(), "24B95B17E2C6B160", key -> 0);
        assertTrue(next.completedQuestIds().contains(DEFENCE.getFirst()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
