package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class NetherResourcesChapterTest {
    private static final List<String> QUESTS = List.of("1C83F0A24D6E917B", "3EA5B2C46F80939D", "50C7D4E681A2B5BF", "72E9F608A3C4D7D1");

    @Test void preservesIndependentBranchAndOriginalRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(27);
        assertEquals("757CF824B1D96E0B", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("07105D263E94F5A2"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of("2D94A1B35E7FA28C", "4FB6C3D57091A4AE", "61D8E5F792B3C6C0", "03FA0719B4D5E8E2"), chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        var rewards = chapter.quests().getLast().rewards();
        assertEquals(List.of("14AB182AC5E6F9F3", "25BC293BD6F70A04"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(8, 5), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:cobblestone", rewards.getFirst().itemId());
    }

    @Test void requiresExactMaterialThresholdsWithoutCompletingReturn() throws Exception {
        var snapshot = snapshot();
        var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> switch (key) {
            case "minecraft:netherrack" -> 15;
            case "minecraft:quartz" -> 3;
            default -> 0;
        });
        assertFalse(low.completedTaskIds().contains("4FB6C3D57091A4AE"));
        assertFalse(low.completedTaskIds().contains("61D8E5F792B3C6C0"));
        var enough = TaskEvaluator.evaluate(snapshot, low, key -> switch (key) {
            case "minecraft:netherrack" -> 16;
            case "minecraft:quartz" -> 4;
            default -> 0;
        });
        assertTrue(enough.completedTaskIds().containsAll(Set.of("4FB6C3D57091A4AE", "61D8E5F792B3C6C0")));
        assertFalse(enough.completedTaskIds().contains("03FA0719B4D5E8E2"));
        assertFalse(enough.completedQuestIds().contains(QUESTS.getLast()));
    }

    @Test void startsWithoutAnyPiglinTradeAndStillRequiresReturnCheck() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of(), Set.of("07105D263E94F5A2"));
        var started = TaskEvaluator.confirm(snapshot, ready, QUESTS.getFirst(), "2D94A1B35E7FA28C", key -> 0);
        assertTrue(started.completedQuestIds().contains(QUESTS.getFirst()));
        assertFalse(started.completedQuestIds().contains("33CB08D1E94FA05C"));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, started, QUESTS.getLast(), "03FA0719B4D5E8E2", key -> 0));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
