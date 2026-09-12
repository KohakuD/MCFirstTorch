package ch.minenox.firsttorch.guide.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public record QuestDefinition(
        String id,
        int order,
        String titleKey,
        String descriptionKey,
        QuestPosition position,
        List<String> prerequisiteQuestIds,
        List<TaskDefinition> tasks,
        List<RewardDefinition> rewards,
        String iconItemId,
        GuideImage image,
        PrerequisiteMode prerequisiteMode) {

    public enum PrerequisiteMode {
        ALL,
        ANY
    }

    public QuestDefinition(String id, int order, String titleKey, String descriptionKey,
            QuestPosition position, List<String> prerequisiteQuestIds,
            List<TaskDefinition> tasks, List<RewardDefinition> rewards, String iconItemId, GuideImage image) {
        this(id, order, titleKey, descriptionKey, position, prerequisiteQuestIds, tasks, rewards, iconItemId, image,
                PrerequisiteMode.ALL);
    }

    public QuestDefinition(String id, int order, String titleKey, String descriptionKey,
            QuestPosition position, List<String> prerequisiteQuestIds,
            List<TaskDefinition> tasks, List<RewardDefinition> rewards, String iconItemId) {
        this(id, order, titleKey, descriptionKey, position, prerequisiteQuestIds, tasks, rewards, iconItemId, null);
    }

    public QuestDefinition(String id, int order, String titleKey, String descriptionKey,
            QuestPosition position, List<String> prerequisiteQuestIds,
            List<TaskDefinition> tasks, List<RewardDefinition> rewards) {
        this(id, order, titleKey, descriptionKey, position, prerequisiteQuestIds, tasks, rewards, null, null);
    }

    public QuestDefinition(
            String id, int order, String titleKey, String descriptionKey,
            QuestPosition position, List<String> prerequisiteQuestIds) {
        this(id, order, titleKey, descriptionKey, position, prerequisiteQuestIds, List.of(), List.of());
    }

    public QuestDefinition {
        prerequisiteQuestIds = prerequisiteQuestIds == null
                ? List.of()
                : List.copyOf(prerequisiteQuestIds);
        tasks = tasks == null ? List.of() : List.copyOf(tasks);
        rewards = rewards == null ? List.of() : List.copyOf(rewards);
        prerequisiteMode = prerequisiteMode == null ? PrerequisiteMode.ALL : prerequisiteMode;
    }

    public boolean prerequisitesMet(Predicate<String> completed) {
        Objects.requireNonNull(completed, "completed predicate must not be null");
        return prerequisiteMode == PrerequisiteMode.ALL
                ? prerequisiteQuestIds.stream().allMatch(completed)
                : prerequisiteQuestIds.stream().anyMatch(completed);
    }

    public boolean prerequisitesMet(Set<String> completedQuestIds) {
        Objects.requireNonNull(completedQuestIds, "completed quest IDs must not be null");
        return prerequisitesMet(completedQuestIds::contains);
    }
}
