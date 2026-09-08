package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import org.junit.jupiter.api.Test;

final class RedstoneDoorTest {
    @Test void doorIsAnOptionalFourLessonContinuationWithOneSmallReward() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters();
        var chapter = chapters.get(54);
        assertEquals("6930C368F51DA24F", chapter.id());
        assertEquals(4, chapter.quests().size());
        String previous = "14A0B0C0D0E00004";
        for (var quest : chapter.quests()) {
            assertEquals(List.of(previous), quest.prerequisiteQuestIds());
            previous = quest.id();
        }
        var ids = chapter.quests().stream().map(q -> q.id()).toList();
        assertTrue(chapters.subList(0, 54).stream().flatMap(c -> c.quests().stream())
                .flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(ids::contains));
        assertEquals(1, chapter.quests().stream().flatMap(q -> q.rewards().stream()).count());
        assertEquals(5, chapter.quests().getLast().rewards().getFirst().amount());
        assertNull(chapter.quests().getLast().rewards().getFirst().itemId());
    }

    @Test void twoPlatesAreRequiredButOwningMaterialsDoesNotProveTheDoorWorks() throws Exception {
        var snapshot = snapshot();
        var chapter = snapshot.guides().getFirst().chapters().get(54);
        var tasks = chapter.quests().stream().flatMap(q -> q.tasks().stream()).toList();
        assertEquals(5, tasks.size());
        assertEquals(List.of("minecraft:iron_door", "minecraft:stone_pressure_plate"),
                tasks.stream().filter(t -> t.automatic()).map(t -> t.itemId()).toList());
        var shortState = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> 1);
        assertTrue(shortState.completedTaskIds().contains(tasks.get(0).id()));
        assertFalse(shortState.completedTaskIds().contains(tasks.get(1).id()));
        var ready = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> 2);
        for (var task : tasks) assertEquals(task.automatic(), ready.completedTaskIds().contains(task.id()));
        assertTrue(ready.completedQuestIds().isEmpty());
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = RedstoneDoorTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
