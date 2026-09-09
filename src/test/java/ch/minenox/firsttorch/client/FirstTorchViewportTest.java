package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FirstTorchViewportTest {
    @Test void defaultsToTheStandardViewportMode() {
        var defaultViewport = FirstTorchViewport.fit(1140, 680);
        var explicitStandardViewport = FirstTorchViewport.fit(1140, 680, false);

        assertEquals(defaultViewport, explicitStandardViewport);
    }

    @Test void retainsCompositionOnTallClient() {
        var viewport = FirstTorchViewport.fit(1140, 680);
        assertEquals(720, viewport.width());
        assertEquals(405, viewport.height());
        assertTrue(viewport.scale() > 1);
        assertTrue(viewport.x() > 0 && viewport.y() > 0);
    }

    @Test void enlargedModeUsesMoreOfALargeWindowWithoutMakingTheGuiTooSmall() {
        var standard = FirstTorchViewport.fit(1140, 680);
        var enlarged = FirstTorchViewport.fit(1140, 680, true);

        assertTrue(enlarged.scale() > standard.scale());
        assertTrue(enlarged.width() < standard.width());
        assertTrue(enlarged.width() >= 500);
        assertTrue(enlarged.height() >= 300);
    }

    @Test void mapsMouseToSameCoordinatesAsRendering() {
        var viewport = FirstTorchViewport.fit(1140, 680);
        assertEquals(123, viewport.localX(viewport.x() + 123 * (double) viewport.scale()), 0.0001);
        assertEquals(234, viewport.localY(viewport.y() + 234 * (double) viewport.scale()), 0.0001);
    }

    @Test void mapsMouseToSameCoordinatesInEnlargedMode() {
        var viewport = FirstTorchViewport.fit(1140, 680, true);
        assertEquals(123, viewport.localX(viewport.x() + 123 * (double) viewport.scale()), 0.0001);
        assertEquals(234, viewport.localY(viewport.y() + 234 * (double) viewport.scale()), 0.0001);
    }

    @Test void keepsSmallGuiLegibleAndWithinWindow() {
        var viewport = FirstTorchViewport.fit(320, 240);
        assertEquals(1F, viewport.scale());
        assertTrue(viewport.x() >= 0 && viewport.y() >= 0);
        assertTrue(viewport.x() + viewport.width() <= 320);
        assertTrue(viewport.y() + viewport.height() <= 240);
    }

    @Test void enlargedModeLeavesSmallWindowsUnchanged() {
        assertEquals(FirstTorchViewport.fit(320, 240), FirstTorchViewport.fit(320, 240, true));
    }

    @Test void enlargedModeStaysInsideEachSupportedWindowSize() {
        int[][] sizes = {{320, 240}, {640, 360}, {800, 600}, {1140, 680}, {1920, 1080}};

        for (int[] size : sizes) {
            var standard = FirstTorchViewport.fit(size[0], size[1]);
            var enlarged = FirstTorchViewport.fit(size[0], size[1], true);

            assertTrue(enlarged.scale() >= standard.scale(), () -> "scale for " + size[0] + "x" + size[1]);
            assertTrue(enlarged.x() >= 0 && enlarged.y() >= 0, () -> "origin for " + size[0] + "x" + size[1]);
            assertTrue(enlarged.x() + enlarged.width() * enlarged.scale() <= size[0],
                    () -> "width for " + size[0] + "x" + size[1]);
            assertTrue(enlarged.y() + enlarged.height() * enlarged.scale() <= size[1],
                    () -> "height for " + size[0] + "x" + size[1]);
        }
    }
}
