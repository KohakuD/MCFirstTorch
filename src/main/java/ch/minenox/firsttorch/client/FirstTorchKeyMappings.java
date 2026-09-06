package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.FirstTorch;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class FirstTorchKeyMappings {
    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(FirstTorch.MOD_ID, "learning"));

    public static final KeyMapping OPEN_GUIDE = new KeyMapping(
            "key.firsttorch.open_guide",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_O,
            CATEGORY);

    private FirstTorchKeyMappings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(OPEN_GUIDE);
    }
}
