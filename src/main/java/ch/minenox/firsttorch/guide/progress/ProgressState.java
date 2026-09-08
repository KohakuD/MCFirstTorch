package ch.minenox.firsttorch.guide.progress;

import java.util.Set;

/** Immutable server-owned task and quest completion state. */
public record ProgressState(Set<String> completedTaskIds, Set<String> completedQuestIds) {
    public static final ProgressState EMPTY = new ProgressState(Set.of(), Set.of());

    public ProgressState {
        completedTaskIds = Set.copyOf(completedTaskIds);
        completedQuestIds = Set.copyOf(completedQuestIds);
    }
}
