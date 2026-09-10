package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveCartographyLayoutTest {
    @Test void originalRecipeAndMapOperationsAreDistinct() {
        var panels = LiveCartographyLayout.PANELS;
        assertEquals(4, panels.size());
        var recipe = panels.getFirst();
        assertEquals("minecraft:cartography_table", recipe.result());
        assertFalse(recipe.shapeless());
        assertEquals(java.util.List.of("minecraft:paper", "minecraft:paper", "",
                "minecraft:oak_planks", "minecraft:oak_planks", "",
                "minecraft:oak_planks", "minecraft:oak_planks", ""), recipe.ingredients());
        var materials = java.util.List.of("minecraft:paper", "minecraft:map", "minecraft:glass_pane");
        for (int index = 1; index < 4; index++) {
            var operation = panels.get(index);
            assertTrue(operation.shapeless());
            assertEquals("minecraft:filled_map", operation.ingredients().getFirst());
            assertEquals(materials.get(index - 1), operation.ingredients().get(1));
            assertEquals("minecraft:filled_map", operation.result());
            assertEquals(index == 2 ? 2 : 1, operation.count());
        }
    }
    @Test void fullWidthTwoRowsAndRasterExclusion() {
        for (int width : new int[]{120, 251, 420, 600}) {
            assertEquals(GuideImageLayout.panelBounds(10, -20, width, 4), LiveCartographyLayout.bounds(10, -20, width));
        }
        assertTrue(LiveCartographyLayout.supports(LiveCartographyLayout.RESOURCE));
        assertFalse(LiveCartographyLayout.supports("other"));
        assertNull(getClass().getResourceAsStream("/assets/firsttorch/textures/questpics/cartography_table_guide.png"));
    }
}
