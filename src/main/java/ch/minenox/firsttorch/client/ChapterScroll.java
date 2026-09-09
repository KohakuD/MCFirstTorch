package ch.minenox.firsttorch.client;

/** Row-based scroll geometry for the chapter list. */
final class ChapterScroll {
    private ChapterScroll() {}

    static int capacity(int panelHeight, int cardHeight) {
        return Math.max(1, (panelHeight - 18 + 5) / (cardHeight + 5));
    }

    static int clamp(int first, int rows, int capacity) {
        return Math.max(0, Math.min(first, Math.max(0, rows - capacity)));
    }

    static int reveal(int first, int target, int rows, int capacity) {
        if (rows <= 0) {
            return 0;
        }
        int visibleRows = Math.max(1, capacity);
        int row = Math.max(0, Math.min(target, rows - 1));
        int next = first;
        if (row < first) {
            next = row;
        } else if (row >= first + visibleRows) {
            next = row - visibleRows + 1;
        }
        return clamp(next, rows, visibleRows);
    }

    static int thumbHeight(int trackHeight, int rows, int capacity) {
        int track = Math.max(0, trackHeight);
        int visibleRows = Math.max(1, capacity);
        if (rows <= visibleRows) {
            return track;
        }
        return Math.min(track, Math.max(12, track * visibleRows / rows));
    }

    static int thumbTop(int trackTop, int trackHeight, int rows, int capacity, int first) {
        int track = Math.max(0, trackHeight);
        int visibleRows = Math.max(1, capacity);
        int maxFirst = Math.max(0, rows - visibleRows);
        int travel = track - thumbHeight(track, rows, visibleRows);
        if (maxFirst == 0 || travel == 0) {
            return trackTop;
        }
        return trackTop + Math.round((float) clamp(first, rows, visibleRows) * travel / maxFirst);
    }

    static int fromThumb(int thumbTop, int trackTop, int trackHeight, int rows, int capacity) {
        int track = Math.max(0, trackHeight);
        int visibleRows = Math.max(1, capacity);
        int maxFirst = Math.max(0, rows - visibleRows);
        int travel = track - thumbHeight(track, rows, visibleRows);
        if (maxFirst == 0 || travel == 0) {
            return 0;
        }
        int offset = Math.max(0, Math.min(thumbTop - trackTop, travel));
        return clamp(Math.round((float) offset * maxFirst / travel), rows, visibleRows);
    }
}
