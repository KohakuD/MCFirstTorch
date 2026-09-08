package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.*;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Source identity, optional navigation and preparation safeguards for the portal route. */
final class StrongholdInteriorBatchTest {
    private static final List<String> INTERIOR = List.of("086D39F5C0A47E2B", "2A8F5B17E2C6904D", "3C906D28F3D7A15E", "4CA17D3904E8B26F", "6EC39F5B260AD481");
    private static final List<String> ROOM = List.of("10E5B17D482CF6A3", "3207D39F6A4E18C5", "5429F5B18C603AE7", "764B17D3AE825C09");
    private static final List<String> PREPARATION = List.of("098E4A06D1B58F3D", "2BA06C28F3D7A15F", "7055B17D482CF6B4", "78DD39F5C0A47F3C", "2B106C28F3D7B26F", "4D328E4A15F9D481", "6F54A06C371BF6A3");

    @Test void retainsSourceIdentitiesDependenciesAndRewardsAcrossThreeSmallChapters() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters().subList(34, 37);
        assertEquals(List.of("757C28E4BF936D0A", "7AA1D479062EB350", "7BB2E58A173FC461"), chapters.stream().map(c -> c.id()).toList());
        assertEquals(List.of(INTERIOR, ROOM, PREPARATION), chapters.stream().map(c -> c.quests().stream().map(q -> q.id()).toList()).toList());
        assertEquals(List.of("74DB8F5B15F9C370"), chapters.getFirst().quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of(INTERIOR.get(1)), chapters.getFirst().quests().getLast().prerequisiteQuestIds());
        assertEquals(List.of(INTERIOR.get(3)), chapters.get(1).quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of(ROOM.getLast()), chapters.getLast().quests().getFirst().prerequisiteQuestIds());
        for (var chapter : chapters.subList(1, 3)) {
            for (int i = 1; i < chapter.quests().size(); i++) assertEquals(List.of(chapter.quests().get(i - 1).id()), chapter.quests().get(i).prerequisiteQuestIds());
        }
        var tasks = chapters.stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.tasks().stream()).toList();
        assertEquals(List.of("197E4A06D1B58F3C", "3B906C28F3D7A15E", "4DA17E3904E8B26F", "5DB28E4A15F9C370", "7FD4A06C371BE592", "21F6C28E593D07B4", "4318E4A07B5F29D6", "653A06C29D714BF8", "075C28E4BF936D1A", "1A9F5B17E2C6904E", "3CB17D3904E8B270", "4DC28E4A15F9C381", "0166C28E593D07C5", "1277D39F6A4E18D6", "2388E4A07B5F29E7", "3499F5B18C603AF8", "45AA06C29D714C09", "56BB17D3AE825D1A", "67CC28E4BF936E2B", "09EE4A06D1B5904D", "3C217D3904E8C370", "5E439F5B260AE592", "7065B17D482C07B4"), tasks.stream().map(t -> t.id()).toList());
        var rewards = chapters.stream().flatMap(c -> c.quests().stream()).flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("186D39F5C0A47E2B", "297E4A06D1B58F3C", "5ED39F5B260AD492", "6FE4A06C371BE5A3", "1AFF5B17E2C6A15E", "0176C28E593D18C5"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(1, 10, 32, 5, 4, 10), rewards.stream().map(r -> r.amount()).toList());
        assertEquals("minecraft:golden_apple", rewards.get(0).itemId());
        assertEquals("minecraft:arrow", rewards.get(2).itemId());
        assertEquals("minecraft:bread", rewards.get(4).itemId());
    }

    @Test void nineInventoryConditionsAreAutomaticStickyAndDoNotBypassLockedQuests() throws Exception {
        var snapshot = snapshot();
        var automatic = snapshot.guides().getFirst().chapters().subList(34, 37).stream()
                .flatMap(c -> c.quests().stream()).flatMap(q -> q.tasks().stream()).filter(TaskDefinition::automatic).toList();
        assertEquals(9, automatic.size());
        assertEquals(List.of(2, 1, 32, 1, 1, 1, 1, 64, 16), automatic.stream().map(t -> t.count()).toList());
        for (var task : automatic) {
            var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals(task.inventoryKey()) ? task.count() - 1 : 0);
            assertFalse(low.completedTaskIds().contains(task.id()));
            var enough = TaskEvaluator.evaluate(snapshot, low, key -> key.equals(task.inventoryKey()) ? task.count() : 0);
            assertTrue(enough.completedTaskIds().contains(task.id()));
            assertTrue(enough.completedQuestIds().isEmpty());
            assertTrue(TaskEvaluator.evaluate(snapshot, enough, key -> 0).completedTaskIds().contains(task.id()));
            assertTrue(ClaimableRewards.ids(snapshot, enough, Set.of(), Set.of()).isEmpty());
        }
    }

    @Test void optionalLibraryNeverBlocksPortalRoomAndReturningRemainsSeparate() throws Exception {
        var snapshot = snapshot();
        var prior = new ProgressState(Set.of(), Set.of(INTERIOR.get(3)));
        var found = TaskEvaluator.confirm(snapshot, prior, ROOM.getFirst(), "21F6C28E593D07B4", key -> 0);
        assertTrue(found.completedQuestIds().contains(ROOM.getFirst()));
        assertFalse(found.completedQuestIds().contains(INTERIOR.getLast()));
        assertFalse(found.completedQuestIds().contains(ROOM.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, found, Set.of(), Set.of()).contains(ROOM.getLast()));
        var ready = new ProgressState(Set.of(), Set.of(ROOM.get(2)));
        var returned = TaskEvaluator.confirm(snapshot, ready, ROOM.getLast(), "075C28E4BF936D1A", key -> 0);
        assertTrue(ClaimableRewards.ids(snapshot, returned, Set.of(), Set.of()).contains(ROOM.getLast()));
        assertFalse(ClaimableRewards.ids(snapshot, returned, Set.of(ROOM.getLast()), Set.of()).contains(ROOM.getLast()));
    }

    @Test void equipmentNeverConfirmsArmourRespawnOrPortalActivation() throws Exception {
        var snapshot = snapshot();
        var equipped = TaskEvaluator.evaluate(snapshot, new ProgressState(Set.of(), Set.of(PREPARATION.get(1))), key -> 4096);
        assertFalse(equipped.completedQuestIds().contains(PREPARATION.get(2)));
        assertFalse(equipped.completedTaskIds().contains("67CC28E4BF936E2B"));
        var checked = TaskEvaluator.confirm(snapshot, equipped, PREPARATION.get(2), "67CC28E4BF936E2B", key -> 4096);
        assertTrue(checked.completedQuestIds().contains(PREPARATION.get(2)));
        assertFalse(checked.completedQuestIds().contains(PREPARATION.get(3)));
        var finalQuest = snapshot.guides().getFirst().chapters().get(36).quests().getLast();
        assertEquals(TaskDefinition.Type.MANUAL, finalQuest.tasks().getFirst().type());
        var before = new ProgressState(Set.of(), Set.of(PREPARATION.get(5)));
        assertFalse(TaskEvaluator.evaluate(snapshot, before, key -> 4096).completedQuestIds().contains(finalQuest.id()));
        var activated = TaskEvaluator.confirm(snapshot, before, finalQuest.id(), "7065B17D482C07B4", key -> 0);
        assertTrue(ClaimableRewards.ids(snapshot, activated, Set.of(), Set.of()).contains(finalQuest.id()));
        assertFalse(ClaimableRewards.ids(snapshot, activated, Set.of(finalQuest.id()), Set.of()).contains(finalQuest.id()));
    }

    @Test void preparationFiltersKeepTheExactSourceAlternatives() throws Exception {
        var equipment = snapshot().guides().getFirst().chapters().get(36).quests().get(2).tasks();
        assertEquals(List.of("firsttorch:iron_or_better_swords", "firsttorch:iron_or_better_pickaxes", "minecraft:shield", "minecraft:water_bucket", "minecraft:cobblestone", "firsttorch:nether_food"),
                equipment.stream().filter(TaskDefinition::automatic).map(TaskDefinition::itemId).toList());
        for (var tag : java.util.Map.of(
                "iron_or_better_swords", Set.of("minecraft:iron_sword", "minecraft:diamond_sword", "minecraft:netherite_sword"),
                "iron_or_better_pickaxes", Set.of("minecraft:iron_pickaxe", "minecraft:diamond_pickaxe", "minecraft:netherite_pickaxe"),
                "nether_food", Set.of("minecraft:cooked_beef", "minecraft:cooked_porkchop", "minecraft:cooked_chicken", "minecraft:cooked_mutton", "minecraft:cooked_rabbit", "minecraft:cooked_cod", "minecraft:cooked_salmon", "minecraft:baked_potato")).entrySet()) {
            try (var input = getClass().getResourceAsStream("/data/firsttorch/tags/item/" + tag.getKey() + ".json")) {
                var values = com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("values");
                var actual = new java.util.HashSet<String>();
                values.forEach(v -> actual.add(v.getAsString()));
                assertEquals(tag.getValue(), actual);
            }
        }
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
