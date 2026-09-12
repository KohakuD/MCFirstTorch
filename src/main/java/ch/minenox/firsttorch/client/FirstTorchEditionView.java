package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.edition.FirstTorchEditionHistory;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLPaths;

final class FirstTorchEditionView {
    // Owner-confirmed released editions, ordered by Minecraft history rather than upload date.
    private static final Set<String> PUBLISHED = Set.of("1.21.1", "26.1.2");
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    private FirstTorchEditionView() {}

    static String current() { return SharedConstants.getCurrentVersion().name(); }

    static List<String> baselines() {
        try { return FirstTorchEditionHistory.create().baselines(current(), PUBLISHED); }
        catch (IllegalArgumentException unsupported) { return List.of(); }
    }

    static String load() {
        var player = Minecraft.getInstance().player;
        if (player == null || baselines().isEmpty()) return null;
        try {
            return EditionViewPreferences.read(FMLPaths.CONFIGDIR.get().resolve("firsttorch-edition-view"),
                    player.getUUID(), current(), baselines());
        } catch (IOException failure) {
            LOGGER.warn("Cannot read First Torch edition preference", failure);
            return null;
        }
    }

    static void save(String baseline) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (baseline != null && !baselines().contains(baseline)) throw new IllegalArgumentException("Invalid baseline");
        try {
            EditionViewPreferences.write(FMLPaths.CONFIGDIR.get().resolve("firsttorch-edition-view"),
                    player.getUUID(), current(), baseline);
        } catch (IOException failure) {
            LOGGER.warn("Cannot save First Torch edition preference", failure);
        }
    }
}
