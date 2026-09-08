package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import java.util.List;
import java.util.Optional;

/** Adds a full-width row beside the central pause-menu rows, never in the music-overlay corner. */
final class PauseGuideEntryLayout {
    private PauseGuideEntryLayout() {}

    static Optional<Rect> find(int width, int height, List<Rect> occupied) {
        if (width < 220 || height < 64) return Optional.empty();
        int x = (width - 204) / 2;
        var menuRows = occupied.stream().filter(rect -> rect.width() >= 180
                && Math.abs(rect.centerX() - width / 2) <= 4).toList();
        if (menuRows.isEmpty()) return Optional.empty();
        int first = menuRows.stream().mapToInt(Rect::y).min().orElseThrow();
        int last = menuRows.stream().mapToInt(Rect::bottom).max().orElseThrow();
        for (int y : new int[]{first - 24, last + 4}) {
            Rect candidate = new Rect(x, y, 204, 20);
            if (y >= 34 && candidate.bottom() <= height - 8
                    && occupied.stream().noneMatch(other -> overlaps(candidate, other))) return Optional.of(candidate);
        }
        return Optional.empty();
    }

    private static boolean overlaps(Rect a, Rect b) {
        return a.x() < b.right() + 3 && a.right() + 3 > b.x()
                && a.y() < b.bottom() + 3 && a.bottom() + 3 > b.y();
    }
}
