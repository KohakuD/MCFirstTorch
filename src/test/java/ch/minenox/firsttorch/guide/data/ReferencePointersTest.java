package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.client.QuestReferenceLinks;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class ReferencePointersTest {
    private static final Set<String> CHAPTER_KEYS = Set.of("field_animals", "field_overworld", "field_nether",
            "field_end", "field_water", "field_special", "field_chambers_garden", "mechanics_blocks", "mechanics_names");

    @Test void referenceLinksTargetExistingQuestsAndTheirChapterTitlesInBothLanguages() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var chapters = GuideJson.read(input).chapters();
            var sources = chapters.stream().filter(c -> CHAPTER_KEYS.stream()
                    .anyMatch(key -> c.titleKey().equals("chapter.firsttorch." + key + ".title")))
                    .flatMap(c -> c.quests().stream()).map(q -> q.id()).collect(Collectors.toSet());
            assertEquals(41, sources.size());
            try (var catalog = getClass().getResourceAsStream("/assets/firsttorch/quest_links.json")) {
                assertNotNull(catalog);
                var entries = JsonParser.parseReader(new InputStreamReader(catalog, StandardCharsets.UTF_8)).getAsJsonObject();
                assertEquals(sources, entries.keySet(), "Every catalogue entry must belong to a reviewed reference card");
            }
            var quests = chapters.stream().flatMap(c -> c.quests().stream())
                    .collect(Collectors.toMap(q -> q.id(), q -> q));
            var chapterTitlesByQuestId = chapters.stream().flatMap(chapter -> chapter.quests().stream()
                    .map(quest -> java.util.Map.entry(quest.id(), chapter.titleKey())))
                    .collect(Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
            assertEquals(sources, sources.stream().filter(source -> !QuestReferenceLinks.forQuest(source).isEmpty())
                    .collect(Collectors.toSet()));
            for (var locale : new String[] {"en_us", "de_de"}) {
                try (var lang = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                    assertNotNull(lang);
                    var strings = JsonParser.parseReader(new InputStreamReader(lang, StandardCharsets.UTF_8)).getAsJsonObject();
                    for (var source : sources) {
                        assertTrue(quests.containsKey(source), source);
                        var prose = strings.get(quests.get(source).descriptionKey()).getAsString();
                        assertFalse(prose.contains("Read more:") || prose.contains("Mehr dazu:"),
                                "Do not duplicate the clickable reference in plain prose: " + source);
                        var links = QuestReferenceLinks.forQuest(source);
                        assertEquals(links.size(), links.stream().map(QuestReferenceLinks.Link::questId).distinct().count(),
                                "Do not repeat destination buttons: " + source);
                        for (var link : QuestReferenceLinks.forQuest(source)) {
                            assertNotEquals(source, link.questId(), "Reference must not link to itself");
                            assertTrue(quests.containsKey(link.questId()), link.questId());
                            assertEquals(chapterTitlesByQuestId.get(link.questId()), link.titleKey(),
                                    source + " -> " + link.questId());
                            assertTrue(strings.has(link.titleKey()), locale + ": " + link.titleKey());
                        }
                    }
                }
            }
        }
    }
}
