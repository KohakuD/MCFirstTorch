package ch.minenox.firsttorch.client;

/** A small crafting grid and a separate collection step, not a workbench recipe. */
final class LiveHandCraftingLayout {
    static final String RESOURCE = "firsttorch:textures/questpics/log_to_planks.png";
    static final String INPUT = "minecraft:oak_log";
    static final String OUTPUT = "minecraft:oak_planks";
    static final int OUTPUT_COUNT = 4;

    private LiveHandCraftingLayout() {}
    static boolean supports(String resource) { return RESOURCE.equals(resource); }
    static FirstTorchLayout.Rect bounds(int x, int y, int width) {
        return new FirstTorchLayout.Rect(x, y, Math.max(1, width),
                GuideImageLayout.height(GuideImageLayout.pairedCellWidth(width), 300, 240));
    }
    static FirstTorchLayout.Rect gridSlot(int index) {
        if (index < 0 || index >= 4) throw new IllegalArgumentException("Small crafting grid has four slots");
        return new FirstTorchLayout.Rect(60 + index % 2 * 44, 32 + index / 2 * 44, 42, 42);
    }
}
