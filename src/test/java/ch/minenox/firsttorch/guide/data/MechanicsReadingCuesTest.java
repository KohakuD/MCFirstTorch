package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

final class MechanicsReadingCuesTest {
    @Test void allMechanicsReadingsDistinguishPracticalHelpFromDiscoveries() throws Exception {
        for (var locale : new String[] {"en_us", "de_de"}) {
            try (var input = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                assertNotNull(input);
                var strings = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
                int readings = 0;
                for (var entry : strings.entrySet()) {
                    var key = entry.getKey();
                    if (!key.startsWith("quest.firsttorch.mechanics_") || !key.endsWith(".description")) continue;
                    readings++;
                    boolean discovery = key.contains("mechanics_names.") || key.contains("mechanics_music.");
                    var label = locale.equals("en_us")
                            ? (discovery ? "Optional discovery" : "Practical reference")
                            : (discovery ? "Freiwillige Entdeckung" : "Praktisches Nachschlagewissen");
                    assertTrue(entry.getValue().getAsString().startsWith(label + "\n\n"), key);
                }
                assertEquals(27, readings, "Review reading cues when adding mechanics cards");
                for (var card : new String[] {"recipe", "charge", "safety"}) {
                    var text = strings.get("quest.firsttorch.mechanics_anchor." + card + ".description").getAsString();
                    assertTrue(text.contains(locale.equals("en_us") ? "Safe optional check:" : "Sicherer freiwilliger"));
                    assertTrue(text.contains(locale.equals("en_us") ? "DANGER:" : "GEFAHR:"));
                }
                assertTrue(strings.get("quest.firsttorch.mechanics_archaeology.brush.description").getAsString()
                        .contains(locale.equals("en_us") ? "Safe optional observation:" : "Sichere freiwillige Beobachtung:"));
            }
        }
    }
}
