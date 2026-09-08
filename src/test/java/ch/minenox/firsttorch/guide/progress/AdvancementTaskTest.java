package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.*;
import ch.minenox.firsttorch.guide.server.ProgressObservation;
import ch.minenox.firsttorch.network.GuideSnapshotWireCodec;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class AdvancementTaskTest {
    private static final String PARENT = "2000000000000001", QUEST = "2000000000000002", TASK = "3000000000000002";
    private TaskDefinition task(String item, int count, String advancement, String criterion) {
        return new TaskDefinition(TASK, TaskDefinition.Type.ADVANCEMENT, item, count, "task.sleep.title", advancement, criterion);
    }
    private TaskDefinition sleep() { return task(null, 1, "minecraft:adventure/sleep_in_bed", "slept_in_bed"); }
    private GuideSnapshot snapshot(TaskDefinition task) {
        var parent = new QuestDefinition(PARENT, 0, "parent.title", "parent.description", new QuestPosition(0, 0), List.of(),
                List.of(new TaskDefinition("3000000000000001", TaskDefinition.Type.MANUAL, null, 1)), List.of());
        var quest = new QuestDefinition(QUEST, 1, "quest.title", "quest.description", new QuestPosition(0, 1), List.of(PARENT), List.of(task), List.of());
        return new GuideSnapshot(List.of(new GuideDefinition(1, "0000000000000001", "guide.title", "guide.description",
                List.of(new ChapterDefinition("1000000000000001", 0, "chapter.title", "chapter.description", List.of(parent, quest))))));
    }

    @Test void automaticProgressPrecompletesBeforePrerequisitesButQuestRemainsGated() {
        var task = sleep(); var snapshot = snapshot(task);
        var counts = Map.of(task.inventoryKey(), 1);
        var precompleted = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> counts.getOrDefault(key, 0));
        assertEquals(Set.of(TASK), precompleted.completedTaskIds());
        assertFalse(precompleted.completedQuestIds().contains(QUEST));
        assertEquals(precompleted, TaskEvaluator.evaluate(snapshot, precompleted, key -> 0));
        var unlocked = new ProgressState(Set.of("3000000000000001"), Set.of(PARENT));
        assertFalse(TaskEvaluator.evaluate(snapshot, unlocked, key -> 0).completedQuestIds().contains(QUEST));
        var done = TaskEvaluator.evaluate(snapshot, unlocked, key -> counts.getOrDefault(key, 0));
        assertTrue(done.completedQuestIds().contains(QUEST));
        assertEquals(done, TaskEvaluator.evaluate(snapshot, done, key -> 0));
        assertThrows(IllegalArgumentException.class, () -> TaskEvaluator.confirm(snapshot, unlocked, QUEST, TASK, key -> 1));
        assertEquals(1, ProgressObservation.create(snapshot, done, Map.of()).taskCounts().get(TASK));
        assertEquals(1, ProgressObservation.create(snapshot, ProgressState.EMPTY, counts).taskCounts().get(TASK));
    }

    @Test void schemaRejectsMalformedAndConflictingFields() {
        assertThrows(IllegalArgumentException.class, () -> snapshot(task("minecraft:white_bed", 1, "minecraft:adventure/sleep_in_bed", null)));
        assertThrows(IllegalArgumentException.class, () -> snapshot(task(null, 2, "minecraft:adventure/sleep_in_bed", null)));
        assertThrows(IllegalArgumentException.class, () -> snapshot(task(null, 1, null, "slept_in_bed")));
        assertThrows(IllegalArgumentException.class, () -> snapshot(task(null, 1, "bad id", null)));
        assertThrows(IllegalArgumentException.class, () -> snapshot(task(null, 1, "minecraft:test", "")));
        assertThrows(IllegalArgumentException.class, () -> snapshot(task(null, 1, "minecraft:test", "x".repeat(257))));
        assertThrows(IllegalArgumentException.class, () -> snapshot(new TaskDefinition(TASK, TaskDefinition.Type.MANUAL,
                null, 1, null, "minecraft:test", null)));
    }

    @Test void wireRetainsExactCriterionAndWholeAdvancementVariant() {
        for (String criterion : new String[]{null, "slept_in_bed"}) {
            var expected = snapshot(task(null, 1, "minecraft:adventure/sleep_in_bed", criterion));
            var raw = Unpooled.buffer();
            try {
                var buffer = new FriendlyByteBuf(raw);
                GuideSnapshotWireCodec.encode(buffer, expected);
                assertEquals(expected, GuideSnapshotWireCodec.decode(buffer));
            } finally { raw.release(); }
        }
    }
}
