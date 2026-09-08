package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.model.QuestDefinition.PrerequisiteMode;
import ch.minenox.firsttorch.guide.validation.GuideValidationException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

final class PrerequisiteModeJsonTest {
    @Test
    void defaultsMissingModeToAll() {
        var quest = GuideJson.read(new StringReader(guideJson(""))).chapters().getFirst().quests().getLast();

        assertEquals(PrerequisiteMode.ALL, quest.prerequisiteMode());
        assertTrue(quest.prerequisitesMet(java.util.Set.of("2000000000000001")));
    }

    @Test
    void readsUppercaseAnyAndUsesAlternativeCompletion() {
        var quest = GuideJson.read(new StringReader(guideJson("\"prerequisiteMode\": \"ANY\",")))
                .chapters().getFirst().quests().getLast();

        assertEquals(PrerequisiteMode.ANY, quest.prerequisiteMode());
        assertTrue(quest.prerequisitesMet(java.util.Set.of("2000000000000001")));
        org.junit.jupiter.api.Assertions.assertFalse(quest.prerequisitesMet(java.util.Set.of("2000000000000002")));
    }

    @Test
    void rejectsUnknownOrNonUppercaseJsonMode() {
        GuideValidationException exception = assertThrows(GuideValidationException.class,
                () -> GuideJson.read(new StringReader(guideJson("\"prerequisiteMode\": \"any\","))));

        assertTrue(exception.getMessage().contains("Unknown quest prerequisiteMode"));
    }

    private static String guideJson(String mode) {
        return """
                {
                  "schemaVersion": 1,
                  "id": "0000000000000001",
                  "titleKey": "guide.test.title",
                  "descriptionKey": "guide.test.description",
                  "chapters": [{
                    "id": "1000000000000001",
                    "order": 0,
                    "titleKey": "chapter.test.title",
                    "descriptionKey": "chapter.test.description",
                    "quests": [
                      { "id": "2000000000000001", "order": 0, "titleKey": "quest.test.title", "descriptionKey": "quest.test.description", "position": { "x": 0, "y": 0 } },
                      { "id": "2000000000000002", "order": 1, "titleKey": "quest.test.title", "descriptionKey": "quest.test.description", "position": { "x": 1, "y": 0 }, %s "prerequisiteQuestIds": ["2000000000000001"] }
                    ]
                  }]
                }
                """.formatted(mode);
    }
}
