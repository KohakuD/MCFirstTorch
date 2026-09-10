package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveRecipePairsTest {
    @Test void pairsKeepBothRecipesAndExcludeTheirRaster() {
        assertEquals(3, LiveRecipePairs.resources().size());
        for (String resource : LiveRecipePairs.resources()) {
            var pair = LiveRecipePairs.find(resource);
            assertEquals(2, pair.size());
            assertNull(getClass().getResourceAsStream("/assets/" + resource.replace(':', '/')), resource);
            pair.forEach(recipe -> assertEquals(9, recipe.ingredients().size()));
        }
        var fence = pair("fence_and_gate");
        assertEquals("minecraft:oak_fence", fence.get(0).result());
        assertEquals(3, fence.get(0).count());
        assertEquals("minecraft:oak_fence_gate", fence.get(1).result());
        assertEquals("minecraft:stick", fence.get(0).ingredients().get(1));
        assertEquals("minecraft:oak_planks", fence.get(1).ingredients().get(1));
        var map = pair("map_recipe");
        assertEquals("minecraft:paper", map.get(0).result());
        assertEquals(3, map.get(0).count());
        assertEquals("minecraft:compass", map.get(1).ingredients().get(4));
        assertEquals("minecraft:arrow", pair("bow_and_arrows").get(1).result());
        assertEquals(4, pair("bow_and_arrows").get(1).count());
        assertNull(LiveRecipePairs.find("firsttorch:textures/questpics/nether_fortress.png"));
    }

    private static java.util.List<LiveRecipeCatalog.Recipe> pair(String name) {
        return LiveRecipePairs.find("firsttorch:textures/questpics/" + name + ".png");
    }
}
