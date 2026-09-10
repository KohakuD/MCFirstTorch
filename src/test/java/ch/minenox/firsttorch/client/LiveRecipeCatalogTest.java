package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveRecipeCatalogTest {
    @Test void migratedRecipesUseNineSlotsAndDoNotShipRasterCopies() {
        assertEquals(10, LiveRecipeCatalog.resources().size());
        for (String resource : LiveRecipeCatalog.resources()) {
            var recipe = LiveRecipeCatalog.find(resource);
            assertEquals(9, recipe.ingredients().size());
            assertTrue(recipe.result().startsWith("minecraft:"));
            assertTrue(recipe.ingredients().stream().allMatch(id -> id.isEmpty() || id.startsWith("minecraft:")));
            assertNull(getClass().getResourceAsStream("/assets/" + resource.replace(':', '/')), resource);
            assertThrows(UnsupportedOperationException.class, () -> recipe.ingredients().set(0, "changed"));
        }
        assertNull(LiveRecipeCatalog.find("firsttorch:textures/questpics/nether_fortress.png"));
        assertNull(LiveRecipeCatalog.find("anothermod:textures/questpics/iron_pickaxe.png"));
    }

    @Test void representativeRecipesKeepTheirIngredientPlacement() {
        assertEquals(java.util.List.of("minecraft:iron_ingot", "minecraft:iron_ingot", "minecraft:iron_ingot",
                "", "minecraft:stick", "", "", "minecraft:stick", ""),
                recipe("iron_pickaxe").ingredients());
        assertEquals(java.util.List.of("minecraft:iron_ingot", "", "minecraft:iron_ingot",
                "", "minecraft:iron_ingot", "", "", "", ""), recipe("bucket").ingredients());
        assertEquals(8, recipe("furnace").ingredients().stream().filter("minecraft:cobblestone"::equals).count());
        assertEquals("", recipe("furnace").ingredients().get(4));
    }

    private static LiveRecipeCatalog.Recipe recipe(String name) {
        return LiveRecipeCatalog.find("firsttorch:textures/questpics/" + name + ".png");
    }
}
