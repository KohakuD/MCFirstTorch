package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Detects server-confirmed automatic quest completions without treating a snapshot as an event. */
final class AutomaticQuestCompletionTracker {
    record Completion(String questId, String titleKey, boolean rewardAvailable) {}

    private Set<String> previous;
    private final Set<String> announced = new HashSet<>();

    List<Completion> observe(GuideSnapshot guides, ProgressPayload progress) {
        if (progress == null || !progress.available() || guides.guides().isEmpty()) {
            // A missing or unavailable observation is a discontinuity, never an event source.
            previous = null;
            return List.of();
        }
        Set<String> current = progress.state().completedQuestIds();
        if (previous == null) {
            previous = Set.copyOf(current);
            announced.addAll(current);
            return List.of();
        }
        var completions = guides.guides().stream().flatMap(guide -> guide.chapters().stream())
                .flatMap(chapter -> chapter.quests().stream())
                .filter(quest -> current.contains(quest.id()) && !previous.contains(quest.id()))
                .filter(quest -> quest.tasks().stream().anyMatch(task -> task.automatic()))
                .filter(quest -> announced.add(quest.id()))
                .map(quest -> new Completion(quest.id(), quest.titleKey(), rewardAvailable(quest, progress)))
                .toList();
        previous = Set.copyOf(current);
        return completions;
    }

    private static boolean rewardAvailable(QuestDefinition quest, ProgressPayload progress) {
        return !quest.rewards().isEmpty()
                && !progress.claimedQuestIds().contains(quest.id())
                && !progress.pendingQuestIds().contains(quest.id())
                && quest.prerequisitesMet(progress.state().completedQuestIds());
    }

    void clear() { previous = null; announced.clear(); }
}
