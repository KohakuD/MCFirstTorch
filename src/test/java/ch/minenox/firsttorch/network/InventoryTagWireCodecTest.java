package ch.minenox.firsttorch.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

final class InventoryTagWireCodecTest {
    @Test
    void roundTripsInventoryTagTypeAndIdentifier() {
        QuestDefinition quest = new QuestDefinition("2000000000000001", 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of(), List.of(new TaskDefinition("3000000000000001",
                TaskDefinition.Type.INVENTORY_TAG, "minecraft:planks", 8)), List.of());
        GuideSnapshot expected = new GuideSnapshot(List.of(new GuideDefinition(1, "0000000000000001", "guide.test.title",
                "guide.test.description", List.of(new ChapterDefinition("1000000000000001", 0,
                "chapter.test.title", "chapter.test.description", List.of(quest))))));
        ByteBuf raw = Unpooled.buffer();
        try {
            FriendlyByteBuf buffer = new FriendlyByteBuf(raw);
            GuideSnapshotWireCodec.encode(buffer, expected);
            assertEquals(expected, GuideSnapshotWireCodec.decode(buffer));
        } finally {
            raw.release();
        }
    }
}
