package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.client.TrophyCatalog;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class ItemInteractionsTest {
    private static final String INTRO = "3C3122DF5C0EA192";
    private static final List<String> CHAPTERS = List.of("771425364758697A", "7825364758697A1B", "79364758697A1B2C");
    private static final List<String> PUMPKIN_QUESTS = List.of("51A0B0C0D0E00001", "51A0B0C0D0E00003", "51A0B0C0D0E00002");
    private static final List<String> PUMPKIN_TASKS = List.of("61A0B0C0D0E00001", "61A0B0C0D0E00003", "61A0B0C0D0E00002");

    @Test void threeSeparateReadingChainsNeverGateEarlierLessons() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        var additions = chapters.subList(68, 71);
        assertEquals(CHAPTERS, additions.stream().map(c -> c.id()).toList());
        for (int c = 0; c < additions.size(); c++) {
            var cards = additions.get(c).quests();
            assertEquals(3, cards.size());
            if (c == 1) assertEquals(PUMPKIN_QUESTS, cards.stream().map(card -> card.id()).toList());
            if (c == 1) assertEquals(PUMPKIN_TASKS, cards.stream().map(card -> card.tasks().getFirst().id()).toList());
            for (int q = 0; q < cards.size(); q++) {
                var card = cards.get(q);
                if (c != 1) assertEquals("5" + c + "A0B0C0D0E0000" + (q + 1), card.id());
                assertEquals(List.of(q == 0 ? INTRO : cards.get(q - 1).id()), card.prerequisiteQuestIds());
                assertEquals(0, card.position().x());
                assertEquals(q * 2, card.position().y());
                assertTrue(card.rewards().isEmpty());
                assertEquals(1, card.tasks().size());
                var task = card.tasks().getFirst();
                if (c != 1) assertEquals("6" + c + "A0B0C0D0E0000" + (q + 1), task.id());
                assertEquals(TaskDefinition.Type.MANUAL, task.type());
                assertEquals(1, task.count());
            }
        }
        var ids = additions.stream().flatMap(c -> c.quests().stream()).map(q -> q.id()).toList();
        assertTrue(chapters.subList(0, 68).stream().flatMap(c -> c.quests().stream())
                .flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(ids::contains));
    }

    @Test void readingNeedsNoItemsOrDangerousExperimentAndTrophiesRemainIndependent() throws Exception {
        var guides = snapshot();
        for (var chapter : guides.guides().getFirst().chapters().subList(68, 71)) {
            var state = new ProgressState(Set.of(), Set.of(INTRO));
            var last = chapter.quests().getLast();
            var initial = state;
            assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(guides, initial,
                    last.id(), last.tasks().getFirst().id(), key -> 0));
            for (var card : chapter.quests()) {
                state = TaskEvaluator.confirm(guides, state, card.id(), card.tasks().getFirst().id(), key -> 0);
                assertTrue(state.completedQuestIds().contains(card.id()));
            }
            var trophies = TrophyCatalog.entries(guides, new ProgressPayload(state, Map.of(), true)).stream()
                    .filter(t -> CHAPTERS.contains(t.chapterId())).toList();
            assertEquals(3, trophies.size());
            assertEquals(1, trophies.stream().filter(TrophyCatalog.Entry::earned).count());
            assertTrue(trophies.stream().filter(TrophyCatalog.Entry::earned).allMatch(t -> t.chapterId().equals(chapter.id())));
        }
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = ItemInteractionsTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
