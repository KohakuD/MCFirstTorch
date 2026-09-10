package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveRecipeCatalogTest {
    @Test void migratedRecipesUseNineSlotsAndDoNotShipRasterCopies() {
        assertEquals(22, LiveRecipeCatalog.resources().size());
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

    @Test void mixedAndMultipleOutputRecipesPreserveTheirMeaning() {
        assertEquals(6, recipe("shield").ingredients().stream().filter("minecraft:oak_planks"::equals).count());
        assertEquals("minecraft:iron_ingot", recipe("shield").ingredients().get(1));
        assertEquals(3, recipe("glass_bottle_recipe").count());
        assertFalse(recipe("glass_bottle_recipe").shapeless());
        assertTrue(recipe("blaze_powder_recipe").shapeless());
        assertEquals(2, recipe("blaze_powder_recipe").count());
        assertEquals(3, recipe("bone_meal_recipe").count());
        assertEquals(java.util.List.of("minecraft:ender_pearl", "minecraft:blaze_powder"),
                recipe("ender_eye_recipe").ingredients().stream().filter(id -> !id.isEmpty()).toList());
        for (String resource : LiveRecipeCatalog.resources()) {
            var value = LiveRecipeCatalog.find(resource);
            if (value.shapeless()) assertTrue(value.ingredients().stream().filter(id -> !id.isEmpty()).count() <= 2);
        }
    }
}
