package ch.minenox.firsttorch.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition.PrerequisiteMode;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.GuideImage;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.junit.jupiter.api.Test;

final class GuideSnapshotWireCodecTest {
    @Test
    void roundTripsSnapshot() {
        ByteBuf raw = Unpooled.buffer();
        try {
            RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(
                    raw, RegistryAccess.EMPTY, ConnectionType.NEOFORGE);
            GuideSnapshot expected = validSnapshot();
            GuideSnapshotPayload.STREAM_CODEC.encode(buffer, new GuideSnapshotPayload(expected));

            assertEquals(expected, GuideSnapshotPayload.STREAM_CODEC.decode(buffer).snapshot());
            assertEquals(0, buffer.readableBytes());
        } finally {
            raw.release();
        }
    }

    @Test
    void roundTripsAnyPrerequisiteMode() {
        ByteBuf raw = Unpooled.buffer();
        try {
            FriendlyByteBuf buffer = new FriendlyByteBuf(raw);
            QuestDefinition first = new QuestDefinition(
                    "2000000000000001", 0, "quest.test.title", "quest.test.description",
                    new QuestPosition(0, 0), List.of(), List.of(), List.of());
            QuestDefinition second = new QuestDefinition(
                    "2000000000000002", 1, "quest.test.title", "quest.test.description",
                    new QuestPosition(1, 0), List.of(first.id()), List.of(), List.of(), null, null, PrerequisiteMode.ANY);
            GuideSnapshot expected = new GuideSnapshot(List.of(new GuideDefinition(1, "0000000000000001",
                    "guide.test.title", "guide.test.description", List.of(new ChapterDefinition("1000000000000001", 0,
                    "chapter.test.title", "chapter.test.description", List.of(first, second))))));

            GuideSnapshotWireCodec.encode(buffer, expected);

            assertEquals(expected, GuideSnapshotWireCodec.decode(buffer));
        } finally {
            raw.release();
        }
    }

    @Test
    void rejectsOversizedGuideCountBeforeAllocation() {
        ByteBuf raw = Unpooled.buffer();
        try {
            FriendlyByteBuf buffer = new FriendlyByteBuf(raw);
            buffer.writeVarInt(GuideSnapshotWireCodec.MAX_GUIDES + 1);

            DecoderException exception = assertThrows(
                    DecoderException.class,
                    () -> GuideSnapshotWireCodec.decode(buffer));
            assertTrue(exception.getMessage().contains("guides count"));
        } finally {
            raw.release();
        }
    }

    @Test
    void revalidatesDecodedSnapshot() {
        ByteBuf raw = Unpooled.buffer();
        try {
            FriendlyByteBuf buffer = new FriendlyByteBuf(raw);
            writeSingleGuide(buffer, "8000000000000001");

            DecoderException exception = assertThrows(
                    DecoderException.class,
                    () -> GuideSnapshotWireCodec.decode(buffer));
            assertTrue(exception.getMessage().contains("Received invalid guide snapshot"));
        } finally {
            raw.release();
        }
    }

    private static GuideSnapshot validSnapshot() {
        QuestDefinition quest = new QuestDefinition(
                "2000000000000001", 0, "quest.test.title", "quest.test.description",
                new QuestPosition(4, -3), List.of(),
                List.of(new TaskDefinition("3000000000000001", TaskDefinition.Type.INVENTORY, "minecraft:oak_log", 8),
                        new TaskDefinition("3000000000000002", TaskDefinition.Type.MANUAL, null, 1)),
                List.of(new RewardDefinition("4000000000000001", RewardDefinition.Type.EXPERIENCE, null, 15),
                        new RewardDefinition("4000000000000002", RewardDefinition.Type.ITEM, "minecraft:bread", 1)),
                "minecraft:compass",
                new GuideImage("firsttorch:textures/guide/compass.png", 320, 180, "guide.test.image.compass"));
        ChapterDefinition chapter = new ChapterDefinition(
                "1000000000000001", 0, "chapter.test.title", "chapter.test.description", List.of(quest));
        GuideDefinition guide = new GuideDefinition(
                1, "0000000000000001", "guide.test.title", "guide.test.description", List.of(chapter));
        return new GuideSnapshot(List.of(guide));
    }

