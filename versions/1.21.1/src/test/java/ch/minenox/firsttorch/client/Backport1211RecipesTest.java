package ch.minenox.firsttorch.client;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class Backport1211RecipesTest {
    @Test void lodestoneUsesNetheriteAndArmourUsesIron() {
        var lodestone = LiveRecipeCatalog.find("firsttorch:textures/questpics/lodestone.png");
        assertEquals("minecraft:netherite_ingot", lodestone.ingredients().get(4));
        assertEquals(8, lodestone.ingredients().stream().filter("minecraft:chiseled_stone_bricks"::equals).count());
        var armour = LiveRecipePanels.find("firsttorch:textures/questpics/armour_recipes.png");
        assertEquals(List.of("minecraft:iron_helmet", "minecraft:iron_chestplate", "minecraft:iron_leggings", "minecraft:iron_boots"),
                armour.stream().map(LiveRecipeCatalog.Recipe::result).toList());
        armour.forEach(recipe -> assertTrue(recipe.ingredients().stream()
                .allMatch(item -> item.isEmpty() || item.equals("minecraft:iron_ingot"))));
    }
}
