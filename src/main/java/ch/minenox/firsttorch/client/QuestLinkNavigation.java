package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.client.GuideBrowserViewModel.Selection;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.ArrayDeque;
import java.util.Optional;

/** Session-only reading history. Following a reference never changes progress or visibility. */
final class QuestLinkNavigation {
    record Location(Selection selection, int scroll, boolean reading, boolean completedExpanded, int chapterFirstRow) {}
    private final ArrayDeque<Location> history = new ArrayDeque<>();

    static Optional<Selection> destination(GuideSnapshot snapshot, ProgressPayload progress, String questId) {
        if (progress == null || !progress.available()) return Optional.empty();
        for (var guide : snapshot.guides()) {
            for (var chapter : ChapterVisibility.visibleChapters(guide, progress)) {
                if (chapter.quests().stream().anyMatch(q -> q.id().equals(questId))) {
                    return Optional.of(new Selection(guide.id(), chapter.id(), questId));
                }
            }
        }
        return Optional.empty();
    }

    void push(Location location) {
        if (history.size() == 32) history.removeLast();
        history.push(location);
    }
    Optional<Location> back(GuideSnapshot snapshot, ProgressPayload progress) {
        while (!history.isEmpty()) {
            var location = history.pop();
            if (destination(snapshot, progress, location.selection().questId())
                    .filter(location.selection()::equals).isPresent()) return Optional.of(location);
        }
        return Optional.empty();
    }
    boolean hasBack() { return !history.isEmpty(); }
    void clear() { history.clear(); }
}
