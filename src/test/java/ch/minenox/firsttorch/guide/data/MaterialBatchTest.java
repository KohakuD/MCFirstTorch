package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.*;
import ch.minenox.firsttorch.guide.progress.*;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class MaterialBatchTest {
    private static final List<String> IDS = List.of("7B83D5F920C4160E", "56CA245D80EB7913",
            "18EC467FA20D9B35", "46B7D91E2A5C803F", "3A0E6891C42FBD57", "65A1B2C3D4E5F607",
            "5C208AB3E641DF79", "6A24D8F30C715BE9", "7E42ACD50863F19B", "1064CEF72A8513BD");
    private static final List<String> PARENTS = List.of("34A8023B6ECF5791", "7B83D5F920C4160E",
            "56CA245D80EB7913", "56CA245D80EB7913", "18EC467FA20D9B35", "3A0E6891C42FBD57",
            "65A1B2C3D4E5F607", "5C208AB3E641DF79", "6A24D8F30C715BE9", "46B7D91E2A5C803F");

    @Test void preservesSourceBranchesTasksAndRewardIds() throws Exception {
        var guide = load();
        var quests = guide.chapters().stream().skip(1).limit(3).flatMap(c -> c.quests().stream()).toList();
        assertEquals(18, quests.size());
        var byId = quests.stream().collect(Collectors.toMap(QuestDefinition::id, q -> q));
        var batch = IDS.stream().map(byId::get).toList();
        assertEquals(IDS, batch.stream().map(QuestDefinition::id).toList());
        for (int i = 0; i < batch.size(); i++) assertEquals(List.of(PARENTS.get(i)), batch.get(i).prerequisiteQuestIds());
        assertEquals(List.of("45B9134C7FDA6802", "07DB356E91FC8A24", "29FD5780B31EAC46",
                "57C8EA2F3B6D9140", "68D9FB304C7EA251", "4B1F79A2D530CE68", "16B2C3D4E5F60718",
                "38D4E5F60718293A", "6D319BC4F752E08A", "1B35E9042D826CF0", "2C46FA153E937D01",
                "0F53BDE6197402AC", "2175DF083B9624CE"),
                batch.stream().flatMap(q -> q.tasks().stream()).map(TaskDefinition::id).toList());
        assertEquals(List.of("27C3D4E5F6071829", "3D570B264FA48E12", "61D304B8A7CE295F", "2F517B3E160C248D"),
                batch.stream().flatMap(q -> q.rewards().stream()).map(RewardDefinition::id).toList());
        var rewards = batch.stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of(3, 5, 2, 3), rewards.stream().map(RewardDefinition::amount).toList());
        assertEquals(List.of("minecraft:cobblestone", "minecraft:apple", "minecraft:white_wool"),
                rewards.stream().filter(r -> r.type() == RewardDefinition.Type.ITEM).map(RewardDefinition::itemId).toList());
        assertEquals(RewardDefinition.Type.EXPERIENCE, rewards.get(1).type());
        assertEquals("minecraft:planks", batch.getFirst().tasks().getFirst().itemId());
        assertEquals("INVENTORY_TAG", batch.getFirst().tasks().getFirst().type().name());
        assertEquals(4, batch.getFirst().tasks().getFirst().count());
        assertEquals(9, batch.stream().filter(q -> q.image() != null).count());
        assertTrue(batch.stream().allMatch(q -> q.iconItemId() != null));
    }

    @Test void materialPathDoesNotRequireOptionalMovementAndMixedManualWorkIsNotSkipped() throws Exception {
        var guide = load();
        var snapshot = new GuideSnapshot(List.of(guide));
        var known = guide.chapters().stream().flatMap(c -> c.quests().stream())
                .collect(Collectors.toMap(QuestDefinition::id, q -> q));
        var intro = guide.chapters().getFirst().quests();
        var tasks = intro.stream().flatMap(q -> q.tasks().stream()).map(TaskDefinition::id).collect(Collectors.toSet());
        var done = intro.stream().map(QuestDefinition::id).collect(Collectors.toSet());
        tasks.add("6A72C4E819B305FD"); done.add("34A8023B6ECF5791");
        var state = new ProgressState(tasks, done);
        var counts = Map.of("#minecraft:planks", 4, "minecraft:crafting_table", 1,
                "minecraft:wooden_pickaxe", 1, "minecraft:wooden_shovel", 1, "minecraft:cobblestone", 8,
                "minecraft:stone_axe", 1, "minecraft:furnace", 1, "minecraft:chest", 1, "minecraft:torch", 8);
        state = TaskEvaluator.evaluate(snapshot, state, id -> counts.getOrDefault(id, 0));
        assertTrue(state.completedQuestIds().containsAll(IDS.subList(0, 3)));
        assertFalse(state.completedQuestIds().contains("0A71D4C8E2F963B5"));
        assertFalse(state.completedQuestIds().contains("46B7D91E2A5C803F"));
        for (String id : List.of("46B7D91E2A5C803F", "65A1B2C3D4E5F607", "6A24D8F30C715BE9", "1064CEF72A8513BD")) {
            var manual = known.get(id).tasks().stream().filter(t -> t.type() == TaskDefinition.Type.MANUAL).findFirst().orElseThrow();
            state = TaskEvaluator.confirm(snapshot, state, id, manual.id(), key -> counts.getOrDefault(key, 0));
        }
        assertTrue(state.completedQuestIds().containsAll(IDS));
        assertEquals(state, TaskEvaluator.evaluate(snapshot, state, id -> 0));
    }

    @Test void batchNodesFitBothMapWidthsWithoutOverlap() throws Exception {
        for (var chapter : load().chapters().subList(1, 4)) {
        var quests = chapter.quests();
        for (boolean reading : List.of(false, true)) {
            var panel = ch.minenox.firsttorch.client.FirstTorchLayout.calculate(720, 405, reading).questMap();
            if (reading) panel = new ch.minenox.firsttorch.client.FirstTorchLayout.Rect(
                    panel.x(), panel.y() + 23, panel.width(), panel.height() - 23);
            var nodes = ch.minenox.firsttorch.client.FirstTorchLayout.questNodes(panel, quests);
            assertTrue(nodes.values().stream().allMatch(panel::contains));
            var rectangles = List.copyOf(nodes.values());
            for (int i = 0; i < rectangles.size(); i++) for (int j = i + 1; j < rectangles.size(); j++) {
                var a = rectangles.get(i); var b = rectangles.get(j);
                assertFalse(a.x() < b.right() && a.right() > b.x() && a.y() < b.bottom() && a.bottom() > b.y());
            }
        }
        }
    }

    private static GuideDefinition load() throws Exception {
        try (var input = MaterialBatchTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return GuideJson.read(input);
        }
    }
}
