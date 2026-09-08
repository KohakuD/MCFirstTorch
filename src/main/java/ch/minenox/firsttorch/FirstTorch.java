package ch.minenox.firsttorch;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(FirstTorch.MOD_ID)
public final class FirstTorch {
    public static final String MOD_ID = "firsttorch";

    public FirstTorch(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT,
                ch.minenox.firsttorch.client.FirstTorchClientConfig.SPEC);
    }
}
