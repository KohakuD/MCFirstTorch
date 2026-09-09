package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Every practical circuit card keeps its build or observation illustration. */
final class RedstoneArtworkCoverageTest {
    @Test void everyCircuitExerciseHasArtwork() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var chapters = GuideJson.read(input).chapters().stream()
                    .filter(c -> c.titleKey().startsWith("chapter.firsttorch.redstone_")).toList();
            assertEquals(6, chapters.size());
            var setupOnly = Set.of("quest.firsttorch.redstone_basics.bench.title");
            int illustrated = 0;
            for (var chapter : chapters) {
                for (var quest : chapter.quests()) {
                    if (setupOnly.contains(quest.titleKey()) || quest.tasks().stream().allMatch(t -> t.automatic())) continue;
                    assertNotNull(quest.image(), quest.titleKey());
                    assertEquals(1672, quest.image().width());
                    assertEquals(941, quest.image().height());
                    illustrated++;
                }
            }
            assertEquals(17, illustrated);
        }
    }
}
