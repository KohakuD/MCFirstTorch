package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class ChapterScrollTest {
    @Test void capacityAlwaysShowsAtLeastOneFullRow() {
        assertEquals(1, ChapterScroll.capacity(0, 54));
        assertEquals(1, ChapterScroll.capacity(72, 54));
        assertEquals(2, ChapterScroll.capacity(131, 54));
    }

    @Test void clampsEmptyAndOverflowingLists() {
        assertEquals(0, ChapterScroll.clamp(8, 0, 3));
        assertEquals(0, ChapterScroll.clamp(-2, 3, 3));
        assertEquals(0, ChapterScroll.clamp(4, 3, 3));
        assertEquals(0, ChapterScroll.clamp(-2, 10, 3));
        assertEquals(7, ChapterScroll.clamp(99, 10, 3));
    }

    @Test void revealMovesOnlyEnoughToMakeTargetVisible() {
        assertEquals(4, ChapterScroll.reveal(4, 5, 12, 3));
        assertEquals(3, ChapterScroll.reveal(4, 3, 12, 3));
        assertEquals(5, ChapterScroll.reveal(4, 7, 12, 3));
        assertEquals(9, ChapterScroll.reveal(4, 11, 12, 3));
        assertEquals(0, ChapterScroll.reveal(4, 0, 0, 3));
    }

    @Test void resizeClampsAndAdjustsThumb() {
        assertEquals(7, ChapterScroll.clamp(7, 10, 3));
        assertEquals(4, ChapterScroll.clamp(7, 10, 6));
        assertEquals(100, ChapterScroll.thumbHeight(100, 3, 3));
        assertEquals(30, ChapterScroll.thumbHeight(100, 10, 3));
        assertEquals(12, ChapterScroll.thumbHeight(20, 10, 3));
    }

    @Test void thumbEndpointsAndRoundTripsMapToRows() {
        assertEquals(20, ChapterScroll.thumbTop(20, 100, 10, 3, 0));
        assertEquals(90, ChapterScroll.thumbTop(20, 100, 10, 3, 7));
        assertEquals(0, ChapterScroll.fromThumb(20, 20, 100, 10, 3));
        assertEquals(7, ChapterScroll.fromThumb(90, 20, 100, 10, 3));
        for (int first = 0; first <= 7; first++) {
            int top = ChapterScroll.thumbTop(20, 100, 10, 3, first);
            assertEquals(first, ChapterScroll.fromThumb(top, 20, 100, 10, 3));
        }
    }
}
