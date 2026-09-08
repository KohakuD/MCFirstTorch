package ch.minenox.firsttorch.guide.progress;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/** Pure development helper that completes one quest and its prerequisite closure. */
public final class DevelopmentQuestCompletion {
    private static final Pattern STABLE_ID = Pattern.compile("[0-7][0-9A-F]{15}");

    private DevelopmentQuestCompletion() {
    }

    /**
     * Completes the selected quest, the required prerequisite paths, and all their tasks.
     * Alternative prerequisites use an already completed path, otherwise the first authored path.
     * Existing completion state is retained verbatim, including historical IDs.
     *
     * @throws IllegalArgumentException when {@code questId} is malformed or not in the snapshot
     */
    public static ProgressState complete(GuideSnapshot snapshot, ProgressState current, String questId) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(current, "current");
        if (questId == null || !STABLE_ID.matcher(questId).matches()) {
            throw new IllegalArgumentException("Quest ID must be a stable ID: " + questId);
        }

        Map<String, QuestDefinition> questsById = new HashMap<>();
        snapshot.guides().forEach(guide -> guide.chapters().forEach(chapter ->
                chapter.quests().forEach(quest -> questsById.put(quest.id(), quest))));
        if (!questsById.containsKey(questId)) {
            throw new IllegalArgumentException("Unknown quest: " + questId);
        }

        Set<String> completedTasks = new HashSet<>(current.completedTaskIds());
        Set<String> completedQuests = new HashSet<>(current.completedQuestIds());
        Set<String> visited = new HashSet<>();
        ArrayDeque<String> pending = new ArrayDeque<>();
        pending.add(questId);
        while (!pending.isEmpty()) {
            String nextId = pending.removeFirst();
            if (!visited.add(nextId)) {
                continue;
            }
            QuestDefinition quest = questsById.get(nextId);
            if (quest == null) {
                throw new IllegalArgumentException("Unknown prerequisite quest: " + nextId);
            }
            completedQuests.add(quest.id());
            quest.tasks().forEach(task -> completedTasks.add(task.id()));
            if (quest.prerequisiteMode() == QuestDefinition.PrerequisiteMode.ANY) {
                if (!quest.prerequisitesMet(completedQuests)) {
                    quest.prerequisiteQuestIds().stream().findFirst().ifPresent(pending::add);
                }
            } else pending.addAll(quest.prerequisiteQuestIds());
        }
        return new ProgressState(completedTasks, completedQuests);
    }
}
