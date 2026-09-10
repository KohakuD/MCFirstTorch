package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveBlockComparisonTest {
    @Test void numberedOrderMatchesCaptionAndExcludesRaster() {
        assertEquals(java.util.List.of("minecraft:sand", "minecraft:suspicious_sand", "minecraft:gravel",
                "minecraft:suspicious_gravel"), LiveBlockComparison.BLOCKS);
        assertTrue(LiveBlockComparison.supports("firsttorch:textures/questpics/archaeology_comparison.png"));
        assertFalse(LiveBlockComparison.supports("firsttorch:textures/questpics/nether_fortress.png"));
        assertNull(getClass().getResourceAsStream("/assets/firsttorch/textures/questpics/archaeology_comparison.png"));
    }

    @Test void fourPanelsFitFullWidthWithEqualModelSpace() {
        for (int index = 0; index < 4; index++) {
            var panel = LiveBlockComparison.panel(index);
            assertEquals(148, panel.width());
            assertEquals(98, panel.height());
            assertEquals(index % 2 * 152, panel.x());
            assertEquals(index / 2 * 102, panel.y());
            assertTrue(panel.right() <= 300);
            assertTrue(panel.bottom() <= 200);
        }
        for (int width : new int[]{120, 251, 420, 600}) {
            var bounds = LiveBlockComparison.bounds(10, -30, width);
            assertEquals(width, bounds.width());
            assertEquals(GuideImageLayout.height(width, 300, 200), bounds.height());
        }
        assertThrows(IllegalArgumentException.class, () -> LiveBlockComparison.panel(4));
    }
}
