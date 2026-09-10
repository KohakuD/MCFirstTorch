package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveBrewingCatalogTest {
    @Test void preservesLoadedIngredientsAndInputPotions() {
        assertEquals(new LiveBrewingCatalog.Step("minecraft:nether_wart", "water"), step("awkward_potion_brewing"));
        assertEquals(new LiveBrewingCatalog.Step("minecraft:blaze_powder", "awkward"), step("strength_potion_brewing"));
        assertEquals(new LiveBrewingCatalog.Step("minecraft:magma_cream", "awkward"), step("fire_resistance_brewing"));
        assertEquals(new LiveBrewingCatalog.Step("minecraft:redstone", "fire_resistance"), step("long_fire_resistance_brewing"));
    }

    @Test void excludesMigratedRastersAndLeavesScreenshotsAlone() {
        assertEquals(4, LiveBrewingCatalog.resources().size());
        for (String resource : LiveBrewingCatalog.resources()) {
            assertNull(getClass().getResourceAsStream("/assets/" + resource.replace(':', '/')), resource);
        }
        assertNull(step("blaze_spawner"));
    }

    private static LiveBrewingCatalog.Step step(String name) {
        return LiveBrewingCatalog.find("firsttorch:textures/questpics/" + name + ".png");
    }
}
