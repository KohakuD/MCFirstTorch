package ch.minenox.firsttorch.guide.validation;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GuideSetValidator {
    private GuideSetValidator() {
    }

    public static void validate(List<GuideDefinition> guides) {
        if (guides == null) {
            throw new GuideValidationException("Guide set must not be null.");
        }

        Set<String> globalIds = new HashSet<>();
        for (GuideDefinition guide : guides) {
            GuideValidator.validate(guide);
            add(globalIds, guide.id());
            for (ChapterDefinition chapter : guide.chapters()) {
                add(globalIds, chapter.id());
                for (QuestDefinition quest : chapter.quests()) {
                    add(globalIds, quest.id());
                    quest.tasks().forEach(task -> add(globalIds, task.id()));
                    quest.rewards().forEach(reward -> add(globalIds, reward.id()));
                }
            }
        }
    }

    private static void add(Set<String> ids, String id) {
        if (!ids.add(id)) {
            throw new GuideValidationException("Duplicate guide object ID across loaded guides: " + id);
        }
    }
}
