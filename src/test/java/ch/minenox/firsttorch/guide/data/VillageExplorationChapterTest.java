package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class VillageExplorationChapterTest {
    private static final List<String> QUESTS = List.of("2F49B6D81E50AC73", "416BD8FA3072CE95",
            "638DFA1C5294E0B7", "14C9E72A5B603DF8");
    private static final List<String> TASKS = List.of("305AC7E92F61BD84", "527CE90B4183DFA6",
            "749E0B2D63A5F1C8", "25DAF83B6C714E09");

    @Test void preservesVillageSourceIdsManualChainAndReward() throws Exception {
        var guide = snapshot().guides().getFirst();
        var chapter = guide.chapters().get(17);
        assertEquals("3DFB517A9E06284C", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASKS, chapter.quests().stream().map(q -> q.tasks().getFirst().id()).toList());
        assertEquals(List.of("7C1683A52D1E79B4"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertTrue(chapter.quests().stream().flatMap(q -> q.tasks().stream()).allMatch(t -> t.type() == TaskDefinition.Type.MANUAL));
        assertEquals("05AF1C3E74B602D9", chapter.quests().get(2).rewards().getFirst().id());
        assertEquals(5, chapter.quests().get(2).rewards().getFirst().amount());
        assertTrue(guide.chapters().stream().filter(c -> !c.id().equals(chapter.id())
                && !c.id().equals("4E0C628BAF17395D")).flatMap(c -> c.quests().stream())
                .noneMatch(q -> q.prerequisiteQuestIds().stream().anyMatch(QUESTS::contains)));
    }

    @Test void manualVillageTasksDoNotCompleteFromInventoryObservations() throws Exception {
        var snapshot = snapshot();
        var state = new ProgressState(Set.of(), Set.of("7C1683A52D1E79B4"));
        assertFalse(TaskEvaluator.evaluate(snapshot, state, key -> 4096).completedQuestIds().contains(QUESTS.getFirst()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, state, QUESTS.get(1), TASKS.get(1), key -> 0));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
