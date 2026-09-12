package ch.minenox.firsttorch.guide.progress;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

/** Versioned, world-owned progress; unknown historical guide IDs remain intact. */
public final class WorldProgressData extends SavedData {
    private static final int VERSION = 1;
    private static final int MAX_PLAYERS = 10_000;
    private static final int MAX_TASKS = 65_536;
    private static final int MAX_QUESTS = 16_384;
    private static final Codec<String> ID_CODEC = Codec.STRING.validate(id ->
            validId(id) ? DataResult.success(id) : DataResult.error(() -> "Invalid progress ID: " + id));
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(value -> {
        try {
            UUID uuid = UUID.fromString(value);
            return uuid.toString().equals(value) ? DataResult.success(uuid)
                    : DataResult.error(() -> "Noncanonical player UUID");
        } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Invalid player UUID");
        }
    }, UUID::toString);
    private static final Codec<ProgressState> STATE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            idSetCodec(MAX_TASKS).fieldOf("completed_tasks").forGetter(ProgressState::completedTaskIds),
            idSetCodec(MAX_QUESTS).fieldOf("completed_quests").forGetter(ProgressState::completedQuestIds)
    ).apply(instance, ProgressState::new));
    private static final Codec<PlayerProgress> PLAYER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("player").forGetter(PlayerProgress::player),
            STATE_CODEC.fieldOf("progress").forGetter(PlayerProgress::progress)
    ).apply(instance, PlayerProgress::new));
    private static final Codec<List<PlayerProgress>> PLAYERS_CODEC = PLAYER_CODEC.listOf(0, MAX_PLAYERS)
            .validate(players -> {
                Set<UUID> ids = new HashSet<>();
                for (PlayerProgress player : players) {
                    if (!ids.add(player.player())) {
                        return DataResult.error(() -> "Duplicate player UUID");
                    }
                }
                return DataResult.success(players);
            });
    private static final Codec<WorldProgressData> RECORD_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.validate(version -> version == VERSION ? DataResult.success(version)
                    : DataResult.error(() -> "Unsupported progress version: " + version))
                    .fieldOf("version").forGetter(data -> VERSION),
            PLAYERS_CODEC.fieldOf("players").forGetter(WorldProgressData::entries)
    ).apply(instance, (version, players) -> new WorldProgressData(players)));
    // SavedDataStorage accepts resultOrPartial: never expose a partially decoded save.
    public static final Codec<WorldProgressData> CODEC = Codec.of(RECORD_CODEC, new Decoder<>() {
        @Override
        public <T> DataResult<Pair<WorldProgressData, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<WorldProgressData, T>> result = RECORD_CODEC.decode(ops, input);
            return result.error().isPresent()
                    ? DataResult.error(() -> result.error().orElseThrow().message()) : result;
        }
    });

    private final Map<UUID, ProgressState> players = new HashMap<>();

    public static WorldProgressData load(CompoundTag tag, HolderLookup.Provider registries) {
        return CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag encoded = (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
        return tag.merge(encoded);
    }
    public WorldProgressData() {}

    private WorldProgressData(List<PlayerProgress> entries) {
        entries.forEach(entry -> players.put(entry.player(), entry.progress()));
    }

    public ProgressState get(UUID player) {
        return players.getOrDefault(Objects.requireNonNull(player), ProgressState.EMPTY);
    }

    public void put(UUID player, ProgressState progress) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(progress);
        if (get(player).equals(progress)) {
            return;
        }
        requireValidIds(progress.completedTaskIds(), MAX_TASKS);
        requireValidIds(progress.completedQuestIds(), MAX_QUESTS);
        if (!players.containsKey(player) && players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("Too many players in world progress");
        }
        players.put(player, progress);
        setDirty();
    }

    private List<PlayerProgress> entries() {
        return players.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(entry -> new PlayerProgress(entry.getKey(), entry.getValue())).toList();
    }

    private static Codec<Set<String>> idSetCodec(int maximum) {
        return ID_CODEC.listOf(0, maximum).comapFlatMap(ids -> {
            Set<String> unique = Set.copyOf(ids);
            return unique.size() == ids.size() ? DataResult.success(unique)
                    : DataResult.error(() -> "Duplicate progress ID");
        }, ids -> ids.stream().sorted().toList());
    }

    private static boolean validId(String id) {
        return id.matches("[0-7][0-9A-F]{15}");
    }

    private static void requireValidIds(Set<String> ids, int maximum) {
        if (ids.size() > maximum || ids.stream().anyMatch(id -> !validId(id))) {
            throw new IllegalStateException("Invalid or excessive progress IDs");
        }
    }

    private record PlayerProgress(UUID player, ProgressState progress) {}
}
