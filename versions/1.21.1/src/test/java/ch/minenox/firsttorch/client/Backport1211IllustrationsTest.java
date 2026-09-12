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
    @Test void imagesUseNativeRenderersOrUnchangedApprovedCapturesWithBilingualCaptions() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var images = GuideJson.read(input).chapters().stream().flatMap(c -> c.quests().stream())
                    .map(q -> q.image()).filter(Objects::nonNull).toList();
            assertEquals(111, images.size());
            int captureReferences = 0;
            var capturePaths = new java.util.HashSet<String>();
            for (var image : images) {
                boolean live = LiveRecipeCatalog.find(image.resource()) != null || LiveBrewingCatalog.find(image.resource()) != null
                        || LiveRecipePanels.find(image.resource()) != null || LiveSmeltingCatalog.find(image.resource()) != null
                        || LiveHandCraftingLayout.supports(image.resource()) || LiveBlockComparison.supports(image.resource())
                        || LiveEnchantingLayout.supports(image.resource()) || LiveCartographyLayout.supports(image.resource())
                        || LiveSceneCatalog.find(image.resource()) != null;
                String path = "assets/" + image.resource().replace(':', '/');
                if (live) {
                    assertNull(getClass().getResource("/" + path), image.resource());
                } else {
                    captureReferences++;
                    capturePaths.add(path);
                    try (var inputImage = getClass().getResourceAsStream("/" + path)) {
                        assertNotNull(inputImage, path);
                        byte[] bytes = inputImage.readAllBytes();
                        assertArrayEquals(java.nio.file.Files.readAllBytes(java.nio.file.Path.of(
                                System.getProperty("firsttorch.captureSourceDir"), path)), bytes, path);
                        var decoded = javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(bytes));
                        assertNotNull(decoded, path);
                        assertEquals(image.width(), decoded.getWidth(), path);
                        assertEquals(image.height(), decoded.getHeight(), path);
                    }
                }
                for (String locale : List.of("en_us", "de_de")) {
                    try (var lang = getClass().getResourceAsStream("/assets/firsttorch/lang/" + locale + ".json")) {
                        var keys = JsonParser.parseReader(new InputStreamReader(lang, StandardCharsets.UTF_8)).getAsJsonObject();
                        assertTrue(keys.has(image.altKey()), image.altKey());
                        assertFalse(keys.get(image.altKey()).getAsString().isBlank(), image.altKey());
                    }
                }
            }
            assertEquals(16, captureReferences);
            assertEquals(15, capturePaths.size());
        }
    }
}