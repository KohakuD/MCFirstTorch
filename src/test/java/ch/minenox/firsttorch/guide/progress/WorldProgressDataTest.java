package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.junit.jupiter.api.Test;

final class WorldProgressDataTest {
    private static final UUID FIRST = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final String TASK = "1000000000000001";
    private static final String QUEST = "2000000000000001";

    @Test
    void nbtRoundTripPreservesIndependentPlayersAndHistoricalIds() {
        WorldProgressData original = new WorldProgressData();
        ProgressState first = new ProgressState(Set.of(TASK, "700000000000000F"), Set.of(QUEST));
        ProgressState second = new ProgressState(Set.of("1000000000000002"), Set.of());
        original.put(FIRST, first);
        original.put(SECOND, second);

        Tag encoded = WorldProgressData.CODEC.encodeStart(NbtOps.INSTANCE, original).getOrThrow();
        WorldProgressData restored = WorldProgressData.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow();
        assertEquals(first, restored.get(FIRST));
        assertEquals(second, restored.get(SECOND));
        assertEquals(ProgressState.EMPTY, restored.get(new UUID(0, 3)));
        assertFalse(restored.isDirty());
        assertEquals(encoded, WorldProgressData.CODEC.encodeStart(NbtOps.INSTANCE, restored).getOrThrow());
    }

    @Test
    void dirtiesOnlyWhenProgressChanges() {
        WorldProgressData data = new WorldProgressData();
        assertFalse(data.isDirty());
        data.put(FIRST, ProgressState.EMPTY);
        assertFalse(data.isDirty());
        ProgressState progress = new ProgressState(Set.of(TASK), Set.of());
        data.put(FIRST, progress);
        assertTrue(data.isDirty());
        data.setDirty(false);
        data.put(FIRST, new ProgressState(Set.of(TASK), Set.of()));
        assertFalse(data.isDirty());
        data.put(FIRST, ProgressState.EMPTY);
        assertTrue(data.isDirty());
    }

    @Test
    void rejectsMissingOrUnsupportedVersionAndRequiredFields() {
        for (String json : new String[]{"{}", "{\"players\":[]}", "{\"version\":1}",
                "{\"version\":0,\"players\":[]}", "{\"version\":2,\"players\":[]}"}) {
            assertRejected(JsonParser.parseString(json).getAsJsonObject());
        }
        JsonObject document = validDocument();
        progress(document).remove("completed_tasks");
        assertRejected(document);
    }

    @Test
    void rejectsMalformedIdsAndPlayerIds() {
        for (String id : new String[]{"", "100000000000001", "10000000000000001", "8000000000000001",
                "100000000000000a", "100000000000000G"}) {
            for (String field : new String[]{"completed_tasks", "completed_quests"}) {
                JsonObject document = validDocument();
                JsonArray ids = new JsonArray();
                ids.add(id);
                progress(document).add(field, ids);
                assertRejected(document);
            }
        }
        JsonObject document = validDocument();
        document.getAsJsonArray("players").get(0).getAsJsonObject().addProperty("player", "invalid");
        assertRejected(document);
    }

    @Test
    void rejectsDuplicatePlayersAndCompletionIds() {
        JsonObject document = validDocument();
        document.getAsJsonArray("players").add(document.getAsJsonArray("players").get(0).deepCopy());
        assertRejected(document);
        for (String field : new String[]{"completed_tasks", "completed_quests"}) {
            document = validDocument();
            JsonArray ids = progress(document).getAsJsonArray(field);
            ids.add(ids.get(0).getAsString());
            assertRejected(document);
        }
    }

    @Test
    void neverReturnsPartialWorldWhenOnePlayerIsCorrupt() {
        JsonObject document = validDocument();
        JsonObject corrupt = document.getAsJsonArray("players").get(0).getAsJsonObject().deepCopy();
        corrupt.addProperty("player", SECOND.toString());
        corrupt.getAsJsonObject("progress").getAsJsonArray("completed_tasks").add("broken");
        document.getAsJsonArray("players").add(corrupt);
        assertRejected(document);
    }

    @Test
    void rejectsExcessiveListsAndInvalidInMemoryProgress() {
        for (String field : new String[]{"completed_tasks", "completed_quests"}) {
            JsonObject document = validDocument();
            JsonArray ids = new JsonArray();
            int maximum = field.equals("completed_tasks") ? 65_536 : 16_384;
            for (int index = 0; index <= maximum; index++) {
                ids.add(String.format("%016X", index));
            }
            progress(document).add(field, ids);
            assertRejected(document);
        }
        JsonObject document = validDocument();
        JsonObject entry = document.getAsJsonArray("players").get(0).getAsJsonObject();
        JsonArray players = new JsonArray();
        for (int index = 0; index <= 10_000; index++) {
            JsonObject player = entry.deepCopy();
            player.addProperty("player", new UUID(0, index).toString());
            players.add(player);
        }
        document.add("players", players);
        assertRejected(document);
        WorldProgressData data = new WorldProgressData();
        assertThrows(IllegalStateException.class,
                () -> data.put(FIRST, new ProgressState(Set.of("invalid"), Set.of())));
        assertFalse(data.isDirty());
        assertEquals(ProgressState.EMPTY, data.get(FIRST));
    }

    private static JsonObject validDocument() {
        return JsonParser.parseString("""
                {"version":1,"players":[{"player":"00000000-0000-0000-0000-000000000001",
                  "progress":{"completed_tasks":["1000000000000001"],
                              "completed_quests":["2000000000000001"]}}]}
                """).getAsJsonObject();
    }

    private static JsonObject progress(JsonObject document) {
        return document.getAsJsonArray("players").get(0).getAsJsonObject().getAsJsonObject("progress");
    }

    private static void assertRejected(JsonObject document) {
        Tag nbt = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, document);
        assertTrue(WorldProgressData.CODEC.parse(NbtOps.INSTANCE, nbt).error().isPresent());
        assertTrue(WorldProgressData.CODEC.parse(NbtOps.INSTANCE, nbt).result().isEmpty());
        assertTrue(WorldProgressData.CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(message -> {}).isEmpty());
    }
}
