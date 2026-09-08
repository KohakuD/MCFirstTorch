package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.util.List;
import org.junit.jupiter.api.Test;

final class BoatExcursionChapterTest {
    private static final List<String> QUESTS = List.of("12E8A5C74F309BD6", "34FAC7E96152BDF8",
            "16B02D4FC7B8135E", "38D24F61E9DA3570", "5AF461830BFC5792", "7C1683A52D1E79B4");

    @Test void preservesBoatExcursionIdsDependenciesEquipmentAndReward() throws Exception {
        var guide = snapshot().guides().getFirst();
        var chapter = guide.chapters().get(16);
        assertEquals("68C1F4072D9A5BE3", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("0BED012A8146C359", "07B5E9C31D864AF2"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(10, chapter.quests().stream().mapToInt(q -> q.tasks().size()).sum());
        var equipment = chapter.quests().get(1).tasks();
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.INVENTORY_TAG, TaskDefinition.Type.INVENTORY,
                TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL), equipment.stream().map(TaskDefinition::type).toList());
        assertEquals(List.of(1, 8, 16, 32, 1), equipment.stream().map(t -> t.count()).toList());
        assertEquals("firsttorch:cooked_food", equipment.get(1).itemId());
        assertEquals("firsttorch:ordinary_boats", chapter.quests().get(3).tasks().getFirst().itemId());
        assertEquals(5, chapter.quests().getLast().rewards().getFirst().amount());
        assertTrue(guide.chapters().stream().filter(c -> !c.id().equals(chapter.id())
                && !c.id().equals("3DFB517A9E06284C")).flatMap(c -> c.quests().stream())
                .noneMatch(q -> q.prerequisiteQuestIds().stream().anyMatch(QUESTS::contains)));
    }

    @Test void equipmentNeedsEveryThresholdAndTheSeparatePracticeCheck() throws Exception {
        var snapshot = snapshot();
        var quest = snapshot.guides().getFirst().chapters().get(16).quests().get(1);
        var state = new ch.minenox.firsttorch.guide.progress.ProgressState(
                java.util.Set.of(), java.util.Set.of(QUESTS.getFirst()));
        for (var missing : quest.tasks().stream().filter(TaskDefinition::automatic).toList()) {
            var counts = new java.util.HashMap<String, Integer>();
            quest.tasks().stream().filter(TaskDefinition::automatic)
                    .forEach(t -> counts.put(t.inventoryKey(), t.count()));
            counts.put(missing.inventoryKey(), missing.count() - 1);
            var result = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, state,
                    key -> counts.getOrDefault(key, 0));
            assertFalse(result.completedTaskIds().contains(missing.id()));
            assertFalse(result.completedQuestIds().contains(quest.id()));
        }
        var counts = new java.util.HashMap<String, Integer>();
        quest.tasks().stream().filter(TaskDefinition::automatic)
                .forEach(t -> counts.put(t.inventoryKey(), t.count()));
        var ready = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, state,
                key -> counts.getOrDefault(key, 0));
        assertFalse(ready.completedQuestIds().contains(quest.id()));
        assertTrue(ch.minenox.firsttorch.guide.progress.TaskEvaluator.confirm(snapshot, ready,
                quest.id(), quest.tasks().getLast().id(), key -> 0).completedQuestIds().contains(quest.id()));
    }

    @Test void boatTagContainsOnlyTheTenOrdinarySourceVariants() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/tags/item/ordinary_boats.json")) {
            var json = com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(
                    input, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            var values = java.util.stream.StreamSupport.stream(json.getAsJsonArray("values").spliterator(), false)
                    .map(com.google.gson.JsonElement::getAsString).toList();
            assertEquals(List.of("oak_boat", "spruce_boat", "birch_boat", "jungle_boat", "acacia_boat",
                    "dark_oak_boat", "mangrove_boat", "cherry_boat", "pale_oak_boat", "bamboo_raft")
                    .stream().map(id -> "minecraft:" + id).toList(), values);
        }
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
