package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FirstTorchViewportTest {
    @Test void retainsCompositionOnTallClient() {
        var viewport = FirstTorchViewport.fit(1140, 680);
        assertEquals(720, viewport.width());
        assertEquals(405, viewport.height());
        assertTrue(viewport.scale() > 1);
        assertTrue(viewport.x() > 0 && viewport.y() > 0);
    }

    @Test void mapsMouseToSameCoordinatesAsRendering() {
        var viewport = FirstTorchViewport.fit(1140, 680);
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
}
