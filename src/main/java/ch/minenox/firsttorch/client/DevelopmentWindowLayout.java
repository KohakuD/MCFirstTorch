package ch.minenox.firsttorch.client;

public final class DevelopmentWindowLayout {
    private DevelopmentWindowLayout() {
    }

    public static WindowBounds leftTwoThirds(WorkArea workArea) {
        int width = Math.max(1, workArea.width() * 2 / 3);
        return new WindowBounds(
                workArea.x(),
                workArea.y(),
                width,
                Math.max(1, workArea.height()));
    }

    public record WorkArea(int x, int y, int width, int height) {
    }

    public record WindowBounds(int x, int y, int width, int height) {
    }
}
