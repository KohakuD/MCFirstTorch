package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.WorldProgressData;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.DimensionDataStorage;

/** Uses the server-global world save, never the client cache or a dimension-local save. */
public final class ServerProgressRepository {
    private static final String FILE_ID = "firsttorch_progress";
    private static final SavedData.Factory<WorldProgressData> FACTORY = new SavedData.Factory<>(
            WorldProgressData::new, WorldProgressData::load);
    private static final Map<DimensionDataStorage, Boolean> BLOCKED = new WeakHashMap<>();

    private ServerProgressRepository() {}

    public static WorldProgressData get(MinecraftServer server) {
        return get(server.overworld().getDataStorage(), server.getWorldPath(LevelResource.ROOT).resolve("data"));
    }

    static WorldProgressData get(DimensionDataStorage storage, Path dataDirectory) {
        if (BLOCKED.containsKey(storage)) throw new IllegalStateException("First Torch progress storage is unavailable");
        WorldProgressData data = storage.get(FACTORY, FILE_ID);
        if (data != null) return data;
        // Vanilla returns null for BOTH missing and unreadable saves. Never replace an existing failed save.
        var path = dataDirectory.resolve(FILE_ID + ".dat");
        if (!Files.notExists(path)) {
            BLOCKED.put(storage, true);
            LogUtils.getLogger().error("First Torch progress cannot be loaded; leaving {} untouched. Restore or repair it before restarting.", path);
            throw new IllegalStateException("Existing First Torch progress could not be loaded");
        }
        data = new WorldProgressData();
        storage.set(FILE_ID, data);
        return data;
    }

    public static void remember(WorldProgressData data, UUID player, ProgressState evaluated) {
        // A temporarily removed datapack must not erase historical completions from the save.
        ProgressState previous = data.get(player);
        var tasks = new HashSet<>(previous.completedTaskIds());
        var quests = new HashSet<>(previous.completedQuestIds());
        tasks.addAll(evaluated.completedTaskIds());
        quests.addAll(evaluated.completedQuestIds());
        data.put(player, new ProgressState(tasks, quests));
    }
}
