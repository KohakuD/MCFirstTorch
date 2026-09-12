package ch.minenox.firsttorch.client;

import java.util.List;
import java.util.Map;

/** Related recipes retain their reading order in a two-column layout. */
final class LiveRecipePanels {
    private static final Map<String, List<LiveRecipeCatalog.Recipe>> PAIRS = Map.of(
            path("armour_recipes"), List.of(
                    armour("iron_helmet", "CCC", "C C", "   "),
                    armour("iron_chestplate", "C C", "CCC", "CCC"),
                    armour("iron_leggings", "CCC", "C C", "C C"),
                    armour("iron_boots", "   ", "C C", "C C")),
            path("fence_and_gate"), List.of(
                    recipe("oak_fence", 3, "oak_planks", "stick", "oak_planks", "oak_planks", "stick", "oak_planks", "", "", ""),
                    recipe("oak_fence_gate", 1, "stick", "oak_planks", "stick", "stick", "oak_planks", "stick", "", "", "")),
            path("map_recipe"), List.of(
                    recipe("paper", 3, "", "", "", "sugar_cane", "sugar_cane", "sugar_cane", "", "", ""),
                    recipe("map", 1, "paper", "paper", "paper", "paper", "compass", "paper", "paper", "paper", "paper")),
            path("paper_and_book"), List.of(
                    recipe("paper", 3, "", "", "", "sugar_cane", "sugar_cane", "sugar_cane", "", "", ""),
                    new LiveRecipeCatalog.Recipe(List.of("minecraft:paper", "minecraft:paper", "minecraft:paper",
                            "minecraft:leather", "", "", "", "", ""), "minecraft:book", 1, true)),
            path("bow_and_arrows"), List.of(
                    recipe("bow", 1, "", "stick", "string", "stick", "", "string", "", "stick", "string"),
                    recipe("arrow", 4, "", "flint", "", "", "stick", "", "", "feather", "")));

    private LiveRecipePanels() {}
    private static LiveRecipeCatalog.Recipe armour(String output, String... rows) {
        return recipe(output, 1, String.join("", rows).chars()
                .mapToObj(c -> c == 'C' ? "iron_ingot" : "").toArray(String[]::new));
    }
    static List<LiveRecipeCatalog.Recipe> find(String resource) { return PAIRS.get(resource); }
    static java.util.Set<String> resources() { return PAIRS.keySet(); }
    private static String path(String name) { return "firsttorch:textures/questpics/" + name + ".png"; }
    private static LiveRecipeCatalog.Recipe recipe(String output, int count, String... slots) {
        return new LiveRecipeCatalog.Recipe(java.util.Arrays.stream(slots)
                .map(id -> id.isEmpty() ? "" : "minecraft:" + id).toList(), "minecraft:" + output, count, false);
    }
}
