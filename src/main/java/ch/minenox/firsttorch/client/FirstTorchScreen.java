package ch.minenox.firsttorch.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class FirstTorchScreen extends Screen {
    private static final int CONTENT_WIDTH = 280;

    private final Screen parent;

    public FirstTorchScreen(Screen parent) {
        super(Component.translatable("screen.firsttorch.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int left = this.width / 2 - CONTENT_WIDTH / 2;
        int top = Math.max(32, this.height / 2 - 60);

        this.addRenderableWidget(new StringWidget(
                left,
                top,
                CONTENT_WIDTH,
                20,
                this.title,
                this.font));

        MultiLineTextWidget notice = new MultiLineTextWidget(
                left,
                top + 34,
                Component.translatable("screen.firsttorch.alpha_notice"),
                this.font)
                .setMaxWidth(CONTENT_WIDTH)
                .setMaxRows(3)
                .setCentered(true);
        this.addRenderableWidget(notice);

        this.addRenderableWidget(Button.builder(
                        Component.translatable("screen.firsttorch.close"),
                        button -> this.onClose())
                .bounds(this.width / 2 - 75, top + 82, 150, 20)
                .build());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(this.parent);
    }
}
