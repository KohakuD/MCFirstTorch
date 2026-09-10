package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class LiveHandCraftingLayoutTest {
    @Test void smallGridHasExactlyTwoRowsAndColumns() {
        for (int index = 0; index < 4; index++) {
            var slot = LiveHandCraftingLayout.gridSlot(index);
            assertEquals(60 + index % 2 * 44, slot.x());
            assertEquals(32 + index / 2 * 44, slot.y());
            assertEquals(42, slot.width());
            assertEquals(42, slot.height());
        }
        assertThrows(IllegalArgumentException.class, () -> LiveHandCraftingLayout.gridSlot(4));
        assertThrows(IllegalArgumentException.class, () -> LiveHandCraftingLayout.gridSlot(-1));
    }

    @Test void retainsFourPlanksAndFullWidthCollectionStepWithoutRaster() {
        assertEquals("minecraft:oak_log", LiveHandCraftingLayout.INPUT);
        assertEquals("minecraft:oak_planks", LiveHandCraftingLayout.OUTPUT);
        assertEquals(4, LiveHandCraftingLayout.OUTPUT_COUNT);
        assertTrue(LiveHandCraftingLayout.supports("firsttorch:textures/questpics/log_to_planks.png"));
        assertFalse(LiveHandCraftingLayout.supports("firsttorch:textures/questpics/place_crafting_table.png"));
        assertNull(getClass().getResourceAsStream("/assets/firsttorch/textures/questpics/log_to_planks.png"));
        for (int width : new int[]{120, 251, 420, 600}) {
            var bounds = LiveHandCraftingLayout.bounds(10, -30, width);
            assertEquals(width, bounds.width());
            assertEquals(10, bounds.x());
            assertEquals(-30, bounds.y());
            assertEquals(GuideImageLayout.height(GuideImageLayout.pairedCellWidth(width), 300, 240), bounds.height());
        }
    }
}
