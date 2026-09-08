package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class ComposterChapterTest {
    private static final List<String> QUESTS = List.of("1AC06B2E9537D4F8", "3CE28D40B759F61A", "5E04AF62D97B183C");

    @Test void preservesIdsGraphThresholdAndRewards() throws Exception {
        var guide = snapshot().guides().getFirst();
        var chapter = guide.chapters().get(13);
        assertEquals("0AC82E476BD3951F", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("62A84E0B73D9F15C"), chapter.quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.getFirst()), chapter.quests().get(1).prerequisiteQuestIds());
        assertEquals(List.of(QUESTS.get(1)), chapter.quests().getLast().prerequisiteQuestIds());
        assertEquals(TaskDefinition.Type.INVENTORY_TAG, chapter.quests().getFirst().tasks().getFirst().type());
        assertEquals(7, chapter.quests().getFirst().tasks().getFirst().count());
        assertEquals(32, chapter.quests().get(1).rewards().getFirst().amount());
        assertEquals(5, chapter.quests().getLast().rewards().getFirst().amount());
        assertEquals(TaskDefinition.Type.MANUAL, chapter.quests().getLast().tasks().getFirst().type());
        assertTrue(guide.chapters().stream().filter(c -> !c.id().equals(chapter.id())).flatMap(c -> c.quests().stream()).noneMatch(q -> q.prerequisiteQuestIds().stream().anyMatch(QUESTS::contains)));
    }

    @Test void sevenSlabsAreRequiredAndFinalManualTaskIsNotAutomatic() throws Exception {
        var snapshot = snapshot();
        var unlocked = new ProgressState(Set.of(), Set.of("62A84E0B73D9F15C"));
        assertFalse(TaskEvaluator.evaluate(snapshot, unlocked, key -> key.equals("#minecraft:wooden_slabs") ? 6 : 0).completedQuestIds().contains(QUESTS.getFirst()));
        var seven = TaskEvaluator.evaluate(snapshot, unlocked, key -> key.equals("#minecraft:wooden_slabs") ? 7 : 0);
        assertTrue(seven.completedQuestIds().contains(QUESTS.getFirst()));
        var composter = TaskEvaluator.evaluate(snapshot, seven, key -> key.equals("minecraft:composter") ? 1 : 0);
        assertFalse(composter.completedQuestIds().contains(QUESTS.getLast()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, seven, QUESTS.getLast(), "6F15B073EA8C294D", key -> 0));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
