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

final class FindingHomeChapterTest {
    private static final List<String> QUESTS = List.of("16C8E2A50D739BF4", "38EA04C72F95BD16",
            "5A0C26E941B7DF38", "7C2E480B63D9F15A", "2F6A91C4D8E307B5");
    private static final List<String> TASKS = List.of("27D9F3B61E84AC05", "49FB15D830A6CE27",
            "6B1D37FA52C8E049", "0D3F591C74EA026B", "40B7E2D96A1C58F3");

    @Test void preservesTheFiveSourceLessonsChainAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(9);
        assertEquals("3B75D9F20C8E4A61", chapter.id());
        assertEquals(13, chapter.order());
        assertEquals("minecraft:compass", chapter.iconItemId());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASKS, chapter.quests().stream().map(q -> q.tasks().getFirst().id()).toList());
        assertEquals(List.of("36CF412575EB038D"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) {
            assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
            assertEquals(TaskDefinition.Type.MANUAL, chapter.quests().get(i).tasks().getFirst().type());
        }
        var rewards = chapter.quests().get(3).rewards();
        assertEquals(2, rewards.size());
        assertEquals("1E406A2D05FB137C", rewards.getFirst().id());
        assertEquals(RewardDefinition.Type.EXPERIENCE, rewards.getFirst().type());
        assertEquals(5, rewards.getFirst().amount());
        assertEquals("6D3A80F152C7BE49", rewards.get(1).id());
        assertEquals(RewardDefinition.Type.ITEM, rewards.get(1).type());
        assertEquals("minecraft:compass", rewards.get(1).itemId());
        assertEquals(2, rewards.get(1).amount());
        assertTrue(chapter.quests().getLast().rewards().isEmpty());
    }

    @Test void manualLessonsNeverCompleteAutomaticallyOrBypassTheirChain() throws Exception {
        var snapshot = snapshot();
        var sourceDone = new ProgressState(Set.of(), Set.of("36CF412575EB038D"));
        var observed = TaskEvaluator.evaluate(snapshot, sourceDone, key -> 4096);
        assertTrue(TASKS.stream().noneMatch(observed.completedTaskIds()::contains));
        assertFalse(observed.completedQuestIds().contains(QUESTS.getFirst()));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, sourceDone,
                QUESTS.get(2), TASKS.get(2), key -> 0));
        var state = sourceDone;
        for (int i = 0; i < QUESTS.size(); i++) {
            state = TaskEvaluator.confirm(snapshot, state, QUESTS.get(i), TASKS.get(i), key -> 0);
        }
        assertTrue(state.completedQuestIds().containsAll(QUESTS));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
