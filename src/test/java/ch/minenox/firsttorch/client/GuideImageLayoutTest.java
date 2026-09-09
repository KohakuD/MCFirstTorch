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

    @Test void packagesUnchangedSourceArtworkWithCorrectDimensionsAndTranslations() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var images = GuideJson.read(input).chapters().stream().flatMap(c -> c.quests().stream())
                    .map(q -> q.image()).filter(java.util.Objects::nonNull).toList();
            assertEquals(93, images.size());
            for (var image : images) {
                String assetPath = "assets/" + image.resource().replace(':', '/');
                try (var png = getClass().getResourceAsStream("/" + assetPath)) {
                    assertNotNull(png);
                    byte[] bytes = png.readAllBytes();
                    String sourceRoot = image.resource().equals("firsttorch:textures/questpics/archaeology_comparison.png")
                            ? "src/main/resources/" : "overrides/resourcepacks/first_torch_guides/";
                    assertArrayEquals(Files.readAllBytes(Path.of(System.getProperty("firsttorch.projectDir"),
                            sourceRoot + assetPath)), bytes);
                    var decoded = ImageIO.read(new java.io.ByteArrayInputStream(bytes));
                    assertEquals(decoded.getWidth(), image.width());
                    assertEquals(decoded.getHeight(), image.height());
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
