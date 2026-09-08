package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class NetherArrivalChapterTest {
    private static final List<String> QUESTS = List.of("457C189D620A3FB4", "12C16D273E94F5A0", "5605A16B72D839E4", "1249E5AFB61C7D28");
    private static final String ENTRY = "568D29AE731B40C5", STAY = "679E3ABF042C51D6";
    private static final String OBSERVATION = "@minecraft:nether/root|entered_nether";

    @Test void preservesSourceChainTasksAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(24);
        assertEquals("735AD6029FB74CE9", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("1249E56A3FD70C81"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of(ENTRY, STAY, "23D27E384FA506B1", "6716B27C83E94AF5", "235AF6B0C72D8E39"), chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("70AF4BC0153D62E7", "01B05CD1264E73F8", "34E38F4950B617C2", "45F4905A61C728D3", "7027C38D94FA5B06", "0138D49EA50B6C17", "346B07C1D83E9F4A", "457C18D2E94FA05B"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(16, 5, 8, 4, 32, 5, 4, 5), rewards.stream().map(r -> r.amount()).toList());
    }

    @Test void priorEntryDoesNotBypassGateOrManualSafetyChecks() throws Exception {
        var snapshot = snapshot();
        var prior = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(OBSERVATION) ? 1 : 0);
        assertTrue(prior.completedTaskIds().contains(ENTRY));
        assertFalse(prior.completedTaskIds().contains(STAY));
        assertFalse(prior.completedQuestIds().contains(QUESTS.getFirst()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, prior, QUESTS.getFirst(), STAY, key -> 0));
    }

    @Test void requiresActualEntryAndKeepsItAfterReturning() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of(), Set.of("1249E56A3FD70C81"));
        var manual = TaskEvaluator.confirm(snapshot, ready, QUESTS.getFirst(), STAY, key -> 0);
        assertFalse(manual.completedQuestIds().contains(QUESTS.getFirst()));
        var entered = TaskEvaluator.evaluate(snapshot, manual, key -> key.equals(OBSERVATION) ? 1 : 0);
        assertTrue(entered.completedQuestIds().contains(QUESTS.getFirst()));
        assertFalse(entered.completedQuestIds().contains(QUESTS.getLast()));
        assertTrue(TaskEvaluator.evaluate(snapshot, entered, key -> 0).completedQuestIds().contains(QUESTS.getFirst()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
