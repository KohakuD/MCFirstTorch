package ch.minenox.firsttorch.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class FirstTorchKeyMappings {
    public static final KeyMapping OPEN_GUIDE = new KeyMapping(
            "key.firsttorch.open_guide", InputConstants.Type.KEYSYM,
            InputConstants.KEY_GRAVE, "key.categories.firsttorch.learning");

    private FirstTorchKeyMappings() {}

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_GUIDE);
    }
}