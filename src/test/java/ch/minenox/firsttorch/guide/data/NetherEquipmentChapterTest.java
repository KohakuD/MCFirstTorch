package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class NetherEquipmentChapterTest {
    private static final List<String> QUESTS = List.of("14C1D6E29A5F730B", "47F40915CD82A63E", "02495E6AB2D7F183", "2BD2E7F34B6081AC", "5E05A1267B93C84F", "1249E56A3FD70C81");

    @Test void preservesSourceGateObjectivesAndRewards() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(23);
        assertEquals("7249C5F18EA63BD8", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("2CE36F9B2840D571"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < QUESTS.size(); i++) assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals(13, chapter.quests().stream().mapToInt(q -> q.tasks().size()).sum());
        assertEquals(5, chapter.quests().getFirst().tasks().getFirst().count());
        assertEquals("minecraft:gold_ingot", chapter.quests().getFirst().tasks().getFirst().itemId());
        assertEquals(List.of(TaskDefinition.Type.INVENTORY, TaskDefinition.Type.MANUAL), chapter.quests().get(1).tasks().stream().map(t -> t.type()).toList());
        assertEquals(List.of(1, 1, 1, 1, 32, 16, 8), chapter.quests().get(2).tasks().stream().map(t -> t.count()).toList());
        assertEquals("firsttorch:iron_or_better_swords", chapter.quests().get(2).tasks().getFirst().itemId());
        assertEquals("firsttorch:nether_food", chapter.quests().get(2).tasks().getLast().itemId());
        assertTrue(chapter.quests().stream().skip(3).allMatch(q -> q.tasks().size() == 1 && q.tasks().getFirst().type() == TaskDefinition.Type.MANUAL));
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("36E3F804BC71952D", "7A273C48F0B5D961", "01384D59A1C6E072", "1AC1D6E23A5F790B", "4DF409156D82A3CE", "7027C3481DB5EA6F", "0138D4592EC6FB70", "346B078C51F92EA3"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(4, 5, 5, 4, 5, 8, 5, 2), rewards.stream().map(r -> r.amount()).toList());
    }

    @Test void automaticThresholdsStaySeparateFromPracticalChecks() throws Exception {
        var snapshot = snapshot();
        var chapter = snapshot.guides().getFirst().chapters().get(23);
        for (var quest : chapter.quests()) for (var task : quest.tasks().stream().filter(TaskDefinition::automatic).toList()) {
            var shortCount = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(task.inventoryKey()) ? task.count() - 1 : 0);
            assertFalse(shortCount.completedTaskIds().contains(task.id()));
            var enough = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(task.inventoryKey()) ? task.count() : 0);
            assertTrue(enough.completedTaskIds().contains(task.id()));
            assertFalse(enough.completedQuestIds().contains(quest.id()));
        }
        var all = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> 4096);
        for (var task : chapter.quests().stream().flatMap(q -> q.tasks().stream()).filter(t -> !t.automatic()).toList()) {
            assertFalse(all.completedTaskIds().contains(task.id()));
        }
    }

    @Test void helmetPossessionDoesNotProveItWasWorn() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of(), Set.of("14C1D6E29A5F730B"));
        var held = TaskEvaluator.evaluate(snapshot, ready, key -> key.equals("minecraft:golden_helmet") ? 1 : 0);
        assertTrue(held.completedTaskIds().contains("58051A26DE93B74F"));
        assertFalse(held.completedQuestIds().contains(QUESTS.get(1)));
        var worn = TaskEvaluator.confirm(snapshot, held, QUESTS.get(1), "69162B37EFA4C850", key -> 0);
        assertTrue(worn.completedQuestIds().contains(QUESTS.get(1)));
        assertFalse(worn.completedQuestIds().contains(QUESTS.getLast()));
    }

    @Test void nativeTagsRetainExactSourceAlternatives() throws Exception {
        assertEquals(Set.of("minecraft:iron_sword", "minecraft:diamond_sword", "minecraft:netherite_sword"), tag("iron_or_better_swords"));
        assertEquals(Set.of("minecraft:cooked_beef", "minecraft:cooked_porkchop", "minecraft:cooked_chicken", "minecraft:cooked_mutton", "minecraft:cooked_rabbit", "minecraft:cooked_cod", "minecraft:cooked_salmon", "minecraft:baked_potato"), tag("nether_food"));
    }

    private Set<String> tag(String name) throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/tags/item/" + name + ".json")) {
            assertNotNull(input);
            var json = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
            return json.getAsJsonArray("values").asList().stream().map(value -> value.getAsString()).collect(java.util.stream.Collectors.toSet());
        }
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
