package ch.minenox.firsttorch.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

/** Projects upward faces from the installed game's block-model resources. */
final class LiveBlockTopRenderer {
    private static final Logger LOG = Logger.getLogger("firsttorch.live_block_top");
    private static final Map<Identifier, Model> MODELS = new HashMap<>();
    private static final Set<Identifier> REPORTED = new HashSet<>();
    private static final Set<Identifier> FAILED = new HashSet<>();
    private static ResourceManager manager;
    private LiveBlockTopRenderer() {}

    static void clear() { MODELS.clear(); REPORTED.clear(); FAILED.clear(); manager = null; }

    static void draw(GuiGraphicsExtractor graphics, Identifier model, int quarterTurns, int x, int y, int size) {
        if (size <= 0) return;
        Model resolved = resolve(model);
        if (resolved == null || resolved.elements() == null) return;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + size / 2F, y + size / 2F);
        graphics.pose().rotate((float) (Math.floorMod(quarterTurns, 4) * Math.PI / 2));
        graphics.pose().translate(-size / 2F, -size / 2F);
        resolved.elements().asList().stream().sorted(Comparator.comparingDouble(LiveBlockTopRenderer::topHeight))
                .forEach(element -> drawTop(graphics, resolved.textures(), element, size));
        graphics.pose().popMatrix();
    }

    /** Package-visible pure decoding seam for model-face regression tests. */
    static Face decodeTopFace(Map<String, String> textures, JsonObject object) {
        if (!object.has("faces") || !object.getAsJsonObject("faces").has("up") || !object.has("from") || !object.has("to")) return null;
        JsonObject up = object.getAsJsonObject("faces").getAsJsonObject("up");
        String path = texture(textures, up.get("texture").getAsString());
        if (path == null) return null;
        JsonArray from = object.getAsJsonArray("from"), to = object.getAsJsonArray("to");
        float[] uv = up.has("uv") ? array(up.getAsJsonArray("uv"))
                : new float[] {from.get(0).getAsFloat(), from.get(2).getAsFloat(), to.get(0).getAsFloat(), to.get(2).getAsFloat()};
        return new Face(path, from.get(0).getAsFloat(), from.get(2).getAsFloat(), to.get(0).getAsFloat(), to.get(2).getAsFloat(),
                uv[0] / 16F, uv[1] / 16F, uv[2] / 16F, uv[3] / 16F, up.has("rotation") ? up.get("rotation").getAsInt() : 0);
    }

    private static void drawTop(GuiGraphicsExtractor graphics, Map<String, String> textures, JsonElement element, int size) {
        if (!element.isJsonObject()) return;
        Face face = decodeTopFace(textures, element.getAsJsonObject());
        if (face == null) return;
        int left = Math.round(face.x0() / 16F * size), top = Math.round(face.z0() / 16F * size);
        int right = Math.round(face.x1() / 16F * size), bottom = Math.round(face.z1() / 16F * size);
        if (right <= left || bottom <= top) return;
        graphics.pose().pushMatrix();
        graphics.pose().translate((left + right) / 2F, (top + bottom) / 2F);
        graphics.pose().rotate((float) (-Math.floorMod(face.rotation(), 360) * Math.PI / 180));
        graphics.pose().translate(-(left + right) / 2F, -(top + bottom) / 2F);
        graphics.blit(Identifier.parse(face.path()), left, top, right, bottom, face.u0(), face.u1(), face.v0(), face.v1());
        graphics.pose().popMatrix();
    }

    private static float topHeight(JsonElement element) {
        return element.isJsonObject() && element.getAsJsonObject().has("to") ? element.getAsJsonObject().getAsJsonArray("to").get(1).getAsFloat() : 0;
    }
    private static float[] array(JsonArray values) { return new float[] {values.get(0).getAsFloat(), values.get(1).getAsFloat(), values.get(2).getAsFloat(), values.get(3).getAsFloat()}; }

    private static String texture(Map<String, String> textures, String value) {
        Set<String> seen = new HashSet<>();
        while (value != null && value.startsWith("#")) { if (!seen.add(value)) return null; value = textures.get(value.substring(1)); }
        if (value == null) return null;
        Identifier id = Identifier.parse(value.contains(":") ? value : "minecraft:" + value);
        return id.getNamespace() + ":textures/" + id.getPath() + ".png";
    }

    private static Model resolve(Identifier model) {
        ResourceManager current = Minecraft.getInstance().getResourceManager();
        if (current != manager) { MODELS.clear(); REPORTED.clear(); FAILED.clear(); manager = current; }
        Identifier resource = modelResource(model);
        if (MODELS.containsKey(resource)) return MODELS.get(resource);
        if (FAILED.contains(resource)) return null;
        Model decoded = resolve(resource, new HashSet<>(), 0);
        if (decoded == null) { FAILED.add(resource); report(resource); } else MODELS.put(resource, decoded);
        return decoded;
    }

    private static Model resolve(Identifier resource, Set<Identifier> ancestry, int depth) {
        if (depth > 24 || !ancestry.add(resource)) return null;
        try {
            var optional = manager.getResource(resource);
            if (optional.isEmpty()) return null;
            try (var reader = new InputStreamReader(optional.get().open())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                Model parent = json.has("parent") ? resolve(modelResource(Identifier.parse(json.get("parent").getAsString())), ancestry, depth + 1) : null;
                if (json.has("parent") && parent == null) return null;
                Map<String, String> textures = new HashMap<>();
                if (parent != null) textures.putAll(parent.textures());
                if (json.has("textures")) for (var entry : json.getAsJsonObject("textures").entrySet()) textures.put(entry.getKey(), entry.getValue().getAsString());
                return new Model(Map.copyOf(textures), json.has("elements") ? json.getAsJsonArray("elements") : parent == null ? null : parent.elements());
            }
        } catch (java.io.IOException | IllegalArgumentException | IllegalStateException | JsonParseException ex) { return null; }
        finally { ancestry.remove(resource); }
    }

    private static void report(Identifier resource) { if (REPORTED.add(resource)) LOG.warning("Cannot load installed block model for First Torch plan: " + resource); }
    private static Identifier modelResource(Identifier model) {
        String path = model.getPath();
        if (path.startsWith("models/")) return model;
        if (!path.startsWith("block/")) path = "block/" + path;
        return Identifier.fromNamespaceAndPath(model.getNamespace(), "models/" + path + (path.endsWith(".json") ? "" : ".json"));
    }
    record Face(String path, float x0, float z0, float x1, float z1, float u0, float v0, float u1, float v1, int rotation) {}
    private record Model(Map<String, String> textures, JsonArray elements) {}
}
