package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.client.ClientPayloadHandlers;
import ch.minenox.firsttorch.guide.server.ServerWelcomeService;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = FirstTorch.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class NetworkEvents {
    private NetworkEvents() {}

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // The registrar defaults to MAIN; client classes are resolved only when a client handles a packet.
        event.registrar(GuideSnapshotPayload.NETWORK_VERSION)
                .playToClient(GuideSnapshotPayload.TYPE, GuideSnapshotPayload.STREAM_CODEC, (payload, context) -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) ClientPayloadHandlers.installGuide(payload);
                })
                .playToClient(ProgressPayload.TYPE, ProgressPayload.STREAM_CODEC, (payload, context) -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) ClientPayloadHandlers.installProgress(payload);
                })
                .playToClient(WelcomePayload.TYPE, WelcomePayload.STREAM_CODEC, (payload, context) -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) ClientPayloadHandlers.requestWelcome();
                })
                .playToServer(WelcomeSeenPayload.TYPE, WelcomeSeenPayload.STREAM_CODEC,
                        (payload, context) -> ServerWelcomeService.INSTANCE.acknowledge(
                                (net.minecraft.server.level.ServerPlayer) context.player()));
    }
}