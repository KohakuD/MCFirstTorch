package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class ReferencePointersTest {
    private static final Map<String, String> TARGETS = Map.ofEntries(
            Map.entry("field_end.enderman", "ender_eyes"),
            Map.entry("field_end.shulker", "shulker_city"),
            Map.entry("field_end.silverfish", "stronghold_interior"),
            Map.entry("field_end.endermite", "ender_eyes"),
            Map.entry("field_water.squid", "excursions"),
            Map.entry("field_water.glow_squid", "mining"),
            Map.entry("field_water.dolphin", "excursions"),
            Map.entry("field_water.turtle", "animal_care"),
            Map.entry("field_special.bee", "mechanics_bees"),
            Map.entry("field_special.fox", "animal_care"),
            Map.entry("field_special.frog", "nether_resources"),
            Map.entry("field_special.allay", "excursions"));

    @Test void pointersNameExistingChaptersExactlyInBothLanguages() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var chapters = GuideJson.read(input).chapters();
            var titles = chapters.stream().map(c -> c.titleKey()).collect(Collectors.toSet());
            var descriptions = chapters.stream().flatMap(c -> c.quests().stream())
                    .map(q -> q.descriptionKey()).collect(Collectors.toSet());
            for (var locale : new String[] {"en_us", "de_de"}) {
                try (var lang = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                    assertNotNull(lang);
                    var strings = JsonParser.parseReader(new InputStreamReader(lang, StandardCharsets.UTF_8)).getAsJsonObject();
                    for (var pointer : TARGETS.entrySet()) {
                        var source = "quest.firsttorch." + pointer.getKey() + ".description";
                        var target = "chapter.firsttorch." + pointer.getValue() + ".title";
                        assertTrue(descriptions.contains(source), source);
                        assertTrue(titles.contains(target), target);
                        assertTrue(strings.get(source).getAsString().contains(strings.get(target).getAsString()),
                                locale + ": " + source + " -> " + target);
                    }
                }
            }
        }
    }
}
