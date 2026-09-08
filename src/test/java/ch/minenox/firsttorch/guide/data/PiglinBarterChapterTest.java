package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class PiglinBarterChapterTest {
    private static final List<String> QUESTS = List.of("07105D263E94F5A2", "6F87C49DA50B6C18", "11A9E6BFC72D8E3A", "33CB08D1E94FA05C");

    @Test void preservesSourceGateChainAndCompensation() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(26);
        assertEquals("7249E5AFB61C7D28", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("5D65A27B83E94AF6"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of("18216E374FA506B3", "0098D5AEB61C7D29", "22BAF7C0D83E9F4B", "44DC19E2FA50B16D"), chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("55ED2AF30B61C27E", "66FE3B041C72D38F"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(1, 5), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:gold_ingot", rewards.getFirst().itemId());
    }

    @Test void onlyGoldIngotCountsAndDoesNotProveBartering() throws Exception {
        var snapshot = snapshot();
        var nugget = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:gold_nugget") ? 9 : 0);
        assertFalse(nugget.completedTaskIds().contains("0098D5AEB61C7D29"));
        var ingot = TaskEvaluator.evaluate(snapshot, nugget, key -> key.equals("minecraft:gold_ingot") ? 1 : 0);
        assertTrue(ingot.completedTaskIds().contains("0098D5AEB61C7D29"));
        assertFalse(ingot.completedQuestIds().contains(QUESTS.get(1)));
        assertFalse(ingot.completedTaskIds().contains("44DC19E2FA50B16D"));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, ingot, QUESTS.getLast(), "44DC19E2FA50B16D", key -> 0));
    }

    @Test void tradeConfirmationDoesNotRequireRandomPearlDrop() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of(), Set.of(QUESTS.get(2)));
        var traded = TaskEvaluator.confirm(snapshot, ready, QUESTS.getLast(), "44DC19E2FA50B16D", key -> 0);
        assertTrue(traded.completedQuestIds().contains(QUESTS.getLast()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
