package ch.minenox.firsttorch.guide.model;

import java.util.List;

public record QuestDefinition(
        String id,
        int order,
        String titleKey,
        String descriptionKey,
        QuestPosition position,
        List<String> prerequisiteQuestIds) {

    public QuestDefinition {
        prerequisiteQuestIds = prerequisiteQuestIds == null
                ? List.of()
                : List.copyOf(prerequisiteQuestIds);
    }
}
