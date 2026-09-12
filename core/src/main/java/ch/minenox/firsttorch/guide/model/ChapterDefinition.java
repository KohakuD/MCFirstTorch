package ch.minenox.firsttorch.guide.model;

import java.util.List;

public record ChapterDefinition(
        String id,
        int order,
        String titleKey,
        String descriptionKey,
        List<QuestDefinition> quests,
        String iconItemId) {

    public ChapterDefinition(String id, int order, String titleKey, String descriptionKey,
            List<QuestDefinition> quests) {
        this(id, order, titleKey, descriptionKey, quests, null);
    }

    public ChapterDefinition {
        quests = quests == null ? null : List.copyOf(quests);
    }
}
