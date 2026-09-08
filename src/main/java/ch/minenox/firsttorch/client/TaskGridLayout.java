package ch.minenox.firsttorch.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntBinaryOperator;
import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;

/** Equal-height rows measured using the actual wrapped labels, without shrinking text. */
final class TaskGridLayout {
    static final int GAP = 6;

    static List<Rect> cards(int x, int y, int width, int count, IntBinaryOperator height) {
        int preferred = count == 3 ? 3 : count > 1 ? 2 : 1;
        int columns = Math.min(preferred, Math.max(1, (width + GAP) / 106));
        int cardWidth = Math.max(1, (width - GAP * (columns - 1)) / columns);
        List<Rect> result = new ArrayList<>();
        for (int start = 0; start < count; start += columns) {
            int end = Math.min(count, start + columns);
            int rowHeight = 42;
            for (int i = start; i < end; i++) rowHeight = Math.max(rowHeight, height.applyAsInt(i, cardWidth));
            for (int i = start; i < end; i++) {
                result.add(new Rect(x + (i - start) * (cardWidth + GAP), y, cardWidth, rowHeight));
            }
            y += rowHeight + GAP;
        }
        return List.copyOf(result);
    }

    private TaskGridLayout() {}
}
