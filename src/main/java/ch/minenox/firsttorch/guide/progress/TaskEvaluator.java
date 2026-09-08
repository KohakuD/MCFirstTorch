package ch.minenox.firsttorch.guide.progress;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;

/** Pure evaluation; inventory is observed, never consumed, and no rewards are granted. */
public final class TaskEvaluator {
    private TaskEvaluator() {
    }

    /**
     * Retains known completions even if requirements change, but drops IDs absent from the
     * current snapshot. Empty-task quests do not automatically complete. Counts are sampled
     * once per inventory key for this evaluation, so prerequisite traversal cannot change the observation.
     */
    public static ProgressState evaluate(GuideSnapshot snapshot, ProgressState state,
            ToIntFunction<String> inventoryCounts) {
        Objects.requireNonNull(inventoryCounts, "inventoryCounts");
        List<QuestDefinition> quests = quests(snapshot);
        Set<String> knownQuests = new HashSet<>();
        Set<String> knownTasks = new HashSet<>();
        for (QuestDefinition quest : quests) {
            knownQuests.add(quest.id());
            quest.tasks().forEach(task -> knownTasks.add(task.id()));
        }
        Set<String> completedTasks = new HashSet<>(state.completedTaskIds());
        Set<String> completedQuests = new HashSet<>(state.completedQuestIds());
        completedTasks.retainAll(knownTasks);
        completedQuests.retainAll(knownQuests);
        Map<String, Integer> counts = new HashMap<>();
        // Automatic observations are facts about the player, not about the current place in
        // the course. Retain them now, but only turn their quest into a completion after its
        // prerequisite chain has opened.
        for (QuestDefinition quest : quests) {
            for (TaskDefinition task : quest.tasks()) {
                if (task.automatic()
                        && !completedTasks.contains(task.id())
                        && counts.computeIfAbsent(task.inventoryKey(),
                                item -> Math.max(0, inventoryCounts.applyAsInt(item))) >= task.count()) {
                    completedTasks.add(task.id());
                }
            }
        }
        boolean changed;
        do {
            changed = false;
            for (QuestDefinition quest : quests) {
                if (!quest.prerequisitesMet(completedQuests)) {
                    continue;
                }
                if (!quest.tasks().isEmpty()
                        && quest.tasks().stream().allMatch(task -> completedTasks.contains(task.id()))) {
                    changed |= completedQuests.add(quest.id());
                }
            }
        } while (changed);
        return new ProgressState(completedTasks, completedQuests);
    }

    /** Rejects unknown, mismatched, non-manual, or locked requests before completing a task. */
    public static ProgressState confirm(GuideSnapshot snapshot, ProgressState state,
            String questId, String taskId, ToIntFunction<String> inventoryCounts) {
        QuestDefinition quest = quests(snapshot).stream().filter(value -> value.id().equals(questId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown quest: " + questId));
        TaskDefinition task = quest.tasks().stream().filter(value -> value.id().equals(taskId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown task for quest: " + taskId));
        if (task.type() != TaskDefinition.Type.MANUAL) {
            throw new IllegalArgumentException("Task is not manual: " + taskId);
        }
        Map<String, Integer> counts = new HashMap<>();
        ToIntFunction<String> observedCounts = item -> counts.computeIfAbsent(item, inventoryCounts::applyAsInt);
        ProgressState evaluated = evaluate(snapshot, state, observedCounts);
        if (!quest.prerequisitesMet(evaluated.completedQuestIds())) {
            throw new IllegalArgumentException("Quest is locked: " + questId);
        }
        Set<String> completedTasks = new HashSet<>(evaluated.completedTaskIds());
        completedTasks.add(taskId);
        return evaluate(snapshot, new ProgressState(completedTasks, evaluated.completedQuestIds()), observedCounts);
    }

    private static List<QuestDefinition> quests(GuideSnapshot snapshot) {
        return snapshot.guides().stream().flatMap(guide -> guide.chapters().stream())
                .flatMap(chapter -> chapter.quests().stream()).toList();
    }
}
