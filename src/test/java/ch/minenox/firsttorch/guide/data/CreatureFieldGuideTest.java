package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class CreatureFieldGuideTest {
    private static final String INTRO = "3C3122DF5C0EA192";

    @Test void referenceCardsNeverGateOneAnotherOrEarlierLessons() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        var references = chapters.subList(55, 58);
        assertEquals(List.of("6A41D479062EB350", "6B52E58A173FC461", "6C63F69B2840D572"),
                references.stream().map(c -> c.id()).toList());
        for (var chapter : references) {
            assertEquals(4, chapter.quests().size());
            for (var quest : chapter.quests()) {
                assertEquals(List.of(INTRO), quest.prerequisiteQuestIds());
                assertEquals(1, quest.tasks().size());
                assertEquals(TaskDefinition.Type.MANUAL, quest.tasks().getFirst().type());
                assertEquals(1, quest.tasks().getFirst().count());
                assertTrue(quest.rewards().isEmpty());
            }
        }
        var ids = references.stream().flatMap(c -> c.quests().stream()).map(q -> q.id()).toList();
        assertTrue(chapters.subList(0, 55).stream().flatMap(c -> c.quests().stream())
                .flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(ids::contains));
    }

    @Test void anyCardCanBeReadFirstWithoutPossessingDrops() throws Exception {
        var snapshot = snapshot();
        var ready = new ProgressState(Set.of(), Set.of(INTRO));
        var cards = snapshot.guides().getFirst().chapters().subList(55, 58).stream()
                .flatMap(c -> c.quests().stream()).toList();
        var inventory = TaskEvaluator.evaluate(snapshot, ready, key -> 999);
        for (var card : cards) {
            assertFalse(inventory.completedQuestIds().contains(card.id()));
            var confirmed = TaskEvaluator.confirm(snapshot, ready, card.id(), card.tasks().getFirst().id(), key -> 0);
            assertTrue(confirmed.completedQuestIds().contains(card.id()));
            assertEquals(1, cards.stream().filter(q -> confirmed.completedQuestIds().contains(q.id())).count());
        }
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = CreatureFieldGuideTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
