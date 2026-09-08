package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.QuestDefinition.PrerequisiteMode;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class ProtectionChapterTest {
    private static final List<String> QUEST_IDS = List.of("4D92C7A10E638BF5", "18A6D3F90C754BE2",
            "6F40D8A21C953BE7", "5E27A4D90B836CF1", "07B5E9C31D864AF2");
    private static final List<String> TASK_IDS = List.of("12A6E3D80C754BF9", "29B7E4C10D836AF5",
            "34C7E2A90D615BF8", "23B9F1C60D745AE8", "45D1A7E80C936BF2");

    @Test void preservesTheBoundedProtectionLessonsAndSourceObjectives() throws Exception {
        var chapter = course().chapters().get(6);
        assertEquals("4C86EA032D9F517B", chapter.id());
        assertEquals(6, chapter.order());
        assertEquals("minecraft:shield", chapter.iconItemId());
        assertEquals(QUEST_IDS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASK_IDS, chapter.quests().stream().map(q -> q.tasks().getFirst().id()).toList());
        assertEquals(TaskDefinition.Type.INVENTORY, chapter.quests().getFirst().tasks().getFirst().type());
        assertEquals("minecraft:shield", chapter.quests().getFirst().tasks().getFirst().itemId());
        assertEquals(PrerequisiteMode.ANY, chapter.quests().get(1).prerequisiteMode());
        assertEquals(List.of("3C16B9E50A724DF8", "6C4AE8F31D957B20"), chapter.quests().get(1).prerequisiteQuestIds());
        assertEquals(List.of("3AC8F5D20E917B64", "56E8B2C10D734AF9"), chapter.quests().stream()
                .flatMap(q -> q.rewards().stream()).map(r -> r.id()).toList());
        assertEquals(5, chapter.quests().get(1).rewards().getFirst().amount());
        assertEquals("minecraft:iron_ingot", chapter.quests().getLast().rewards().getFirst().itemId());
    }

    @Test void armourUnlocksFromEitherIngotButShieldStillNeedsIron() throws Exception {
        var guide = course();
        var snapshot = new GuideSnapshot(List.of(guide));
        var before = new ProgressState(java.util.Set.of(), java.util.Set.of("6C4AE8F31D957B20"));
        var state = TaskEvaluator.confirm(snapshot, before, "18A6D3F90C754BE2", "29B7E4C10D836AF5",
                key -> key.equals("minecraft:copper_ingot") ? 1 : 0);
        assertTrue(state.completedQuestIds().contains("18A6D3F90C754BE2"));
        assertFalse(state.completedQuestIds().contains("4D92C7A10E638BF5"));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, state,
                "4D92C7A10E638BF5", "12A6E3D80C754BF9", key -> 0));
    }

    private static ch.minenox.firsttorch.guide.model.GuideDefinition course() throws Exception {
        try (var input = ProtectionChapterTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            assertNotNull(input);
            return GuideJson.read(input);
        }
    }
}
