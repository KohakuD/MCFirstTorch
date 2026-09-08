package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.FirstTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = FirstTorch.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {
    private ClientGameEvents() {
    }

    @SubscribeEvent
    public static void onPauseMenu(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof PauseScreen pause) || !pause.showsPauseMenu()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        Component label = Component.translatable("screen.firsttorch.pause_entry");
        // Init normally clears widgets; guard duplicate delivery without changing any other controls.
        if (event.getListenersList().stream().anyMatch(widget -> widget instanceof FirstTorchButton button
                && button.getMessage().equals(label))) return;
        var occupied = event.getListenersList().stream().filter(AbstractWidget.class::isInstance)
                .map(AbstractWidget.class::cast).filter(widget -> widget.visible)
                .map(widget -> new FirstTorchLayout.Rect(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight())).toList();
        PauseGuideEntryLayout.find(pause.width, pause.height, occupied).ifPresent(bounds -> {
            var entry = new FirstTorchButton(bounds.x(), bounds.y(), bounds.width(), bounds.height(), label,
                    ignored -> minecraft.setScreenAndShow(new FirstTorchScreen(pause)),
                    ignored -> Component.translatable("screen.firsttorch.pause_entry.narration"),
                    null, FirstTorchButton.Kind.PAUSE_ENTRY, false);
            entry.preview(new ItemStack(Items.BOOK), false, false);
            event.addListener(entry);
        });
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        DevelopmentWindowController.applyIfRequested(minecraft);
        FirstTorchWelcome.tick(minecraft);
        while (FirstTorchKeyMappings.OPEN_GUIDE.consumeClick()) {
            if (minecraft.player != null && minecraft.screen == null) {
                minecraft.setScreen(new FirstTorchScreen(null));
            }
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        FirstTorchWelcome.clear();
        ClientGuideCache.clear();
        ClientProgressCache.clear();
    }
}
