package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class NetherSafetyChapterTest {
    private static final List<String> QUESTS = List.of("236B08D1E94FA05C", "458D2AF30B61C27E", "670F4C152D83E490", "19216E374FA506B2", "5D65A27B83E94AF6");

    @Test void preservesSourceChainObjectivesAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(25);
        assertEquals("746BE713A0C85DFA", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("1249E5AFB61C7D28"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        var tasks = chapter.quests().stream().flatMap(q -> q.tasks().stream()).toList();
        assertEquals(List.of("347C19E2FA50B16D", "569E3B041C72D38F", "08105D263E94F5A1", "2A327F4850B617C3", "3B43805961C728D4", "4C54916A72D839E5", "6E76B38C94FA5B07"), tasks.stream().map(t -> t.id()).toList());
        assertEquals(List.of(1, 1, 1, 6, 3, 1, 1), tasks.stream().map(t -> t.count()).toList());
        assertEquals(List.of(TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL, TaskDefinition.Type.MANUAL), tasks.stream().map(t -> t.type()).toList());
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("21A9E6BFC72D8E3A", "32BAF7C0D83E9F4B", "43CB08D1E94FA05C", "1098D5AEB61C7D29"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(4, 8, 2, 10), rewards.stream().map(r -> r.amount()).toList());
    }

    @Test void markerMaterialsCountEarlyButNeverProvePlacement() throws Exception {
        var snapshot = snapshot();
        var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> switch (key) {
            case "minecraft:cobblestone" -> 5;
            case "minecraft:torch" -> 2;
            default -> 0;
        });
        assertFalse(low.completedTaskIds().contains("2A327F4850B617C3"));
        assertFalse(low.completedTaskIds().contains("3B43805961C728D4"));
        var enough = TaskEvaluator.evaluate(snapshot, low, key -> switch (key) {
            case "minecraft:cobblestone" -> 6;
            case "minecraft:torch" -> 3;
            default -> 0;
        });
        assertTrue(enough.completedTaskIds().containsAll(Set.of("2A327F4850B617C3", "3B43805961C728D4")));
        assertFalse(enough.completedTaskIds().contains("4C54916A72D839E5"));
        assertFalse(enough.completedQuestIds().contains(QUESTS.get(3)));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, enough, QUESTS.get(3), "4C54916A72D839E5", key -> 0));
    }

    @Test void placedMarkersAndReturnNeedSeparateConfirmations() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of("2A327F4850B617C3", "3B43805961C728D4"), Set.of(QUESTS.get(2)));
        var placed = TaskEvaluator.confirm(snapshot, ready, QUESTS.get(3), "4C54916A72D839E5", key -> 0);
        assertTrue(placed.completedQuestIds().contains(QUESTS.get(3)));
        assertFalse(placed.completedQuestIds().contains(QUESTS.getLast()));
        var returned = TaskEvaluator.confirm(snapshot, placed, QUESTS.getLast(), "6E76B38C94FA5B07", key -> 0);
        assertTrue(returned.completedQuestIds().contains(QUESTS.getLast()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
