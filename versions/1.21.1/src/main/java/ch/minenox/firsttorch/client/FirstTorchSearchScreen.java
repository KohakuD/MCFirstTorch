package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import java.util.function.Predicate;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** A small modal search dialog that leaves filtering and selection to its browser parent. */
final class FirstTorchSearchScreen extends Screen {
    private final Screen parent;
    private final Predicate<String> search;
    private String query;
    private EditBox input;
    private boolean noResults;

    FirstTorchSearchScreen(Screen parent, String initialQuery, Predicate<String> search) {
        super(Component.translatable("screen.firsttorch.search"));
        this.parent = parent;
        this.query = initialQuery;
        this.search = search;
    }

    @Override
    protected void init() {
        int dialogWidth = Math.min(300, width - 24);
        int dialogHeight = 92;
        int x = (width - dialogWidth) / 2;
        int y = (height - dialogHeight) / 2;
        Component searchLabel = Component.translatable("screen.firsttorch.search");

        input = new EditBox(font, x + 12, y + 31, dialogWidth - 24, 16, searchLabel);
        input.setHint(searchLabel);
        input.setMaxLength(80);
        input.setValue(query);
        input.setResponder(value -> { query = value; noResults = false; });
        addRenderableWidget(input);

        int buttonWidth = (dialogWidth - 30) / 2;
        addRenderableWidget(new FirstTorchButton(x + 12, y + 61, buttonWidth, 18, searchLabel,
                ignored -> submit(), ignored -> searchLabel.copy(), null, FirstTorchButton.Kind.FOOTER, false));
        Component cancel = Component.translatable("gui.cancel");
        addRenderableWidget(new FirstTorchButton(x + 18 + buttonWidth, y + 61, buttonWidth, 18, cancel,
                ignored -> closeToParent(), ignored -> cancel.copy(), null, FirstTorchButton.Kind.NAVIGATION, false));
        setInitialFocus(input);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        parent.render(graphics, -1, -1, partialTick);
        graphics.flush();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        graphics.fill(0, 0, width, height, 0x98000000);
        int dialogWidth = Math.min(300, width - 24);
        Rect dialog = new Rect((width - dialogWidth) / 2, (height - 92) / 2, dialogWidth, 92);
        FirstTorchTheme.frame(graphics, dialog, false);
        FirstTorchText text = new FirstTorchText(graphics);
        text.acceptScrollingWithDefaultCenter(Component.translatable("screen.firsttorch.search")
                        .copy().withStyle(style -> style.withColor(FirstTorchTheme.GOLD)),
                dialog.x() + 12, dialog.right() - 12, dialog.y() + 11, dialog.y() + 21);
        if (noResults) {
            text.acceptScrollingWithDefaultCenter(Component.translatable("screen.firsttorch.search.none")
                            .copy().withStyle(style -> style.withColor(FirstTorchTheme.AMBER)),
                    dialog.x() + 12, dialog.right() - 12, dialog.y() + 49, dialog.y() + 59);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.flush();
        graphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (input.isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            submit();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        closeToParent();
    }

    @Override
    public Component getNarrationMessage() {
        return noResults ? super.getNarrationMessage().copy().append(". ")
                .append(Component.translatable("screen.firsttorch.search.none")) : super.getNarrationMessage();
    }

    private void submit() {
        noResults = !search.test(query);
        if (!noResults) {
            closeToParent();
            return;
        }
        setFocused(input);
        triggerImmediateNarration(false);
    }

    private void closeToParent() {
        minecraft.setScreen(parent);
    }
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // This screen draws its own background before its content and native widgets.
    }

}
