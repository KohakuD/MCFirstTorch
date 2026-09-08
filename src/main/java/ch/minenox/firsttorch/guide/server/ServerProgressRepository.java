package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.WorldProgressData;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;

/** Uses the server-global world save, never the client cache or a dimension-local save. */
public final class ServerProgressRepository {
    private static final SavedDataType<WorldProgressData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(FirstTorch.MOD_ID, "progress"),
            WorldProgressData::new, WorldProgressData.CODEC);
    private static final Map<MinecraftServer, Boolean> BLOCKED = new WeakHashMap<>();

    private ServerProgressRepository() {}

    public static WorldProgressData get(MinecraftServer server) {
        if (BLOCKED.containsKey(server)) throw new IllegalStateException("First Torch progress storage is unavailable");
        var storage = server.getDataStorage();
        WorldProgressData data = storage.get(TYPE);
        if (data != null) return data;
        // Vanilla returns null for BOTH missing and unreadable saves. Never replace an existing failed save.
        var path = TYPE.id().withSuffix(".dat").resolveAgainst(server.getWorldPath(LevelResource.DATA));
        if (!Files.notExists(path)) {
            BLOCKED.put(server, true);
            LogUtils.getLogger().error("First Torch progress cannot be loaded; leaving {} untouched. Restore or repair it before restarting.", path);
            throw new IllegalStateException("Existing First Torch progress could not be loaded");
        }
        data = new WorldProgressData();
        storage.set(TYPE, data);
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
