package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.network.GuideSnapshotPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = FirstTorch.MOD_ID)
public final class ServerGuideEvents {
    private static final Identifier RELOAD_LISTENER_ID =
            Identifier.fromNamespaceAndPath(FirstTorch.MOD_ID, "guides");

    private ServerGuideEvents() {
    }

    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(RELOAD_LISTENER_ID, new GuideReloadListener());
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
