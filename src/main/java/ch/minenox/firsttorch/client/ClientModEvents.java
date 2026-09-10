package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.FirstTorch;
import ch.minenox.firsttorch.network.GuideSnapshotPayload;
import ch.minenox.firsttorch.network.ProgressPayload;
import ch.minenox.firsttorch.network.WelcomePayload;
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
    public static void registerSceneReload(net.neoforged.neoforge.client.event.AddClientReloadListenersEvent event) {
        event.addListener(net.minecraft.resources.Identifier.fromNamespaceAndPath(FirstTorch.MOD_ID, "live_scenes"),
                (net.minecraft.server.packs.resources.ResourceManagerReloadListener) manager -> LiveSceneRenderer.clear());
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        FirstTorchKeyMappings.register(event);
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(GuideSnapshotPayload.TYPE, (payload, context) -> {
            ClientProgressCache.clear();
            ClientGuideCache.install(payload.snapshot());
        });
        event.register(ProgressPayload.TYPE, (payload, context) -> ClientProgressCache.install(payload));
        event.register(WelcomePayload.TYPE, (payload, context) -> FirstTorchWelcome.request());
    }
}
