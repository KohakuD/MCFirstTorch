package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class PauseGuideEntryLayoutTest {
    @Test void alignsWithCentralMenuAboveItsFirstRow() {
        for (int width : new int[]{320, 640, 1280}) {
            Rect result = PauseGuideEntryLayout.find(width, 480, List.of(new Rect(width / 2 - 102, 100, 204, 160),
                    new Rect(0, 0, 180, 30))).orElseThrow();
            assertEquals(new Rect(width / 2 - 102, 76, 204, 20), result);
            assertTrue(result.right() < width);
        }
    }

    @Test void fullWidthTitleDoesNotBecomeTheFirstMenuRow() {
        for (int height : new int[]{240, 360, 480}) {
            int firstRow = (height - 190) / 4 + 50;
            var title = new Rect(0, 40, 640, 9);
            var menu = new Rect(218, firstRow, 204, 140);
            var result = PauseGuideEntryLayout.find(640, height, List.of(title, menu)).orElseThrow();
            // The smallest viewport has insufficient room between title and menu.
            assertEquals(height == 240 ? menu.bottom() + 4 : firstRow - 24, result.y());
            assertTrue(result.y() >= title.bottom() + 3);
        }
    }

    @Test void avoidsExistingControlsAndDoesNotForceAnOverlappingButton() {
        Rect result = PauseGuideEntryLayout.find(320, 240, List.of(new Rect(58, 60, 204, 140),
                new Rect(130, 40, 60, 9))).orElseThrow();
        assertEquals(new Rect(58, 204, 204, 20), result);
        assertTrue(PauseGuideEntryLayout.find(320, 240, List.of(new Rect(0, 0, 320, 240))).isEmpty());
        assertTrue(PauseGuideEntryLayout.find(120, 30, List.of()).isEmpty());
    }
}
