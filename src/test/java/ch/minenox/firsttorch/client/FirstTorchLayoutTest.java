package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import ch.minenox.firsttorch.client.FirstTorchLayout.ScreenLayout;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class FirstTorchLayoutTest {
    @Test
    void laysOutTypicalGuiAsThreeNonOverlappingColumns() {
        ScreenLayout layout = FirstTorchLayout.calculate(854, 480);

        assertTrue(layout.chapters().right() < layout.questMap().x());
        assertTrue(layout.questMap().right() < layout.details().x());
        assertTrue(layout.questMap().width() > layout.chapters().width());
        assertTrue(layout.footer().y() >= layout.questMap().bottom());
        assertTrue(layout.details().right() <= 854);
        assertTrue(layout.footer().bottom() <= 480);
    }

    @Test
    void keepsAllColumnsAndQuestNodesInsideSmallGui() {
        ScreenLayout layout = FirstTorchLayout.calculate(320, 240);
        List<QuestDefinition> quests = List.of(
                quest("2000000000000001", 0, -4, -2),
                quest("2000000000000002", 1, 3, 5));

        Map<String, Rect> nodes = FirstTorchLayout.questNodes(layout.questMap(), quests);

        assertTrue(layout.chapters().width() > 0);
        assertTrue(layout.questMap().width() >= 84);
        assertTrue(layout.details().width() > 0);
        assertEquals(2, nodes.size());
        assertTrue(nodes.values().stream().allMatch(layout.questMap()::contains));
        assertTrue(layout.details().right() <= 320);
        assertTrue(layout.footer().bottom() <= 240);
    }

    @Test
    void readingModeGivesDetailsAboutTwoThirdsOfTheAvailableWidth() {
        ScreenLayout overview = FirstTorchLayout.calculate(720, 405);
        ScreenLayout reading = FirstTorchLayout.calculate(720, 405, true);
        Rect screen = new Rect(0, 0, 720, 405);
        int columnWidth = reading.chapters().width() + reading.questMap().width() + reading.details().width();

        assertEquals(52, reading.chapters().width());
        assertEquals(0.65, (double) reading.details().width() / columnWidth, 0.01);
        assertTrue(reading.details().width() > overview.details().width());
        assertTrue(reading.chapters().right() < reading.questMap().x());
        assertTrue(reading.questMap().right() < reading.details().x());
        assertTrue(List.of(reading.topBar(), reading.chapters(), reading.questMap(), reading.details(), reading.footer())
                .stream().allMatch(screen::contains));
    }

    @Test
    void preservesOverviewAndSmallWindowFallback() {
        ScreenLayout overview = FirstTorchLayout.calculate(720, 405);
        assertEquals(173, overview.chapters().width());
        assertEquals(251, overview.questMap().width());
        assertEquals(270, overview.details().width());
        assertEquals(overview, FirstTorchLayout.calculate(720, 405, false));
        assertEquals(FirstTorchLayout.calculate(320, 240), FirstTorchLayout.calculate(320, 240, true));
    }

    @Test
    void keepsEveryPreviewChapterInsideTheReadingMapBelowItsOverviewButton() {
        Rect map = FirstTorchLayout.calculate(720, 405, true).questMap();
        Rect nodeArea = new Rect(map.x(), map.y() + 23, map.width(), map.height() - 23);

        for (var guide : DesignPreview.snapshot().guides()) {
            for (var chapter : guide.chapters()) {
                Map<String, Rect> nodes = FirstTorchLayout.questNodes(nodeArea, chapter.quests());
                assertEquals(chapter.quests().size(), nodes.size());
                assertTrue(nodes.values().stream().allMatch(nodeArea::contains), chapter.id());
                assertTrue(nodes.values().stream().allMatch(node -> node.y() >= map.y() + 24), chapter.id());
            }
        }
    }

    @Test
    void mapsQuestCoordinatesDeterministically() {
        Rect panel = new Rect(10, 20, 300, 180);
        QuestDefinition right = quest("2000000000000002", 1, 5, 0);
        QuestDefinition left = quest("2000000000000001", 0, 0, 0);

        Map<String, Rect> first = FirstTorchLayout.questNodes(panel, List.of(right, left));
        Map<String, Rect> second = FirstTorchLayout.questNodes(panel, List.of(left, right));

        assertEquals(first, second);
        assertTrue(first.get(left.id()).x() < first.get(right.id()).x());
    }

    private static QuestDefinition quest(String id, int order, int x, int y) {
        return new QuestDefinition(
                id, order, "quest.test.title", "quest.test.description",
                new QuestPosition(x, y), List.of());
    }
}
