package ch.minenox.firsttorch.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
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
                new QuestPosition(4, -3), List.of());
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
        buffer.writeVarInt(1);
        buffer.writeUtf("2000000000000001", 16);
        buffer.writeVarInt(0);
        buffer.writeUtf("quest.test.title", 256);
        buffer.writeUtf("quest.test.description", 256);
        buffer.writeInt(0);
        buffer.writeInt(0);
        buffer.writeVarInt(0);
    }
}
