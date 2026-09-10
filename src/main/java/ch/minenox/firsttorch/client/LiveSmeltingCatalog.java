package ch.minenox.firsttorch.client;

import java.util.Map;
import java.util.Set;

/** Representative smelting ingredients, followed by collecting the result. */
final class LiveSmeltingCatalog {
    record Recipe(String input, String fuel, String result) {}
    private static final Map<String, Recipe> RECIPES = Map.of(
            path("charcoal"), new Recipe("minecraft:oak_log", "minecraft:oak_planks", "minecraft:charcoal"),
            path("cooking_food"), new Recipe("minecraft:beef", "minecraft:coal", "minecraft:cooked_beef"));

    private LiveSmeltingCatalog() {}
    static Recipe find(String resource) { return RECIPES.get(resource); }
    static Set<String> resources() { return RECIPES.keySet(); }
    private static String path(String name) { return "firsttorch:textures/questpics/" + name + ".png"; }

    static FirstTorchLayout.Rect bounds(int x, int y, int width) {
        return new FirstTorchLayout.Rect(x, y, Math.max(1, width),
                GuideImageLayout.height(GuideImageLayout.pairedCellWidth(width), 176, 166));
    }
}
