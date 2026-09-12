package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.edition.FirstTorchEditionHistory;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class TrickyTrialsReferencesTest {
    @Test void backfilled121LessonsExistWithoutBecomingLaterMinecraftInnovations() {
        var guide = GuideJson.read(getClass().getResourceAsStream("/data/firsttorch/guides/course.json"));
        var chapter = guide.chapters().stream().filter(c -> c.id().equals("7A12100000000001"))
                .findFirst().orElseThrow();
        assertEquals(12, chapter.quests().size());
        assertEquals(List.of("1CA0B0C0D0E00001", "1CA0B0C0D0E00002"),
                chapter.quests().subList(3, 5).stream().map(q -> q.id()).toList());
        assertEquals(12, chapter.quests().stream().map(q -> q.position()).distinct().count());
        assertTrue(chapter.quests().stream().allMatch(q -> q.position().x() >= 0 && q.position().x() <= 6
                && q.position().y() >= 0 && q.position().y() <= 6));
        assertEquals(List.of(0, 0, 0, 2, 2, 4, 4, 4, 6, 6, 6, 6),
                chapter.quests().stream().map(q -> q.position().y()).toList());
        var crafter = guide.chapters().stream().filter(c -> c.id().equals("7A12100000000002"))
                .findFirst().orElseThrow();
        assertEquals("chapter.firsttorch.redstone_crafter.title", crafter.titleKey());
        assertEquals(List.of("2A12100000000001", "2A12100000000002"),
                crafter.quests().stream().map(q -> q.id()).toList());
        assertEquals(List.of("10A0B0C0D0E00006"), crafter.quests().getFirst().prerequisiteQuestIds());
        assertEquals(List.of("2A12100000000001"), crafter.quests().getLast().prerequisiteQuestIds());
        assertTrue(crafter.quests().stream().allMatch(q -> q.rewards().isEmpty()));
        var ids = chapter.quests().stream().map(q -> q.id()).collect(Collectors.toSet());
        var history = FirstTorchEditionHistory.create();
        var snapshot = new GuideSnapshot(List.of(guide));
        assertTrue(history.compare("1.21.1", "26.2", snapshot).changedQuestIds().stream().noneMatch(ids::contains));
        assertTrue(history.compare("1.21", "1.21.1", snapshot).changedQuestIds().stream().noneMatch(ids::contains));
        for (var quest : chapter.quests()) {
            assertEquals(List.of("3C3122DF5C0EA192"), quest.prerequisiteQuestIds());
            assertEquals(1, quest.tasks().size());
            assertEquals(TaskDefinition.Type.MANUAL, quest.tasks().getFirst().type());
            assertTrue(quest.rewards().isEmpty());
        }
        for (var old : guide.chapters()) if (!old.id().equals(chapter.id()))
            for (var quest : old.quests()) assertTrue(quest.prerequisiteQuestIds().stream().noneMatch(ids::contains));
    }
    @Test void hazardousPotionReadingsNeedNoBrewingDamageOrCombat() {
        var guide = GuideJson.read(getClass().getResourceAsStream("/data/firsttorch/guides/course.json"));
        var snapshot = new GuideSnapshot(List.of(guide));
        var cards = guide.chapters().stream().flatMap(c -> c.quests().stream())
                .filter(q -> List.of("2A12100000000009", "2A1210000000000A",
                        "2A1210000000000B", "2A1210000000000C").contains(q.id())).toList();
        assertEquals(4, cards.size());
        var ready = new ch.minenox.firsttorch.guide.progress.ProgressState(
                java.util.Set.of(), java.util.Set.of("3C3122DF5C0EA192"));
        var supplied = ch.minenox.firsttorch.guide.progress.TaskEvaluator.evaluate(snapshot, ready, key -> 999);
        for (var card : cards) {
            assertFalse(supplied.completedQuestIds().contains(card.id()));
            var confirmed = ch.minenox.firsttorch.guide.progress.TaskEvaluator.confirm(
                    snapshot, ready, card.id(), card.tasks().getFirst().id(), key -> 0);
            assertTrue(confirmed.completedQuestIds().contains(card.id()));
            assertEquals(1, cards.stream().filter(q -> confirmed.completedQuestIds().contains(q.id())).count());
        }
    }
    @Test void paleGardenExpansionRespectsTargetAndComparisonBoundaries() {
        var guide = GuideJson.read(getClass().getResourceAsStream("/data/firsttorch/guides/course.json"));
        var snapshot = new GuideSnapshot(List.of(guide));
        var all = guide.chapters().stream().flatMap(c -> c.quests().stream()).toList();
        var ids = java.util.Set.of("1CA0B0C0D0E00004", "1CA0B0C0D0E00005",
                "1CA0B0C0D0E00006", "1CA0B0C0D0E00007");
        var cards = all.stream().filter(q -> ids.contains(q.id())).toList();
        boolean supported = all.stream().anyMatch(q -> q.id().equals("1CA0B0C0D0E00003"));
        assertEquals(supported ? 4 : 0, cards.size());
        var history = FirstTorchEditionHistory.create();
        if (supported) assertTrue(history.compare("1.21.1", "26.1.2", snapshot).changedQuestIds().containsAll(ids));
        assertTrue(history.compare("26.1.2", "26.2", snapshot).changedQuestIds().stream().noneMatch(ids::contains));
        assertEquals(cards.size(), cards.stream().map(q -> q.position()).distinct().count());
        for (var card : cards) {
            assertEquals(List.of("3C3122DF5C0EA192"), card.prerequisiteQuestIds());
            assertEquals(TaskDefinition.Type.MANUAL, card.tasks().getFirst().type());
            assertTrue(card.rewards().isEmpty());
        }
    }
}
