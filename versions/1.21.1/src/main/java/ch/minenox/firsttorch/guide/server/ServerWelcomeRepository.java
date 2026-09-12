package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.progress.WorldWelcomeData;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.DimensionDataStorage;

/** Accesses acknowledgement data separately from quest progress to keep both schemas independently recoverable. */
public final class ServerWelcomeRepository {
    private static final String FILE_ID = "firsttorch_welcome";
    private static final SavedData.Factory<WorldWelcomeData> FACTORY = new SavedData.Factory<>(
            WorldWelcomeData::new, WorldWelcomeData::load);
    private static final Map<DimensionDataStorage, Boolean> BLOCKED = new WeakHashMap<>();

    private ServerWelcomeRepository() {}

    public static WorldWelcomeData get(MinecraftServer server) {
        return get(server.overworld().getDataStorage(), server.getWorldPath(LevelResource.ROOT).resolve("data"));
    }

    static WorldWelcomeData get(DimensionDataStorage storage, Path dataDirectory) {
        if (BLOCKED.containsKey(storage)) throw new IllegalStateException("First Torch welcome storage is unavailable");
        WorldWelcomeData data = storage.get(FACTORY, FILE_ID);
        if (data != null) return data;
        var path = dataDirectory.resolve(FILE_ID + ".dat");
        if (!Files.notExists(path)) {
            BLOCKED.put(storage, true);
            LogUtils.getLogger().error("First Torch welcome acknowledgements cannot be loaded; leaving {} untouched. Restore or repair it before restarting.", path);
            throw new IllegalStateException("Existing First Torch welcome acknowledgements could not be loaded");
        }
        data = new WorldWelcomeData();
        storage.set(FILE_ID, data);
        return data;
    }
}
