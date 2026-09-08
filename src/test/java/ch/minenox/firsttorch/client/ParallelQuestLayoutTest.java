package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.client.FirstTorchLayout.Rect;
import ch.minenox.firsttorch.guide.data.GuideJson;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class ParallelQuestLayoutTest {
    @Test
    void recognisesOnlyTheBundledForkJoinCourse() throws Exception {
        List<QuestDefinition> course = courseQuests();

        assertTrue(ParallelQuestLayout.supports(course));
        for (var chapter : DesignPreview.snapshot().guides().getFirst().chapters()) {
            assertFalse(ParallelQuestLayout.supports(chapter.quests()), chapter.id());
        }
        assertFalse(ParallelQuestLayout.supports(load("getting_started").chapters().getFirst().quests()));
    }

    @Test
    void laysOutTheCourseAsAVerticalForkJoinInOverviewAndReadingAreas() throws Exception {
        List<QuestDefinition> quests = courseQuests();
        FirstTorchLayout.ScreenLayout overview = FirstTorchLayout.calculate(720, 405);
        FirstTorchLayout.ScreenLayout reading = FirstTorchLayout.calculate(720, 405, true);
        Rect readingNodes = new Rect(reading.questMap().x(), reading.questMap().y() + 23,
                reading.questMap().width(), reading.questMap().height() - 23);

        for (Rect panel : List.of(overview.questMap(), readingNodes)) {
            Map<String, Rect> nodes = ParallelQuestLayout.nodes(panel, quests);
            assertEquals(nodes, FirstTorchLayout.questNodes(panel, quests));
            assertEquals(quests.size(), nodes.size());
            assertTrue(nodes.values().stream().allMatch(panel::contains));
            assertNoOverlap(nodes);

            QuestDefinition root = quests.getFirst();
            QuestDefinition sink = quests.getLast();
            Rect rootNode = nodes.get(root.id());
            Rect sinkNode = nodes.get(sink.id());
            List<Rect> branches = quests.subList(1, quests.size() - 1).stream()
                    .map(quest -> nodes.get(quest.id())).toList();

            assertTrue(rootNode.bottom() < branches.getFirst().y());
            assertTrue(branches.getLast().bottom() < sinkNode.y());
            assertEquals(rootNode.centerX(), sinkNode.centerX());
            assertTrue(branches.stream().allMatch(node -> node.centerX() == rootNode.centerX()));
            for (int index = 1; index < branches.size(); index++) {
                assertTrue(branches.get(index - 1).bottom() < branches.get(index).y());
            }
        }
    }

    @Test
    void routesOrthogonalConnectionsAroundOtherNodesAndMarksEveryInboundEdge() throws Exception {
        List<QuestDefinition> quests = courseQuests();
        Rect panel = FirstTorchLayout.calculate(720, 405).questMap();
        Map<String, Rect> nodes = ParallelQuestLayout.nodes(panel, quests);
        List<ParallelQuestLayout.Segment> segments = ParallelQuestLayout.connections(nodes, quests);

        assertTrue(segments.stream().allMatch(this::isOrthogonal));
        for (ParallelQuestLayout.Segment segment : segments) {
            assertTrue(nodes.values().stream().noneMatch(node -> crossesInterior(segment, node)
                    && !touchesEndpoint(segment, node)), segment.toString());
        }
        for (QuestDefinition quest : quests.subList(1, quests.size())) {
            assertTrue(segments.stream().anyMatch(segment -> segment.arrow() && endsAt(segment, nodes.get(quest.id()))),
                    "missing inbound arrow for " + quest.id());
        }
    }

    @Test
    void leavesCourseDependenciesUntouched() throws Exception {
        List<QuestDefinition> quests = courseQuests();
        Map<String, List<String>> dependencies = new LinkedHashMap<>();
        for (QuestDefinition quest : quests) {
            dependencies.put(quest.id(), quest.prerequisiteQuestIds());
        }

        Rect panel = FirstTorchLayout.calculate(720, 405).questMap();
        assertTrue(ParallelQuestLayout.supports(quests));
        ParallelQuestLayout.nodes(panel, quests);
        ParallelQuestLayout.connections(ParallelQuestLayout.nodes(panel, quests), quests);

        assertEquals(dependencies, quests.stream().collect(java.util.stream.Collectors.toMap(
                QuestDefinition::id, QuestDefinition::prerequisiteQuestIds, (left, right) -> left,
                LinkedHashMap::new)));
    }

    private static List<QuestDefinition> courseQuests() throws IOException {
        return load("course").chapters().getFirst().quests();
    }

    private static ch.minenox.firsttorch.guide.model.GuideDefinition load(String name) throws IOException {
        try (var input = ParallelQuestLayoutTest.class.getResourceAsStream("/data/firsttorch/guides/" + name + ".json")) {
            assertTrue(input != null, name);
            return GuideJson.read(input);
        }
    }

    private static void assertNoOverlap(Map<String, Rect> nodes) {
        List<Rect> values = List.copyOf(nodes.values());
        for (int first = 0; first < values.size(); first++) {
            for (int second = first + 1; second < values.size(); second++) {
                Rect left = values.get(first);
                Rect right = values.get(second);
                assertFalse(left.x() < right.right() && right.x() < left.right()
                        && left.y() < right.bottom() && right.y() < left.bottom());
            }
        }
    }

    private boolean isOrthogonal(ParallelQuestLayout.Segment segment) {
        return segment.x1() == segment.x2() || segment.y1() == segment.y2();
    }

    private static boolean crossesInterior(ParallelQuestLayout.Segment segment, Rect node) {
        if (segment.y1() == segment.y2()) {
            return segment.y1() > node.y() && segment.y1() < node.bottom()
                    && Math.max(segment.x1(), segment.x2()) > node.x()
                    && Math.min(segment.x1(), segment.x2()) < node.right();
        }
        return segment.x1() > node.x() && segment.x1() < node.right()
                && Math.max(segment.y1(), segment.y2()) > node.y()
                && Math.min(segment.y1(), segment.y2()) < node.bottom();
    }

    private static boolean endsAt(ParallelQuestLayout.Segment segment, Rect node) {
        // Arrow tips leave five pixels of breathing room around the medallion.
        return segment.y2() == node.centerY()
                && ((segment.x2() == node.x() - 5 && segment.x1() < segment.x2())
                || (segment.x2() == node.right() + 5 && segment.x1() > segment.x2()));
    }

    private static boolean touchesEndpoint(ParallelQuestLayout.Segment segment, Rect node) {
        return insideOrBorder(segment.x1(), segment.y1(), node) || insideOrBorder(segment.x2(), segment.y2(), node);
    }

    private static boolean insideOrBorder(int x, int y, Rect node) {
        return x >= node.x() && x <= node.right() && y >= node.y() && y <= node.bottom();
    }

}
