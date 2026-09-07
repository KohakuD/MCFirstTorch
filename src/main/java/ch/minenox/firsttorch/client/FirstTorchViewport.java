package ch.minenox.firsttorch.client;

/** Keeps the authored composition intact instead of stretching it across the window. */
public record FirstTorchViewport(int width, int height, float scale, float x, float y) {
    public static FirstTorchViewport fit(int screenWidth, int screenHeight) {
        float scale = Math.max(1F, Math.min(screenWidth / 780F, screenHeight / 465F));
        int width = Math.min(720, Math.max(1, (int) (screenWidth / scale) - 16));
        int height = Math.min(405, Math.max(1, (int) (screenHeight / scale) - 16));
        return new FirstTorchViewport(width, height, scale,
                (screenWidth - width * scale) / 2F, (screenHeight - height * scale) / 2F);
    }

    public double localX(double screenX) { return (screenX - x) / scale; }
    public double localY(double screenY) { return (screenY - y) / scale; }
}
