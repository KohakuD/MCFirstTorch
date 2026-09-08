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

    @Test void avoidsExistingControlsAndDoesNotForceAnOverlappingButton() {
        Rect result = PauseGuideEntryLayout.find(320, 240, List.of(new Rect(58, 60, 204, 140),
                new Rect(130, 40, 60, 9))).orElseThrow();
        assertEquals(new Rect(58, 204, 204, 20), result);
        assertTrue(PauseGuideEntryLayout.find(320, 240, List.of(new Rect(0, 0, 320, 240))).isEmpty());
        assertTrue(PauseGuideEntryLayout.find(120, 30, List.of()).isEmpty());
    }
}
