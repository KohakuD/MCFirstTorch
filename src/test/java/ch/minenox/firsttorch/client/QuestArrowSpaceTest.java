package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.data.GuideJson;
import java.util.List;
import org.junit.jupiter.api.Test;

final class QuestArrowSpaceTest {
    @Test void everyMaterialEdgeLeavesRoomForArrowShaftAndTipInBothModes() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            for (var chapter : GuideJson.read(input).chapters().stream().skip(1).toList()) {
            var quests = chapter.quests();
            for (boolean reading : List.of(false, true)) {
                var panel = FirstTorchLayout.calculate(720, 405, reading).questMap();
                if (reading) panel = new FirstTorchLayout.Rect(panel.x(), panel.y() + 23, panel.width(), panel.height() - 23);
                var nodes = FirstTorchLayout.questNodes(panel, quests);
                for (var quest : quests) {
                    var target = nodes.get(quest.id());
                    assertTrue(target.width() <= 28);
                    for (String id : quest.prerequisiteQuestIds()) {
                        var source = nodes.get(id);
                        if (source == null) continue; // Cross-chapter prerequisite has no node in this map.
                        double gap = Math.hypot(target.centerX() - source.centerX(), target.centerY() - source.centerY())
                                - source.width() / 2.0 - target.width() / 2.0;
                        // Renderer trims 2 + 5 pixels, then draws a five-pixel arrowhead.
                        assertTrue(gap >= 14, id + " -> " + quest.id() + " gap=" + gap);
                    }
                }
            }
            }
        }
    }

    @Test void iconsShrinkWithNodesAndLeaveAnInnerMargin() {
        for (int diameter : new int[]{12, 16, 20, 24, 28, 48}) {
            int size = FirstTorchLayout.nodeIconSize(diameter);
            assertTrue(size <= 12);
            assertTrue(size <= diameter - 6);
        }
        assertEquals(12, FirstTorchLayout.nodeIconSize(28));
        assertEquals(8, FirstTorchLayout.nodeIconSize(20));
    }
}
