package ch.minenox.firsttorch.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** A small, dismissible first contact; no inventory item or quest completion is granted. */
final class FirstTorchWelcomeScreen extends Screen {
    private FirstTorchLayout.Rect panel;
    private List<FormattedCharSequence> lines = List.of();

    FirstTorchWelcomeScreen() { super(Component.translatable("screen.firsttorch.welcome.title")); }

    private Component body() {
        return Component.translatable("screen.firsttorch.welcome.body");
    }

    @Override protected void init() {
        int panelWidth = Math.min(340, width - 24);
        lines = font.split(body(), panelWidth - 28);
        int panelHeight = 69 + lines.size() * 10;
        panel = new FirstTorchLayout.Rect((width - panelWidth) / 2, (height - panelHeight) / 2, panelWidth, panelHeight);
        int buttonWidth = (panelWidth - 34) / 2;
        var open = Component.translatable("screen.firsttorch.welcome.open");
        var later = Component.translatable("screen.firsttorch.welcome.later");
        var primary = addRenderableWidget(new FirstTorchButton(panel.x() + 14, panel.bottom() - 32, buttonWidth, 18,
                open, ignored -> {
                    FirstTorchWelcome.acknowledge();
                    minecraft.setScreen(new FirstTorchScreen(null));
                }, ignored -> open.copy(), null, FirstTorchButton.Kind.FOOTER, false));
        addRenderableWidget(new FirstTorchButton(panel.x() + 20 + buttonWidth, panel.bottom() - 32, buttonWidth, 18,
                later, ignored -> onClose(), ignored -> later.copy(), null, FirstTorchButton.Kind.NAVIGATION, false));
        setInitialFocus(primary);
    }

    @Override public Component getNarrationMessage() {
        return getTitle().copy().append(". ").append(body());
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xA0000000);
        FirstTorchTheme.frame(graphics, panel, false);
        new FirstTorchText(graphics).acceptScrollingWithDefaultCenter(getTitle().copy().withStyle(style -> style.withColor(FirstTorchTheme.GOLD)),
                panel.x() + 14, panel.right() - 14, panel.y() + 12, panel.y() + 23);
        int y = panel.y() + 31;
        for (var line : lines) {
            graphics.drawString(font, line, panel.x() + 14, y, FirstTorchTheme.TEXT, false);
            y += 10;
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override public void onClose() {
        FirstTorchWelcome.acknowledge();
        minecraft.setScreen(null);
    }
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // This screen draws its own background before its content and native widgets.
    }

}
