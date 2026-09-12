package ch.minenox.firsttorch.guide.validation;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.GuideImage;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class GuideValidator {
    public static final int SCHEMA_VERSION = 1;
    public static final int MAX_TASKS_PER_QUEST = 32;
    public static final int MAX_REWARDS_PER_QUEST = 32;

    private static final int MAX_ABSOLUTE_POSITION = 1_000_000;
    private static final Pattern STABLE_ID = Pattern.compile("[0-7][0-9A-F]{15}");
    private static final Pattern ITEM_ID = Pattern.compile("[a-z0-9_.-]+:[a-z0-9/._-]+");
    private static final Pattern IMAGE_RESOURCE =
            Pattern.compile("[a-z0-9_.-]+:textures/(?:[a-z0-9_.-]+/)*[a-z0-9_.-]+\\.png");
    private static final Pattern TRANSLATION_KEY =
            Pattern.compile("[a-z0-9][a-z0-9_.-]*\\.[a-z0-9_.-]+");

    private GuideValidator() {
    }

    public static void validate(GuideDefinition guide) {
        if (guide == null) {
            fail("Guide definition must not be null.");
        }
        if (guide.schemaVersion() != SCHEMA_VERSION) {
            fail("Unsupported guide schema version: " + guide.schemaVersion());
        }

        Set<String> allIds = new HashSet<>();
        validateId(guide.id(), "Guide", allIds);
        validateTranslationKey(guide.titleKey(), "Guide title");
        validateTranslationKey(guide.descriptionKey(), "Guide description");
        if (guide.chapters() == null || guide.chapters().isEmpty()) {
            fail("Guide must contain at least one chapter.");
        }

        Set<Integer> chapterOrders = new HashSet<>();
        Map<String, QuestDefinition> questsById = new LinkedHashMap<>();
        for (ChapterDefinition chapter : guide.chapters()) {
            if (chapter == null) {
                fail("Guide must not contain a null chapter.");
            }
            validateId(chapter.id(), "Chapter", allIds);
            validateOrder(chapter.order(), "Chapter " + chapter.id(), chapterOrders);
            validateTranslationKey(chapter.titleKey(), "Chapter title");
            if (chapter.iconItemId() != null) validateItemId(chapter.iconItemId());
            validateTranslationKey(chapter.descriptionKey(), "Chapter description");
            if (chapter.quests() == null || chapter.quests().isEmpty()) {
                fail("Chapter " + chapter.id() + " must contain at least one quest.");
            }

            Set<Integer> questOrders = new HashSet<>();
            Set<QuestPosition> positions = new HashSet<>();
            for (QuestDefinition quest : chapter.quests()) {
                if (quest == null) {
                    fail("Chapter " + chapter.id() + " must not contain a null quest.");
                }
                validateId(quest.id(), "Quest", allIds);
                validateOrder(quest.order(), "Quest " + quest.id(), questOrders);
                validateTranslationKey(quest.titleKey(), "Quest title");
                validateTranslationKey(quest.descriptionKey(), "Quest description");
                if (quest.prerequisiteMode() == null) {
                    fail("Quest " + quest.id() + " must have a prerequisite mode.");
                }
                if (quest.iconItemId() != null) validateItemId(quest.iconItemId());
                if (quest.image() != null) validateImage(quest.image());
                validatePosition(chapter.id(), quest, positions);
                validateTasksAndRewards(quest, allIds);
                questsById.put(quest.id(), quest);
            }
        }

        validatePrerequisites(questsById);
        validateAcyclic(questsById);
    }

    private static void validateTasksAndRewards(QuestDefinition quest, Set<String> allIds) {
        if (quest.tasks().size() > MAX_TASKS_PER_QUEST) {
            fail("Quest " + quest.id() + " exceeds the task limit of " + MAX_TASKS_PER_QUEST + ".");
        }
        if (quest.rewards().size() > MAX_REWARDS_PER_QUEST) {
            fail("Quest " + quest.id() + " exceeds the reward limit of " + MAX_REWARDS_PER_QUEST + ".");
        }
        for (TaskDefinition task : quest.tasks()) {
            validateId(task.id(), "Task", allIds);
            if (task.titleKey() != null) {
                if (task.titleKey().length() > 256) fail("Task title must be at most 256 characters: " + task.titleKey());
                validateTranslationKey(task.titleKey(), "Task title");
            }
            if (task.type() == null) {
                fail("Task " + task.id() + " must have a supported type.");
            }
            if (task.type() != TaskDefinition.Type.ADVANCEMENT
                    && (task.advancementId() != null || task.criterion() != null)) {
                fail("Only advancement tasks may declare advancementId or criterion: " + task.id());
            }
            switch (task.type()) {
                case MANUAL -> {
                    requireNoItem(task.itemId(), "Manual task " + task.id());
                    validateAmount(task.count(), 1, "Manual task count");
                }
                case INVENTORY, INVENTORY_TAG -> {
                    validateItemId(task.itemId());
                    validateAmount(task.count(), 4096, "Inventory task count");
                }
                case ADVANCEMENT -> {
                    requireNoItem(task.itemId(), "Advancement task " + task.id());
                    validateItemId(task.advancementId());
                    validateAmount(task.count(), 1, "Advancement task count");
                    if (task.criterion() != null && (task.criterion().length() > 256
                            || !task.criterion().matches("[a-zA-Z0-9_./:-]+"))) {
                        fail("Advancement criterion must be a bounded non-empty name: " + task.id());
                    }
                }
            }
        }
        for (RewardDefinition reward : quest.rewards()) {
            validateId(reward.id(), "Reward", allIds);
            if (reward.type() == null) {
                fail("Reward " + reward.id() + " must have a supported type.");
            }
            switch (reward.type()) {
                case EXPERIENCE -> {
                    requireNoItem(reward.itemId(), "Experience reward " + reward.id());
                    validateAmount(reward.amount(), 10000, "Experience reward amount");
                }
                case ITEM -> {
                    validateItemId(reward.itemId());
                    validateAmount(reward.amount(), 4096, "Item reward amount");
                }
            }
        }
    }

    private static void validateItemId(String itemId) {
        if (itemId == null || itemId.length() > 256 || !ITEM_ID.matcher(itemId).matches()) {
            fail("Item ID must use a lowercase namespace:path identifier: " + itemId);
        }
    }

    private static void validateImage(GuideImage image) {
        String resource = image.resource();
        if (resource == null || resource.length() > 256 || resource.indexOf('\\') >= 0
                || resource.contains("/../") || !IMAGE_RESOURCE.matcher(resource).matches()) {
            fail("Guide image resource must use a lowercase namespace:textures/...png path: " + resource);
        }
        if (image.width() < 1 || image.width() > 4096 || image.height() < 1 || image.height() > 4096) {
            fail("Guide image dimensions must be between 1 and 4096.");
        }
        if (image.altKey() == null || image.altKey().length() > 256) {
            fail("Guide image alt key must be at most 256 characters: " + image.altKey());
        }
        validateTranslationKey(image.altKey(), "Guide image alt");
    }

    private static void requireNoItem(String itemId, String label) {
        if (itemId != null) {
            fail(label + " must not specify an item ID.");
        }
    }

    private static void validateAmount(int amount, int maximum, String label) {
        if (amount < 1 || amount > maximum) {
            fail(label + " must be between 1 and " + maximum + ".");
        }
    }

    private static void validateId(String id, String type, Set<String> allIds) {
        if (id == null || !STABLE_ID.matcher(id).matches()) {
            fail(type + " ID must be 16 uppercase hexadecimal characters and start with 0-7: " + id);
        }
        if (!allIds.add(id)) {
            fail("Duplicate guide object ID: " + id);
        }
    }

    private static void validateOrder(int order, String label, Set<Integer> siblingOrders) {
        if (order < 0) {
            fail(label + " order must not be negative.");
        }
        if (!siblingOrders.add(order)) {
            fail(label + " uses a duplicate sibling order: " + order);
        }
    }

    private static void validateTranslationKey(String key, String label) {
        if (key == null || key.isBlank() || !TRANSLATION_KEY.matcher(key).matches()) {
            fail(label + " must be a non-empty translation key: " + key);
        }
    }

    private static void validatePosition(
            String chapterId,
            QuestDefinition quest,
            Set<QuestPosition> positions) {
        QuestPosition position = quest.position();
        if (position == null) {
            fail("Quest " + quest.id() + " must have a position.");
        }
        if (position.x() < -MAX_ABSOLUTE_POSITION
                || position.x() > MAX_ABSOLUTE_POSITION
                || position.y() < -MAX_ABSOLUTE_POSITION
                || position.y() > MAX_ABSOLUTE_POSITION) {
            fail("Quest " + quest.id() + " position is outside the supported range.");
        }
        if (!positions.add(position)) {
            fail("Chapter " + chapterId + " contains duplicate quest position " + position + ".");
        }
    }

    private static void validatePrerequisites(Map<String, QuestDefinition> questsById) {
        for (QuestDefinition quest : questsById.values()) {
            if (quest.prerequisiteMode() == QuestDefinition.PrerequisiteMode.ANY
                    && quest.prerequisiteQuestIds().isEmpty()) {
                fail("Quest " + quest.id() + " with prerequisite mode ANY must have at least one prerequisite.");
            }
            Set<String> uniquePrerequisites = new HashSet<>();
            for (String prerequisiteId : quest.prerequisiteQuestIds()) {
                if (!uniquePrerequisites.add(prerequisiteId)) {
                    fail("Quest " + quest.id() + " contains duplicate prerequisite " + prerequisiteId + ".");
                }
                if (quest.id().equals(prerequisiteId)) {
                    fail("Quest " + quest.id() + " must not depend on itself.");
                }
                if (!questsById.containsKey(prerequisiteId)) {
                    fail("Quest " + quest.id() + " references missing prerequisite " + prerequisiteId + ".");
                }
            }
        }
    }

    private static void validateAcyclic(Map<String, QuestDefinition> questsById) {
        Map<String, VisitState> states = new HashMap<>();
        for (String questId : questsById.keySet()) {
            visit(questId, questsById, states);
        }
    }

    private static void visit(
            String questId,
            Map<String, QuestDefinition> questsById,
            Map<String, VisitState> states) {
        VisitState state = states.get(questId);
        if (state == VisitState.VISITED) {
            return;
        }
        if (state == VisitState.VISITING) {
            fail("Quest prerequisite cycle includes " + questId + ".");
        }

        states.put(questId, VisitState.VISITING);
        for (String prerequisiteId : questsById.get(questId).prerequisiteQuestIds()) {
            visit(prerequisiteId, questsById, states);
        }
        states.put(questId, VisitState.VISITED);
    }

    private static void fail(String message) {
        throw new GuideValidationException(message);
    }

    private enum VisitState {
        VISITING,
        VISITED
    }
}
