package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.FirstTorch;
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
                .playToClient(GuideSnapshotPayload.TYPE, GuideSnapshotPayload.STREAM_CODEC);
    }
}
