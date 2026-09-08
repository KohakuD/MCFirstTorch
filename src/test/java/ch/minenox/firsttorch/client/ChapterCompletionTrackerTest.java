package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.data.GuideJson;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class ChapterCompletionTrackerTest {
    private GuideSnapshot course() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }

    private ProgressPayload progress(Set<String> quests) {
        return new ProgressPayload(new ProgressState(Set.of(), quests), Map.of(), true);
    }

    @Test void celebratesOnlyTheNewCompletionOnceRegardlessOfRewardUpdates() throws Exception {
        var guides = course();
        var chapter = guides.guides().getFirst().chapters().getFirst();
        var all = chapter.quests().stream().map(q -> q.id()).collect(Collectors.toSet());
        var partial = chapter.quests().stream().skip(1).map(q -> q.id()).collect(Collectors.toSet());
        var tracker = new ChapterCompletionTracker();
        assertTrue(tracker.observe(guides, progress(partial)).isEmpty());
        assertEquals(List.of(chapter), tracker.observe(guides, progress(all)));
        assertTrue(tracker.observe(guides, progress(all)).isEmpty());
        assertTrue(tracker.observe(guides, new ProgressPayload(progress(all).state(), Map.of(), true, all, Set.of())).isEmpty());
        tracker.observe(guides, progress(Set.of()));
        assertTrue(tracker.observe(guides, progress(all)).isEmpty());
    }

    @Test void existingSaveReloadAndReconnectNeverReplay() throws Exception {
        var guides = course();
        var all = guides.guides().getFirst().chapters().stream().flatMap(c -> c.quests().stream())
                .map(q -> q.id()).collect(Collectors.toSet());
        var tracker = new ChapterCompletionTracker();
        assertTrue(tracker.observe(guides, ProgressPayload.UNAVAILABLE).isEmpty());
        assertTrue(tracker.observe(guides, progress(all)).isEmpty());
        tracker.clear();
        assertTrue(tracker.observe(guides, progress(all)).isEmpty());
    }

    @Test void unavailableOrMissingDefinitionsCannotCreateFalseTransitions() throws Exception {
        var guides = course();
        var tracker = new ChapterCompletionTracker();
        var all = guides.guides().getFirst().chapters().getFirst().quests().stream().map(q -> q.id()).collect(Collectors.toSet());
        assertTrue(tracker.observe(GuideSnapshot.EMPTY, progress(all)).isEmpty());
        assertTrue(tracker.observe(guides, progress(all)).isEmpty());
        assertTrue(tracker.observe(guides, ProgressPayload.UNAVAILABLE).isEmpty());
        assertTrue(tracker.observe(guides, progress(all)).isEmpty());
    }
}
