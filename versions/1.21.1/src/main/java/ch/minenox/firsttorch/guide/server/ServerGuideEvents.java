package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.network.GuideSnapshotPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "firsttorch")
public final class ServerGuideEvents {
    private ServerGuideEvents() {
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new GuideReloadListener());
    }

    @SubscribeEvent
    public static void syncGuides(OnDatapackSyncEvent event) {
        GuideSnapshotPayload payload = new GuideSnapshotPayload(ServerGuideRepository.INSTANCE.snapshot());
        event.getRelevantPlayers().forEach(player -> {
            PacketDistributor.sendToPlayer(player, payload);
            ServerTaskEvents.sync(player, true);
            try {
                ServerWelcomeService.INSTANCE.offer(player);
            } catch (IllegalStateException ignored) {
                // A missing or unreadable progress/welcome save must not offer a stale first-join modal.
            }
        });
    }
}
