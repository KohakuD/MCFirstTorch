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

final class BrewingChapterTest {
    private static final List<String> QUESTS = List.of("0A6417D3BE825CF9", "2C8639F5D0A47E1B", "4EA85B17F2C6903D", "60CA7D3914E8B25F", "12EC9F5B360AD471", "340EB17D582CF693", "5620D39F7A4E18B5");

    @Test void preservesSourceSequenceTasksImagesAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(30);
        assertEquals("735A06C29D714BE8", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("734140DAA3E544D2"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        var tasks = chapter.quests().stream().flatMap(q -> q.tasks().stream()).toList();
        assertEquals(List.of("1B7528E4CF936D0A", "3D974A06E1B58F2C", "5FB96C2803D7A14E", "71DB8E4A25F9C360", "23FDA06C471BE582", "451FC28E693D07A4", "6731E4A08B5F29C6"), tasks.stream().map(t -> t.id()).toList());
        assertEquals(List.of(1, 2, 1, 3, 1, 1, 1), tasks.stream().map(t -> t.count()).toList());
        assertEquals(List.of(TaskDefinition.Type.MANUAL, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL, TaskDefinition.Type.ADVANCEMENT, TaskDefinition.Type.MANUAL), tasks.stream().map(t -> t.type()).toList());
        assertEquals("minecraft:nether/brew_potion", tasks.get(5).advancementId());
        assertEquals("potion", tasks.get(5).criterion());
        assertEquals(5, chapter.quests().stream().filter(q -> q.image() != null).count());
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("0B9746E2C5F183AD", "1CA857F3D60294BE"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(3, 10), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:redstone", rewards.getFirst().itemId());
    }

    @Test void inventoryAndExistingAdvancementCountEarlyWithoutManualBypass() throws Exception {
        var snapshot = snapshot();
        for (var quest : snapshot.guides().getFirst().chapters().get(30).quests()) {
            var task = quest.tasks().getFirst();
            if (!task.automatic()) continue;
            var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(task.inventoryKey()) ? task.count() - 1 : 0);
            assertFalse(low.completedTaskIds().contains(task.id()));
            var enough = TaskEvaluator.evaluate(snapshot, low, key -> key.equals(task.inventoryKey()) ? task.count() : 0);
            assertTrue(enough.completedTaskIds().contains(task.id()));
            assertFalse(enough.completedQuestIds().contains(quest.id()));
            assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, enough, quest.id(), task.id(), key -> 4096));
        }
    }

    @Test void genericBrewingAdvancementDoesNotConfirmStrengthExerciseOrPayRewards() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of(), Set.copyOf(QUESTS.subList(0, 5)));
        assertFalse(TaskEvaluator.evaluate(snapshot, ready, key -> 0).completedQuestIds().contains(QUESTS.get(5)));
        var brewed = TaskEvaluator.evaluate(snapshot, ready, key -> key.equals("@minecraft:nether/brew_potion|potion") ? 1 : 0);
        assertTrue(brewed.completedQuestIds().contains(QUESTS.get(5)));
        assertFalse(brewed.completedQuestIds().contains(QUESTS.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, brewed, Set.of(), Set.of()).contains(QUESTS.getLast()));
        var practised = TaskEvaluator.confirm(snapshot, brewed, QUESTS.getLast(), "6731E4A08B5F29C6", key -> 0);
        assertTrue(ClaimableRewards.ids(snapshot, practised, Set.of(), Set.of()).contains(QUESTS.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, practised, Set.of(QUESTS.getLast()), Set.of()).contains(QUESTS.getLast()));
        assertEquals(practised, TaskEvaluator.evaluate(snapshot, practised, key -> 0));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
