package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.HashMap;
import java.util.Map;

/** Projects one server inventory observation without changing completions or inventory. */
public final class ProgressObservation {
    private ProgressObservation() {}

    /** Reward storage failures block payouts, not reading or completing lessons. */
    static ProgressPayload withClaims(ProgressPayload progress, java.util.Set<String> claimed,
            java.util.Set<String> pending, boolean rewardsAvailable, java.util.Set<String> rewardIds) {
        if (!progress.available()) return progress;
        var blocked = new java.util.HashSet<>(pending);
        if (!rewardsAvailable) blocked.addAll(rewardIds);
        blocked.removeAll(claimed);
        return new ProgressPayload(progress.state(), progress.taskCounts(), true, claimed, blocked);
    }

    public static ProgressPayload create(GuideSnapshot guide, ProgressState state, Map<String, Integer> inventory) {
        Map<String, Integer> taskCounts = new HashMap<>();
        guide.guides().forEach(book -> book.chapters().forEach(chapter -> chapter.quests().forEach(quest -> {
            for (TaskDefinition task : quest.tasks()) {
                int count = state.completedTaskIds().contains(task.id()) ? task.count()
                        : task.automatic()
                        ? Math.clamp(inventory.getOrDefault(task.inventoryKey(), 0), 0, task.count()) : 0;
                taskCounts.put(task.id(), count);
            }
        })));
        try {
            return new ProgressPayload(state, taskCounts, true);
        } catch (IllegalArgumentException exception) {
            // Completions still persist when the current guide exceeds the transport budget.
            return ProgressPayload.UNAVAILABLE;
        }
    }
}
