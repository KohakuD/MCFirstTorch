package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class MapsChapterTest {
    private static final List<String> QUESTS = List.of("43F829E5B61A7C0D", "65BA4B07D83C9E2F");
    private static final List<String> TASKS = List.of("54A93AF6C72B8D1E", "76CB5C18E94DAF30", "07DC6D29FA5EB041");

    @Test void preservesOptionalMapIdsTasksAndGraph() throws Exception {
        var guide = snapshot().guides().getFirst();
        var chapter = guide.chapters().get(11);
        assertEquals("7FB91D365AC2840E", chapter.id());
        assertEquals(15, chapter.order());
        assertEquals("minecraft:filled_map", chapter.iconItemId());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASKS, chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        assertEquals(List.of("2F6A91C4D8E307B5"), chapter.quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.getFirst()), chapter.quests().getLast().prerequisiteQuestIds());
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL),
                chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(TaskDefinition::type).toList());
        assertTrue(chapter.quests().stream().allMatch(q -> q.rewards().isEmpty()));
        assertTrue(guide.chapters().stream().filter(c -> !c.id().equals(chapter.id())).flatMap(c -> c.quests().stream())
                .noneMatch(q -> q.prerequisiteQuestIds().stream().anyMatch(QUESTS::contains)));
    }

    @Test void mapInventoryTasksAreAutomaticButExpansionStillNeedsManualConfirmation() throws Exception {
        var snapshot = snapshot();
        var unlocked = new ProgressState(Set.of(), Set.of("2F6A91C4D8E307B5"));
        var filled = TaskEvaluator.evaluate(snapshot, unlocked, key -> key.equals("minecraft:filled_map") ? 1 : 0);
        assertTrue(filled.completedQuestIds().contains(QUESTS.getFirst()));
        var table = TaskEvaluator.evaluate(snapshot, filled, key -> key.equals("minecraft:cartography_table") ? 1 : 0);
        assertTrue(table.completedTaskIds().contains(TASKS.get(1)));
        assertFalse(table.completedQuestIds().contains(QUESTS.getLast()));
        assertTrue(TaskEvaluator.confirm(snapshot, table, QUESTS.getLast(), TASKS.getLast(), key -> 0)
                .completedQuestIds().contains(QUESTS.getLast()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
