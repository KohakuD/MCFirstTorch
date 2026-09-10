package ch.minenox.firsttorch.client;

import java.util.Map;
import java.util.Set;

/** Shows the loaded brewing stand before conversion, matching the lesson captions. */
final class LiveBrewingCatalog {
    record Step(String ingredient, String inputPotion) {}
    private static final Map<String, Step> STEPS = Map.of(
            path("awkward_potion_brewing"), new Step("minecraft:nether_wart", "water"),
            path("strength_potion_brewing"), new Step("minecraft:blaze_powder", "awkward"),
            path("fire_resistance_brewing"), new Step("minecraft:magma_cream", "awkward"),
            path("long_fire_resistance_brewing"), new Step("minecraft:redstone", "fire_resistance"));

    private LiveBrewingCatalog() {}
    static Step find(String resource) { return STEPS.get(resource); }
    static Set<String> resources() { return STEPS.keySet(); }
    private static String path(String name) { return "firsttorch:textures/questpics/" + name + ".png"; }
}
