package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class GuideJsonResourceTest {
    private static final String GUIDE_RESOURCE = "/data/firsttorch/guides/getting_started.json";

    @Test
    void loadsAndValidatesBundledAlphaExample() throws IOException {
        GuideDefinition guide;
        try (InputStream input = resource(GUIDE_RESOURCE)) {
            guide = GuideJson.read(input);
        }

        assertEquals("0A13F17C00000001", guide.id());
        assertEquals(1, guide.chapters().size());
        assertEquals(2, guide.chapters().getFirst().quests().size());
        assertTrue(guide.chapters().getFirst().quests().getFirst().prerequisiteQuestIds().isEmpty());
    }

    @Test
    void providesEveryExampleTranslationInEnglishAndGerman() throws IOException {
        GuideDefinition guide;
        try (InputStream input = resource(GUIDE_RESOURCE)) {
            guide = GuideJson.read(input);
        }

        JsonObject english = language("/assets/firsttorch/lang/en_us.json");
        JsonObject german = language("/assets/firsttorch/lang/de_de.json");
        assertEquals(english.keySet(), german.keySet());
        for (String key : translationKeys(guide)) {
            assertTrue(english.has(key), "Missing English translation: " + key);
            assertTrue(german.has(key), "Missing German translation: " + key);
        }
    }

    private static Set<String> translationKeys(GuideDefinition guide) {
        Set<String> keys = new LinkedHashSet<>();
        keys.add(guide.titleKey());
        keys.add(guide.descriptionKey());
        for (ChapterDefinition chapter : guide.chapters()) {
            keys.add(chapter.titleKey());
            keys.add(chapter.descriptionKey());
            for (QuestDefinition quest : chapter.quests()) {
                keys.add(quest.titleKey());
                keys.add(quest.descriptionKey());
            }
        }
        return keys;
    }

    private static JsonObject language(String path) throws IOException {
        try (InputStream input = resource(path);
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static InputStream resource(String path) {
        InputStream input = GuideJsonResourceTest.class.getResourceAsStream(path);
        assertNotNull(input, "Missing test resource: " + path);
        return input;
    }
}
