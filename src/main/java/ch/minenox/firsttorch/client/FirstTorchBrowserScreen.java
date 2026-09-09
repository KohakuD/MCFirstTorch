package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import ch.minenox.firsttorch.client.FirstTorchLayout.ScreenLayout;
import ch.minenox.firsttorch.client.GuideBrowserViewModel.Selection;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
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
    private String query = "";
    private boolean searchEmpty;
    private boolean reading;
    private ProgressPayload observedProgress;
    private int detailsScroll;
    private int detailsMaxScroll;
    private boolean trophiesOpen;
    private List<TrophyCatalog.Entry> trophies = List.of();
    private String selectedTrophy;
    private boolean completedExpanded;
    private int chapterFirstRow;
    private int chapterRowCount;
    private int chapterCapacity = 1;
    private boolean revealChapter = true;
    private boolean draggingChapters;
    private double chapterDragOffset;
    private double chapterWheelRemainder;
    private final java.util.ArrayList<FirstTorchButton> chapterButtons = new java.util.ArrayList<>();
    private boolean recommendOnOpen = true;
    private final QuestLinkNavigation linkNavigation = new QuestLinkNavigation();
    private final java.util.ArrayList<FirstTorchButton> referenceButtons = new java.util.ArrayList<>();

    FirstTorchBrowserScreen(Screen parent) {
        super(Component.translatable("screen.firsttorch.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        referenceButtons.clear();
        chapterButtons.clear();
        if (observedSnapshot != ClientGuideCache.snapshot()) {
            linkNavigation.clear();
            chapterFirstRow = 0;
            revealChapter = true;
        }
        observedSnapshot = ClientGuideCache.snapshot();
        observedProgress = ClientProgressCache.snapshot();
        GuideSnapshot displayed = usesPreview() ? DesignPreview.snapshot() : observedSnapshot;
        if (!usesPreview() && recommendOnOpen && liveAvailable() && !observedSnapshot.guides().isEmpty()) {
            selection = QuestRecommendation.choose(observedSnapshot, observedProgress);
            reading = selection.questId() != null;
            recommendOnOpen = false;
            revealChapter = true;
            completedExpanded = selection.questId() != null && completed(selection.questId());
        }
        if (usesPreview() && !DesignPreview.isPreview(selection.guideId())) {
            selection = new Selection("0D15000000000001", null, "2D15000000000004");
        }
        viewModel = usesPreview() ? GuideBrowserViewModel.resolve(displayed, selection)
                : GuideBrowserViewModel.resolve(displayed, selection, observedProgress);
        selection = viewModel.selection();
        FirstTorchViewport resizedViewport = FirstTorchViewport.fit(width, height, FirstTorchClientConfig.ENLARGED_VIEW.get());
        if (!viewport.equals(resizedViewport)) revealChapter = true;
        viewport = resizedViewport;
        layout = FirstTorchLayout.calculate(viewport.width(), viewport.height(), reading && !trophiesOpen);
        Rect map = layout.questMap();
        Rect nodeArea = reading ? new Rect(map.x(), map.y() + 23, map.width(), map.height() - 23) : map;
        questNodes = FirstTorchLayout.questNodes(nodeArea, viewModel.quests());
        trophies = TrophyCatalog.entries(observedSnapshot, observedProgress);
        if (trophies.stream().noneMatch(entry -> entry.chapterId().equals(selectedTrophy))) {
            selectedTrophy = trophies.isEmpty() ? null : trophies.getFirst().chapterId();
        }
        addHeaderControls();
        if (!trophiesOpen) addGuideNavigation();
        addChapterButtons();
        if (trophiesOpen) addTrophyButtons();
        else {
            addQuestButtons();
            addManualConfirmation();
            addRewardClaim();
            addTestCompletion();
            addReferenceButtons();
        }
        if (reading && !trophiesOpen) {
            Component overview = Component.translatable("screen.firsttorch.overview");
            addRenderableWidget(button(map.x() + 7, map.y() + 7, map.width() - 14, 17,
                    overview, ignored -> { reading = false; rebuildWidgets(); }, overview,
                    Tooltip.create(overview), FirstTorchButton.Kind.NAVIGATION, false));
        }
        if (!preview() && !trophiesOpen && linkNavigation.hasBack()) {
            Rect footer = layout.footer();
            Component back = Component.translatable("screen.firsttorch.reference.back");
            int backWidth = Math.min(210, footer.width() - 16);
            addRenderableWidget(button(footer.right() - backWidth - 8, footer.y() + 2, backWidth,
                    footer.height() - 4, back, ignored -> returnFromReference(), back, null,
                    FirstTorchButton.Kind.NAVIGATION, true));
        }
    }

    private void addReferenceButtons() {
        if (preview() || viewModel.quest() == null) return;
        Rect panel = layout.details();
        for (var link : QuestReferenceLinks.forQuest(viewModel.quest().id())) {
            boolean available = QuestLinkNavigation.destination(observedSnapshot, observedProgress, link.questId()).isPresent();
            Component label = Component.literal("› ").append(Component.translatable(link.titleKey()));
            if (!available) label = label.copy().append(" — ").append(Component.translatable("screen.firsttorch.reference.unavailable"));
            var control = button(panel.x() + 10, 0, Math.max(1, panel.width() - 20), 18,
                    label, ignored -> followReference(link.questId()), label, null, FirstTorchButton.Kind.NAVIGATION, true);
            control.active = available;
            referenceButtons.add(control);
            addRenderableWidget(control);
        }
        positionReferenceButtons();
    }

    private void positionReferenceButtons() {
        if (referenceButtons.isEmpty() || viewModel.quest() == null) return;
        Rect panel = layout.details();
        int y = LiveDetailsRenderer.referenceTop(font, panel, viewModel.quest()) - detailsScroll;
        for (var control : referenceButtons) {
            control.setY(y);
            control.visible = y >= panel.y() + 10 && y + control.getHeight() <= panel.bottom() - 34;
            if (!control.visible && control.isFocused()) setFocused(null);
            y += 24;
        }
    }

    private void followReference(String questId) {
        var destination = QuestLinkNavigation.destination(observedSnapshot, observedProgress, questId);
        if (destination.isEmpty() || destination.get().equals(selection)) return;
        linkNavigation.push(new QuestLinkNavigation.Location(selection, detailsScroll, reading, completedExpanded, chapterFirstRow));
        selection = destination.get();
        reading = true;
        trophiesOpen = false;
        detailsScroll = 0;
        completedExpanded = true;
        chapterFirstRow = 0;
        revealChapter = true;
        rebuildWidgets();
    }

    private void returnFromReference() {
        linkNavigation.back(observedSnapshot, observedProgress).ifPresent(location -> {
            selection = location.selection();
            detailsScroll = location.scroll();
            reading = location.reading();
            completedExpanded = location.completedExpanded();
            chapterFirstRow = location.chapterFirstRow();
            revealChapter = false;
            trophiesOpen = false;
        });
        rebuildWidgets();
    }

    @Override
    public void tick() {
        GuideSnapshot current = ClientGuideCache.snapshot();
        if (current != observedSnapshot || ClientProgressCache.snapshot() != observedProgress) {
            rebuildWidgets();
        }
    }

    private boolean usesPreview() {
        if (previewChoice != null) return previewChoice;
        return false;
    }

    private boolean liveAvailable() {
        return observedProgress != null && observedProgress.available();
    }

    private void addRewardClaim() {
        if (preview() || !liveAvailable() || viewModel.quest() == null) return;
        QuestDefinition quest = viewModel.quest();
        if (quest.rewards().isEmpty() || !completed(quest.id())
                || !quest.prerequisitesMet(this::completed)
                || observedProgress.claimedQuestIds().contains(quest.id())
                || observedProgress.pendingQuestIds().contains(quest.id())) return;
        Rect panel = layout.details();
        Component label = Component.translatable("screen.firsttorch.reward.claim");
        addRenderableWidget(button(panel.x() + 10, panel.bottom() - 28, panel.width() - 20, 18,
                label, control -> {
                    if (minecraft.player != null) minecraft.player.connection.sendCommand("firsttorch claim " + quest.id());
                }, label, null, FirstTorchButton.Kind.FOOTER, false));
    }

    private boolean testCompletionAvailable() {
        if (preview() || !liveAvailable() || viewModel.quest() == null || completed(viewModel.quest().id())) return false;
        var server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) return false;
        var owner = server.getSingleplayerProfile();
        return ch.minenox.firsttorch.guide.progress.DevelopmentQuestAccess.allowed(
                ch.minenox.firsttorch.guide.progress.DevelopmentQuestAccess.enabled(), true,
                owner == null ? null : owner.id(), minecraft.player.getUUID());
    }

    private void addTestCompletion() {
        if (!testCompletionAvailable()) return;
        Rect panel = layout.details();
        int width = (panel.width() - 26) / 2;
        String questId = viewModel.quest().id();
        Component label = Component.translatable("screen.firsttorch.test.complete");
        addRenderableWidget(button(panel.right() - 10 - width, panel.bottom() - 28, width, 18,
                label, control -> {
                    if (minecraft.player != null) minecraft.player.connection.sendCommand("firsttorch test_complete " + questId);
                }, Component.translatable("screen.firsttorch.test.narration"), null, FirstTorchButton.Kind.FOOTER, false));
    }

    private boolean completed(String questId) {
        return preview() ? DesignPreview.completed(questId)
                : liveAvailable() && observedProgress.state().completedQuestIds().contains(questId);
    }

    private void addManualConfirmation() {
        if (preview() || !liveAvailable() || viewModel.quest() == null) return;
        QuestDefinition quest = viewModel.quest();
        if (!quest.prerequisitesMet(this::completed)) return;
        if (completed(quest.id())) return;
        quest.tasks().stream().filter(task -> task.type() == TaskDefinition.Type.MANUAL
                && !observedProgress.state().completedTaskIds().contains(task.id())).findFirst().ifPresent(task -> {
            Rect panel = layout.details();
            Component label = Component.translatable("screen.firsttorch.task.confirm");
            int actionWidth = testCompletionAvailable() ? (panel.width() - 26) / 2 : panel.width() - 20;
            addRenderableWidget(button(panel.x() + 10, panel.bottom() - 28, actionWidth, 18,
                    label, control -> {
                        if (minecraft.player != null) {
                            minecraft.player.connection.sendCommand("firsttorch confirm " + quest.id() + " " + task.id());
                        }
                    }, label, null, FirstTorchButton.Kind.FOOTER, false));
        });
    }

    private boolean preview() {
        return viewModel.guide() != null && DesignPreview.isPreview(viewModel.guide().id());
    }

    private void togglePreview() {
        linkNavigation.clear();
        trophiesOpen = false;
        detailsScroll = 0;
        reading = false;
        previewChoice = !preview();
        chapterFirstRow = 0;
        revealChapter = true;
        selection = Selection.EMPTY;
        query = "";
        searchEmpty = false;
        rebuildWidgets();
    }

    private boolean search(String query) {
        // Refresh visibility on submission, not on every edit in the modal.
        rebuildWidgets();
        this.query = query;
        searchEmpty = false;
        if (query.isBlank() || viewModel.guide() == null) return false;
        String needle = query.toLowerCase(Locale.ROOT).strip();
        searchEmpty = true;
        search: for (ChapterDefinition chapter : viewModel.chapters()) {
            for (QuestDefinition quest : chapter.quests()) {
                if (Component.translatable(quest.titleKey()).getString().toLowerCase(Locale.ROOT).contains(needle)) {
                    selection = new Selection(selection.guideId(), chapter.id(), quest.id());
                    if (ChapterArchive.completed(chapter, this::completed)) completedExpanded = true;
                    revealChapter = true;
                    searchEmpty = false;
                    break search;
                }
            }
        }
        if (!searchEmpty) {
            reading = true;
            trophiesOpen = false;
            detailsScroll = 0;
        }
        return !searchEmpty;
    }

    private boolean hasClaimableRewards() {
        return !usesPreview() && liveAvailable()
                && !ch.minenox.firsttorch.guide.progress.ClaimableRewards.ids(observedSnapshot,
                        observedProgress.state(), observedProgress.claimedQuestIds(),
                        observedProgress.pendingQuestIds()).isEmpty();
    }

    private void addHeaderControls() {
        Rect bar = layout.topBar();
        int size = 20;
        int x = bar.right() - 2 * size - 13;
        int y = bar.y() + 7;
        var references = ReferenceIndex.chapters(viewModel.chapters());
        if (!preview() && !references.isEmpty()) {
            Component indexLabel = Component.translatable("screen.firsttorch.reference.index");
            addRenderableWidget(button(x - size - 5, y + size + 4, size, size, Component.empty(),
                    ignored -> minecraft.setScreenAndShow(new FirstTorchReferenceScreen(this, references, this::openReferenceChapter)),
                    indexLabel, null, FirstTorchButton.Kind.REFERENCE_INDEX, false)
                    .preview(new ItemStack(Items.BOOKSHELF), false, false));
        }
        if (hasClaimableRewards()) {
            Component claimAll = Component.translatable("screen.firsttorch.reward.claim_all");
            addRenderableWidget(button(x - size - 5, y, size, size, Component.empty(),
                    control -> {
                        if (minecraft.player != null) {
                            control.active = false;
                            minecraft.player.connection.sendCommand("firsttorch claim_all");
                        }
                    }, claimAll, null, FirstTorchButton.Kind.CLAIM_ALL, true));
        }
        Component search = Component.translatable("screen.firsttorch.search");
        addRenderableWidget(button(x, y, size, size, Component.empty(),
                ignored -> minecraft.setScreenAndShow(new FirstTorchSearchScreen(this, query, this::search)),
                search, null, FirstTorchButton.Kind.SEARCH, false));
        Component access = Component.translatable("screen.firsttorch.accessibility");
        Component collectionLabel = Component.translatable("screen.firsttorch.trophies");
        addRenderableWidget(button(x + size + 4, y, size, size, Component.empty(),
                ignored -> { trophiesOpen = !trophiesOpen; detailsScroll = 0; rebuildWidgets(); },
                collectionLabel, null, FirstTorchButton.Kind.TROPHY, trophiesOpen));
        addRenderableWidget(button(x, y + size + 4, size, size, Component.empty(),
                ignored -> minecraft.setScreenAndShow(new FirstTorchAccessibilityScreen(this, minecraft.options)),
                access, null, FirstTorchButton.Kind.ACCESSIBILITY, false));
        addRenderableWidget(button(x + size + 4, y + size + 4, size, size,
                Component.empty(), ignored -> togglePreview(),
                Component.translatable("screen.firsttorch.preview.toggle"),
                Tooltip.create(Component.translatable("screen.firsttorch.preview.toggle")),
                FirstTorchButton.Kind.SETTINGS, preview()));
    }

    private void openReferenceChapter(String chapterId) {
        // Re-check after modal time: a server reload may have replaced the visible catalogue.
        rebuildWidgets();
        if (ReferenceIndex.chapters(viewModel.chapters()).stream().noneMatch(c -> c.id().equals(chapterId))) return;
        linkNavigation.push(new QuestLinkNavigation.Location(selection, detailsScroll, reading, completedExpanded, chapterFirstRow));
        completedExpanded = true;
        revealChapter = true;
        selectChapter(chapterId);
    }

    private ItemStack icon(String objectId) {
        if (!preview()) {
            return viewModel.quests().stream().filter(q -> q.id().equals(objectId))
                    .findFirst().map(QuestIcons::resolve)
                    .orElseGet(() -> new ItemStack(Items.BOOK));
        }
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
        if (!trophiesOpen) drawConnections(graphics);
        drawText(graphics);
        positionReferenceButtons();
        super.extractRenderState(graphics, (int) viewport.localX(mouseX), (int) viewport.localY(mouseY), partialTick);
        drawChapterScrollbar(graphics);
        if (!preview()) ChapterFirework.draw(graphics, font, viewport.width(), viewport.height());
        graphics.pose().popMatrix();
    }

    private MouseButtonEvent localEvent(MouseButtonEvent event) {
        return new MouseButtonEvent(viewport.localX(event.x()), viewport.localY(event.y()), event.buttonInfo());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        var local = localEvent(event);
        Rect track = chapterTrack();
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && chapterRowCount > chapterCapacity
                && contains(track, local.x(), local.y())) {
            int top = ChapterScroll.thumbTop(track.y(), track.height(), chapterRowCount, chapterCapacity, chapterFirstRow);
            int height = ChapterScroll.thumbHeight(track.height(), chapterRowCount, chapterCapacity);
            chapterDragOffset = local.y() >= top && local.y() < top + height ? local.y() - top : height / 2.0;
            draggingChapters = true;
            dragChapters(local.y());
            return true;
        }
        return super.mouseClicked(localEvent(event), doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggingChapters && event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            draggingChapters = false;
            return true;
        }
        return super.mouseReleased(localEvent(event));
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (draggingChapters) {
            dragChapters(viewport.localY(event.y()));
            return true;
        }
        return super.mouseDragged(localEvent(event), dx / viewport.scale(), dy / viewport.scale());
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        Rect panel = layout.details();
        double localX = viewport.localX(x), localY = viewport.localY(y);
        if (contains(layout.chapters(), localX, localY)) {
            chapterWheelRemainder -= scrollY;
            int rows = (int) chapterWheelRemainder;
            chapterWheelRemainder -= rows;
            scrollChapters(chapterFirstRow + rows);
            return true;
        }
        if ((trophiesOpen || !preview()) && localX >= panel.x() && localX < panel.right()
                && localY >= panel.y() && localY < panel.bottom()) {
            detailsScroll = Math.max(0, Math.min(detailsMaxScroll, detailsScroll - (int) (scrollY * 20)));
            positionReferenceButtons();
            return true;
        }
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
        completedExpanded = false;
        chapterFirstRow = 0;
        revealChapter = true;
        reading = false;
        int size = viewModel.guides().size();
        if (size == 0) return;
        selection = new Selection(viewModel.guides().get(Math.floorMod(viewModel.guideIndex() + delta, size)).id(), null, null);
        rebuildWidgets();
    }

    private static boolean contains(Rect bounds, double x, double y) {
        return x >= bounds.x() && x < bounds.right() && y >= bounds.y() && y < bounds.bottom();
    }

    private Rect chapterTrack() {
        Rect panel = layout.chapters();
        return new Rect(panel.right() - 13, panel.y() + 9, 6, Math.max(1, panel.height() - 18));
    }

    private void drawChapterScrollbar(GuiGraphicsExtractor graphics) {
        if (chapterRowCount <= chapterCapacity) return;
        Rect track = chapterTrack();
        graphics.fill(track.x(), track.y(), track.right(), track.bottom(), 0xFF111415);
        int top = ChapterScroll.thumbTop(track.y(), track.height(), chapterRowCount, chapterCapacity, chapterFirstRow);
        int height = ChapterScroll.thumbHeight(track.height(), chapterRowCount, chapterCapacity);
        graphics.fill(track.x(), top, track.right(), top + height, FirstTorchTheme.AMBER);
        graphics.fill(track.x() + 1, top + 1, track.right() - 1, top + height - 1, 0xFF9D793C);
    }

    private void dragChapters(double y) {
        Rect track = chapterTrack();
        scrollChapters(ChapterScroll.fromThumb((int) Math.round(y - chapterDragOffset),
                track.y(), track.height(), chapterRowCount, chapterCapacity));
    }

    private void scrollChapters(int first) {
        int clamped = ChapterScroll.clamp(first, chapterRowCount, chapterCapacity);
        if (clamped == chapterFirstRow) return;
        boolean chapterFocused = chapterButtons.contains(getFocused());
        chapterFirstRow = clamped;
        revealChapter = false;
        rebuildWidgets();
        if (chapterFocused && !chapterButtons.isEmpty()) setFocused(chapterButtons.getFirst());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (chapterButtons.contains(getFocused())) {
            int first = switch (event.key()) {
                case GLFW.GLFW_KEY_PAGE_UP -> chapterFirstRow - chapterCapacity;
                case GLFW.GLFW_KEY_PAGE_DOWN -> chapterFirstRow + chapterCapacity;
                case GLFW.GLFW_KEY_HOME -> 0;
                case GLFW.GLFW_KEY_END -> chapterRowCount;
                default -> -1;
            };
            if (event.key() == GLFW.GLFW_KEY_PAGE_UP || first >= 0) {
                scrollChapters(first);
                return true;
            }
        }
        return super.keyPressed(event);
    }

    private void addChapterButton(FirstTorchButton card) {
        chapterButtons.add(card);
        addRenderableWidget(card);
    }

    private void addChapterButtons() {
        chapterRowCount = 0;
        if (viewModel.chapters().isEmpty()) {
            chapterFirstRow = 0;
            return;
        }
        Rect panel = layout.chapters();
        int cardHeight = Math.max(23, Math.min(42, panel.height() / 7));
        chapterCapacity = ChapterScroll.capacity(panel.height(), cardHeight);
        var rows = ChapterArchive.rows(ReferenceIndex.courseChapters(viewModel.chapters()), this::completed, completedExpanded);
        chapterRowCount = rows.size();
        chapterFirstRow = ChapterScroll.clamp(chapterFirstRow, chapterRowCount, chapterCapacity);
        if (revealChapter) {
            for (int index = 0; index < rows.size(); index++) {
                if (viewModel.chapter() != null && viewModel.chapter().equals(rows.get(index).chapter())) {
                    chapterFirstRow = ChapterScroll.reveal(chapterFirstRow, index, chapterRowCount, chapterCapacity);
                    break;
                }
            }
            revealChapter = false;
        }
        int end = Math.min(rows.size(), chapterFirstRow + chapterCapacity);
        int cardWidth = panel.width() - (chapterRowCount > chapterCapacity ? 24 : 14);
        int y = panel.y() + 9;
        for (int index = chapterFirstRow; index < end; index++) {
            var row = rows.get(index);
            if (row.heading()) {
                Component archive = Component.translatable("screen.firsttorch.chapter.completed", row.completedCount());
                addChapterButton(button(panel.x() + 7, y, cardWidth, cardHeight, archive,
                        ignored -> { completedExpanded = !completedExpanded; chapterFirstRow = 0; rebuildWidgets(); },
                        archive.copy().append(". ").append(Component.translatable(completedExpanded
                                ? "screen.firsttorch.chapter.collapse" : "screen.firsttorch.chapter.expand"))
                                .append(". ").append(Component.translatable("screen.firsttorch.chapter.scroll")),
                        null, FirstTorchButton.Kind.ARCHIVE, completedExpanded));
            } else {
                ChapterDefinition chapter = row.chapter();
                Component label = cardWidth >= 100 ? Component.translatable(chapter.titleKey()) : Component.empty();
                FirstTorchButton card = button(panel.x() + 7, y, cardWidth, cardHeight, label,
                        ignored -> selectChapter(chapter.id()),
                        Component.translatable("screen.firsttorch.chapter.narration", Component.translatable(chapter.titleKey()))
                                .append(". ").append(Component.translatable("screen.firsttorch.chapter.scroll")),
                        null, FirstTorchButton.Kind.CARD, !trophiesOpen && chapter.id().equals(selection.chapterId()));
                if (preview()) card.preview(icon(chapter.id()), false, false);
                else card.preview(QuestIcons.resolveItem(chapter.iconItemId()), false, false);
                addChapterButton(card);
            }
            y += cardHeight + 5;
        }
    }

    private void selectChapter(String chapterId) {
        trophiesOpen = false;
        detailsScroll = 0;
        reading = false;
        selection = new Selection(selection.guideId(), chapterId, null);
        rebuildWidgets();
    }

    private void addQuestButtons() {
        var claimable = !preview() && liveAvailable()
                ? ch.minenox.firsttorch.guide.progress.ClaimableRewards.ids(observedSnapshot,
                        observedProgress.state(), observedProgress.claimedQuestIds(), observedProgress.pendingQuestIds())
                : java.util.List.<String>of();
        for (int index = 0; index < viewModel.quests().size(); index++) {
            QuestDefinition quest = viewModel.quests().get(index);
            Rect bounds = questNodes.get(quest.id());
            if (bounds == null) continue;
            var narration = Component.translatable(quest.titleKey()).append(". ")
                    .append(Component.translatable(QuestNarrationState.key(preview() || liveAvailable(),
                            !quest.prerequisitesMet(this::completed), completed(quest.id()), claimable.contains(quest.id()))))
                    .append(". ")
                    .append(Component.translatable(quest.descriptionKey()));
            if (quest.image() != null) narration.append(". ").append(Component.translatable(quest.image().altKey()));
            FirstTorchButton node = button(bounds.x(), bounds.y(), bounds.width(), bounds.height(),
                    Component.empty(), ignored -> selectQuest(quest.id()),
                    narration,
                    null,
                    FirstTorchButton.Kind.MEDALLION, quest.id().equals(selection.questId()));
            if (preview()) node.preview(icon(quest.id()), DesignPreview.completed(quest.id()),
                    !quest.prerequisitesMet(DesignPreview::completed));
            else node.preview(icon(quest.id()), completed(quest.id()),
                    liveAvailable() && !quest.prerequisitesMet(this::completed));
            addRenderableWidget(node);
        }
    }

    private void addTrophyButtons() {
        Rect panel = layout.questMap();
        int selected = 0;
        for (int i = 0; i < trophies.size(); i++) if (trophies.get(i).chapterId().equals(selectedTrophy)) selected = i;
        int capacity = TrophyCollectionLayout.capacity(panel);
        int start = selected / capacity * capacity;
        List<Rect> cards = TrophyCollectionLayout.cards(panel, Math.max(0, trophies.size() - start));
        for (int i = 0; i < cards.size(); i++) {
            var entry = trophies.get(start + i);
            addRenderableWidget(new TrophyCardButton(cards.get(i), entry, entry.chapterId().equals(selectedTrophy),
                    ignored -> { selectedTrophy = entry.chapterId(); detailsScroll = 0; rebuildWidgets(); }));
        }
        if (trophies.size() > capacity) {
            int previousIndex = Math.max(0, start - capacity);
            int nextIndex = Math.min(trophies.size() - 1, start + capacity);
            Component previousLabel = Component.translatable("screen.firsttorch.chapter.previous_page");
            Component nextLabel = Component.translatable("screen.firsttorch.chapter.next_page");
            var previous = button(panel.x() + 10, panel.bottom() - 23, 20, 15, Component.literal("‹"),
                    ignored -> { selectedTrophy = trophies.get(previousIndex).chapterId(); detailsScroll = 0; rebuildWidgets(); },
                    previousLabel, null, FirstTorchButton.Kind.NAVIGATION, false);
            var next = button(panel.right() - 30, panel.bottom() - 23, 20, 15, Component.literal("›"),
                    ignored -> { selectedTrophy = trophies.get(nextIndex).chapterId(); detailsScroll = 0; rebuildWidgets(); },
                    nextLabel, null, FirstTorchButton.Kind.NAVIGATION, false);
            previous.active = start > 0;
            next.active = start + capacity < trophies.size();
            addRenderableWidget(previous);
            addRenderableWidget(next);
        }
    }

    private void selectQuest(String questId) {
        detailsScroll = 0;
        reading = true;
        selection = new Selection(selection.guideId(), selection.chapterId(), questId);
        rebuildWidgets();
    }

    private void drawConnections(GuiGraphicsExtractor graphics) {
        if (ParallelQuestLayout.supports(viewModel.quests())
                && questNodes.values().stream().map(Rect::centerX).distinct().count() == 1) {
            for (var segment : ParallelQuestLayout.connections(questNodes, viewModel.quests())) {
                drawLine(graphics, segment.x1(), segment.y1(), segment.x2(), segment.y2());
                if (segment.arrow()) {
                    int direction = Integer.signum(segment.x2() - segment.x1());
                    for (int offset = -1; offset <= 1; offset++) {
                        drawLine(graphics, segment.x2(), segment.y2(), segment.x2() - direction * 4,
                                segment.y2() - 3 + offset);
                        drawLine(graphics, segment.x2(), segment.y2(), segment.x2() - direction * 4,
                                segment.y2() + 3 + offset);
                    }
                }
            }
            return;
        }
        for (QuestDefinition quest : viewModel.quests()) {
            Rect target = questNodes.get(quest.id());
            if (target == null) continue;
            for (String prerequisiteId : quest.prerequisiteQuestIds()) {
                Rect source = questNodes.get(prerequisiteId);
                if (source != null) {
                    // Long vertical prerequisites must not disappear underneath intermediate quests.
                    boolean obstructed = source.centerX() == target.centerX() && questNodes.values().stream()
                            .anyMatch(node -> node != source && node != target && node.centerX() == source.centerX()
                                    && node.centerY() > Math.min(source.centerY(), target.centerY())
                                    && node.centerY() < Math.max(source.centerY(), target.centerY()));
                    if (obstructed) {
                        int nextColumn = questNodes.values().stream().mapToInt(Rect::centerX)
                                .filter(cx -> cx > source.centerX()).min().orElse(source.centerX() + 48);
                        int railX = (source.centerX() + nextColumn) / 2;
                        drawLine(graphics, source.right() + 2, source.centerY(), railX, source.centerY());
                        drawLine(graphics, railX, source.centerY(), railX, target.centerY());
                        drawArrow(graphics, new Rect(railX, target.centerY(), 0, 0), target);
                    } else drawArrow(graphics, source, target);
                }
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
        if (trophiesOpen) guideLabel = Component.translatable("screen.firsttorch.trophies");
        if (top.width() >= 600) text.acceptScrollingWithDefaultCenter(colored(guideLabel, FirstTorchTheme.MUTED),
                top.centerX() - 61, top.centerX() + 61, top.bottom() - 20, top.bottom() - 5);
        drawProgress(graphics, text, top);
        if (trophiesOpen) {
            Rect panel = layout.questMap();
            text.acceptScrollingWithDefaultCenter(colored(Component.translatable("screen.firsttorch.trophies.milestones"), FirstTorchTheme.GOLD),
                    panel.x() + 10, panel.right() - 10, panel.y() + 9, panel.y() + 21);
            var entry = trophies.stream().filter(t -> t.chapterId().equals(selectedTrophy)).findFirst().orElse(null);
            detailsMaxScroll = TrophyCollectionRenderer.draw(graphics, font, layout.details(), entry, liveAvailable(), detailsScroll);
            detailsScroll = Math.min(detailsScroll, detailsMaxScroll);
        }
        else if (viewModel.guide() == null) drawEmptyState(text);
        else if (preview()) PreviewDetailsRenderer.draw(graphics, font, layout.details(), viewModel.quest());
        else {
            detailsMaxScroll = LiveDetailsRenderer.draw(graphics, font, layout.details(), viewModel.quest(), observedProgress, detailsScroll);
            detailsScroll = Math.min(detailsScroll, detailsMaxScroll);
        }
        Rect footer = layout.footer();
        Component hint = Component.translatable(searchEmpty ? "screen.firsttorch.search.none" : "screen.firsttorch.controls_hint");
        if (trophiesOpen) hint = Component.translatable("screen.firsttorch.trophies.back");
        if (preview()) hint = Component.translatable("screen.firsttorch.preview").append("  ·  ").append(hint);
        text.acceptScrollingWithDefaultCenter(colored(hint, FirstTorchTheme.MUTED),
                footer.x() + 8, footer.right() - 8 - (!preview() && !trophiesOpen && linkNavigation.hasBack()
                        ? Math.min(210, footer.width() - 16) + 8 : 0), footer.y() + 3, footer.bottom() - 3);
    }

    private void drawMasthead(GuiGraphicsExtractor graphics, Rect top) {
        boolean compact = top.width() < 600;
        Component brand = Component.translatable("screen.firsttorch.brand");
        float scale = compact ? 1.5F : 2.4F;
        int brandWidth = Math.round(font.width(brand) * scale);
        int torchSize = compact ? 24 : 40;
        int groupWidth = brandWidth + torchSize + 9;
        int x = Math.min(top.centerX() - groupWidth / 2,
                top.right() - (hasClaimableRewards() ? 91 : 66) - groupWidth);
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
        int w = compact ? Math.max(40, top.width() / 2 - 105) : 145;
        int completed = trophiesOpen ? (int) trophies.stream().filter(TrophyCatalog.Entry::earned).count()
                : (int) viewModel.quests().stream().filter(q -> completed(q.id())).count();
        int total = trophiesOpen ? trophies.size() : viewModel.quests().size();
        Component progress = (!trophiesOpen && preview()) || liveAvailable() ? Component.translatable(trophiesOpen
                ? "screen.firsttorch.trophies.progress" : "screen.firsttorch.progress", completed, total)
                : Component.translatable("screen.firsttorch.progress.unavailable");
        if (!compact) {
            text.accept(x, y, colored(Component.translatable(trophiesOpen ? "screen.firsttorch.trophies"
                    : preview() ? "screen.firsttorch.preview" : "screen.firsttorch.chapters"), FirstTorchTheme.GOLD));
            y += 14;
        }
        text.acceptScrollingWithDefaultCenter(colored(progress, FirstTorchTheme.TEXT), x, x + w, y, y + 10);
        if ((!trophiesOpen && preview()) || liveAvailable()) {
            int barY = y + 13;
            graphics.fill(x, barY, x + w, barY + 4, 0xFF0E1011);
            graphics.fill(x, barY, x + w * completed / Math.max(1, total), barY + 4, FirstTorchTheme.GOLD);
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
        if (trophiesOpen) {
            trophiesOpen = false;
            detailsScroll = 0;
            rebuildWidgets();
            return;
        }
        minecraft.setScreenAndShow(parent);
    }
}
