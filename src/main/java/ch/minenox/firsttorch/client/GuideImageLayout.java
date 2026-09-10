package ch.minenox.firsttorch.client;

final class GuideImageLayout {
    private GuideImageLayout() {}

    static int pairedCellWidth(int availableWidth) {
        return Math.max(1, (availableWidth - 4) / 2);
    }

    static FirstTorchLayout.Rect pairedBounds(int x, int y, int availableWidth) {
        return new FirstTorchLayout.Rect(x, y, Math.max(1, availableWidth),
                height(pairedCellWidth(availableWidth), 300, 169));
    }

    static FirstTorchLayout.Rect bounds(int x, int y, int availableWidth, int sourceWidth, int sourceHeight) {
        int width = Math.max(1, availableWidth / 2);
        return new FirstTorchLayout.Rect(x + (availableWidth - width) / 2, y, width,
                height(width, sourceWidth, sourceHeight));
    }

    static int height(int availableWidth, int sourceWidth, int sourceHeight) {
        return Math.max(1, (int) Math.round((double) availableWidth * sourceHeight / sourceWidth));
    }
}
