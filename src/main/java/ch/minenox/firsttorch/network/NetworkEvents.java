package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.guide.server.ServerWelcomeService;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = FirstTorch.MOD_ID)
public final class NetworkEvents {
    private NetworkEvents() {
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(GuideSnapshotPayload.NETWORK_VERSION)
                .playToClient(GuideSnapshotPayload.TYPE, GuideSnapshotPayload.STREAM_CODEC)
                .playToClient(ProgressPayload.TYPE, ProgressPayload.STREAM_CODEC)
                .playToClient(WelcomePayload.TYPE, WelcomePayload.STREAM_CODEC)
                .playToServer(WelcomeSeenPayload.TYPE, WelcomeSeenPayload.STREAM_CODEC,
                        (payload, context) -> ServerWelcomeService.INSTANCE.acknowledge(
                                (net.minecraft.server.level.ServerPlayer) context.player()));
    }
}
