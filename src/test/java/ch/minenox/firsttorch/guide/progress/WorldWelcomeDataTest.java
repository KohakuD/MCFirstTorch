package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.junit.jupiter.api.Test;

final class WorldWelcomeDataTest {
    private static final UUID FIRST = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test void roundTripPreservesPlayerIsolationAndAcknowledgementIsIdempotent() {
        WorldWelcomeData original = new WorldWelcomeData();
        original.acknowledge(FIRST);
        original.acknowledge(FIRST);
        Tag encoded = WorldWelcomeData.CODEC.encodeStart(NbtOps.INSTANCE, original).getOrThrow();
        WorldWelcomeData restored = WorldWelcomeData.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow();
        assertTrue(restored.isAcknowledged(FIRST));
        assertFalse(restored.isAcknowledged(SECOND));
        assertEquals(encoded, WorldWelcomeData.CODEC.encodeStart(NbtOps.INSTANCE, restored).getOrThrow());
        restored.setDirty(false);
        restored.acknowledge(FIRST);
        assertFalse(restored.isDirty());
    }

    @Test void rejectsInvalidVersionDuplicateOrNoncanonicalPlayerWithoutPartialRecovery() {
        for (String json : new String[]{
                "{}", "{\"version\":1}", "{\"version\":2,\"acknowledged_players\":[]}",
                "{\"version\":1,\"acknowledged_players\":[\"invalid\"]}",
                "{\"version\":1,\"acknowledged_players\":[\"00000000-0000-0000-0000-000000000001\",\"00000000-0000-0000-0000-000000000001\"]}"}) {
            JsonObject document = JsonParser.parseString(json).getAsJsonObject();
            Tag tag = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, document);
            assertTrue(WorldWelcomeData.CODEC.parse(NbtOps.INSTANCE, tag).error().isPresent());
            assertTrue(WorldWelcomeData.CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(message -> {}).isEmpty());
        }
        JsonObject excessive = new JsonObject();
        excessive.addProperty("version", 1);
        JsonArray players = new JsonArray();
        for (int index = 0; index <= 10_000; index++) players.add(new UUID(0, index).toString());
        excessive.add("acknowledged_players", players);
        assertTrue(WorldWelcomeData.CODEC.parse(NbtOps.INSTANCE, JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, excessive))
                .resultOrPartial(message -> {}).isEmpty());
    }

    @Test void enforcesCapacityBeforeMutationWhileExistingAcknowledgementsStayIdempotent() {
        WorldWelcomeData data = new WorldWelcomeData();
        UUID first = new UUID(0, 0);
        for (int index = 0; index < 10_000; index++) data.acknowledge(new UUID(0, index));
        data.setDirty(false);
        data.acknowledge(first);
        assertFalse(data.isDirty());
        assertThrows(IllegalStateException.class, () -> data.acknowledge(new UUID(1, 0)));
        assertFalse(data.isDirty());
        assertFalse(data.isAcknowledged(new UUID(1, 0)));
    }
}
