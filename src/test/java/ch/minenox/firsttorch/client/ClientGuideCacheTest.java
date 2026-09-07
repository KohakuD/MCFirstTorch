package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

final class ClientGuideCacheTest {
    @AfterEach
    void clearCache() {
        ClientGuideCache.clear();
    }

    @Test
    void installsDefensiveImmutableSnapshotAndClearsIt() {
        GuideSnapshot source = validSnapshot();

        ClientGuideCache.install(source);

        assertEquals(source, ClientGuideCache.snapshot());
        assertNotSame(source, ClientGuideCache.snapshot());
        ClientGuideCache.clear();
        assertEquals(GuideSnapshot.EMPTY, ClientGuideCache.snapshot());
    }

    private static GuideSnapshot validSnapshot() {
        QuestDefinition quest = new QuestDefinition(
                "2000000000000001", 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of());
        ChapterDefinition chapter = new ChapterDefinition(
                "1000000000000001", 0, "chapter.test.title", "chapter.test.description", List.of(quest));
        GuideDefinition guide = new GuideDefinition(
                1, "0000000000000001", "guide.test.title", "guide.test.description", List.of(chapter));
        return new GuideSnapshot(List.of(guide));
    }
}
