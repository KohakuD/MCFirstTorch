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

final class OptionalCompletionBatchTest {
    private static final List<String> MOBS = List.of("0D51B7F3AE264C09", "52A60C48F37B915E", "15D93F7B26AEC481", "37FB519D48C0E6A3", "591D73BF6AE208C5", "6B3F95D18C042AE7", "1D62C804BF375E1A");
    private static final List<String> BASTION = List.of("0756BFDE228E428E", "3DE769F7D32C4A9C", "639CF2C727F64672", "69CCB8EF5C5C4F39", "4BBE2FCA33A44E31", "2B8E074C6B014FB6", "5648FD5148EA4CA9");

    @Test void retainsOriginalIdsBranchesAndRewardsWithoutGatingTheCourse() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        var mobs = chapters.get(47).quests();
        var bastion = chapters.get(48).quests();
        assertEquals("702FA5D18C643BE9", chapters.get(47).id());
        assertEquals("63DA6D029FB74CE9", chapters.get(48).id());
        assertEquals(MOBS, mobs.stream().map(q -> q.id()).toList());
        assertEquals(BASTION, bastion.stream().map(q -> q.id()).toList());
        assertEquals(List.of("07B5E9C31D864AF2"), mobs.getFirst().prerequisiteQuestIds());
        assertEquals(List.of(MOBS.getFirst()), mobs.get(1).prerequisiteQuestIds());
        for (int i = 2; i <= 5; i++) assertEquals(List.of(MOBS.get(1)), mobs.get(i).prerequisiteQuestIds());
        assertEquals(List.of(MOBS.get(3)), mobs.get(6).prerequisiteQuestIds());
        assertEquals(List.of("07105D263E94F5A2"), bastion.getFirst().prerequisiteQuestIds());
        for (int i = 1; i < 7; i++) assertEquals(List.of(BASTION.get(i - 1)), bastion.get(i).prerequisiteQuestIds());
        assertTrue(chapters.subList(0, 47).stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(id -> MOBS.contains(id) || BASTION.contains(id)));
        var tasks = chapters.subList(47, 49).stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.tasks().stream()).toList();
        assertEquals(List.of("1E62C804BF375D1A", "2F73D915C0486E2B", "3084EA26D1597F3C", "63B71D59048CA26F", "26EA408C37BFD592", "480C62AE59D1F7B4", "6A2E84C07BF319D6", "0C51B7F3AE264D09", "2E73D915C0486F2B", "4AC7E176091C4381", "78AFC92617A54283", "54148BC05EBD45D0", "14D146623A394141", "4E35081C01814BE1", "15AA86345B664DDC", "63C39DFA6EEC4313", "3F9D7D5D79B54342", "06B993147A0C4E1C", "402ABF971FE94B86", "50F984274598419E", "232D9AA60A8E408C", "301286261C404A31"), tasks.stream().map(t -> t.id()).toList());
        var rewards = chapters.subList(47, 49).stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("4195FB37E26A804D", "74C82E6A159DB370", "3F84EA26D159704C", "543FFA158A2E4B1C"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(4, 2, 5, 10), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:torch", rewards.get(0).itemId());
        assertEquals("minecraft:bread", rewards.get(1).itemId());
    }

    @Test void nativeTagsMatchRetainedFiltersIncludingTheirFoodDifference() throws Exception {
        assertEquals(Set.of("minecraft:wooden_sword", "minecraft:stone_sword", "minecraft:copper_sword", "minecraft:iron_sword", "minecraft:diamond_sword", "minecraft:netherite_sword"), tag("ordinary_swords"));
        var cooked = tag("cooked_food");
        var substantial = tag("nether_food");
        assertEquals(9, cooked.size());
        assertEquals(8, substantial.size());
        assertTrue(cooked.contains("minecraft:dried_kelp"));
        assertFalse(substantial.contains("minecraft:dried_kelp"));
        assertTrue(cooked.containsAll(substantial));
        var chapters = snapshot().guides().getFirst().chapters();
        var prep = chapters.get(47).quests().getFirst().tasks();
        assertEquals("#firsttorch:ordinary_swords", prep.getFirst().inventoryKey());
        assertEquals("#firsttorch:cooked_food", prep.get(2).inventoryKey());
        assertEquals(2, prep.get(2).count());
        var hard = chapters.get(48).quests().get(1).tasks();
        assertEquals("#firsttorch:iron_or_better_swords", hard.get(0).inventoryKey());
        assertEquals("#firsttorch:iron_or_better_pickaxes", hard.get(1).inventoryKey());
        assertEquals("#firsttorch:nether_food", hard.get(4).inventoryKey());
        assertEquals(List.of(1, 1, 1, 64, 16, 1), hard.stream().map(t -> t.count()).toList());
        assertEquals(TaskDefinition.Type.MANUAL, hard.getLast().type());
    }

    @Test void quantitiesAndBastionObservationNeverReplaceManualSafety() throws Exception {
        var snapshot = snapshot();
        var chapter = snapshot.guides().getFirst().chapters().get(48);
        var prep = chapter.quests().get(1);
        var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:cobblestone") ? 63 : key.equals("#firsttorch:nether_food") ? 15 : 0);
        assertFalse(low.completedTaskIds().contains("4E35081C01814BE1"));
        assertFalse(low.completedTaskIds().contains("15AA86345B664DDC"));
        var supplied = TaskEvaluator.evaluate(snapshot, new ProgressState(Set.of(), Set.of(BASTION.getFirst())), key -> 64);
        assertFalse(supplied.completedQuestIds().contains(prep.id()));
        assertFalse(supplied.completedTaskIds().contains("63C39DFA6EEC4313"));
        var edge = chapter.quests().get(3);
        var entry = edge.tasks().getFirst();
        assertEquals("minecraft:nether/find_bastion", entry.advancementId());
        assertEquals("bastion", entry.criterion());
        var old = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("@minecraft:nether/find_fortress|fortress") ? 1 : 0);
        assertFalse(old.completedTaskIds().contains(entry.id()));
        var found = TaskEvaluator.evaluate(snapshot, old, key -> key.equals(entry.inventoryKey()) ? 1 : 0);
        assertTrue(found.completedTaskIds().contains(entry.id()));
        assertFalse(found.completedQuestIds().contains(edge.id()));
        var unlocked = new ProgressState(found.completedTaskIds(), Set.of(BASTION.get(2)));
        assertTrue(TaskEvaluator.confirm(snapshot, unlocked, edge.id(), edge.tasks().getLast().id(), key -> 0).completedQuestIds().contains(edge.id()));
    }

    private static Set<String> tag(String name) throws Exception {
        try (var input = OptionalCompletionBatchTest.class.getResourceAsStream("/data/firsttorch/tags/item/" + name + ".json")) {
            var array = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("values");
            var result = new java.util.HashSet<String>();
            array.forEach(value -> result.add(value.getAsString()));
            return result;
        }
    }
    private static GuideSnapshot snapshot() throws Exception {
        try (var input = OptionalCompletionBatchTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
