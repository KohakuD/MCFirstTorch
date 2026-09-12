package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

final class LiveSceneCatalogTest {
    private static final String LIVE_PREFIX = "image.firsttorch.live.";

    @Test void catalogContainsAllFortyEightMigratedDiagrams() {
        assertEquals(48, LiveSceneCatalog.scenes().size());
    }

    @Test void sceneLabelsArePresentAndNonemptyInBothLanguages() throws IOException {
        var english = language("en_us");
        var german = language("de_de");
        assertEquals(liveKeys(english), liveKeys(german));
        for (var language : new JsonObject[] {english, german}) {
            for (var key : liveKeys(language)) {
                assertFalse(language.get(key).getAsString().isBlank(), key);
            }
        }
        for (var entry : LiveSceneCatalog.scenes().entrySet()) {
            for (var operation : entry.getValue().operations()) {
                if (operation instanceof LiveScene.Text text && text.translated()
                        && text.value().startsWith(LIVE_PREFIX)) {
                    assertTrue(english.has(text.value()), entry.getKey() + ": " + text.value());
                    assertTrue(german.has(text.value()), entry.getKey() + ": " + text.value());
                }
            }
        }
    }

    @Test void allOperationsHavePositiveDimensionsAndValidTextureCoordinates() {
        for (var entry : LiveSceneCatalog.scenes().entrySet()) {
            var scene = entry.getValue();
            dimensions(scene.width(), scene.height(), entry.getKey());
            assertFalse(scene.operations().isEmpty(), entry.getKey());
            for (var operation : scene.operations()) {
                String context = entry.getKey() + ": " + operation;
                switch (operation) {
                    case LiveScene.Item item -> dimensions(item.size(), item.size(), context);
                    case LiveScene.Entity entity -> dimensions(entity.width(), entity.height(), context);
                    case LiveScene.Texture texture -> texture(texture, context);
                    case LiveScene.TintedTexture tinted -> texture(tinted.texture(), context);
                    case LiveScene.Box box -> dimensions(box.width(), box.height(), context);
                    case LiveScene.Line line -> assertTrue(line.thickness() > 0, context);
                    case LiveScene.Text text -> dimensions(text.width(), text.height(), context);
                    case LiveScene.BlockTop block -> dimensions(block.size(), block.size(), context);
                    case LiveScene.Wire wire -> {
                        dimensions(wire.size(), wire.size(), context);
                        assertTrue(wire.power() >= 0 && wire.power() <= 15, context);
                    }
                    default -> fail("Unvalidated scene operation: " + context);
                }
            }
        }
    }

    @Test void exportRuntimeSceneManifestForInstalledAssetAudit() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        var manifest = new JsonObject();
        for (var id : new TreeSet<>(LiveSceneCatalog.scenes().keySet())) {
            var scene = LiveSceneCatalog.scenes().get(id);
            var description = new JsonObject();
            description.addProperty("width", scene.width());
            description.addProperty("height", scene.height());
            description.addProperty("wide", scene.wide());
            var operations = new JsonArray();
            for (var operation : scene.operations()) {
                var encoded = gson.toJsonTree(operation).getAsJsonObject();
                encoded.addProperty("type", operation.getClass().getSimpleName());
                operations.add(encoded);
            }
            description.add("operations", operations);
            manifest.add(id, description);
        }
        Path output = Path.of(System.getProperty("firsttorch.reportDir",
                Path.of(System.getProperty("firsttorch.projectDir"), "build", "reports").toString()), "live-scenes.json");
        Files.createDirectories(output.getParent());
        Files.writeString(output, gson.toJson(manifest), StandardCharsets.UTF_8);
        assertEquals(LiveSceneCatalog.scenes().size(), manifest.size());
    }

    private static void dimensions(int width, int height, String context) {
        assertTrue(width > 0 && height > 0, context);
    }

    private static void texture(LiveScene.Texture texture, String context) {
        dimensions(texture.width(), texture.height(), context);
        for (float coordinate : new float[] {texture.u0(), texture.v0(), texture.u1(), texture.v1()}) {
            assertTrue(Float.isFinite(coordinate) && coordinate >= 0 && coordinate <= 1, context);
        }
        assertNotEquals(texture.u0(), texture.u1(), context);
        assertNotEquals(texture.v0(), texture.v1(), context);
    }

    private static JsonObject language(String locale) throws IOException {
        try (var reader = Files.newBufferedReader(Path.of(System.getProperty("firsttorch.projectDir"), "src", "main", "resources", "assets",
                "firsttorch", "lang", locale + ".json"), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static Set<String> liveKeys(JsonObject language) {
        var keys = new TreeSet<String>();
        for (var key : language.keySet()) if (key.startsWith(LIVE_PREFIX)) keys.add(key);
        return keys;
    }
}
