package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class ChapterSplitTest {
    @Test void keepsSmallThematicChaptersAndAllExistingCompletionIds() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var guide = GuideJson.read(input);
            assertEquals(List.of(8, 5, 5, 8, 6, 6, 5, 6, 6, 5, 4, 2, 6, 3, 6, 5, 6, 4, 6, 6, 6, 3, 5, 6, 4, 5, 4, 4, 4, 3, 7, 4, 6, 6, 5, 4, 7, 6, 3, 6, 5, 4, 8, 5, 5, 4, 7, 7, 7, 6, 4, 4, 4, 4, 4, 11, 5, 4, 4, 4, 4, 3, 3, 3), guide.chapters().stream().map(c -> c.quests().size()).toList());
            assertEquals("01F57C0E3B9D2468", guide.chapters().get(1).id());
            assertEquals(List.of("7B83D5F920C4160E", "56CA245D80EB7913", "46B7D91E2A5C803F", "1064CEF72A8513BD", "3286E0194CA735DF"),
                    guide.chapters().get(2).quests().stream().map(q -> q.id()).toList());
            assertEquals(List.of("18EC467FA20D9B35", "3A0E6891C42FBD57", "65A1B2C3D4E5F607", "5C208AB3E641DF79",
                    "6A24D8F30C715BE9", "7E42ACD50863F19B", "6E9B173C5D802AF4", "54A8023B6EC957F1"),
                    guide.chapters().get(3).quests().stream().map(q -> q.id()).toList());
            var quests = guide.chapters().stream().limit(4).flatMap(c -> c.quests().stream()).toList();
            var tasks = quests.stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).collect(Collectors.toSet());
            var completed = quests.stream().map(q -> q.id()).collect(Collectors.toSet());
            assertEquals(26, completed.size());
            assertEquals(29, tasks.size());
            var previous = new ProgressState(tasks, completed);
            assertEquals(previous, TaskEvaluator.evaluate(new GuideSnapshot(List.of(guide)), previous, key -> 0));
            // Shelter and tools branch from individual lessons, never from completing optional movement.
            assertEquals(List.of("34A8023B6ECF5791"), guide.chapters().get(2).quests().getFirst().prerequisiteQuestIds());
            assertEquals(List.of("56CA245D80EB7913"), guide.chapters().get(3).quests().getFirst().prerequisiteQuestIds());
        }
    }
}
