package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class TrophyCollectionLayoutTest {
    @Test void fullBrowserShowsFourCardsInTwoRows() {
        Rect panel = FirstTorchLayout.calculate(720, 405).questMap();
        var cards = TrophyCollectionLayout.cards(panel, 4);
        assertEquals(4, cards.size());
        assertEquals(2, cards.stream().map(Rect::x).distinct().count());
        assertEquals(2, cards.stream().map(Rect::y).distinct().count());
    }

    @Test void narrowAndShortWindowsPageWithoutOverlappingNavigation() {
        for (int width : new int[]{304, 400, 500, 600, 720}) {
            for (int height : new int[]{224, 300, 405}) {
                Rect panel = FirstTorchLayout.calculate(width, height).questMap();
                var cards = TrophyCollectionLayout.cards(panel, 4);
                assertTrue(cards.size() <= TrophyCollectionLayout.capacity(panel));
                for (var card : cards) {
                    assertTrue(card.x() >= panel.x() + 10);
                    assertTrue(card.right() <= panel.right() - 10);
                    assertTrue(card.y() >= panel.y() + 27);
                    assertTrue(card.bottom() <= panel.bottom() - 27);
                }
                for (int i = 0; i < cards.size(); i++) for (int j = i + 1; j < cards.size(); j++) {
                    var a = cards.get(i); var b = cards.get(j);
                    assertTrue(a.right() <= b.x() || b.right() <= a.x() || a.bottom() <= b.y() || b.bottom() <= a.y());
                }
            }
        }
        assertTrue(TrophyCollectionLayout.cards(new Rect(0, 0, 240, 300), 0).isEmpty());
    }
}
