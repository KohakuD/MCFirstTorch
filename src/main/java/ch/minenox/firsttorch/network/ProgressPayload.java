package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import io.netty.handler.codec.DecoderException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Immutable, clientbound-only observation of this player's server-owned progress. */
public record ProgressPayload(ProgressState state, Map<String, Integer> taskCounts, boolean available,
        Set<String> claimedQuestIds, Set<String> pendingQuestIds)
        implements CustomPacketPayload {
    public static final int MAX_TASKS = 65_536;
    public static final int MAX_QUESTS = 16_384;
    private static final int MAX_BYTES = 1_000_000;
    public static final ProgressPayload UNAVAILABLE = new ProgressPayload(ProgressState.EMPTY, Map.of(), false);
    public static final Type<ProgressPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(FirstTorch.MOD_ID, "progress"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ProgressPayload> STREAM_CODEC =
            StreamCodec.of(ProgressPayload::encode, ProgressPayload::decode);

    public ProgressPayload(ProgressState state, Map<String, Integer> taskCounts, boolean available) {
        this(state, taskCounts, available, Set.of(), Set.of());
    }

    public ProgressPayload {
        if (state == null || taskCounts == null) throw new IllegalArgumentException("Missing progress.");
        taskCounts = Map.copyOf(taskCounts);
        claimedQuestIds = Set.copyOf(claimedQuestIds);
        pendingQuestIds = Set.copyOf(pendingQuestIds);
        checkSize(claimedQuestIds.size(), MAX_QUESTS);
        checkSize(pendingQuestIds.size(), MAX_QUESTS);
        claimedQuestIds.forEach(ProgressPayload::checkId);
        pendingQuestIds.forEach(ProgressPayload::checkId);
        if (claimedQuestIds.stream().anyMatch(pendingQuestIds::contains)) throw new IllegalArgumentException("Conflicting claim states.");
        checkSize(state.completedTaskIds().size(), MAX_TASKS);
        checkSize(state.completedQuestIds().size(), MAX_QUESTS);
        checkSize(taskCounts.size(), MAX_TASKS);
        state.completedTaskIds().forEach(ProgressPayload::checkId);
        state.completedQuestIds().forEach(ProgressPayload::checkId);
        taskCounts.forEach((id, count) -> {
            checkId(id);
            if (count < 0 || count > 4096) throw new IllegalArgumentException("Invalid task count.");
        });
        if (!available && (!state.equals(ProgressState.EMPTY) || !taskCounts.isEmpty() || !claimedQuestIds.isEmpty() || !pendingQuestIds.isEmpty())) {
            throw new IllegalArgumentException("Unavailable progress must be empty.");
        }
        // Fixed-width IDs keep the conservative packet bound below the play payload limit.
        long bytes = 16L + 8L * (state.completedTaskIds().size() + state.completedQuestIds().size())
                + 10L * taskCounts.size() + 8L * (claimedQuestIds.size() + pendingQuestIds.size());
        if (bytes > MAX_BYTES) throw new IllegalArgumentException("Progress payload exceeds byte budget.");
    }

    public static void encode(FriendlyByteBuf buffer, ProgressPayload payload) {
        buffer.writeBoolean(payload.available());
        writeIds(buffer, payload.state().completedTaskIds());
        writeIds(buffer, payload.state().completedQuestIds());
        buffer.writeVarInt(payload.taskCounts().size());
        payload.taskCounts().forEach((id, count) -> {
            buffer.writeLong(Long.parseLong(id, 16));
            buffer.writeVarInt(count);
        });
        writeIds(buffer, payload.claimedQuestIds());
        writeIds(buffer, payload.pendingQuestIds());
    }

    public static ProgressPayload decode(FriendlyByteBuf buffer) {
        try {
            boolean available = buffer.readBoolean();
            Set<String> tasks = readIds(buffer, MAX_TASKS);
            Set<String> quests = readIds(buffer, MAX_QUESTS);
            int size = readSize(buffer, MAX_TASKS);
            Map<String, Integer> counts = new HashMap<>();
            for (int i = 0; i < size; i++) {
                String id = readId(buffer);
                int count = buffer.readVarInt();
                if (count < 0 || count > 4096 || counts.putIfAbsent(id, count) != null) {
                    throw new IllegalArgumentException("Invalid or duplicate task count.");
                }
            }
            Set<String> claimed = readIds(buffer, MAX_QUESTS);
            Set<String> pending = readIds(buffer, MAX_QUESTS);
            return new ProgressPayload(new ProgressState(tasks, quests), counts, available, claimed, pending);
        } catch (IllegalArgumentException exception) {
            throw new DecoderException("Invalid progress payload: " + exception.getMessage(), exception);
        }
    }

    private static void writeIds(FriendlyByteBuf buffer, Set<String> ids) {
        buffer.writeVarInt(ids.size());
        ids.forEach(id -> buffer.writeLong(Long.parseLong(id, 16)));
    }

    private static Set<String> readIds(FriendlyByteBuf buffer, int maximum) {
        int size = readSize(buffer, maximum);
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < size; i++) {
            if (!ids.add(readId(buffer))) throw new IllegalArgumentException("Duplicate completion ID.");
        }
        return ids;
    }

    private static String readId(FriendlyByteBuf buffer) {
        long id = buffer.readLong();
        if (id < 0) throw new IllegalArgumentException("Invalid stable ID.");
        return String.format(java.util.Locale.ROOT, "%016X", id);
    }

    private static int readSize(FriendlyByteBuf buffer, int maximum) {
        int size = buffer.readVarInt();
        checkSize(size, maximum);
        return size;
    }

    private static void checkSize(int size, int maximum) {
        if (size < 0 || size > maximum) throw new IllegalArgumentException("Progress collection exceeds limit.");
    }

    private static void checkId(String id) {
        if (id == null || !id.matches("[0-7][0-9A-F]{15}")) {
            throw new IllegalArgumentException("Invalid stable ID.");
        }
    }

    @Override
    public Type<ProgressPayload> type() { return TYPE; }
}
