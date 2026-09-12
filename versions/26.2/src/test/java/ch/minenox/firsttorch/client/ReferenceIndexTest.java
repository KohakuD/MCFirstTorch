package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.data.GuideJson;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class ReferenceIndexTest {
    @Test void indexesAllCurrentReferenceChaptersWithoutCourseLessons() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var chapters = GuideJson.read(input).chapters();
            var indexed = ReferenceIndex.chapters(chapters);
            assertEquals(17, indexed.size());
            assertEquals(67, indexed.stream().mapToInt(c -> c.quests().size()).sum());
            assertEquals(7, indexed.stream().filter(c -> c.titleKey().contains(".field_")).count());
            assertEquals(10, indexed.stream().filter(c -> c.titleKey().contains(".mechanics_")).count());
            assertEquals(indexed, chapters.stream().filter(indexed::contains).toList());
            var course = ReferenceIndex.courseChapters(chapters);
            assertEquals(chapters.size(), course.size() + indexed.size());
            assertTrue(course.stream().noneMatch(indexed::contains));
            assertEquals(course, chapters.stream().filter(c -> !indexed.contains(c)).toList());
            assertTrue(ChapterArchive.rows(course, id -> true, true).stream()
                    .filter(row -> !row.heading()).noneMatch(row -> indexed.contains(row.chapter())));
        }
    }

    @Test void cannotIntroduceHiddenChapters() throws Exception {
        assertTrue(ReferenceIndex.chapters(List.of()).isEmpty());
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var all = GuideJson.read(input).chapters();
            var reference = ReferenceIndex.chapters(all).getFirst();
            assertEquals(List.of(reference), ReferenceIndex.chapters(List.of(all.getFirst(), reference)));
        }
    }
}
