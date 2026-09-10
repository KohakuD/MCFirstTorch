package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.data.GuideJson;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

final class GuideImageLayoutTest {
    @Test void redstoneCaptionsNoLongerDescribeLetterComponents() throws Exception {
        for (String locale : List.of("en_us", "de_de")) {
            try (var input = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                var keys = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
                for (String name : List.of("redstone_inputs", "redstone_short_line", "redstone_dust_limit",
                        "repeater_range", "repeater_delay", "iron_door_plan", "repeater_direction", "comparator_read")) {
                    String caption = keys.get("image.firsttorch." + name + ".description").getAsString();
                    for (String obsolete : List.of("0/L", "0/C", "/D", "D x15", "P =", "T =", "L =", "C = single", "placeholder", "Platzhalter")) {
                        assertFalse(caption.contains(obsolete), name + ": " + obsolete);
                    }
                }
            }
        }
    }

    @Test void imagesUseHalfThePaneWidthAndRemainCentred() {
        for (int available : new int[]{1, 120, 251, 420, 600}) {
            var bounds = GuideImageLayout.bounds(10, -30, available, 1672, 941);
            assertEquals(Math.max(1, available / 2), bounds.width());
            assertTrue(Math.abs((bounds.x() - 10) - (10 + available - bounds.right())) <= 1);
            assertEquals(-30, bounds.y());
            assertTrue(Math.abs(bounds.height() - bounds.width() * 941.0 / 1672) <= 1);
        }
    }

    @Test void preservesAspectRatioAcrossReadingWidths() {
        for (int width : new int[]{1, 120, 250, 420, 600}) {
            int height = GuideImageLayout.height(width, 1672, 941);
            assertTrue(Math.abs(height - width * 941.0 / 1672) <= 1);
        }
    }

    @Test void pairedRecipesUseFullWidthWithoutVerticalPadding() {
        for (int width : new int[]{120, 251, 420, 600}) {
            var bounds = GuideImageLayout.pairedBounds(10, -30, width);
            int cellWidth = GuideImageLayout.pairedCellWidth(width);
            assertEquals(10, bounds.x());
            assertEquals(-30, bounds.y());
            assertEquals(width, bounds.width());
            assertTrue(width - 2 * cellWidth >= 4);
            assertTrue(width - 2 * cellWidth <= 5);
            assertEquals(GuideImageLayout.height(cellWidth, 300, 169), bounds.height());
            assertTrue(Math.abs(bounds.height() - cellWidth * 169.0 / 300) <= 1);
        }
    }

    @Test void fourRecipesHaveTwoFullWidthRows() {
        for (int width : new int[]{120, 251, 420, 600}) {
            var pair = GuideImageLayout.pairedBounds(10, -30, width);
            var four = GuideImageLayout.panelBounds(10, -30, width, 4);
            assertEquals(width, four.width());
            assertEquals(pair.height() * 2 + 4, four.height());
            assertEquals(pair.x(), four.x());
            assertEquals(pair.y(), four.y());
        }
        assertThrows(IllegalArgumentException.class, () -> GuideImageLayout.panelBounds(0, 0, 300, 5));
    }

    @Test void packagesUnchangedSourceArtworkWithCorrectDimensionsAndTranslations() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var images = GuideJson.read(input).chapters().stream().flatMap(c -> c.quests().stream())
                    .map(q -> q.image()).filter(java.util.Objects::nonNull).toList();
            assertEquals(111, images.size());
            for (var image : images) {
                String assetPath = "assets/" + image.resource().replace(':', '/');
                try (var png = getClass().getResourceAsStream("/" + assetPath)) {
                    if (LiveRecipeCatalog.find(image.resource()) != null || LiveBrewingCatalog.find(image.resource()) != null
                            || LiveRecipePanels.find(image.resource()) != null || LiveSmeltingCatalog.find(image.resource()) != null
                            || LiveHandCraftingLayout.supports(image.resource()) || LiveBlockComparison.supports(image.resource())
                            || LiveEnchantingLayout.supports(image.resource()) || LiveCartographyLayout.supports(image.resource())) {
                        assertNull(png, "Live recipe must not package its old raster: " + image.resource());
                    } else {
                    assertNotNull(png);
                    byte[] bytes = png.readAllBytes();
                    String sourceRoot = "src/main/resources/";
                    assertArrayEquals(Files.readAllBytes(Path.of(System.getProperty("firsttorch.projectDir"),
                            sourceRoot + assetPath)), bytes);
                    var decoded = ImageIO.read(new java.io.ByteArrayInputStream(bytes));
                    assertEquals(decoded.getWidth(), image.width());
                    assertEquals(decoded.getHeight(), image.height());
                    }
                }
                for (String locale : List.of("en_us", "de_de")) {
                    try (var lang = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                        var keys = JsonParser.parseReader(new InputStreamReader(lang, StandardCharsets.UTF_8)).getAsJsonObject();
                        assertTrue(keys.has(image.altKey()));
                        assertFalse(keys.get(image.altKey()).getAsString().isBlank());
                    }
                }
            }
        }
    }
}
