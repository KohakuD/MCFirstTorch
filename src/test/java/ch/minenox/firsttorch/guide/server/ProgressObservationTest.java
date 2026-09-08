package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.*;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class ProgressObservationTest {
    private static final String FIRST = "2000000000000001";
    private static final String SECOND = "2000000000000002";
    private static final String MANUAL = "3000000000000001";
    private static final String LOGS = "3000000000000002";
    private static final GuideSnapshot GUIDE = new GuideSnapshot(List.of(new GuideDefinition(
            1, "0000000000000001", "guide.test.title", "guide.test.description",
            List.of(new ChapterDefinition("1000000000000001", 0, "chapter.test.title", "chapter.test.description",
                    List.of(quest(FIRST, List.of(), new TaskDefinition(MANUAL, TaskDefinition.Type.MANUAL, null, 1)),
                            quest(SECOND, List.of(FIRST), new TaskDefinition(LOGS, TaskDefinition.Type.INVENTORY,
                                    "minecraft:oak_log", 8))))))));

    @Test
    void showsAutomaticCountsWhileLockedThenKeepsStickyCompletion() {
        assertEquals(Map.of(MANUAL, 0, LOGS, 8),
                ProgressObservation.create(GUIDE, ProgressState.EMPTY, Map.of("minecraft:oak_log", 64)).taskCounts());
        ProgressState unlocked = new ProgressState(Set.of(MANUAL), Set.of(FIRST));
        for (int count : new int[] {-1, 0, 7, 8, 64}) {
            var observation = ProgressObservation.create(GUIDE, unlocked, Map.of("minecraft:oak_log", count));
            assertEquals(Math.clamp(count, 0, 8), observation.taskCounts().get(LOGS));
            assertEquals(1, observation.taskCounts().get(MANUAL));
            assertEquals(unlocked, observation.state());
        }
        ProgressState complete = new ProgressState(Set.of(MANUAL, LOGS), Set.of(FIRST, SECOND));
        assertEquals(8, ProgressObservation.create(GUIDE, complete, Map.of()).taskCounts().get(LOGS));
    }

    private static QuestDefinition quest(String id, List<String> prerequisites, TaskDefinition task) {
        int order = id.equals(FIRST) ? 0 : 1;
        return new QuestDefinition(id, order, "quest.test.title", "quest.test.description", new QuestPosition(order, 0),
                prerequisites, List.of(task), List.of());
    }
}
