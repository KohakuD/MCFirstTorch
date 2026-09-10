package ch.minenox.firsttorch.client;

import java.util.List;
import java.util.Map;

/** Original layout data only; item models and textures are supplied by Minecraft at render time. */
final class LiveRecipeCatalog {
    record Ingredient(String item, int count) {}
    record Recipe(List<String> ingredients, String result, int count, boolean shapeless) {
        Recipe {
            ingredients = List.copyOf(ingredients);
            if (ingredients.size() != 9 || count < 1 || count > 64) throw new IllegalArgumentException("Invalid recipe layout");
        }

        List<Ingredient> groupedIngredients() {
            var counts = new java.util.LinkedHashMap<String, Integer>();
            ingredients.stream().filter(id -> !id.isEmpty()).forEach(id -> counts.merge(id, 1, Integer::sum));
            return counts.entrySet().stream().map(entry -> new Ingredient(entry.getKey(), entry.getValue())).toList();
        }
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
            entry("golden_helmet", "gold_ingot", "PPP", "P P", "   ", "golden_helmet"),
            grid("shield", "shield", 1, "oak_planks", "iron_ingot", "oak_planks",
                    "oak_planks", "oak_planks", "oak_planks", "", "oak_planks", ""),
            grid("bookshelf_recipe", "bookshelf", 1,
                    "oak_planks", "oak_planks", "oak_planks", "book", "book", "book", "oak_planks", "oak_planks", "oak_planks"),
            grid("bread", "bread", 1, "", "", "", "wheat", "wheat", "wheat", "", "", ""),
            grid("shulker_box_recipe", "shulker_box", 1, "", "shulker_shell", "", "", "chest", "", "", "shulker_shell", ""),
            grid("brewing_stand_recipe", "brewing_stand", 1, "", "blaze_rod", "", "cobblestone", "cobblestone", "cobblestone", "", "", ""),
            grid("glass_bottle_recipe", "glass_bottle", 3, "glass", "", "glass", "", "glass", "", "", "", ""),
            grid("enchanting_table_recipe", "enchanting_table", 1, "", "book", "", "diamond", "obsidian", "diamond", "obsidian", "obsidian", "obsidian"),
            loose("blaze_powder_recipe", "blaze_powder", 2, "blaze_rod"),
            loose("bone_meal_recipe", "bone_meal", 3, "bone"),
            loose("ender_eye_recipe", "ender_eye", 1, "ender_pearl", "blaze_powder"),
            loose("flint_and_steel", "flint_and_steel", 1, "iron_ingot", "flint"),
            loose("magma_cream_recipe", "magma_cream", 1, "blaze_powder", "slime_ball"),
            loose("firework_rocket_recipe", "firework_rocket", 3, "paper", "gunpowder"),
            entry("boat_recipe", "oak_planks", "   ", "P P", "PPP", "oak_boat"),
            entry("stone_hoe", "cobblestone", "PP ", " S ", " S ", "stone_hoe"),
            grid("bed", "white_bed", 1, "", "", "", "white_wool", "white_wool", "white_wool",
                    "oak_planks", "oak_planks", "oak_planks"),
            grid("stonecutter", "stonecutter", 1, "", "", "", "", "iron_ingot", "", "stone", "stone", "stone"),
            grid("lodestone", "lodestone", 1, "chiseled_stone_bricks", "chiseled_stone_bricks", "chiseled_stone_bricks",
                    "chiseled_stone_bricks", "iron_ingot", "chiseled_stone_bricks",
                    "chiseled_stone_bricks", "chiseled_stone_bricks", "chiseled_stone_bricks"));

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
                new Recipe(slots, "minecraft:" + result, 1, false));
    }

    private static Map.Entry<String, Recipe> grid(String name, String result, int count, String... slots) {
        return recipe(name, result, count, false, slots);
    }

    private static Map.Entry<String, Recipe> loose(String name, String result, int count, String... items) {
        String[] slots = new String[9];
        java.util.Arrays.fill(slots, "");
        System.arraycopy(items, 0, slots, 0, items.length);
        return recipe(name, result, count, true, slots);
    }

    private static Map.Entry<String, Recipe> recipe(String name, String result, int count, boolean shapeless, String... slots) {
        return Map.entry("firsttorch:textures/questpics/" + name + ".png", new Recipe(
                java.util.Arrays.stream(slots).map(id -> id.isEmpty() ? "" : "minecraft:" + id).toList(),
                "minecraft:" + result, count, shapeless));
    }
}
