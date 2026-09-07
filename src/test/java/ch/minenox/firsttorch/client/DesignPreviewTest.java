package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class DesignPreviewTest {
    @Test void providesFiveChaptersAndBranchedEightQuestExampleWithoutChangingLiveCache() {
        var live = ClientGuideCache.snapshot();
        var guide = DesignPreview.snapshot().guides().getFirst();
        assertEquals(5, guide.chapters().size());
        var quests = guide.chapters().getFirst().quests();
        assertEquals(8, quests.size());
        assertEquals(3, quests.stream().filter(q -> DesignPreview.completed(q.id())).count());
        assertEquals(2, quests.get(3).prerequisiteQuestIds().size());
        assertSame(live, ClientGuideCache.snapshot());
        assertFalse(DesignPreview.isPreview("0A13F17C00000001"));
        assertFalse(DesignPreview.completed("2A13F17C00000001"));
    }

    @Test void everyPreviewTextHasBothTranslationsAndEveryObjectAnIcon() throws Exception {
        var keys = new HashSet<String>();
        var guide = DesignPreview.snapshot().guides().getFirst();
        keys.add(guide.titleKey());
        keys.add(guide.descriptionKey());
        for (var chapter : guide.chapters()) {
            keys.add(chapter.titleKey());
            keys.add(chapter.descriptionKey());
            assertNotEquals("minecraft:book", DesignPreview.itemId(chapter.id()));
            for (var quest : chapter.quests()) {
                keys.add(quest.titleKey());
                keys.add(quest.descriptionKey());
                assertNotEquals("minecraft:book", DesignPreview.itemId(quest.id()));
            }
        }
        for (var locale : new String[] {"en_us", "de_de"}) {
            try (var input = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                assertNotNull(input);
                var language = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
                for (var key : keys) assertTrue(language.has(key), locale + ": " + key);
            }
        }
    }
}
