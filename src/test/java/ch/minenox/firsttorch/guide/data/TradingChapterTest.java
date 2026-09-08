package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class TradingChapterTest {
    private static final List<String> QUESTS = List.of("36EB094C7D825F1A", "580D2B6E1FA47C3D",
            "7A2F4D801C639E5F", "4B70A16D3E92C5F8", "6D92C38F50B4E71A", "0FB4E5A172D6093C");

    @Test void preservesTradingChainTasksAndSourceRewards() throws Exception {
        var guide = snapshot().guides().getFirst();
        var chapter = guide.chapters().get(18);
        assertEquals("4E0C628BAF17395D", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("14C9E72A5B603DF8"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(TaskDefinition.Type.ADVANCEMENT, chapter.quests().get(2).tasks().getFirst().type());
        assertEquals("minecraft:adventure/trade", chapter.quests().get(2).tasks().getFirst().advancementId());
        assertEquals("traded", chapter.quests().get(2).tasks().getFirst().criterion());
        assertEquals(4, chapter.quests().get(2).rewards().getFirst().amount());
        assertEquals(5, chapter.quests().get(2).rewards().get(1).amount());
        assertEquals(3, chapter.quests().getLast().rewards().getFirst().amount());
        assertEquals(5, chapter.quests().getLast().rewards().get(1).amount());
        assertTrue(guide.chapters().stream().filter(c -> !c.id().equals(chapter.id())).flatMap(c -> c.quests().stream())
                .noneMatch(q -> q.prerequisiteQuestIds().stream().anyMatch(QUESTS::contains)));
    }

    @Test void priorTradeAdvancementIsRecordedButCannotBypassLockedQuest() throws Exception {
        var snapshot = snapshot();
        var state = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                key -> key.equals("@minecraft:adventure/trade|traded") ? 1 : 0);
        assertTrue(state.completedTaskIds().contains("0B305E912D74AF60"));
        assertFalse(state.completedQuestIds().contains(QUESTS.get(2)));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot,
                new ProgressState(Set.of(), Set.of("14C9E72A5B603DF8")), QUESTS.get(1), "691E3C7F20B58D4E", key -> 0));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
