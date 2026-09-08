package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class RedstoneBasicsTest {
    private static String quest(int index) { return "10A0B0C0D0E0000" + index; }

    @Test void optionalChapterOpensAfterCourseWithoutGatingEarlierLessons() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        var chapter = chapters.get(49);
        assertEquals("64EB7E13A0C85DFA", chapter.id());
        assertEquals(49, chapter.order());
        assertEquals(6, chapter.quests().size());
        for (int i = 0; i < 6; i++) {
            assertEquals(quest(i + 1), chapter.quests().get(i).id());
            assertEquals(List.of(i == 0 ? "3C3122DF5C0EA192" : quest(i)), chapter.quests().get(i).prerequisiteQuestIds());
        }
        assertTrue(chapters.subList(0, 49).stream().flatMap(c -> c.quests().stream())
                .flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(id -> id.startsWith("10A0B0C0D0E0")));
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of(5, 5), rewards.stream().map(r -> r.amount()).toList());
        assertTrue(rewards.stream().allMatch(r -> r.itemId() == null));
    }

    @Test void materialChecksStayAutomaticAndCircuitProofStaysManual() throws Exception {
        var snapshot = snapshot();
        var quests = snapshot.guides().getFirst().chapters().get(49).quests();
        assertEquals(List.of(1, 1, 2, 1, 1, 3), quests.stream().map(q -> q.tasks().size()).toList());
        var dust = quests.get(1).tasks().getFirst();
        assertEquals(TaskDefinition.Type.INVENTORY, dust.type());
        assertEquals("minecraft:redstone", dust.itemId());
        assertEquals(20, dust.count());
        var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:redstone") ? 19 : 0);
        assertFalse(low.completedTaskIds().contains(dust.id()));
        var enough = TaskEvaluator.evaluate(snapshot, low, key -> key.equals("minecraft:redstone") ? 20 : 0);
        assertTrue(enough.completedTaskIds().contains(dust.id()));
        assertFalse(enough.completedQuestIds().contains(quests.get(1).id()));
        assertTrue(TaskEvaluator.evaluate(snapshot, enough, key -> 0).completedTaskIds().contains(dust.id()));
        assertEquals(List.of("minecraft:lever", "minecraft:redstone_lamp"), quests.get(2).tasks().stream().map(t -> t.itemId()).toList());
        assertEquals(List.of("minecraft:stone_button", "minecraft:stone_pressure_plate"), quests.get(5).tasks().stream().filter(TaskDefinition::automatic).map(t -> t.itemId()).toList());
        var supplied = TaskEvaluator.evaluate(snapshot, new ProgressState(Set.of(), Set.of("3C3122DF5C0EA192")), key -> 64);
        for (var q : quests) for (var task : q.tasks()) if (!task.automatic()) {
            assertEquals(TaskDefinition.Type.MANUAL, task.type());
            assertFalse(supplied.completedTaskIds().contains(task.id()));
        }
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = RedstoneBasicsTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
