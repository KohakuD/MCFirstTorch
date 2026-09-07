package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertTrue;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import java.util.List;
import org.junit.jupiter.api.Test;

class FirstTorchCompactMapTest {
    @Test void previewBranchesFitWithVisibleConnectionGaps() {
        var quests = DesignPreview.snapshot().guides().getFirst().chapters().getFirst().quests();
        for (int[] size : new int[][] {{720, 405}, {320, 240}}) {
            var panel = FirstTorchLayout.calculate(size[0], size[1]).questMap();
            var nodes = FirstTorchLayout.questNodes(panel, quests);
            assertTrue(nodes.values().stream().allMatch(panel::contains));
            for (var quest : quests) {
                var target = nodes.get(quest.id());
                for (var id : quest.prerequisiteQuestIds()) {
                    var source = nodes.get(id);
                    double distance = Math.hypot(target.centerX() - source.centerX(), target.centerY() - source.centerY());
                    assertTrue(distance > source.width() / 2.0 + target.width() / 2.0 + 7);
                }
            }
        }
    }
    @Test void keepsTwoSampleLessonsSeparatedOnSmallViewport() {
        var viewport = FirstTorchViewport.fit(320, 240);
        var panel = FirstTorchLayout.calculate(viewport.width(), viewport.height()).questMap();
        var first = new QuestDefinition("2000000000000001", 0, "quest.test.title", "quest.test.description", new QuestPosition(0, 0), List.of());
        var second = new QuestDefinition("2000000000000002", 1, "quest.test.title", "quest.test.description", new QuestPosition(2, 0), List.of(first.id()));
        var nodes = FirstTorchLayout.questNodes(panel, List.of(first, second));
        assertTrue(nodes.get(first.id()).right() < nodes.get(second.id()).x());
        assertTrue(nodes.values().stream().allMatch(panel::contains));
    }
}
