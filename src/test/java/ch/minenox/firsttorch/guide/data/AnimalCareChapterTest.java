package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.util.List;
import org.junit.jupiter.api.Test;

final class AnimalCareChapterTest {
    @Test void thresholdsAndPriorBreedingRespectQuestGates() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var snapshot = new GuideSnapshot(List.of(GuideJson.read(input)));
            var unlocked = new ch.minenox.firsttorch.guide.progress.ProgressState(
                    java.util.Set.of(), java.util.Set.of("26EC824FB71D3590"));
            var shortCount = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, unlocked,
                    key -> key.equals("#minecraft:wooden_fences") ? 14 : key.equals("#minecraft:fence_gates") ? 1 : 0);
            assertFalse(shortCount.completedQuestIds().contains("174BD2A608E35C91"));
            var enough = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, shortCount,
                    key -> key.equals("#minecraft:wooden_fences") ? 15 : 0);
            assertTrue(enough.completedQuestIds().contains("174BD2A608E35C91"));
            assertFalse(enough.completedQuestIds().contains("4A7E05D93B168FC4"));
            var breeding = snapshot.guides().getFirst().chapters().get(14).quests().get(4).tasks().getFirst();
            assertEquals("bred", breeding.criterion());
            var early = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, enough,
                    key -> key.equals(breeding.inventoryKey()) ? 1 : 0);
            assertTrue(early.completedTaskIds().contains(breeding.id()));
            assertFalse(early.completedQuestIds().contains("20D46B3F917CE52A"));
            var opened = new ch.minenox.firsttorch.guide.progress.ProgressState(
                    early.completedTaskIds(), java.util.Set.of("0EB2491D7F5AC308"));
            assertTrue(ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, opened, key -> 0)
                    .completedQuestIds().contains("20D46B3F917CE52A"));
        }
    }

    @Test void feedTagContainsExactlyTheSourceChoices() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/tags/item/animal_feed.json")) {
            var json = com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(
                    input, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            assertEquals(List.of("minecraft:wheat", "minecraft:wheat_seeds", "minecraft:carrot",
                    "minecraft:potato", "minecraft:beetroot"),
                    java.util.stream.StreamSupport.stream(json.getAsJsonArray("values").spliterator(), false)
                            .map(com.google.gson.JsonElement::getAsString).toList());
        }
    }

    @Test void preservesAnimalCareGraphTagsAndRewards() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var guide = GuideJson.read(input);
            var chapter = guide.chapters().get(14);
            assertEquals("1BD93F587CE4062A", chapter.id());
            assertEquals(6, chapter.quests().size());
            assertEquals(List.of("26EC824FB71D3590"), chapter.quests().getFirst().prerequisiteQuestIds());
            assertEquals("minecraft:wooden_fences", chapter.quests().getFirst().tasks().getFirst().itemId());
            assertEquals(15, chapter.quests().getFirst().tasks().getFirst().count());
            assertEquals("firsttorch:animal_feed", chapter.quests().get(2).tasks().getFirst().itemId());
            assertEquals(2, chapter.quests().get(2).tasks().getFirst().count());
            assertEquals(TaskDefinition.Type.ADVANCEMENT, chapter.quests().get(4).tasks().getFirst().type());
            assertEquals("minecraft:husbandry/breed_an_animal", chapter.quests().get(4).tasks().getFirst().advancementId());
            assertEquals(4, chapter.quests().get(1).rewards().getFirst().amount());
            assertEquals(1, chapter.quests().getLast().rewards().getFirst().amount());
            assertEquals(5, chapter.quests().getLast().rewards().get(1).amount());
        }
    }
}
