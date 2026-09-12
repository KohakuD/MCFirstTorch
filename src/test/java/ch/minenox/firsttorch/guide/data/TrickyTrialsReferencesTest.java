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
        assertEquals(8, chapter.quests().size());
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
}
