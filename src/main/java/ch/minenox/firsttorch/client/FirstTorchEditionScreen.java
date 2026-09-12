package ch.minenox.firsttorch.client;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Returning-learner choice, also reachable from the browser header. */
final class FirstTorchEditionScreen extends Screen {
    private final Screen parent;
    private final Consumer<String> choose;
    private boolean versions;
    private FirstTorchLayout.Rect panel;
    private List<FormattedCharSequence> lines = List.of();

    FirstTorchEditionScreen(Screen parent, Consumer<String> choose) {
        super(Component.translatable("screen.firsttorch.edition.title"));
        this.parent = parent;
        this.choose = choose;
    }

    private Component body() {
        return Component.translatable(versions ? "screen.firsttorch.edition.explanation" : "screen.firsttorch.edition.question");
    }

    @Override protected void init() {
        int w = Math.min(360, width - 24);
        lines = font.split(body(), w - 28);
        int rows = versions ? FirstTorchEditionView.baselines().size() + 1 : 2;
        int h = 48 + lines.size() * 10 + rows * 24;
        panel = new FirstTorchLayout.Rect((width - w) / 2, (height - h) / 2, w, h);
        int y = panel.y() + 36 + lines.size() * 10;
        if (versions) {
            for (String baseline : FirstTorchEditionView.baselines()) {
                addChoice(y, Component.literal(baseline), () -> choose.accept(baseline));
                y += 24;
            }
            addChoice(y, Component.translatable("screen.firsttorch.edition.all"), () -> choose.accept(null));
        } else {
            addChoice(y, Component.translatable("gui.yes"), () -> { versions = true; rebuildWidgets(); });
            addChoice(y + 24, Component.translatable("screen.firsttorch.edition.no"), () -> choose.accept(null));
        }
        if (!children().isEmpty()) setInitialFocus(children().getFirst());
    }

    private void addChoice(int y, Component label, Runnable action) {
        addRenderableWidget(new FirstTorchButton(panel.x() + 14, y, panel.width() - 28, 18,
                label, ignored -> action.run(), ignored -> label.copy(), null, FirstTorchButton.Kind.NAVIGATION, false));
    }

    @Override public Component getNarrationMessage() { return getTitle().copy().append(". ").append(body()); }

    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xE0101214);
        FirstTorchTheme.frame(graphics, panel, false);
        graphics.textRenderer().acceptScrollingWithDefaultCenter(getTitle(), panel.x() + 14, panel.right() - 14,
                panel.y() + 12, panel.y() + 23);
        int y = panel.y() + 31;
        for (var line : lines) {
            graphics.text(font, line, panel.x() + 14, y, FirstTorchTheme.TEXT, false);
            y += 10;
        }
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override public void onClose() { minecraft.setScreenAndShow(parent); }
}
