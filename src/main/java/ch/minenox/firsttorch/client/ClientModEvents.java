package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.network.GuideSnapshotPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(modid = FirstTorch.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        FirstTorchKeyMappings.register(event);
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(GuideSnapshotPayload.TYPE, (payload, context) -> ClientGuideCache.install(payload.snapshot()));
    }
}
