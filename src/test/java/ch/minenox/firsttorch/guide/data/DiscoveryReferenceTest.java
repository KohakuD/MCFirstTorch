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

final class DiscoveryReferenceTest {
    private static final String INTRO = "3C3122DF5C0EA192";

    @Test void discoveriesKeepIdsAndTrophiesWhileArchaeologyAndCuringAreOrdered() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        var additions = chapters.subList(65, 68);
        assertEquals(List.of("74E1F20314253647", "75F2031425364758", "7603142536475869"),
                additions.stream().map(c -> c.id()).toList());
        for (var chapter : additions) {
            assertEquals(3, chapter.quests().size());
            assertEquals(3, chapter.quests().stream().map(q -> q.position()).distinct().count());
            for (var quest : chapter.quests()) {
                assertEquals(1, quest.tasks().size());
                assertEquals(TaskDefinition.Type.MANUAL, quest.tasks().getFirst().type());
                assertEquals(1, quest.tasks().getFirst().count());
                assertTrue(quest.rewards().isEmpty());
            }
        }
        assertOrderedVertically(additions.get(0));
        assertOrderedVertically(additions.get(2));
        assertEquals(List.of(INTRO), additions.get(1).quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of(INTRO), additions.get(1).quests().get(1).prerequisiteQuestIds());
        assertEquals(List.of(INTRO), additions.get(1).quests().get(2).prerequisiteQuestIds());
        var ids = additions.stream().flatMap(c -> c.quests().stream()).map(q -> q.id()).toList();
        assertTrue(chapters.subList(0, 65).stream().flatMap(c -> c.quests().stream())
                .flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(ids::contains));
    }

    @Test void orderedReadingsBlockLaterCardsButCompleteSequentiallyWithoutItems() throws Exception {
        var guides = snapshot();
        var ready = new ProgressState(Set.of(), Set.of(INTRO));
        for (var chapter : List.of(guides.guides().getFirst().chapters().get(65), guides.guides().getFirst().chapters().get(67))) {
            var cards = chapter.quests();
            assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(guides, ready,
                    cards.get(2).id(), cards.get(2).tasks().getFirst().id(), key -> 0));
            var first = TaskEvaluator.confirm(guides, ready, cards.get(0).id(), cards.get(0).tasks().getFirst().id(), key -> 0);
            var second = TaskEvaluator.confirm(guides, first, cards.get(1).id(), cards.get(1).tasks().getFirst().id(), key -> 0);
            var complete = TaskEvaluator.confirm(guides, second, cards.get(2).id(), cards.get(2).tasks().getFirst().id(), key -> 0);
            assertTrue(cards.stream().allMatch(card -> complete.completedQuestIds().contains(card.id())));
        }
    }

    private static void assertOrderedVertically(ch.minenox.firsttorch.guide.model.ChapterDefinition chapter) {
        var cards = chapter.quests();
        assertEquals(List.of(INTRO), cards.get(0).prerequisiteQuestIds());
        assertEquals(List.of(cards.get(0).id()), cards.get(1).prerequisiteQuestIds());
        assertEquals(List.of(cards.get(1).id()), cards.get(2).prerequisiteQuestIds());
        assertEquals(List.of(0, 0, 0), cards.stream().map(q -> q.position().x()).toList());
        assertEquals(List.of(0, 2, 4), cards.stream().map(q -> q.position().y()).toList());
    }

    @Test void trophiesAreIndependentAndUseChapterIcons() throws Exception {
        var guides = snapshot();
        var additions = guides.guides().getFirst().chapters().subList(65, 68);
        var done = additions.getFirst().quests().stream().map(q -> q.id()).collect(java.util.stream.Collectors.toSet());
        var progress = new ProgressPayload(new ProgressState(Set.of(), done), Map.of(), true);
        var ids = additions.stream().map(c -> c.id()).toList();
        var trophies = TrophyCatalog.entries(guides, progress).stream().filter(t -> ids.contains(t.chapterId())).toList();
        assertEquals(List.of(true, false, false), trophies.stream().map(TrophyCatalog.Entry::earned).toList());
        assertEquals(List.of("minecraft:brush", "minecraft:music_disc_cat", "minecraft:golden_apple"),
                trophies.stream().map(TrophyCatalog.Entry::iconItemId).toList());
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = DiscoveryReferenceTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
