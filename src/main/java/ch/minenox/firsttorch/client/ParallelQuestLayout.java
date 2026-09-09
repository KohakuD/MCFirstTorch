package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** A shared fork/join bus for independent lessons, without changing prerequisite semantics. */
final class ParallelQuestLayout {
    private ParallelQuestLayout() {}

    static boolean fits(Rect panel, List<QuestDefinition> quests) {
        // Match the minimum 12-pixel node plus eight-pixel row gap in nodes().
        // A fixed height cutoff changed the graph topology when browser zoom reduced its canvas.
        return supports(quests) && panel.width() >= 110 && panel.height() >= 24 + quests.size() * 20;
    }

    static boolean supports(List<QuestDefinition> quests) {
        if (quests.size() < 6 || quests.size() > 10
                || quests.stream().anyMatch(q -> q.prerequisiteMode() == QuestDefinition.PrerequisiteMode.ANY)) return false;
        var roots = quests.stream().filter(q -> q.prerequisiteQuestIds().isEmpty()).toList();
        if (roots.size() != 1) return false;
        String root = roots.getFirst().id();
        var branches = quests.stream().filter(q -> q.prerequisiteQuestIds().equals(List.of(root))).toList();
        if (branches.size() != quests.size() - 2) return false;
        Set<String> ids = branches.stream().map(QuestDefinition::id).collect(Collectors.toSet());
        return quests.stream().anyMatch(q -> q.prerequisiteQuestIds().size() == ids.size()
                && Set.copyOf(q.prerequisiteQuestIds()).equals(ids));
    }

    private static List<QuestDefinition> ordered(List<QuestDefinition> quests) {
        var root = quests.stream().filter(q -> q.prerequisiteQuestIds().isEmpty()).findFirst().orElseThrow();
        var branches = quests.stream().filter(q -> q.prerequisiteQuestIds().equals(List.of(root.id())))
                .sorted(Comparator.comparingInt(QuestDefinition::order).thenComparing(QuestDefinition::id)).toList();
        var sink = quests.stream().filter(q -> q.prerequisiteQuestIds().size() == branches.size()).findFirst().orElseThrow();
        var ordered = new ArrayList<QuestDefinition>();
        ordered.add(root);
        ordered.addAll(branches);
        ordered.add(sink);
        return ordered;
    }

    static Map<String, Rect> nodes(Rect panel, List<QuestDefinition> quests) {
        var ordered = ordered(quests);
        int size = Math.min(FirstTorchLayout.NODE_SIZE, Math.max(12, (panel.height() - 24) / ordered.size() - 8));
        int span = Math.max(0, panel.height() - 24 - size);
        Map<String, Rect> nodes = new LinkedHashMap<>();
        for (int i = 0; i < ordered.size(); i++) {
            int y = panel.y() + 12 + i * span / (ordered.size() - 1);
            nodes.put(ordered.get(i).id(), new Rect(panel.centerX() - size / 2, y, size, size));
        }
        return java.util.Collections.unmodifiableMap(nodes);
    }

    static List<Segment> connections(Map<String, Rect> nodes, List<QuestDefinition> quests) {
        var ordered = ordered(quests);
        Rect root = nodes.get(ordered.getFirst().id()), sink = nodes.get(ordered.getLast().id());
        int left = root.x() - 24, right = root.right() + 24;
        var branches = ordered.subList(1, ordered.size() - 1);
        Rect first = nodes.get(branches.getFirst().id()), last = nodes.get(branches.getLast().id());
        var segments = new ArrayList<Segment>();
        // Source feeds only the left bus; the right bus collects all branches into the sink.
        segments.add(new Segment(root.x() - 3, root.centerY(), left, root.centerY(), false));
        segments.add(new Segment(left, root.centerY(), left, last.centerY(), false));
        segments.add(new Segment(right, first.centerY(), right, sink.centerY(), false));
        for (var branch : branches) {
            Rect node = nodes.get(branch.id());
            segments.add(new Segment(left, node.centerY(), node.x() - 5, node.centerY(), true));
            segments.add(new Segment(node.right() + 3, node.centerY(), right, node.centerY(), true));
        }
        segments.add(new Segment(right, sink.centerY(), sink.right() + 5, sink.centerY(), true));
        return List.copyOf(segments);
    }

    record Segment(int x1, int y1, int x2, int y2, boolean arrow) {}
}
