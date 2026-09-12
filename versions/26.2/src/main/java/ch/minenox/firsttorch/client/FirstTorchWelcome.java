package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.network.WelcomeSeenPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/** Waits for actual play, never replacing loading, inventory or another mod's screen. */
final class FirstTorchWelcome {
    private static final WelcomePromptState STATE = new WelcomePromptState();

    static void request() { STATE.request(); }
    static void clear() { STATE.clear(); }

    static void tick(Minecraft minecraft) {
        if (!STATE.pending()) return;
        var progress = ClientProgressCache.snapshot();
        boolean ready = minecraft.player != null && minecraft.level != null && minecraft.player.isAlive()
                && progress != null && progress.available() && !ClientGuideCache.snapshot().guides().isEmpty();
        if (STATE.ready(ready, minecraft.gui.screen() == null, minecraft.player == null ? 0 : minecraft.player.tickCount)) {
            minecraft.setScreenAndShow(new FirstTorchWelcomeScreen());
        }
    }

    static void acknowledge() {
        STATE.handled();
        if (Minecraft.getInstance().getConnection() != null) {
            ClientPacketDistributor.sendToServer(new WelcomeSeenPayload());
        }
    }
}
