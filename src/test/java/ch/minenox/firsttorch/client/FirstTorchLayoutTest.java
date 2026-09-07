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
