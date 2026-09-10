package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonParser;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class LiveBlockTopRendererTest {
    @Test void decodesDefaultUvAndFaceRotationWithoutClientResources() {
        var element = JsonParser.parseString("""
                {"from":[2,0,4],"to":[10,6,12],"faces":{"up":{"texture":"#top","rotation":90}}}
                """).getAsJsonObject();
        var face = LiveBlockTopRenderer.decodeTopFace(Map.of("top", "minecraft:block/repeater"), element);
        assertNotNull(face);
        assertEquals("minecraft:textures/block/repeater.png", face.path());
        assertEquals(2F / 16F, face.u0());
        assertEquals(4F / 16F, face.v0());
        assertEquals(10F / 16F, face.u1());
        assertEquals(12F / 16F, face.v1());
        assertEquals(90, face.rotation());
    }

    @Test void cyclicTextureReferencesAndMissingTopFacesAreRejected() {
        var element = JsonParser.parseString("""
                {"from":[0,0,0],"to":[16,1,16],"faces":{"up":{"texture":"#a"}}}
                """).getAsJsonObject();
        assertNull(LiveBlockTopRenderer.decodeTopFace(Map.of("a", "#b", "b", "#a"), element));
        assertNull(LiveBlockTopRenderer.decodeTopFace(Map.of(), JsonParser.parseString("{}").getAsJsonObject()));
    }
}
