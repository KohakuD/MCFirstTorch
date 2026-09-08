package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.Test;

final class MiningChapterTest {
    private static final List<String> QUESTS = List.of("1A62D8E30C745BF9", "2E17C9A40D638BF5",
            "4059EBC61F85AD27", "627B0D8131A7CF49", "14AD2F0353C9E16B", "36CF412575EB038D");
    private static final List<String> TASKS = List.of("2B73E9F40D856AC1", "3C84FA150E967BD2", "4D950B261FA78CE3",
            "3F28DAB50E749C16", "516AFC702096BE38", "738C1E9242B8D05A", "25BE301464DAF27C", "47D0523686FC149E");

    @Test void preservesSourceIdentifiersAndSixLessonChain() throws Exception {
        var chapter = snapshot().guides().getFirst().chapters().get(7);
        assertEquals("5D97FB143EA0628C", chapter.id());
        assertEquals(QUESTS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASKS, chapter.quests().stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        assertEquals(List.of("07B5E9C31D864AF2"), chapter.quests().getFirst().prerequisiteQuestIds());
        for (int i = 1; i < 6; i++) {
            assertEquals(List.of(QUESTS.get(i - 1)), chapter.quests().get(i).prerequisiteQuestIds());
            assertEquals(TaskDefinition.Type.MANUAL, chapter.quests().get(i).tasks().getFirst().type());
        }
        assertEquals(List.of(2, 16, 4), chapter.quests().getFirst().tasks().stream().map(t -> t.count()).toList());
        var rewards = chapter.quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(1, rewards.size());
        assertEquals("58E16347970D25AF", rewards.getFirst().id());
        assertEquals(ch.minenox.firsttorch.guide.model.RewardDefinition.Type.EXPERIENCE, rewards.getFirst().type());
        assertEquals(10, rewards.getFirst().amount());
    }

    @Test void pickaxeTagExcludesWoodAndGoldAndKeepsEverySourceAlternative() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/tags/item/stone_or_better_pickaxes.json")) {
            assertNotNull(input);
            var tag = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
            var values = tag.getAsJsonArray("values");
            var actual = new HashSet<String>();
            values.forEach(value -> actual.add(value.getAsString()));
            assertEquals(5, values.size());
            assertEquals(Set.of("minecraft:stone_pickaxe", "minecraft:copper_pickaxe", "minecraft:iron_pickaxe",
                    "minecraft:diamond_pickaxe", "minecraft:netherite_pickaxe"), actual);
        }
    }

    @Test void allThreeThresholdsAreRequiredAndLockedItemsAreObservedWithoutUnlockingQuest() throws Exception {
        var snapshot = snapshot();
        var unlocked = new ProgressState(Set.of(), Set.of("07B5E9C31D864AF2"));
        for (int missing = 0; missing < 3; missing++) {
            final int index = missing;
            var insufficient = TaskEvaluator.evaluate(snapshot, unlocked, key -> switch (key) {
                case "#firsttorch:stone_or_better_pickaxes" -> index == 0 ? 1 : 2;
                case "minecraft:torch" -> index == 1 ? 15 : 16;
                case "#firsttorch:cooked_food" -> index == 2 ? 3 : 4;
                default -> 0;
            });
            assertFalse(insufficient.completedQuestIds().contains(QUESTS.getFirst()));
        }
        java.util.function.ToIntFunction<String> full = key -> switch (key) {
            case "#firsttorch:stone_or_better_pickaxes" -> 2;
            case "minecraft:torch" -> 16;
            case "#firsttorch:cooked_food" -> 4;
            default -> 0;
        };
        var locked = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, full);
        assertTrue(locked.completedTaskIds().containsAll(TASKS.subList(0, 3)));
        assertFalse(locked.completedQuestIds().contains(QUESTS.getFirst()));
        var ready = TaskEvaluator.evaluate(snapshot, unlocked, full);
        assertTrue(ready.completedQuestIds().contains(QUESTS.getFirst()));
        assertFalse(ready.completedQuestIds().contains(QUESTS.get(1)));
        assertTrue(TaskEvaluator.evaluate(snapshot, ready, key -> 0).completedQuestIds().contains(QUESTS.getFirst()));
        var prepared = ready;
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, prepared, QUESTS.getFirst(), TASKS.getFirst(), full));
        for (int i = 1; i < 6; i++) ready = TaskEvaluator.confirm(snapshot, ready, QUESTS.get(i), TASKS.get(i + 2), key -> 0);
        assertTrue(ready.completedQuestIds().containsAll(QUESTS));
    }

    private GuideSnapshot snapshot() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
