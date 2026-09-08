package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.model.TaskDefinition;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class FoodChapterTest {
    private static final List<String> QUEST_IDS = List.of("1D4A83C6E2057B9F", "35B7E10C9A624DF8",
            "4A91D6F30C7E285B", "5B28E7A14D906CF3", "274B9E60A3D85FC1", "6C03B5E98A417DF2");
    private static final List<String> TASK_IDS = List.of("6F20B4D98C315EA7", "09E4A72D5C813BF6",
            "2E73C5A09D164BF8", "18C6F2A75D409BE3", "43D1A8F70C625BE9", "3D85F1C70A624BE9");

    @Test void preservesTheBoundedFoodPathAndOptionalEatingLesson() throws Exception {
        var chapter = course().chapters().get(4);
        assertEquals("2A64C8E10B7D395F", chapter.id());
        assertEquals(4, chapter.order());
        assertEquals(QUEST_IDS, chapter.quests().stream().map(q -> q.id()).toList());
        assertEquals(TASK_IDS, chapter.quests().stream().map(q -> q.tasks().getFirst().id()).toList());

        var eating = chapter.quests().getFirst();
        assertEquals(List.of("54A8023B6EC957F1"), eating.prerequisiteQuestIds());
        var eatTask = eating.tasks().getFirst();
        assertEquals(TaskDefinition.Type.ADVANCEMENT, eatTask.type());
        assertEquals("minecraft:husbandry/root", eatTask.advancementId());
        assertEquals("consumed_item", eatTask.criterion());

        var source = chapter.quests().get(1);
        assertEquals(List.of("54A8023B6EC957F1"), source.prerequisiteQuestIds());
        assertFalse(source.prerequisiteQuestIds().contains(eating.id()));
        for (int i = 2; i < chapter.quests().size(); i++)
            assertEquals(List.of(chapter.quests().get(i - 1).id()), chapter.quests().get(i).prerequisiteQuestIds());
        assertEquals("firsttorch:raw_food", source.tasks().getFirst().itemId());
        assertEquals("firsttorch:cooked_food", chapter.quests().get(2).tasks().getFirst().itemId());
        assertEquals("firsttorch:cooked_food", chapter.quests().getLast().tasks().getFirst().itemId());
        assertEquals(4, chapter.quests().getLast().tasks().getFirst().count());
        assertEquals(List.of("0C71E4A95B263DF8", "14B8D2F60A975CE3", "72E5A1C83D609BF4"),
                chapter.quests().stream().flatMap(q -> q.rewards().stream()).map(r -> r.id()).toList());
    }

    @Test void foodTagsAreExactNineItemWhitelists() throws Exception {
        assertEquals(Set.of("minecraft:beef", "minecraft:porkchop", "minecraft:chicken", "minecraft:mutton", "minecraft:rabbit",
                "minecraft:cod", "minecraft:salmon", "minecraft:potato", "minecraft:kelp"), tag("raw_food"));
        assertEquals(Set.of("minecraft:cooked_beef", "minecraft:cooked_porkchop", "minecraft:cooked_chicken", "minecraft:cooked_mutton", "minecraft:cooked_rabbit",
                "minecraft:cooked_cod", "minecraft:cooked_salmon", "minecraft:baked_potato", "minecraft:dried_kelp"), tag("cooked_food"));
    }

    @Test void threeCookedItemsAreInsufficientAndFourFinishWithoutEatingPractice() throws Exception {
        var guide = course();
        var snapshot = new ch.minenox.firsttorch.guide.GuideSnapshot(List.of(guide));
        var earlier = guide.chapters().stream().limit(4).flatMap(chapter -> chapter.quests().stream()).toList();
        var state = new ch.minenox.firsttorch.guide.progress.ProgressState(
                earlier.stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).collect(java.util.stream.Collectors.toSet()),
                earlier.stream().map(q -> q.id()).collect(java.util.stream.Collectors.toSet()));
        java.util.function.ToIntFunction<String> three = key -> key.equals("#firsttorch:raw_food") ? 1
                : key.equals("#firsttorch:cooked_food") ? 3 : 0;
        state = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, state, three);
        assertTrue(state.completedQuestIds().contains(QUEST_IDS.get(2)));
        for (int index : List.of(3, 4)) state = ch.minenox.firsttorch.guide.progress.TaskEvaluator.confirm(
                snapshot, state, QUEST_IDS.get(index), TASK_IDS.get(index), three);
        assertFalse(state.completedTaskIds().contains(TASK_IDS.getLast()));
        assertFalse(state.completedQuestIds().contains(QUEST_IDS.getLast()));
        state = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, state,
                key -> key.equals("#firsttorch:cooked_food") ? 4 : 0);
        assertTrue(state.completedQuestIds().contains(QUEST_IDS.getLast()));
        assertFalse(state.completedQuestIds().contains(QUEST_IDS.getFirst()));
        var rewards = guide.chapters().get(4).quests().stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("minecraft:apple", "minecraft:charcoal", "minecraft:cookie"),
                rewards.stream().map(r -> r.itemId()).toList());
        assertTrue(rewards.stream().allMatch(r -> r.amount() == 1));
    }

    private static ch.minenox.firsttorch.guide.model.GuideDefinition course() throws Exception {
        try (var input = FoodChapterTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            assertNotNull(input);
            return GuideJson.read(input);
        }
    }

    private static Set<String> tag(String name) throws Exception {
        try (var input = FoodChapterTest.class.getResourceAsStream("/data/firsttorch/tags/item/" + name + ".json")) {
            assertNotNull(input);
            var values = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("values");
            assertEquals(9, values.size());
            var result = new java.util.HashSet<String>();
            values.forEach(value -> result.add(value.getAsString()));
            assertEquals(9, result.size());
            return result;
        }
    }
}
