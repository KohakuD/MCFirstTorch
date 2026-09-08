package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import java.util.ArrayList;
import java.util.List;

/** Paged cards keep the collection usable at the browser's smallest supported size. */
final class TrophyCollectionLayout {
    private TrophyCollectionLayout() {}

    static int capacity(Rect panel) {
        int columns = panel.width() >= 180 ? 2 : 1;
        return columns * Math.max(1, Math.min(2, (panel.height() - 54) / 95));
    }

    static List<Rect> cards(Rect panel, int count) {
        int columns = panel.width() >= 180 ? 2 : 1;
        int visible = Math.min(count, capacity(panel));
        int rows = Math.max(1, (visible + columns - 1) / columns);
        int width = Math.max(1, (panel.width() - 20 - (columns - 1) * 8) / columns);
        int height = Math.max(1, Math.min(125, (panel.height() - 54 - (rows - 1) * 8) / rows));
        List<Rect> cards = new ArrayList<>();
        for (int i = 0; i < visible; i++) {
            cards.add(new Rect(panel.x() + 10 + i % columns * (width + 8),
                    panel.y() + 27 + i / columns * (height + 8), width, height));
        }
        return List.copyOf(cards);
    }
}
