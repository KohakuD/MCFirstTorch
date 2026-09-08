package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;

final class TaskGridLayoutTest {
    @Test void usesRequestedRowsAndEqualHeights() {
        for (int count = 1; count <= 8; count++) {
            var cards = TaskGridLayout.cards(10, 20, 440, count, (index, width) -> index % 2 == 0 ? 44 : 74);
            int columns = count == 3 ? 3 : count > 1 ? 2 : 1;
            assertEquals(count, cards.size());
            assertEquals((count + columns - 1) / columns, cards.stream().map(Rect::y).distinct().count());
            for (int i = 0; i < count; i++) {
                assertTrue(cards.get(i).right() <= 450);
                if (i % columns != 0) {
                    assertEquals(cards.get(i - 1).height(), cards.get(i).height());
                    assertEquals(cards.get(i - 1).right() + 6, cards.get(i).x());
                }
            }
        }
    }

    @Test void narrowPanelsKeepTextReadableAndEmptyListsStayEmpty() {
        var cards = TaskGridLayout.cards(0, 0, 180, 3, (index, width) -> 50);
        assertEquals(3, cards.stream().map(Rect::y).distinct().count());
        assertTrue(cards.stream().allMatch(card -> card.width() == 180));
        assertTrue(TaskGridLayout.cards(0, 0, 440, 0, (index, width) -> 50).isEmpty());
    }

    @Test void completionBadgesLeaveTheCentralIconUncovered() {
        for (int diameter : new int[]{12, 16, 20, 24, 28, 48}) {
            Rect node = new Rect(0, 0, diameter, diameter);
            Rect badge = FirstTorchLayout.nodeCompletionBadge(node);
            int iconSize = FirstTorchLayout.nodeIconSize(diameter);
            assertTrue(badge.width() <= 10);
            assertTrue(badge.x() >= (diameter + iconSize) / 2);
            assertTrue(badge.y() >= (diameter + iconSize) / 2);
        }
    }
}
