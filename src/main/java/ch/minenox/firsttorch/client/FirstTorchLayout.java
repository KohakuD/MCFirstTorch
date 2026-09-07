package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FirstTorchLayout {
    public static final int PANEL_GAP = 5;
    public static final int NODE_SIZE = 48;

    private static final int MARGIN = 8;
    private static final int TOP_BAR_HEIGHT = 58;
    private static final int FOOTER_HEIGHT = 28;
    private static final int MIN_CENTER_WIDTH = 84;

    private FirstTorchLayout() {
    }

    public static ScreenLayout calculate(int screenWidth, int screenHeight) {
        int contentWidth = Math.max(1, screenWidth - MARGIN * 2 - PANEL_GAP * 2);
        int leftWidth = Math.max(58, contentWidth * 25 / 100);
        int rightWidth = Math.max(74, contentWidth * 39 / 100);
        int centerWidth = contentWidth - leftWidth - rightWidth;
        if (centerWidth < MIN_CENTER_WIDTH) {
            int sideSpace = Math.max(2, contentWidth - MIN_CENTER_WIDTH);
            leftWidth = sideSpace * 45 / 100;
            rightWidth = sideSpace - leftWidth;
            centerWidth = contentWidth - leftWidth - rightWidth;
        }

        Rect topBar = new Rect(MARGIN, MARGIN, Math.max(1, screenWidth - MARGIN * 2), TOP_BAR_HEIGHT);
        int panelTop = topBar.bottom() + PANEL_GAP;
        int panelHeight = Math.max(48, screenHeight - panelTop - FOOTER_HEIGHT - MARGIN);
        Rect chapters = new Rect(MARGIN, panelTop, leftWidth, panelHeight);
        Rect questMap = new Rect(chapters.right() + PANEL_GAP, panelTop, centerWidth, panelHeight);
        Rect details = new Rect(questMap.right() + PANEL_GAP, panelTop, rightWidth, panelHeight);
        Rect footer = new Rect(MARGIN, panelTop + panelHeight + PANEL_GAP,
                Math.max(1, screenWidth - MARGIN * 2), FOOTER_HEIGHT - PANEL_GAP);
        return new ScreenLayout(topBar, chapters, questMap, details, footer);
    }

    public static Map<String, Rect> questNodes(Rect panel, List<QuestDefinition> quests) {
        if (quests.isEmpty()) {
            return Map.of();
        }
        List<QuestDefinition> ordered = quests.stream()
                .sorted(Comparator.comparingInt(QuestDefinition::order).thenComparing(QuestDefinition::id))
                .toList();
        int minX = ordered.stream().map(QuestDefinition::position).mapToInt(QuestPosition::x).min().orElse(0);
        int maxX = ordered.stream().map(QuestDefinition::position).mapToInt(QuestPosition::x).max().orElse(0);
        int minY = ordered.stream().map(QuestDefinition::position).mapToInt(QuestPosition::y).min().orElse(0);
        int maxY = ordered.stream().map(QuestDefinition::position).mapToInt(QuestPosition::y).max().orElse(0);

        int columns = (int) ordered.stream().map(quest -> quest.position().x()).distinct().count();
        int nodeSize = Math.min(NODE_SIZE, Math.max(20, (panel.width() - 28) / Math.max(1, columns) - 8));
        int rows = (int) ordered.stream().map(quest -> quest.position().y()).distinct().count();
        nodeSize = Math.min(nodeSize, Math.max(12, (panel.height() - 24) / Math.max(1, rows) - 10));
        int left = panel.x() + 14;
        int top = panel.y() + 12;
        int usableWidth = Math.max(0, panel.width() - 28 - nodeSize);
        int usableHeight = Math.max(0, panel.height() - 24 - nodeSize);
        int mapWidth = Math.min(usableWidth, Math.max(0, (maxX - minX) * 48));
        int mapHeight = Math.min(usableHeight, Math.max(0, (maxY - minY) * 58));
        left += (usableWidth - mapWidth) / 2;
        top += (usableHeight - mapHeight) / 3;
        usableWidth = mapWidth;
        usableHeight = mapHeight;
        Map<String, Rect> result = new LinkedHashMap<>();
        for (QuestDefinition quest : ordered) {
            int x = coordinate(quest.position().x(), minX, maxX, left, usableWidth);
            int y = coordinate(quest.position().y(), minY, maxY, top, usableHeight);
            result.put(quest.id(), new Rect(x, y, nodeSize, nodeSize));
        }
        return Collections.unmodifiableMap(result);
    }

    private static int coordinate(int value, int minimum, int maximum, int start, int extent) {
        if (minimum == maximum) {
            return start + extent / 2;
        }
        return start + (int)Math.round((double)(value - minimum) * extent / (maximum - minimum));
    }

    public record ScreenLayout(Rect topBar, Rect chapters, Rect questMap, Rect details, Rect footer) {
    }

    public record Rect(int x, int y, int width, int height) {
        public int right() {
            return x + width;
        }

        public int bottom() {
            return y + height;
        }

        public int centerX() {
            return x + width / 2;
        }

        public int centerY() {
            return y + height / 2;
        }

        public boolean contains(Rect other) {
            return other.x >= x && other.y >= y && other.right() <= right() && other.bottom() <= bottom();
        }
    }
}
