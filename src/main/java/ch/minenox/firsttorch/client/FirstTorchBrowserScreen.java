package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import ch.minenox.firsttorch.client.FirstTorchLayout.ScreenLayout;
import ch.minenox.firsttorch.client.GuideBrowserViewModel.Selection;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Native, client-only presentation of the synchronised guide definitions. */
class FirstTorchBrowserScreen extends Screen {
    private final ItemStack brandTorch = new ItemStack(Items.TORCH);
    private final Screen parent;
    private FirstTorchViewport viewport = FirstTorchViewport.fit(320, 240);
    private GuideSnapshot observedSnapshot = GuideSnapshot.EMPTY;
    private Selection selection = Selection.EMPTY;
    private GuideBrowserViewModel viewModel = GuideBrowserViewModel.resolve(GuideSnapshot.EMPTY, Selection.EMPTY);
    private ScreenLayout layout = FirstTorchLayout.calculate(320, 240);
    private Map<String, Rect> questNodes = Map.of();
    private Boolean previewChoice;
    private EditBox searchBox;
    private String query = "";
    private boolean searchPending;
    private boolean searchEmpty;

    FirstTorchBrowserScreen(Screen parent) {
        super(Component.translatable("screen.firsttorch.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        boolean searching = searchBox != null && searchBox.isFocused();
        observedSnapshot = ClientGuideCache.snapshot();
        GuideSnapshot displayed = usesPreview() ? DesignPreview.snapshot() : observedSnapshot;
        if (usesPreview() && !DesignPreview.isPreview(selection.guideId())) {
            selection = new Selection("0D15000000000001", null, "2D15000000000004");
        }
        viewModel = GuideBrowserViewModel.resolve(displayed, selection);
        selection = viewModel.selection();
        viewport = FirstTorchViewport.fit(width, height);
        layout = FirstTorchLayout.calculate(viewport.width(), viewport.height());
        questNodes = FirstTorchLayout.questNodes(layout.questMap(), viewModel.quests());
        addHeaderControls();
        addGuideNavigation();
        addChapterButtons();
        addQuestButtons();
        if (searching) setFocused(searchBox);
    }

    @Override
    public void tick() {
        GuideSnapshot current = ClientGuideCache.snapshot();
        if (current != observedSnapshot) {
            rebuildWidgets();
        }
        if (searchPending) {
            searchPending = false;
            search();
        }
    }

    private boolean usesPreview() {
        if (previewChoice != null) return previewChoice;
        return observedSnapshot.guides().size() == 1
                && observedSnapshot.guides().getFirst().id().equals("0A13F17C00000001");
    }

    private boolean preview() {
        return viewModel.guide() != null && DesignPreview.isPreview(viewModel.guide().id());
    }

    private void togglePreview() {
        previewChoice = !preview();
        selection = Selection.EMPTY;
        query = "";
        searchEmpty = false;
        rebuildWidgets();
    }

    private void search() {
        searchEmpty = false;
        if (query.isBlank() || viewModel.guide() == null) return;
        String needle = query.toLowerCase(Locale.ROOT).strip();
        searchEmpty = true;
        search: for (ChapterDefinition chapter : viewModel.chapters()) {
            for (QuestDefinition quest : chapter.quests()) {
                if (Component.translatable(quest.titleKey()).getString().toLowerCase(Locale.ROOT).contains(needle)) {
                    selection = new Selection(selection.guideId(), chapter.id(), quest.id());
                    searchEmpty = false;
                    break search;
                }
            }
        }
        rebuildWidgets();
    }

    private void addHeaderControls() {
        Rect bar = layout.topBar();
        boolean compact = bar.width() < 600;
        int areaWidth = compact ? Math.min(148, bar.width() / 2) : 150;
        int x = bar.right() - areaWidth - 9;
        int y = compact ? bar.bottom() - 23 : bar.y() + 10;
        int searchWidth = compact ? areaWidth - 44 : areaWidth;
        Component search = Component.translatable("screen.firsttorch.search");
        searchBox = new EditBox(font, x, y, searchWidth, 16, search);
        searchBox.setHint(search);
        searchBox.setMaxLength(80);
        searchBox.setValue(query);
        searchBox.setResponder(value -> { query = value; searchPending = true; });
        addRenderableWidget(searchBox);
        Component access = Component.translatable("screen.firsttorch.accessibility");
        Component settings = Component.translatable("screen.firsttorch.settings");
        int controlsY = compact ? y : y + 22;
        int controlsX = compact ? x + searchWidth + 3 : x;
        int buttonWidth = compact ? 19 : 73;
        addRenderableWidget(button(controlsX, controlsY, buttonWidth, 16,
                compact ? Component.literal("A") : access,
                ignored -> minecraft.setScreenAndShow(new AccessibilityOptionsScreen(this, minecraft.options)),
                access, Tooltip.create(access), FirstTorchButton.Kind.NAVIGATION, false));
        addRenderableWidget(button(controlsX + buttonWidth + 4, controlsY, buttonWidth, 16,
                compact ? Component.literal("⚙") : settings, ignored -> togglePreview(),
                Component.translatable("screen.firsttorch.preview.toggle"),
                Tooltip.create(Component.translatable("screen.firsttorch.preview.toggle")),
                FirstTorchButton.Kind.NAVIGATION, preview()));
    }

    private ItemStack icon(String objectId) {
        if (!preview()) return new ItemStack(Items.BOOK);
        return new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(DesignPreview.itemId(objectId))));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, FirstTorchTheme.BACKGROUND);
        graphics.pose().pushMatrix();
        graphics.pose().translate(viewport.x(), viewport.y());
        graphics.pose().scale(viewport.scale(), viewport.scale());
        FirstTorchTheme.frame(graphics, layout.topBar(), false);
        FirstTorchTheme.frame(graphics, layout.chapters(), false);
        FirstTorchTheme.frame(graphics, layout.questMap(), false);
        FirstTorchTheme.frame(graphics, layout.details(), false);
        FirstTorchTheme.frame(graphics, layout.footer(), false);
        drawConnections(graphics);
        drawText(graphics);
        super.extractRenderState(graphics, (int) viewport.localX(mouseX), (int) viewport.localY(mouseY), partialTick);
        graphics.pose().popMatrix();
    }

    private MouseButtonEvent localEvent(MouseButtonEvent event) {
        return new MouseButtonEvent(viewport.localX(event.x()), viewport.localY(event.y()), event.buttonInfo());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return super.mouseClicked(localEvent(event), doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return super.mouseReleased(localEvent(event));
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        return super.mouseDragged(localEvent(event), dx / viewport.scale(), dy / viewport.scale());
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return super.mouseScrolled(viewport.localX(x), viewport.localY(y), scrollX, scrollY);
    }

    private FirstTorchButton button(int x, int y, int w, int h, Component message,
            FirstTorchButton.OnPress press, Component narration, Tooltip tooltip,
            FirstTorchButton.Kind kind, boolean selected) {
        return new FirstTorchButton(x, y, w, h, message, press,
                ignored -> narration.copy(), tooltip, kind, selected);
    }

    private void addGuideNavigation() {
        if (viewModel.guides().size() < 2) return;
        Rect bar = layout.topBar();
        Component previous = Component.translatable("screen.firsttorch.guide.previous");
        Component next = Component.translatable("screen.firsttorch.guide.next");
        addRenderableWidget(button(bar.centerX() - 86, bar.bottom() - 20, 20, 15,
                Component.literal("‹"), ignored -> switchGuide(-1), previous, Tooltip.create(previous),
                FirstTorchButton.Kind.NAVIGATION, false));
        addRenderableWidget(button(bar.centerX() + 66, bar.bottom() - 20, 20, 15,
                Component.literal("›"), ignored -> switchGuide(1), next, Tooltip.create(next),
                FirstTorchButton.Kind.NAVIGATION, false));
    }

    private void switchGuide(int delta) {
        int size = viewModel.guides().size();
        if (size == 0) return;
        selection = new Selection(viewModel.guides().get(Math.floorMod(viewModel.guideIndex() + delta, size)).id(), null, null);
        rebuildWidgets();
    }

    private void addChapterButtons() {
        if (viewModel.chapters().isEmpty()) return;
        Rect panel = layout.chapters();
        int cardHeight = Math.max(23, Math.min(42, panel.height() / 7));
        int visibleCount = Math.max(1, (panel.height() - 51) / (cardHeight + 5));
        int selectedIndex = Math.max(0, viewModel.chapters().indexOf(viewModel.chapter()));
        int pageStart = selectedIndex / visibleCount * visibleCount;
        int pageEnd = Math.min(viewModel.chapters().size(), pageStart + visibleCount);
        int y = panel.y() + 9;
        for (int index = pageStart; index < pageEnd; index++) {
            ChapterDefinition chapter = viewModel.chapters().get(index);
            Component label = Component.literal((index + 1) + "  ").append(Component.translatable(chapter.titleKey()));
            FirstTorchButton card = button(panel.x() + 7, y, panel.width() - 14, cardHeight, label,
                    ignored -> selectChapter(chapter.id()),
                    Component.translatable("screen.firsttorch.chapter.narration", Component.translatable(chapter.titleKey())),
                    Tooltip.create(Component.translatable(chapter.descriptionKey())),
                    FirstTorchButton.Kind.CARD, chapter.id().equals(selection.chapterId()));
            if (preview()) card.preview(icon(chapter.id()), false, false);
            addRenderableWidget(card);
            y += cardHeight + 5;
        }
        if (viewModel.chapters().size() > visibleCount) {
            Component previousLabel = Component.translatable("screen.firsttorch.chapter.previous_page");
            Component nextLabel = Component.translatable("screen.firsttorch.chapter.next_page");
            FirstTorchButton previous = button(panel.x() + 7, panel.bottom() - 23, 20, 16,
                    Component.literal("↑"), ignored -> selectChapterPage(pageStart - visibleCount),
                    previousLabel, Tooltip.create(previousLabel), FirstTorchButton.Kind.NAVIGATION, false);
            FirstTorchButton next = button(panel.right() - 27, panel.bottom() - 23, 20, 16,
                    Component.literal("↓"), ignored -> selectChapterPage(pageStart + visibleCount),
                    nextLabel, Tooltip.create(nextLabel), FirstTorchButton.Kind.NAVIGATION, false);
            previous.active = pageStart > 0;
            next.active = pageEnd < viewModel.chapters().size();
            addRenderableWidget(previous);
            addRenderableWidget(next);
        }
    }

    private void selectChapterPage(int index) {
        selectChapter(viewModel.chapters().get(Math.max(0, Math.min(index, viewModel.chapters().size() - 1))).id());
    }

    private void selectChapter(String chapterId) {
        selection = new Selection(selection.guideId(), chapterId, null);
        rebuildWidgets();
    }

    private void addQuestButtons() {
        for (int index = 0; index < viewModel.quests().size(); index++) {
            QuestDefinition quest = viewModel.quests().get(index);
            Rect bounds = questNodes.get(quest.id());
            if (bounds == null) continue;
            FirstTorchButton node = button(bounds.x(), bounds.y(), bounds.width(), bounds.height(),
                    Component.empty(), ignored -> selectQuest(quest.id()),
                    Component.translatable(quest.titleKey()).append(". ").append(Component.translatable(quest.descriptionKey())),
                    Tooltip.create(Component.translatable(quest.titleKey())),
                    FirstTorchButton.Kind.MEDALLION, quest.id().equals(selection.questId()));
            if (preview()) node.preview(icon(quest.id()), DesignPreview.completed(quest.id()),
                    quest.prerequisiteQuestIds().stream().anyMatch(id -> !DesignPreview.completed(id)));
            addRenderableWidget(node);
        }
    }

    private void selectQuest(String questId) {
        selection = new Selection(selection.guideId(), selection.chapterId(), questId);
        rebuildWidgets();
    }

    private void drawConnections(GuiGraphicsExtractor graphics) {
        for (QuestDefinition quest : viewModel.quests()) {
            Rect target = questNodes.get(quest.id());
            if (target == null) continue;
            for (String prerequisiteId : quest.prerequisiteQuestIds()) {
                Rect source = questNodes.get(prerequisiteId);
                if (source != null) drawArrow(graphics, source, target);
            }
        }
    }

    private static void drawArrow(GuiGraphicsExtractor graphics, Rect source, Rect target) {
        double dx = target.centerX() - source.centerX(), dy = target.centerY() - source.centerY();
        double distance = Math.hypot(dx, dy);
        if (distance < 1) return;
        double ux = dx / distance, uy = dy / distance;
        double start = source.width() / 2.0 + 2, end = target.width() / 2.0 + 5;
        if (distance <= start + end) return;
        int sx = (int) Math.round(source.centerX() + ux * start);
        int sy = (int) Math.round(source.centerY() + uy * start);
        int tx = (int) Math.round(target.centerX() - ux * end);
        int ty = (int) Math.round(target.centerY() - uy * end);
        drawLine(graphics, sx, sy, tx, ty);
        for (int offset = -1; offset <= 1; offset++) {
            drawLine(graphics, tx, ty, (int) Math.round(tx - ux * 5 - uy * (3 + offset)),
                    (int) Math.round(ty - uy * 5 + ux * (3 + offset)));
            drawLine(graphics, tx, ty, (int) Math.round(tx - ux * 5 + uy * (3 + offset)),
                    (int) Math.round(ty - uy * 5 - ux * (3 + offset)));
        }
    }

    private static void drawLine(GuiGraphicsExtractor graphics, int x, int y, int targetX, int targetY) {
        int dx = Math.abs(targetX - x), sx = x < targetX ? 1 : -1;
        int dy = -Math.abs(targetY - y), sy = y < targetY ? 1 : -1;
        int error = dx + dy;
        while (true) {
            graphics.fill(x, y, x + 1, y + 1, FirstTorchTheme.CONNECTION);
            if (x == targetX && y == targetY) return;
            int twice = error * 2;
            if (twice >= dy) { error += dy; x += sx; }
            if (twice <= dx) { error += dx; y += sy; }
        }
    }

    private void drawText(GuiGraphicsExtractor graphics) {
        Rect top = layout.topBar();
        drawMasthead(graphics, top);
        ActiveTextCollector text = graphics.textRenderer();
        Component guideLabel = viewModel.guide() == null
                ? Component.translatable("screen.firsttorch.status.empty")
                : Component.translatable(viewModel.guide().titleKey());
        if (viewModel.guides().size() > 1) {
            guideLabel = Component.translatable("screen.firsttorch.guide.page",
                    viewModel.guideIndex() + 1, viewModel.guides().size(), guideLabel);
        }
        if (top.width() >= 600) text.acceptScrollingWithDefaultCenter(colored(guideLabel, FirstTorchTheme.MUTED),
                top.centerX() - 61, top.centerX() + 61, top.bottom() - 20, top.bottom() - 5);
        drawProgress(graphics, text, top);
        if (viewModel.guide() == null) drawEmptyState(text);
        else if (preview()) PreviewDetailsRenderer.draw(graphics, font, layout.details(), viewModel.quest());
        else drawDetails(graphics, text);
        Rect footer = layout.footer();
        Component hint = Component.translatable(searchEmpty ? "screen.firsttorch.search.none" : "screen.firsttorch.controls_hint");
        if (preview()) hint = Component.translatable("screen.firsttorch.preview").append("  ·  ").append(hint);
        text.acceptScrollingWithDefaultCenter(colored(hint, FirstTorchTheme.MUTED),
                footer.x() + 8, footer.right() - 8, footer.y() + 3, footer.bottom() - 3);
    }

    private void drawMasthead(GuiGraphicsExtractor graphics, Rect top) {
        boolean compact = top.width() < 600;
        Component brand = Component.translatable("screen.firsttorch.brand");
        float scale = compact ? 1.5F : 2.4F;
        int brandWidth = Math.round(font.width(brand) * scale);
        int torchSize = compact ? 24 : 40;
        int groupWidth = brandWidth + torchSize + 9;
        int x = top.centerX() - groupWidth / 2;
        int y = top.y() + (compact ? 4 : 5);
        // Original vanilla assets, with a separate brass mounting collar.
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y + 2);
        graphics.pose().scale(torchSize / 16F, torchSize / 16F);
        graphics.item(brandTorch, 0, 0);
        graphics.pose().popMatrix();
        int flameWidth = Math.max(10, torchSize / 2);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, graphics.getSprite(ModelBakery.FIRE_0),
                x + (torchSize - flameWidth) / 2, y, flameWidth, Math.max(13, torchSize * 3 / 5));
        int collarY = y + torchSize * 3 / 5;
        FirstTorchTheme.buttonPlate(graphics,
                new Rect(x + torchSize / 4, collarY, torchSize / 2, Math.max(5, torchSize / 5)), false, true);
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + torchSize + 9, y + (compact ? 4 : 7));
        graphics.pose().scale(scale, scale);
        graphics.textRenderer().accept(1, 1, colored(brand, FirstTorchTheme.GOLD_DARK));
        graphics.textRenderer().accept(0, 0, colored(brand, FirstTorchTheme.AMBER));
        graphics.pose().popMatrix();
    }

    private void drawProgress(GuiGraphicsExtractor graphics, ActiveTextCollector text, Rect top) {
        boolean compact = top.width() < 600;
        int x = top.x() + 11, y = compact ? top.bottom() - 22 : top.y() + 11;
        int w = compact ? Math.max(40, top.width() / 2 - 25) : 145;
        int completed = preview() ? (int) viewModel.quests().stream().filter(q -> DesignPreview.completed(q.id())).count() : 0;
        Component progress = preview() ? Component.translatable("screen.firsttorch.progress", completed, viewModel.quests().size())
                : Component.translatable("screen.firsttorch.progress.unavailable");
        if (!compact) {
            text.accept(x, y, colored(Component.translatable(preview() ? "screen.firsttorch.preview" : "screen.firsttorch.chapters"), FirstTorchTheme.GOLD));
            y += 14;
        }
        text.acceptScrollingWithDefaultCenter(colored(progress, FirstTorchTheme.TEXT), x, x + w, y, y + 10);
        if (preview()) {
            int barY = y + 13;
            graphics.fill(x, barY, x + w, barY + 4, 0xFF0E1011);
            graphics.fill(x, barY, x + w * completed / Math.max(1, viewModel.quests().size()), barY + 4, FirstTorchTheme.GOLD);
        }
    }

    private void drawEmptyState(ActiveTextCollector text) {
        Rect panel = layout.questMap();
        int x = panel.x() + 10, w = panel.width() - 20, y = panel.y() + 35;
        y = drawWrapped(text, Component.translatable("screen.firsttorch.empty.title"), x, y, w, 3, FirstTorchTheme.TEXT) + 6;
        drawWrapped(text, Component.translatable("screen.firsttorch.empty.description"), x, y, w,
                Math.max(0, (panel.bottom() - y - 8) / 10), FirstTorchTheme.MUTED);
    }

    private void drawDetails(GuiGraphicsExtractor graphics, ActiveTextCollector text) {
        Rect panel = layout.details();
        int x = panel.x() + 10, w = panel.width() - 20, y = panel.y() + 10;
        if (viewModel.quest() == null) {
            drawWrapped(text, Component.translatable("screen.firsttorch.quest.empty"), x, y, w, 4, FirstTorchTheme.MUTED);
            return;
        }
        int titleRows = Math.max(1, Math.min(3, (panel.height() - 80) / 10));
        if (w >= 150) {
            Rect badge = new Rect(x, y, 44, 44);
            FirstTorchTheme.medallion(graphics, badge, true, false);
            graphics.item(icon(viewModel.quest().id()), badge.centerX() - 8, badge.centerY() - 8);
            drawWrapped(text, Component.translatable(viewModel.quest().titleKey()).copy().withStyle(style -> style.withBold(true)),
                    x + 55, y + 8, w - 55, 3, FirstTorchTheme.TEXT);
            y += 56;
        } else {
            y = drawWrapped(text, Component.translatable(viewModel.quest().titleKey()), x, y, w, titleRows, FirstTorchTheme.TEXT) + 6;
        }
        int descriptionRows = Math.max(0, Math.min(12, (panel.bottom() - y - 55) / 10));
        y = drawWrapped(text, Component.translatable(viewModel.quest().descriptionKey()), x, y, w,
                descriptionRows, FirstTorchTheme.MUTED) + 9;
        int requestedRows = viewModel.prerequisites().isEmpty() ? 2 : Math.min(6, viewModel.prerequisites().size() * 2);
        int insetBottom = Math.min(panel.bottom() - 8, y + 22 + requestedRows * 10);
        FirstTorchTheme.inset(graphics, new Rect(x - 3, y - 3, w + 6, Math.max(1, insetBottom - y + 3)));
        text.acceptScrollingWithDefaultCenter(colored(Component.translatable("screen.firsttorch.prerequisites"), FirstTorchTheme.GOLD),
                x, x + w, y, y + 12);
        y += 16;
        int remainingRows = Math.max(0, (insetBottom - y - 3) / 10);
        if (viewModel.prerequisites().isEmpty()) {
            drawWrapped(text, Component.translatable("screen.firsttorch.prerequisites.none"), x, y, w,
                    remainingRows, FirstTorchTheme.MUTED);
            return;
        }
        for (GuideBrowserViewModel.Prerequisite prerequisite : viewModel.prerequisites()) {
            if (remainingRows <= 0) break;
            Component line = Component.translatable("screen.firsttorch.prerequisite.entry", Component.translatable(prerequisite.titleKey()));
            if (preview() && DesignPreview.completed(prerequisite.questId())) line = Component.literal("✓ ").append(line);
            int nextY = drawWrapped(text, line, x, y, w, remainingRows, FirstTorchTheme.TEXT);
            remainingRows -= (nextY - y) / 10;
            y = nextY;
        }
    }

    private int drawWrapped(ActiveTextCollector text, Component component, int x, int y, int maxWidth, int maxRows, int color) {
        List<FormattedCharSequence> lines = font.split(colored(component, color), Math.max(1, maxWidth));
        int rows = Math.max(0, Math.min(maxRows, lines.size()));
        for (int row = 0; row < rows; row++) text.accept(x, y + row * 10, lines.get(row));
        return y + rows * 10;
    }

    private static Component colored(Component component, int color) {
        return component.copy().withStyle(style -> style.withColor(color));
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }
}
