package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.data.GuideJson;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class Backport1211IllustrationsTest {
    @Test void everyRetainedImageHasANativeRendererAndBilingualCaptionWithoutOldRaster() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var images = GuideJson.read(input).chapters().stream().flatMap(c -> c.quests().stream())
                    .map(q -> q.image()).filter(Objects::nonNull).toList();
            assertEquals(95, images.size());
            for (var image : images) {
                assertTrue(LiveRecipeCatalog.find(image.resource()) != null || LiveBrewingCatalog.find(image.resource()) != null
                        || LiveRecipePanels.find(image.resource()) != null || LiveSmeltingCatalog.find(image.resource()) != null
                        || LiveHandCraftingLayout.supports(image.resource()) || LiveBlockComparison.supports(image.resource())
                        || LiveEnchantingLayout.supports(image.resource()) || LiveCartographyLayout.supports(image.resource())
                        || LiveSceneCatalog.find(image.resource()) != null, image.resource());
                assertNull(getClass().getResource("/assets/" + image.resource().replace(':', '/')), image.resource());
                for (String locale : List.of("en_us", "de_de")) {
                    try (var lang = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                        var keys = JsonParser.parseReader(new InputStreamReader(lang, StandardCharsets.UTF_8)).getAsJsonObject();
                        assertTrue(keys.has(image.altKey()), image.altKey());
                        assertFalse(keys.get(image.altKey()).getAsString().isBlank(), image.altKey());
                    }
                }
            }
        }
    }
}