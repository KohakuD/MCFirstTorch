package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** Modal, scrollable index of the reference chapters currently visible to the learner. */
final class FirstTorchReferenceScreen extends Screen {
    private static final int ROW_STEP = 24;

    private final Screen parent;
    private final List<ChapterDefinition> chapters;
    private final Consumer<String> selectChapter;
    private int firstRow;
    private int rowCapacity = 1;
    private Rect dialog = new Rect(0, 0, 1, 1);
    private Rect list = new Rect(0, 0, 1, 1);

    FirstTorchReferenceScreen(Screen parent, List<ChapterDefinition> chapters, Consumer<String> selectChapter) {
        super(Component.translatable("screen.firsttorch.reference.index"));
        this.parent = parent;
        this.chapters = List.copyOf(chapters);
        this.selectChapter = selectChapter;
    }

    @Override
    protected void init() {
        super.init();
        int dialogWidth = Math.max(1, Math.min(340, width - 24));
        int dialogHeight = Math.max(1, Math.min(300, height - 24));
        dialog = new Rect((width - dialogWidth) / 2, (height - dialogHeight) / 2, dialogWidth, dialogHeight);

        int listTop = dialog.y() + 29;
        int closeY = dialog.bottom() - 29;
        int listHeight = Math.max(0, closeY - listTop - 6);
        list = new Rect(dialog.x() + 12, listTop, Math.max(1, dialog.width() - 24), listHeight);
        rowCapacity = Math.max(1, list.height() / ROW_STEP);
        firstRow = ChapterScroll.clamp(firstRow, chapters.size(), rowCapacity);

        int rowHeight = Math.max(12, Math.min(21, list.height() / rowCapacity - 3));
        int end = Math.min(chapters.size(), firstRow + rowCapacity);
        for (int index = firstRow; index < end; index++) {
            ChapterDefinition chapter = chapters.get(index);
            Component title = Component.translatable(chapter.titleKey());
            FirstTorchButton row = new FirstTorchButton(list.x(), list.y() + (index - firstRow) * ROW_STEP,
                    list.width() - (chapters.size() > rowCapacity ? 8 : 0), rowHeight, title, ignored -> select(chapter),
                    ignored -> title.copy().append(". ").append(Component.translatable("screen.firsttorch.chapter.scroll")), null,
                    FirstTorchButton.Kind.CARD, false);
            row.preview(QuestIcons.resolveItem(chapter.iconItemId()), false, false);
            addRenderableWidget(row);
            if (index == firstRow) setInitialFocus(row);
        }

        Component close = Component.translatable("gui.back");
        addRenderableWidget(new FirstTorchButton(dialog.x() + 12, closeY, Math.max(1, dialog.width() - 24),
                Math.max(12, Math.min(18, dialog.bottom() - closeY - 11)), close,
                ignored -> closeToParent(), ignored -> close.copy(), null, FirstTorchButton.Kind.NAVIGATION, false));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        parent.render(graphics, -1, -1, partialTick);
        graphics.flush();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        graphics.fill(0, 0, width, height, 0x98000000);
        FirstTorchTheme.frame(graphics, dialog, false);
        if (chapters.size() > rowCapacity) {
            int thumbTop = ChapterScroll.thumbTop(list.y(), list.height(), chapters.size(), rowCapacity, firstRow);
            int thumbHeight = ChapterScroll.thumbHeight(list.height(), chapters.size(), rowCapacity);
            graphics.fill(list.right() - 4, list.y(), list.right() - 1, list.bottom(), FirstTorchTheme.BACKGROUND);
            graphics.fill(list.right() - 4, thumbTop, list.right() - 1, thumbTop + thumbHeight, FirstTorchTheme.AMBER);
        }
        FirstTorchText text = new FirstTorchText(graphics);
        text.acceptScrollingWithDefaultCenter(Component.translatable("screen.firsttorch.reference.index")
                        .copy().withStyle(style -> style.withColor(FirstTorchTheme.GOLD)),
                dialog.x() + 12, dialog.right() - 12, dialog.y() + 11, dialog.y() + 21);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.flush();
        graphics.pose().popPose();
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (x >= list.x() && x < list.right() && y >= list.y() && y < list.bottom()) {
            scrollTo(firstRow - (int) Math.signum(scrollY));
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int target = switch (keyCode) {
            case GLFW.GLFW_KEY_PAGE_UP -> firstRow - rowCapacity;
            case GLFW.GLFW_KEY_PAGE_DOWN -> firstRow + rowCapacity;
            case GLFW.GLFW_KEY_HOME -> 0;
            case GLFW.GLFW_KEY_END -> chapters.size();
            default -> Integer.MIN_VALUE;
        };
        if (target != Integer.MIN_VALUE) {
            scrollTo(target);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        closeToParent();
    }

    private void scrollTo(int target) {
        int clamped = ChapterScroll.clamp(target, chapters.size(), rowCapacity);
        if (clamped == firstRow) return;
        firstRow = clamped;
        rebuildWidgets();
    }

    private void select(ChapterDefinition chapter) {
        selectChapter.accept(chapter.id());
        closeToParent();
    }

    private void closeToParent() {
        minecraft.setScreen(parent);
    }
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // This screen draws its own background before its content and native widgets.
    }

}
