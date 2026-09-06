package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class DevelopmentWindowLayoutTest {
    @Test
    void usesLeftTwoThirdsOfTheAvailableWorkArea() {
        DevelopmentWindowLayout.WindowBounds bounds =
                DevelopmentWindowLayout.leftTwoThirds(
                        new DevelopmentWindowLayout.WorkArea(10, 20, 3000, 1400));

        assertEquals(new DevelopmentWindowLayout.WindowBounds(10, 20, 2000, 1400), bounds);
    }

    @Test
    void keepsPositiveBoundsForSmallWorkAreas() {
        DevelopmentWindowLayout.WindowBounds bounds =
                DevelopmentWindowLayout.leftTwoThirds(
                        new DevelopmentWindowLayout.WorkArea(-5, -10, 1, 0));

        assertEquals(new DevelopmentWindowLayout.WindowBounds(-5, -10, 1, 1), bounds);
    }
}
