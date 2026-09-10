package ch.minenox.firsttorch.client;

import java.util.List;
import java.util.Map;

/** Original layout data only; item models and textures are supplied by Minecraft at render time. */
final class LiveRecipeCatalog {
    record Recipe(List<String> ingredients, String result) {
        Recipe { ingredients = List.copyOf(ingredients); }
    }
    private static final Map<String, Recipe> RECIPES = Map.ofEntries(
            entry("wooden_pickaxe", "oak_planks", "PPP", " S ", " S ", "wooden_pickaxe"),
            entry("wooden_shovel", "oak_planks", " P ", " S ", " S ", "wooden_shovel"),
            entry("stone_pickaxe", "cobblestone", "PPP", " S ", " S ", "stone_pickaxe"),
            entry("stone_axe", "cobblestone", "PP ", "PS ", " S ", "stone_axe"),
            entry("iron_pickaxe", "iron_ingot", "PPP", " S ", " S ", "iron_pickaxe"),
            entry("iron_sword", "iron_ingot", " P ", " P ", " S ", "iron_sword"),
            entry("furnace", "cobblestone", "PPP", "P P", "PPP", "furnace"),
            entry("chest", "oak_planks", "PPP", "P P", "PPP", "chest"),
            entry("bucket", "iron_ingot", "P P", " P ", "   ", "bucket"),
            entry("golden_helmet", "gold_ingot", "PPP", "P P", "   ", "golden_helmet"));

    private LiveRecipeCatalog() {}

    static Recipe find(String resource) { return RECIPES.get(resource); }
    static java.util.Set<String> resources() { return RECIPES.keySet(); }

    private static Map.Entry<String, Recipe> entry(String name, String material,
            String top, String middle, String bottom, String result) {
        var slots = (top + middle + bottom).chars().mapToObj(c -> switch (c) {
            case 'P' -> "minecraft:" + material;
            case 'S' -> "minecraft:stick";
            default -> "";
        }).toList();
        return Map.entry("firsttorch:textures/questpics/" + name + ".png",
                new Recipe(slots, "minecraft:" + result));
    }
}
