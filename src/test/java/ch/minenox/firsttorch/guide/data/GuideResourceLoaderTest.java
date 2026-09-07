package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.server.ServerGuideRepository;
import ch.minenox.firsttorch.guide.validation.GuideValidationException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class GuideResourceLoaderTest {
    @Test
    void sortsResourcesDeterministically() {
        Map<String, GuideResourceLoader.ReaderSource> resources = new LinkedHashMap<>();
        resources.put("firsttorch:guides/z_last.json", () -> new StringReader(guideJson("0000000000000002", "1000000000000002", "2000000000000002")));
        resources.put("firsttorch:guides/a_first.json", () -> new StringReader(guideJson("0000000000000001", "1000000000000001", "2000000000000001")));

        GuideSnapshot snapshot = GuideResourceLoader.load(resources);

        assertEquals("0000000000000001", snapshot.guides().get(0).id());
        assertEquals("0000000000000002", snapshot.guides().get(1).id());
    }

    @Test
    void reportsTheFailingResourcePath() {
        GuideValidationException exception = assertThrows(
                GuideValidationException.class,
                () -> GuideResourceLoader.load(Map.of(
                        "firsttorch:guides/broken.json",
                        () -> new StringReader("{not json}"))));

        assertTrue(exception.getMessage().contains("firsttorch:guides/broken.json"));
    }

    @Test
    void rejectsIdsDuplicatedAcrossResources() {
        GuideValidationException exception = assertThrows(
                GuideValidationException.class,
                () -> GuideResourceLoader.load(Map.of(
                        "firsttorch:guides/one.json",
                        () -> new StringReader(guideJson("0000000000000001", "1000000000000001", "2000000000000001")),
                        "firsttorch:guides/two.json",
                        () -> new StringReader(guideJson("0000000000000002", "1000000000000002", "2000000000000001")))));

        assertTrue(exception.getMessage().contains("across loaded guides"));
    }

    @Test
    void failedPreparationCannotPartiallyReplaceRepositorySnapshot() {
        ServerGuideRepository repository = new ServerGuideRepository();
        GuideSnapshot original = GuideResourceLoader.load(Map.of(
                "firsttorch:guides/valid.json",
                () -> new StringReader(guideJson("0000000000000001", "1000000000000001", "2000000000000001"))));
        repository.replace(original);

        assertThrows(GuideValidationException.class, () -> GuideResourceLoader.load(Map.of(
                "firsttorch:guides/valid.json",
                () -> new StringReader(guideJson("0000000000000002", "1000000000000002", "2000000000000002")),
                "firsttorch:guides/broken.json",
                () -> new StringReader("{}"))));

        assertSame(original, repository.snapshot());
    }

    private static String guideJson(String guideId, String chapterId, String questId) {
        return """
                {
                  "schemaVersion": 1,
                  "id": "%s",
                  "titleKey": "guide.test.title",
                  "descriptionKey": "guide.test.description",
                  "chapters": [{
                    "id": "%s",
                    "order": 0,
                    "titleKey": "chapter.test.title",
                    "descriptionKey": "chapter.test.description",
                    "quests": [{
                      "id": "%s",
                      "order": 0,
                      "titleKey": "quest.test.title",
                      "descriptionKey": "quest.test.description",
                      "position": {"x": 0, "y": 0},
                      "prerequisiteQuestIds": []
                    }]
                  }]
                }
                """.formatted(guideId, chapterId, questId);
    }
}
