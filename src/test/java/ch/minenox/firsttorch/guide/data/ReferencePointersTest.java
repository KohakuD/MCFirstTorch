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
    private static final Set<String> SOURCES = Set.of(
            "19A0B0C0D0E00001", "19A0B0C0D0E00002", "19A0B0C0D0E00003", "19A0B0C0D0E00004",
            "1AA0B0C0D0E00001", "1AA0B0C0D0E00002", "1AA0B0C0D0E00003", "1AA0B0C0D0E00004",
            "1BA0B0C0D0E00001", "1BA0B0C0D0E00002", "1BA0B0C0D0E00003", "1BA0B0C0D0E00004");

    @Test void referenceLinksTargetExistingQuestsAndTheirChapterTitlesInBothLanguages() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var chapters = GuideJson.read(input).chapters();
            var quests = chapters.stream().flatMap(c -> c.quests().stream())
                    .collect(Collectors.toMap(q -> q.id(), q -> q));
            var chapterTitlesByQuestId = chapters.stream().flatMap(chapter -> chapter.quests().stream()
                    .map(quest -> java.util.Map.entry(quest.id(), chapter.titleKey())))
                    .collect(Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
            assertEquals(SOURCES, SOURCES.stream().filter(source -> !QuestReferenceLinks.forQuest(source).isEmpty())
                    .collect(Collectors.toSet()));
            for (var locale : new String[] {"en_us", "de_de"}) {
                try (var lang = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                    assertNotNull(lang);
                    var strings = JsonParser.parseReader(new InputStreamReader(lang, StandardCharsets.UTF_8)).getAsJsonObject();
                    for (var source : SOURCES) {
                        assertTrue(quests.containsKey(source), source);
                        var prose = strings.get(quests.get(source).descriptionKey()).getAsString();
                        assertFalse(prose.contains("Read more:") || prose.contains("Mehr dazu:"),
                                "Do not duplicate the clickable reference in plain prose: " + source);
                        for (var link : QuestReferenceLinks.forQuest(source)) {
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
