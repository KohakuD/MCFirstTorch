package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveEnchantingLayoutTest {
    @Test void regionsMatchVanillaSlotsAndThreeOfferRows() {
        assertEquals(new FirstTorchLayout.Rect(14, 46, 18, 18), LiveEnchantingLayout.region(1));
        assertEquals(new FirstTorchLayout.Rect(34, 46, 18, 18), LiveEnchantingLayout.region(2));
        assertEquals(new FirstTorchLayout.Rect(60, 14, 108, 57), LiveEnchantingLayout.region(3));
        assertThrows(IllegalArgumentException.class, () -> LiveEnchantingLayout.region(4));
    }
    @Test void singleSurfaceStaysCompactAndRasterIsNotPackaged() {
        for (int width : new int[]{120, 251, 420, 600}) {
            var bounds = LiveEnchantingLayout.bounds(10, -30, width);
            assertEquals(GuideImageLayout.bounds(10, -30, width, 176, 84), bounds);
        }
        assertTrue(LiveEnchantingLayout.supports(LiveEnchantingLayout.RESOURCE));
        assertFalse(LiveEnchantingLayout.supports("other"));
        assertNull(getClass().getResourceAsStream("/assets/firsttorch/textures/questpics/enchanting_interface.png"));
    }
}
