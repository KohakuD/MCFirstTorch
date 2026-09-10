package ch.minenox.firsttorch.client;

/** Numbered teaching regions follow the original 26.1.2 enchanting menu. */
final class LiveEnchantingLayout {
    static final String RESOURCE = "firsttorch:textures/questpics/enchanting_interface.png";
    private LiveEnchantingLayout() {}
    static boolean supports(String resource) { return RESOURCE.equals(resource); }
    static FirstTorchLayout.Rect bounds(int x, int y, int width) {
        return GuideImageLayout.bounds(x, y, width, 176, 84);
    }
    static FirstTorchLayout.Rect region(int index) {
        return switch (index) {
            case 1 -> new FirstTorchLayout.Rect(14, 46, 18, 18);
            case 2 -> new FirstTorchLayout.Rect(34, 46, 18, 18);
            case 3 -> new FirstTorchLayout.Rect(60, 14, 108, 57);
            default -> throw new IllegalArgumentException("Unknown enchanting region");
        };
    }
}
