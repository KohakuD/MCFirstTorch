package ch.minenox.firsttorch.network;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.progress.ProgressState;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

final class ProgressPayloadTest {
    private static final String TASK = "3000000000000001";
    private static final String QUEST = "2000000000000001";

    @Test
    void roundTripsAvailableAndUnavailable() {
        for (ProgressPayload expected : new ProgressPayload[] {
                new ProgressPayload(new ProgressState(Set.of(TASK), Set.of(QUEST)), Map.of(TASK, 8), true),
                new ProgressPayload(new ProgressState(Set.of(TASK), Set.of(QUEST)), Map.of(TASK, 8), true, Set.of(QUEST), Set.of()),
                new ProgressPayload(new ProgressState(Set.of(TASK), Set.of(QUEST)), Map.of(TASK, 8), true, Set.of(), Set.of(QUEST)),
                new ProgressPayload(ProgressState.EMPTY, Map.of(TASK, 7), true), ProgressPayload.UNAVAILABLE }) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                ProgressPayload.encode(buffer, expected);
                assertEquals(expected, ProgressPayload.decode(buffer));
                assertEquals(0, buffer.readableBytes());
            } finally { buffer.release(); }
        }
    }

    @Test
    void rejectsOversizedAndNegativeCollectionsBeforeReadingEntries() {
        for (int size : new int[] {-1, ProgressPayload.MAX_TASKS + 1}) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                buffer.writeBoolean(true);
                buffer.writeVarInt(size);
                assertThrows(DecoderException.class, () -> ProgressPayload.decode(buffer));
            } finally { buffer.release(); }
        }
    }

    @Test
    void rejectsDuplicateCompletionsAndNegativeIds() {
        for (boolean duplicate : new boolean[] {true, false}) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                buffer.writeBoolean(true);
                buffer.writeVarInt(duplicate ? 2 : 1);
                buffer.writeLong(duplicate ? 1 : -1);
                if (duplicate) buffer.writeLong(1);
                assertThrows(DecoderException.class, () -> ProgressPayload.decode(buffer));
            } finally { buffer.release(); }
        }
    }

    @Test
    void rejectsDuplicateAndOutOfRangeCounts() {
        for (int count : new int[] {-1, 4097, 1}) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                buffer.writeBoolean(true);
                buffer.writeVarInt(0);
                buffer.writeVarInt(0);
                buffer.writeVarInt(2);
                buffer.writeLong(1);
                buffer.writeVarInt(count);
                buffer.writeLong(1);
                buffer.writeVarInt(count);
                assertThrows(DecoderException.class, () -> ProgressPayload.decode(buffer));
            } finally { buffer.release(); }
        }
    }

    @Test
    void validatesAndCopiesObservation() {
        Map<String, Integer> counts = new HashMap<>(Map.of(TASK, 7));
        ProgressPayload payload = new ProgressPayload(ProgressState.EMPTY, counts, true);
        counts.clear();
        assertEquals(7, payload.taskCounts().get(TASK));
        assertThrows(UnsupportedOperationException.class, () -> payload.taskCounts().clear());
        assertThrows(IllegalArgumentException.class,
                () -> new ProgressPayload(ProgressState.EMPTY, Map.of("8000000000000001", 1), true));
        assertThrows(IllegalArgumentException.class,
                () -> new ProgressPayload(ProgressState.EMPTY, Map.of(TASK, 1), false));
    }

    @Test void rejectsConflictingAndUnavailableClaimStates() {
        assertThrows(IllegalArgumentException.class, () -> new ProgressPayload(ProgressState.EMPTY, Map.of(), true, Set.of(QUEST), Set.of(QUEST)));
        assertThrows(IllegalArgumentException.class, () -> new ProgressPayload(ProgressState.EMPTY, Map.of(), false, Set.of(QUEST), Set.of()));
        assertThrows(IllegalArgumentException.class, () -> new ProgressPayload(ProgressState.EMPTY, Map.of(), true, Set.of("8000000000000001"), Set.of()));
    }
}
