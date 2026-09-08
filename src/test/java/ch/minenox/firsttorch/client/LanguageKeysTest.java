package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.stream.JsonReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

final class LanguageKeysTest {
    @Test void translationsHaveUniqueKeysAndSeparateCelebrationFromArchiveCount() throws Exception {
        for (String language : new String[] {"en_us", "de_de"}) {
            var values = new HashMap<String, String>();
            try (var input = getClass().getResourceAsStream("/assets/firsttorch/lang/" + language + ".json");
                 var reader = new JsonReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    assertNull(values.put(key, reader.nextString()), language + ": duplicate " + key);
                }
                reader.endObject();
            }
            assertFalse(values.get("screen.firsttorch.chapter.celebration").contains("%s"));
            assertTrue(values.get("screen.firsttorch.chapter.completed").contains("%s"));
            assertFalse(values.get("screen.firsttorch.welcome.body").contains("%s"));
            assertTrue(values.get("screen.firsttorch.welcome.body").contains("ESC"));
        }
    }
}
