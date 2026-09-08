package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.guide.progress.WorldWelcomeData;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;

/** Accesses acknowledgement data separately from quest progress to keep both schemas independently recoverable. */
public final class ServerWelcomeRepository {
    private static final SavedDataType<WorldWelcomeData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(FirstTorch.MOD_ID, "welcome"), WorldWelcomeData::new, WorldWelcomeData.CODEC);
    private static final Map<MinecraftServer, Boolean> BLOCKED = new WeakHashMap<>();

    private ServerWelcomeRepository() {}

    public static WorldWelcomeData get(MinecraftServer server) {
        if (BLOCKED.containsKey(server)) throw new IllegalStateException("First Torch welcome storage is unavailable");
        var storage = server.getDataStorage();
        WorldWelcomeData data = storage.get(TYPE);
        if (data != null) return data;
        var path = TYPE.id().withSuffix(".dat").resolveAgainst(server.getWorldPath(LevelResource.DATA));
        if (!Files.notExists(path)) {
            BLOCKED.put(server, true);
            LogUtils.getLogger().error("First Torch welcome acknowledgements cannot be loaded; leaving {} untouched. Restore or repair it before restarting.", path);
            throw new IllegalStateException("Existing First Torch welcome acknowledgements could not be loaded");
        }
        data = new WorldWelcomeData();
        storage.set(TYPE, data);
        return data;
    }
}
