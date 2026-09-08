package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class NetherPortalChapterTest {
    private static final List<String> QUESTS = List.of("0249C5F18EA63BD7", "246BE713A0C85DF9", "468D0935C2EA7F1B", "79B03C68F51DA24E", "2CE36F9B2840D571");

    @Test void preservesOriginalGateChainAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(22);
        assertEquals("6138B4E07D952AC6", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("6A2C84E05D916B37"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(List.of(TaskDefinition.Type.MANUAL, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL),
                chapter.quests().stream().map(q -> q.tasks().getFirst().type()).toList());
        assertEquals(List.of("135AD6029FB74CE8", "357CF824B1D96E0A", "579E1A46D3FB802C", "0AC14D79062EB35F", "3DF470AC3951E682"),
                chapter.quests().stream().map(q -> q.tasks().getFirst().id()).toList());
        assertEquals(List.of(1, 1, 1, 10, 1), chapter.quests().stream().map(q -> q.tasks().getFirst().count()).toList());
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("68AF2B57E40C913D", "1BD25E8A173FC460", "4E0581BD4A62F793", "5F1692CE5B7308A4"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(1, 5, 8, 5), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:iron_ingot", rewards.getFirst().itemId());
        assertEquals("minecraft:torch", rewards.get(2).itemId());
    }

    @Test void requiresTenNormalObsidianAndLeavesSafetyChecksManual() throws Exception {
        var snapshot = snapshot();
        var nine = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:obsidian") ? 9 : 0);
        assertFalse(nine.completedTaskIds().contains("0AC14D79062EB35F"));
        var crying = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:crying_obsidian") ? 10 : 0);
        assertFalse(crying.completedTaskIds().contains("0AC14D79062EB35F"));
        var ten = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:obsidian") ? 10 : 0);
        assertTrue(ten.completedTaskIds().contains("0AC14D79062EB35F"));
        assertFalse(ten.completedQuestIds().contains(QUESTS.get(3)));
        assertFalse(ten.completedTaskIds().contains("135AD6029FB74CE8"));
        assertFalse(ten.completedTaskIds().contains("3DF470AC3951E682"));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, ten, QUESTS.getLast(), "3DF470AC3951E682", key -> 0));
    }

    @Test void firstEnchantmentOpensPreparationWithoutLibraryCompletion() throws Exception {
        var snapshot = snapshot();
        var prior = new ProgressState(Set.of(), Set.of("6A2C84E05D916B37"));
        var ready = TaskEvaluator.confirm(snapshot, prior, QUESTS.getFirst(), "135AD6029FB74CE8", key -> 0);
        assertTrue(ready.completedQuestIds().contains(QUESTS.getFirst()));
        assertFalse(ready.completedQuestIds().contains("4093C7EBF526DA81"));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
