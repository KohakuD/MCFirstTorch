package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class LodestoneChapterTest {
    private static final List<String> QUESTS = List.of("15E7C9A42B806DF3", "37A9EBC64D028F15",
            "59CBED086F24A137", "0BED012A8146C359");
    private static final List<String> TASKS = List.of("26F8DAB53C917E04", "48BADCF75E139026",
            "6ADCF0197035B248", "3E1045DAB479F68C", "1CFE123B9257D46A");

    @Test void preservesSourceIdsCountsChainAndReward() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(10);
        assertEquals("6EA80C254FB1739D", chapter.id());
        assertEquals(10, chapter.order());
        assertEquals("minecraft:lodestone", chapter.iconItemId());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASKS, chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        assertEquals(List.of("2F6A91C4D8E307B5"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY,
                TaskDefinition.Type.ADVANCEMENT, TaskDefinition.Type.MANUAL), chapter.quests().stream()
                .flatMap(q -> q.tasks().stream()).map(TaskDefinition::type).toList());
        var advancement = chapter.quests().getLast().tasks().getFirst();
        assertEquals("minecraft:adventure/use_lodestone", advancement.advancementId());
        assertEquals("use_lodestone", advancement.criterion());
        var reward = chapter.quests().getLast().rewards().getFirst();
        assertEquals("2D0F234CA368E57B", reward.id());
        assertEquals(RewardDefinition.Type.EXPERIENCE, reward.type());
        assertEquals(5, reward.amount());
    }

    @Test void advancementIsAutomaticStickyAndStillRequiresFinalManualConfirmation() throws Exception {
        var snapshot = snapshot();
        var before = new ProgressState(Set.of(), Set.of("2F6A91C4D8E307B5", QUESTS.getFirst(), QUESTS.get(1), QUESTS.get(2)));
        var absent = TaskEvaluator.evaluate(snapshot, before, key -> 0);
        assertFalse(absent.completedTaskIds().contains(TASKS.get(3)));
        var manualOnly = TaskEvaluator.confirm(snapshot, absent, QUESTS.getLast(), TASKS.getLast(), key -> 0);
        assertFalse(manualOnly.completedQuestIds().contains(QUESTS.getLast()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, absent, QUESTS.getLast(), TASKS.get(3), key -> 0));
        var detected = TaskEvaluator.evaluate(snapshot, before, key -> key.equals("@minecraft:adventure/use_lodestone|use_lodestone") ? 1 : 0);
        assertTrue(detected.completedTaskIds().contains(TASKS.get(3)));
        assertFalse(detected.completedQuestIds().contains(QUESTS.getLast()));
        var sticky = TaskEvaluator.evaluate(snapshot, detected, key -> 0);
        assertTrue(sticky.completedTaskIds().contains(TASKS.get(3)));
        assertTrue(TaskEvaluator.confirm(snapshot, sticky, QUESTS.getLast(), TASKS.getLast(), key -> 0).completedQuestIds().contains(QUESTS.getLast()));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
