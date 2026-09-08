package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.model.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class ChapterArchiveTest {
    @Test void collapsedGroupKeepsActiveChaptersAndCountsCompletedOnes() {
        var chapters = List.of(chapter("a"), chapter("b"), chapter("c"));
        var rows = ChapterArchive.rows(chapters, id -> !id.equals("b"), false);
        assertEquals(2, rows.size());
        assertEquals("b", rows.getLast().chapter().id());
        assertTrue(rows.getFirst().heading());
        assertEquals(2, rows.getFirst().completedCount());
        assertEquals(3, chapters.size());
        var expanded = ChapterArchive.rows(chapters, id -> !id.equals("b"), true);
        assertTrue(expanded.getFirst().heading());
        assertEquals(List.of("a", "c", "b"), expanded.stream().filter(r -> !r.heading()).map(r -> r.chapter().id()).toList());
    }
    @Test void expandedGroupRetainsOriginalOrderAndAllCompletedCoursesRemainReachable() {
        var chapters = List.of(chapter("a"), chapter("b"));
        var rows = ChapterArchive.rows(chapters, id -> true, true);
        assertEquals(3, rows.size());
        assertTrue(rows.getFirst().heading());
        assertEquals(chapters, rows.stream().filter(r -> !r.heading()).map(ChapterArchive.Row::chapter).toList());
        assertEquals(1, ChapterArchive.rows(chapters, id -> true, false).size());
        assertEquals(2, ChapterArchive.rows(chapters, id -> false, false).size());
        assertTrue(ChapterArchive.rows(List.of(), id -> true, true).isEmpty());
    }
    private static ChapterDefinition chapter(String id) {
        return new ChapterDefinition(id, 0, "title", "description", List.of(
                new QuestDefinition(id, 0, "title", "description", new QuestPosition(0, 0), List.of())));
    }
}
