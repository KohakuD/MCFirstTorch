package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Session transition detection; initial saves, reconnects and reloads are baselines, not celebrations. */
final class ChapterCompletionTracker {
    private Set<String> previous;
    private final Set<String> celebrated = new HashSet<>();

    List<ChapterDefinition> observe(GuideSnapshot guides, ProgressPayload progress) {
        if (progress == null || !progress.available() || guides.guides().isEmpty()) return List.of();
        var completed = guides.guides().stream().flatMap(g -> g.chapters().stream())
                .filter(c -> !c.quests().isEmpty() && c.quests().stream()
                        .allMatch(q -> progress.state().completedQuestIds().contains(q.id()))).toList();
        Set<String> current = new HashSet<>();
        completed.forEach(c -> current.add(c.id()));
        List<ChapterDefinition> newlyCompleted = previous == null ? List.of() : completed.stream()
                .filter(c -> !previous.contains(c.id()) && !celebrated.contains(c.id())).toList();
        celebrated.addAll(current);
        previous = current;
        return newlyCompleted;
    }

    void clear() { previous = null; celebrated.clear(); }
}
