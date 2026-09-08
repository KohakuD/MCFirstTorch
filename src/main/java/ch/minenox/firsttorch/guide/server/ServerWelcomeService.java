package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.network.WelcomePayload;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

/** Sends one welcome offer per connection and records only a server-authorized acknowledgement. */
public final class ServerWelcomeService {
    public static final ServerWelcomeService INSTANCE = new ServerWelcomeService();
    private final Map<ServerPlayer, Boolean> offeredThisSession = new WeakHashMap<>();

    private ServerWelcomeService() {}

    public void offer(ServerPlayer player) {
        var snapshot = ServerGuideRepository.INSTANCE.snapshot();
        if (snapshot.guides().isEmpty() || offeredThisSession.containsKey(player)) return;
        var server = player.level().getServer();
        var welcomes = ServerWelcomeRepository.get(server);
        if (welcomes.isAcknowledged(player.getUUID())) return;
        var progress = ServerProgressRepository.get(server).get(player.getUUID());
        if (hasGenuinelyStarted(progress.completedQuestIds(), progress.completedTaskIds(), snapshot.guides().stream()
                .flatMap(guide -> guide.chapters().stream()).flatMap(chapter -> chapter.quests().stream())
                .flatMap(quest -> quest.tasks().stream()).filter(task -> task.type() == TaskDefinition.Type.MANUAL)
                .map(TaskDefinition::id).collect(java.util.stream.Collectors.toUnmodifiableSet()))) {
            welcomes.acknowledge(player.getUUID());
            return;
        }
        PacketDistributor.sendToPlayer(player, WelcomePayload.INSTANCE);
        offeredThisSession.put(player, true);
    }

    public void acknowledge(ServerPlayer player) {
        if (!offeredThisSession.containsKey(player)) return;
        try {
            ServerWelcomeRepository.get(player.level().getServer()).acknowledge(player.getUUID());
        } catch (IllegalStateException ignored) {
            // The repository already logged an unreadable save; never let a client packet tear down its handler.
        }
    }

    static boolean hasGenuinelyStarted(Set<String> completedQuests, Set<String> completedTasks, Set<String> manualTaskIds) {
        return !completedQuests.isEmpty() || completedTasks.stream().anyMatch(manualTaskIds::contains);
    }
}
