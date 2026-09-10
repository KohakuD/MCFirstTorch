package ch.minenox.firsttorch.client;

import java.util.ArrayList;
import java.util.List;

/** Immutable teaching drawing: installed assets plus original explanatory geometry. */
record LiveScene(int width, int height, boolean wide, List<Op> operations) {
    LiveScene {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Invalid scene size");
        operations = List.copyOf(operations);
    }
    interface Op {}
    record Item(String id, int x, int y, int size) implements Op {}
    record Entity(String id, int x, int y, int width, int height) implements Op {}
    record Texture(String path, int x, int y, int width, int height,
                   float u0, float v0, float u1, float v1) implements Op {}
    record Box(int x, int y, int width, int height, int color, boolean outline) implements Op {}
    record Line(int x1, int y1, int x2, int y2, int thickness, int color, boolean arrow) implements Op {}
    record Text(String value, int x, int y, int width, int height, boolean translated) implements Op {}
    record BlockTop(String model, int quarterTurns, int x, int y, int size) implements Op {}
    record TintedTexture(Texture texture, int color) implements Op {}
    record Wire(int power, int x, int y, int size) implements Op {}
    FirstTorchLayout.Rect bounds(int x, int y, int availableWidth) {
        return wide ? new FirstTorchLayout.Rect(x, y, Math.max(1, availableWidth),
                GuideImageLayout.height(availableWidth, width, height))
                : GuideImageLayout.bounds(x, y, availableWidth, width, height);
    }
    static final class Builder {
        private final int width, height;
        private final boolean wide;
        private final List<Op> operations = new ArrayList<>();
        Builder(int width, int height, boolean wide) { this.width = width; this.height = height; this.wide = wide; }
        Builder item(String id, int x, int y, int size) { operations.add(new Item(id, x, y, size)); return this; }
        Builder entity(String id, int x, int y, int width, int height) { operations.add(new Entity(id, x, y, width, height)); return this; }
        Builder texture(String path, int x, int y, int width, int height) { return crop(path, x, y, width, height, 0, 0, 1, 1); }
        Builder crop(String path, int x, int y, int width, int height, float u0, float v0, float u1, float v1) {
            operations.add(new Texture(path, x, y, width, height, u0, v0, u1, v1)); return this;
        }
        Builder fill(int x, int y, int width, int height, int color) { operations.add(new Box(x, y, width, height, color, false)); return this; }
        Builder outline(int x, int y, int width, int height, int color) { operations.add(new Box(x, y, width, height, color, true)); return this; }
        Builder line(int x1, int y1, int x2, int y2, int thickness, int color) { operations.add(new Line(x1, y1, x2, y2, thickness, color, false)); return this; }
        Builder arrow(int x1, int y1, int x2, int y2, int thickness, int color) { operations.add(new Line(x1, y1, x2, y2, thickness, color, true)); return this; }
        Builder text(String text, int x, int y, int width, int height) { operations.add(new Text(text, x, y, width, height, false)); return this; }
        Builder key(String key, int x, int y, int width, int height) { operations.add(new Text(key, x, y, width, height, true)); return this; }
        Builder blockTop(String model, int quarterTurns, int x, int y, int size) { operations.add(new BlockTop(model, quarterTurns, x, y, size)); return this; }
        Builder tintedCrop(String path, int x, int y, int width, int height, float u0, float v0, float u1, float v1, int color) {
            operations.add(new TintedTexture(new Texture(path, x, y, width, height, u0, v0, u1, v1), color)); return this;
        }
        Builder wire(int power, int x, int y, int size) {
            if (power < 0 || power > 15) throw new IllegalArgumentException("Invalid wire power");
            operations.add(new Wire(power, x, y, size)); return this;
        }
        LiveScene build() { return new LiveScene(width, height, wide, operations); }
    }
}
