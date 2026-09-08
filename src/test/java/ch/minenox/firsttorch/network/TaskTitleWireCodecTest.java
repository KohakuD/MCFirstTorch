package ch.minenox.firsttorch.network;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.validation.GuideValidator;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

final class TaskTitleWireCodecTest {
    @Test
    void roundTripsAnExplicitTaskTitle() {
        TaskDefinition task = new TaskDefinition("3000000000000001", TaskDefinition.Type.MANUAL, null, 1,
                "task.firsttorch.3000000000000001.title");
        GuideSnapshot expected = snapshot(task);
        var raw = Unpooled.buffer();
        try {
            var buffer = new FriendlyByteBuf(raw);
            GuideSnapshotWireCodec.encode(buffer, expected);
            assertEquals(expected, GuideSnapshotWireCodec.decode(buffer));
        } finally {
            raw.release();
        }
    }

    @Test
    void retainsTheFourArgumentConstructorAndRejectsInvalidOptionalTitles() {
        TaskDefinition legacy = new TaskDefinition("3000000000000001", TaskDefinition.Type.MANUAL, null, 1);
        assertNull(legacy.titleKey());
        assertDoesNotThrow(() -> GuideValidator.validate(snapshot(legacy).guides().getFirst()));
        TaskDefinition invalid = new TaskDefinition("3000000000000001", TaskDefinition.Type.MANUAL, null, 1, "not a key");
        assertThrows(IllegalArgumentException.class, () -> GuideValidator.validate(snapshot(invalid).guides().getFirst()));
        TaskDefinition oversized = new TaskDefinition("3000000000000001", TaskDefinition.Type.MANUAL, null, 1,
                "task." + "x".repeat(252));
        assertThrows(IllegalArgumentException.class, () -> GuideValidator.validate(snapshot(oversized).guides().getFirst()));
    }

    private static GuideSnapshot snapshot(TaskDefinition task) {
        var quest = new QuestDefinition("2000000000000001", 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of(), List.of(task), List.of());
        return new GuideSnapshot(List.of(new GuideDefinition(1, "0000000000000001", "guide.test.title",
                "guide.test.description", List.of(new ChapterDefinition("1000000000000001", 0,
                "chapter.test.title", "chapter.test.description", List.of(quest))))));
    }
}