    private static void writeSingleGuide(FriendlyByteBuf buffer, String guideId) {
        buffer.writeVarInt(1);
        buffer.writeVarInt(1);
        buffer.writeUtf(guideId, 16);
        buffer.writeUtf("guide.test.title", 256);
        buffer.writeUtf("guide.test.description", 256);
        buffer.writeVarInt(1);
        buffer.writeUtf("1000000000000001", 16);
        buffer.writeVarInt(0);
        buffer.writeUtf("chapter.test.title", 256);
        buffer.writeUtf("chapter.test.description", 256);
        buffer.writeUtf("", 256);
        buffer.writeVarInt(1);
        buffer.writeUtf("2000000000000001", 16);
        buffer.writeVarInt(0);
        buffer.writeUtf("quest.test.title", 256);
        buffer.writeUtf("quest.test.description", 256);
        buffer.writeUtf("", 256);
        buffer.writeBoolean(false);
        buffer.writeInt(0);
        buffer.writeInt(0);
        buffer.writeVarInt(0);
        buffer.writeUtf("ALL", 16);
        buffer.writeVarInt(0);
        buffer.writeVarInt(0);
    }

    @Test
    void rejectsOversizedTaskListBeforeReadingEntries() {
        ByteBuf raw = Unpooled.buffer();
        try {
            FriendlyByteBuf buffer = new FriendlyByteBuf(raw);
            writeSingleGuide(buffer, "0000000000000001");
            buffer.writerIndex(buffer.writerIndex() - 2);
            buffer.writeVarInt(GuideSnapshotWireCodec.MAX_ENTRIES_PER_QUEST + 1);
            assertThrows(DecoderException.class, () -> GuideSnapshotWireCodec.decode(buffer));
        } finally {
            raw.release();
        }
    }

    @Test
    void rejectsUnknownTaskType() {
        ByteBuf raw = Unpooled.buffer();
        try {
            FriendlyByteBuf buffer = new FriendlyByteBuf(raw);
            writeSingleGuide(buffer, "0000000000000001");
            buffer.writerIndex(buffer.writerIndex() - 2);
            buffer.writeVarInt(1);
            buffer.writeUtf("3000000000000001", 16);
            buffer.writeUtf("UNKNOWN", 16);
            DecoderException error = assertThrows(DecoderException.class, () -> GuideSnapshotWireCodec.decode(buffer));
            assertTrue(error.getMessage().contains("Unknown guide entry type"));
        } finally {
            raw.release();
        }
    }

    @Test
    void rejectsOversizedRewardListBeforeReadingEntries() {
        ByteBuf raw = Unpooled.buffer();
        try {
            FriendlyByteBuf buffer = new FriendlyByteBuf(raw);
            writeSingleGuide(buffer, "0000000000000001");
            buffer.writerIndex(buffer.writerIndex() - 1);
            buffer.writeVarInt(GuideSnapshotWireCodec.MAX_ENTRIES_PER_QUEST + 1);
            assertThrows(DecoderException.class, () -> GuideSnapshotWireCodec.decode(buffer));
        } finally {
            raw.release();
        }
    }

    @Test
    void acceptsExistingQuestWithoutTasksOrRewards() {
        ByteBuf raw = Unpooled.buffer();
        ByteBuf encoded = Unpooled.buffer();
        try {
            FriendlyByteBuf buffer = new FriendlyByteBuf(raw);
            writeSingleGuide(buffer, "0000000000000001");
            GuideSnapshot snapshot = GuideSnapshotWireCodec.decode(buffer);
            var quest = snapshot.guides().getFirst().chapters().getFirst().quests().getFirst();
            assertTrue(quest.tasks().isEmpty());
            assertTrue(quest.rewards().isEmpty());
            assertNull(quest.image());
            FriendlyByteBuf roundTrip = new FriendlyByteBuf(encoded);
            GuideSnapshotWireCodec.encode(roundTrip, snapshot);
            assertEquals(snapshot, GuideSnapshotWireCodec.decode(roundTrip));
        } finally {
            raw.release();
            encoded.release();
        }
    }
}
