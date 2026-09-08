package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.guide.progress.ClaimableRewards;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class FortressReturnChapterTest {
    private static final String GATE = "676BFD158DEB4F7F";
    private static final List<String> QUESTS = List.of("5B0F14AD63E74BD3", "15F17869891E4CE0", "734140DAA3E544D2");
    private static final String RETURN_TASK = "4C2E1220EA4C4B18";

    @Test void preservesSourceIdsThresholdsAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(29);
        assertEquals("777EA146D3FB802D", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of(GATE), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        var tasks = chapter.quests().stream().flatMap(q -> q.tasks().stream()).toList();
        assertEquals(List.of("31EF76AC88474111", "0966E3DA03134381", "25DF04566B784491", RETURN_TASK), tasks.stream().map(t -> t.id()).toList());
        assertEquals(List.of(2, 4, 4, 1), tasks.stream().map(t -> t.count()).toList());
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("059014A07CBC4436", "55ACAB9F392443B6"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(RewardDefinition.Type.ITEM, rewards.getFirst().type());
        assertEquals("minecraft:cobblestone", rewards.getFirst().itemId());
        assertEquals(16, rewards.getFirst().amount());
        assertEquals(RewardDefinition.Type.EXPERIENCE, rewards.getLast().type());
        assertEquals(10, rewards.getLast().amount());
    }

    @Test void automaticItemsCountEarlyButDoNotBypassGateOrProveReturn() throws Exception {
        var snapshot = snapshot();
        var tasks = snapshot.guides().getFirst().chapters().get(29).quests().stream()
                .limit(2).flatMap(q -> q.tasks().stream()).toList();
        for (var task : tasks) {
            var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(task.itemId()) ? task.count() - 1 : 0);
            assertFalse(low.completedTaskIds().contains(task.id()));
            var enough = TaskEvaluator.evaluate(snapshot, low, key -> key.equals(task.itemId()) ? task.count() : 0);
            assertTrue(enough.completedTaskIds().contains(task.id()));
            assertTrue(QUESTS.stream().noneMatch(enough.completedQuestIds()::contains));
            assertFalse(enough.completedTaskIds().contains(RETURN_TASK));
        }
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, ProgressState.EMPTY, QUESTS.getLast(), RETURN_TASK, key -> 0));
    }

    @Test void returnRequiresExplicitConfirmationAndRewardCanOnlyBeClaimedOnce() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of("31EF76AC88474111", "0966E3DA03134381", "25DF04566B784491"), Set.of(GATE));
        var inventoryDone = TaskEvaluator.evaluate(snapshot, ready, key -> 0);
        assertTrue(inventoryDone.completedQuestIds().containsAll(QUESTS.subList(0, 2)));
        assertFalse(inventoryDone.completedQuestIds().contains(QUESTS.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, inventoryDone, Set.of(), Set.of()).contains(QUESTS.getLast()));
        var returned = TaskEvaluator.confirm(snapshot, inventoryDone, QUESTS.getLast(), RETURN_TASK, key -> 0);
        assertTrue(returned.completedQuestIds().contains(QUESTS.getLast()));
        assertTrue(ClaimableRewards.ids(snapshot, returned, Set.of(), Set.of()).contains(QUESTS.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, returned, Set.of(QUESTS.getLast()), Set.of()).contains(QUESTS.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, returned, Set.of(), Set.of(QUESTS.getLast())).contains(QUESTS.getLast()));
        assertEquals(returned, TaskEvaluator.evaluate(snapshot, returned, key -> 0));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
