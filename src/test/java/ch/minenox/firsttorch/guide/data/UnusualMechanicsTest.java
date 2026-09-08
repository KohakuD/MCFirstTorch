package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.client.TrophyCatalog;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import ch.minenox.firsttorch.network.ProgressPayload;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class UnusualMechanicsTest {
    private static final String INTRO = "3C3122DF5C0EA192";

    @Test void threeCompactChaptersNeverGateEachOtherOrEarlierQuests() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        var additions = chapters.subList(62, 65);
        assertEquals(List.of("chapter.firsttorch.mechanics_blocks.title", "chapter.firsttorch.mechanics_names.title",
                "chapter.firsttorch.mechanics_bees.title"), additions.stream().map(c -> c.titleKey()).toList());
        for (var chapter : additions) {
            assertEquals(3, chapter.quests().size());
            assertEquals(3, chapter.quests().stream().map(q -> q.position()).distinct().count());
            for (var quest : chapter.quests()) {
                assertEquals(List.of(INTRO), quest.prerequisiteQuestIds());
                assertEquals(1, quest.tasks().size());
                assertEquals(TaskDefinition.Type.MANUAL, quest.tasks().getFirst().type());
                assertEquals(1, quest.tasks().getFirst().count());
                assertTrue(quest.rewards().isEmpty());
            }
        }
        var ids = additions.stream().flatMap(c -> c.quests().stream()).map(q -> q.id()).toList();
        assertTrue(chapters.subList(0, 62).stream().flatMap(c -> c.quests().stream())
                .flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(ids::contains));
    }

    @Test void inventoryDoesNotReplaceReadingAndAnyCardCanBeFirst() throws Exception {
        var guides = snapshot();
        var ready = new ProgressState(Set.of(), Set.of(INTRO));
        var cards = guides.guides().getFirst().chapters().subList(62, 65).stream()
                .flatMap(c -> c.quests().stream()).toList();
        var inventory = TaskEvaluator.evaluate(guides, ready, item -> 999);
        for (var card : cards) {
            assertFalse(inventory.completedQuestIds().contains(card.id()));
            var confirmed = TaskEvaluator.confirm(guides, ready, card.id(), card.tasks().getFirst().id(), item -> 0);
            assertTrue(confirmed.completedQuestIds().contains(card.id()));
            assertEquals(1, cards.stream().filter(q -> confirmed.completedQuestIds().contains(q.id())).count());
        }
    }

    @Test void threeBookTrophiesAreIndependentAndMatchChapterIcons() throws Exception {
        var guides = snapshot();
        var additions = guides.guides().getFirst().chapters().subList(62, 65);
        var completed = additions.getLast().quests().stream().map(q -> q.id()).collect(java.util.stream.Collectors.toSet());
        var progress = new ProgressPayload(new ProgressState(Set.of(), completed), Map.of(), true);
        var ids = additions.stream().map(c -> c.id()).toList();
        var trophies = TrophyCatalog.entries(guides, progress).stream().filter(t -> ids.contains(t.chapterId())).toList();
        assertEquals(List.of(false, false, true), trophies.stream().map(TrophyCatalog.Entry::earned).toList());
        assertEquals(List.of("minecraft:white_concrete", "minecraft:name_tag", "minecraft:honey_bottle"),
                trophies.stream().map(TrophyCatalog.Entry::iconItemId).toList());
    }

    @Test void bothLanguagesPreserveCaseSensitiveNamesAndSafetyDistinctions() throws Exception {
        for (var language : List.of("en_us", "de_de")) {
            try (var input = getClass().getResourceAsStream("/assets/firsttorch/lang/" + language + ".json")) {
                var text = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
                var upsideDown = text.get("quest.firsttorch.mechanics_names.dinnerbone.description").getAsString();
                assertTrue(upsideDown.contains("Dinnerbone") && upsideDown.contains("Grumm"));
                assertTrue(text.get("quest.firsttorch.mechanics_names.jeb.description").getAsString().contains("jeb_"));
                assertTrue(text.get("quest.firsttorch.mechanics_names.toast.description").getAsString().contains("Toast"));
                var honey = text.get("quest.firsttorch.mechanics_bees.honey_bottle.description").getAsString().toLowerCase(java.util.Locale.ROOT);
                assertTrue(honey.contains(language.equals("de_de") ? "teppich" : "carpet"));
            }
        }
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = UnusualMechanicsTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
