package ch.minenox.firsttorch.client;

import java.util.List;

/** Crafting first, then three distinct cartography-table operations. */
final class LiveCartographyLayout {
    static final String RESOURCE = "firsttorch:textures/questpics/cartography_table_guide.png";
    static final List<String> LABELS = List.of("craft", "scale", "copy", "lock");
    static final List<LiveRecipeCatalog.Recipe> PANELS = List.of(
            new LiveRecipeCatalog.Recipe(List.of("minecraft:paper", "minecraft:paper", "",
                    "minecraft:oak_planks", "minecraft:oak_planks", "",
                    "minecraft:oak_planks", "minecraft:oak_planks", ""), "minecraft:cartography_table", 1, false),
            operation("paper", 1), operation("map", 2), operation("glass_pane", 1));
    private LiveCartographyLayout() {}
    private static LiveRecipeCatalog.Recipe operation(String material, int count) {
        return new LiveRecipeCatalog.Recipe(List.of("minecraft:filled_map", "minecraft:" + material,
                "", "", "", "", "", "", ""), "minecraft:filled_map", count, true);
    }
    static boolean supports(String resource) { return RESOURCE.equals(resource); }
    static FirstTorchLayout.Rect bounds(int x, int y, int width) {
        return GuideImageLayout.panelBounds(x, y, width, 4);
    }
}
