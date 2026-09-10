package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveSmeltingCatalogTest {
    @Test void recipesKeepInputFuelAndResultWithoutRasterCopies() {
        assertEquals(2, LiveSmeltingCatalog.resources().size());
        assertEquals(new LiveSmeltingCatalog.Recipe("minecraft:oak_log", "minecraft:oak_planks", "minecraft:charcoal"),
                LiveSmeltingCatalog.find("firsttorch:textures/questpics/charcoal.png"));
        assertEquals(new LiveSmeltingCatalog.Recipe("minecraft:beef", "minecraft:coal", "minecraft:cooked_beef"),
                LiveSmeltingCatalog.find("firsttorch:textures/questpics/cooking_food.png"));
        for (String resource : LiveSmeltingCatalog.resources()) {
            assertNull(getClass().getResourceAsStream("/assets/" + resource.replace(':', '/')));
        }
        assertNull(LiveSmeltingCatalog.find("firsttorch:textures/questpics/nether_fortress.png"));
    }

    @Test void bothFurnacesUseFullWidthAndOriginalGuiAspect() {
        for (int width : new int[]{120, 251, 420, 600}) {
            var bounds = LiveSmeltingCatalog.bounds(10, -30, width);
            assertEquals(width, bounds.width());
            assertEquals(10, bounds.x());
            assertEquals(-30, bounds.y());
            assertEquals(GuideImageLayout.height(GuideImageLayout.pairedCellWidth(width), 176, 166), bounds.height());
        }
    }
}
