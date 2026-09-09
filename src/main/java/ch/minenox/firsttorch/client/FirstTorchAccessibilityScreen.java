package ch.minenox.firsttorch.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;

final class FirstTorchAccessibilityScreen extends AccessibilityOptionsScreen {
    FirstTorchAccessibilityScreen(Screen parent, Options options) { super(parent, options); }

    @Override protected void addOptions() {
        this.list.addSmall(new OptionInstance<?>[]{OptionInstance.createBoolean(
                "options.firsttorch.chapterFireworks", FirstTorchClientConfig.CHAPTER_FIREWORKS.get(), enabled -> {
                    FirstTorchClientConfig.CHAPTER_FIREWORKS.set(enabled);
                    FirstTorchClientConfig.CHAPTER_FIREWORKS.save();
                }), OptionInstance.createBoolean(
                "options.firsttorch.enlargedView", FirstTorchClientConfig.ENLARGED_VIEW.get(), enabled -> {
                    FirstTorchClientConfig.ENLARGED_VIEW.set(enabled);
                    FirstTorchClientConfig.ENLARGED_VIEW.save();
                })});
        this.list.addSmall(new OptionInstance<?>[]{OptionInstance.createBoolean(
                "options.firsttorch.quietSurfaces", FirstTorchClientConfig.QUIET_SURFACES.get(), enabled -> {
                    FirstTorchClientConfig.QUIET_SURFACES.set(enabled);
                    FirstTorchClientConfig.QUIET_SURFACES.save();
                })});
        super.addOptions();
    }
}
