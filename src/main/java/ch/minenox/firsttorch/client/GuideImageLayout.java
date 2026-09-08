package ch.minenox.firsttorch.client;

final class GuideImageLayout {
    private GuideImageLayout() {}

    static FirstTorchLayout.Rect bounds(int x, int y, int availableWidth, int sourceWidth, int sourceHeight) {
        int width = Math.max(1, availableWidth / 2);
        return new FirstTorchLayout.Rect(x + (availableWidth - width) / 2, y, width,
                height(width, sourceWidth, sourceHeight));
    }

    static int height(int availableWidth, int sourceWidth, int sourceHeight) {
        return Math.max(1, (int) Math.round((double) availableWidth * sourceHeight / sourceWidth));
    }
}
