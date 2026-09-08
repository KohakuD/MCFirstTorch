package ch.minenox.firsttorch.guide.progress;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.saveddata.SavedData;

/** World-owned acknowledgement state for the native first-join welcome. */
public final class WorldWelcomeData extends SavedData {
    private static final int VERSION = 1;
    private static final int MAX_PLAYERS = 10_000;
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(value -> {
        try {
            UUID uuid = UUID.fromString(value);
            return uuid.toString().equals(value) ? DataResult.success(uuid)
                    : DataResult.error(() -> "Noncanonical player UUID");
        } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Invalid player UUID");
        }
    }, UUID::toString);
    private static final Codec<List<UUID>> PLAYERS_CODEC = UUID_CODEC.listOf(0, MAX_PLAYERS).validate(players -> {
        Set<UUID> unique = new HashSet<>(players);
        return unique.size() == players.size() ? DataResult.success(players)
                : DataResult.error(() -> "Duplicate player UUID");
    });
    private static final Codec<WorldWelcomeData> RECORD_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.validate(version -> version == VERSION ? DataResult.success(version)
                    : DataResult.error(() -> "Unsupported welcome version: " + version))
                    .fieldOf("version").forGetter(data -> VERSION),
            PLAYERS_CODEC.fieldOf("acknowledged_players").forGetter(WorldWelcomeData::entries)
    ).apply(instance, (version, players) -> new WorldWelcomeData(players)));
    // SavedDataStorage accepts resultOrPartial: a corrupt acknowledgement save must never become partial state.
    public static final Codec<WorldWelcomeData> CODEC = Codec.of(RECORD_CODEC, new Decoder<>() {
        @Override
        public <T> DataResult<Pair<WorldWelcomeData, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<WorldWelcomeData, T>> result = RECORD_CODEC.decode(ops, input);
            return result.error().isPresent() ? DataResult.error(() -> result.error().orElseThrow().message()) : result;
        }
    });

    private final Set<UUID> acknowledged = new HashSet<>();

    public WorldWelcomeData() {}

    private WorldWelcomeData(List<UUID> players) {
        acknowledged.addAll(players);
    }

    public boolean isAcknowledged(UUID player) {
        return acknowledged.contains(Objects.requireNonNull(player));
    }

    public void acknowledge(UUID player) {
        Objects.requireNonNull(player);
        if (acknowledged.contains(player)) return;
        if (acknowledged.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("Too many players in world welcome acknowledgements");
        }
        acknowledged.add(player);
        setDirty();
    }

    private List<UUID> entries() {
        return acknowledged.stream().sorted().toList();
    }
}
