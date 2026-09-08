package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class NetherFortressChapterTest {
    private static final List<String> QUESTS = List.of("78FAD3D980F349BA", "73B2B39D1C674728", "3D912A104D4A4E4E", "676BFD158DEB4F7F");

    @Test void preservesSourceGateTasksAndNoMaterialRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(28);
        assertEquals("766D0935C2EA7F1C", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("07105D263E94F5A2"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of("36AD35CCA4724357", "1B63A993ED3C4DB3", "3E4AF2D781EC4B57", "65C89337665C4E28", "21D6CE3AD23A412B", "5583FD1F127A4B12", "22D566F202A7429E", "0F3B0653610D483B", "54A8AA81D49E4187"), chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        assertEquals(List.of(1, 1, 1, 64, 16, 1), chapter.quests().getFirst().tasks().stream().map(t -> t.count()).toList());
        assertEquals(1, chapter.quests().stream().mapToInt(q -> q.rewards().size()).sum());
        var discoveryReward = chapter.quests().get(1).rewards().getFirst();
        assertEquals("277EA146D3FB802D", discoveryReward.id());
        assertEquals(ch.minenox.firsttorch.guide.model.RewardDefinition.Type.EXPERIENCE, discoveryReward.type());
        assertEquals(5, discoveryReward.amount());
    }

    @Test void inventoryThresholdsCountEarlyWithoutProvingSafety() throws Exception {
        var snapshot = snapshot();
        var tasks = snapshot.guides().getFirst().chapters().get(28).quests().getFirst().tasks();
        for (var task : tasks) {
            if (task.type() == TaskDefinition.Type.MANUAL) continue;
            String key = (task.type() == TaskDefinition.Type.INVENTORY_TAG ? "#" : "") + task.itemId();
            var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, observed -> observed.equals(key) ? task.count() - 1 : 0);
            assertFalse(low.completedTaskIds().contains(task.id()), task.id());
            var enough = TaskEvaluator.evaluate(snapshot, low, observed -> observed.equals(key) ? task.count() : 0);
            assertTrue(enough.completedTaskIds().contains(task.id()), task.id());
            assertFalse(enough.completedQuestIds().contains(QUESTS.getFirst()));
            assertFalse(enough.completedTaskIds().contains("5583FD1F127A4B12"));
        }
    }

    @Test void physicalPreparationNeedsNoBarterOrResourcesButRequiresItsOwnCheck() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of("36AD35CCA4724357", "1B63A993ED3C4DB3", "3E4AF2D781EC4B57", "65C89337665C4E28", "21D6CE3AD23A412B"), Set.of("07105D263E94F5A2"));
        assertFalse(TaskEvaluator.evaluate(snapshot, ready, key -> 0).completedQuestIds().contains(QUESTS.getFirst()));
        var prepared = TaskEvaluator.confirm(snapshot, ready, QUESTS.getFirst(), "5583FD1F127A4B12", key -> 0);
        assertTrue(prepared.completedQuestIds().contains(QUESTS.getFirst()));
        assertFalse(prepared.completedQuestIds().contains(QUESTS.getLast()));
        assertFalse(prepared.completedQuestIds().contains("33CB08D1E94FA05C"));
        assertFalse(prepared.completedQuestIds().contains("72E9F608A3C4D7D1"));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
