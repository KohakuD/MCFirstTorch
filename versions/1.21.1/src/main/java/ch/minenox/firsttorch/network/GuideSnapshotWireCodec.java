package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.GuideImage;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public final class GuideSnapshotWireCodec {
    public static final int MAX_GUIDES = 64;
    public static final int MAX_CHAPTERS_PER_GUIDE = 256;
    public static final int MAX_QUESTS_PER_CHAPTER = 4096;
    public static final int MAX_PREREQUISITES_PER_QUEST = 128;
    public static final int MAX_TOTAL_QUESTS = 16_384;
    public static final int MAX_TRANSLATION_KEY_LENGTH = 256;
    public static final int MAX_ENTRIES_PER_QUEST = 32;

    private static final int STABLE_ID_LENGTH = 16;

    private GuideSnapshotWireCodec() {
    }

    public static void encode(FriendlyByteBuf output, GuideSnapshot snapshot) {
        requireSize("guides", snapshot.guides().size(), MAX_GUIDES);
        output.writeVarInt(snapshot.guides().size());
        int totalQuests = 0;
        for (GuideDefinition guide : snapshot.guides()) {
            output.writeVarInt(guide.schemaVersion());
            writeId(output, guide.id());
            writeKey(output, guide.titleKey());
            writeKey(output, guide.descriptionKey());
            requireSize("chapters", guide.chapters().size(), MAX_CHAPTERS_PER_GUIDE);
            output.writeVarInt(guide.chapters().size());
            for (ChapterDefinition chapter : guide.chapters()) {
                writeId(output, chapter.id());
                output.writeVarInt(chapter.order());
                writeKey(output, chapter.titleKey());
                writeKey(output, chapter.descriptionKey());
                output.writeUtf(chapter.iconItemId() == null ? "" : chapter.iconItemId(), 256);
                requireSize("quests", chapter.quests().size(), MAX_QUESTS_PER_CHAPTER);
                totalQuests += chapter.quests().size();
                requireSize("total quests", totalQuests, MAX_TOTAL_QUESTS);
                output.writeVarInt(chapter.quests().size());
                for (QuestDefinition quest : chapter.quests()) {
                    writeId(output, quest.id());
                    output.writeVarInt(quest.order());
                    writeKey(output, quest.titleKey());
                    writeKey(output, quest.descriptionKey());
                    output.writeUtf(quest.iconItemId() == null ? "" : quest.iconItemId(), 256);
                    output.writeBoolean(quest.image() != null);
                    if (quest.image() != null) {
                        output.writeUtf(quest.image().resource(), 256);
                        output.writeVarInt(quest.image().width());
                        output.writeVarInt(quest.image().height());
                        output.writeUtf(quest.image().altKey(), 256);
                    }
                    output.writeInt(quest.position().x());
                    output.writeInt(quest.position().y());
                    requireSize("prerequisites", quest.prerequisiteQuestIds().size(), MAX_PREREQUISITES_PER_QUEST);
                    output.writeVarInt(quest.prerequisiteQuestIds().size());
                    for (String prerequisite : quest.prerequisiteQuestIds()) {
                        writeId(output, prerequisite);
                    }
                    output.writeUtf(quest.prerequisiteMode().name(), 16);
                    requireSize("tasks", quest.tasks().size(), MAX_ENTRIES_PER_QUEST);
                    output.writeVarInt(quest.tasks().size());
                    for (TaskDefinition task : quest.tasks()) {
                        writeId(output, task.id());
                        output.writeUtf(task.type().name(), 16);
                        output.writeUtf(task.itemId() == null ? "" : task.itemId(), 256);
                        output.writeVarInt(task.count());
                        output.writeUtf(task.titleKey() == null ? "" : task.titleKey(), MAX_TRANSLATION_KEY_LENGTH);
                        output.writeUtf(task.advancementId() == null ? "" : task.advancementId(), 256);
                        output.writeUtf(task.criterion() == null ? "" : task.criterion(), 256);
                    }
                    requireSize("rewards", quest.rewards().size(), MAX_ENTRIES_PER_QUEST);
                    output.writeVarInt(quest.rewards().size());
                    for (RewardDefinition reward : quest.rewards()) {
                        writeId(output, reward.id());
                        output.writeUtf(reward.type().name(), 16);
                        output.writeUtf(reward.itemId() == null ? "" : reward.itemId(), 256);
                        output.writeVarInt(reward.amount());
                    }
                }
            }
        }
    }

    public static GuideSnapshot decode(FriendlyByteBuf input) {
        int guideCount = readSize(input, "guides", MAX_GUIDES);
        List<GuideDefinition> guides = new ArrayList<>(guideCount);
        int totalQuests = 0;
        for (int guideIndex = 0; guideIndex < guideCount; guideIndex++) {
            int schemaVersion = input.readVarInt();
            String id = input.readUtf(STABLE_ID_LENGTH);
            String titleKey = input.readUtf(MAX_TRANSLATION_KEY_LENGTH);
            String descriptionKey = input.readUtf(MAX_TRANSLATION_KEY_LENGTH);
            int chapterCount = readSize(input, "chapters", MAX_CHAPTERS_PER_GUIDE);
            List<ChapterDefinition> chapters = new ArrayList<>(chapterCount);
            for (int chapterIndex = 0; chapterIndex < chapterCount; chapterIndex++) {
                String chapterId = input.readUtf(STABLE_ID_LENGTH);
                int order = input.readVarInt();
                String chapterTitleKey = input.readUtf(MAX_TRANSLATION_KEY_LENGTH);
                String chapterDescriptionKey = input.readUtf(MAX_TRANSLATION_KEY_LENGTH);
                String chapterIconItemId = input.readUtf(256);
                int questCount = readSize(input, "quests", MAX_QUESTS_PER_CHAPTER);
                totalQuests += questCount;
                if (totalQuests > MAX_TOTAL_QUESTS) {
                    throw new DecoderException("Guide snapshot exceeds maximum total quests of " + MAX_TOTAL_QUESTS + ".");
                }
                List<QuestDefinition> quests = new ArrayList<>(questCount);
                for (int questIndex = 0; questIndex < questCount; questIndex++) {
                    String questId = input.readUtf(STABLE_ID_LENGTH);
                    int questOrder = input.readVarInt();
                    String questTitleKey = input.readUtf(MAX_TRANSLATION_KEY_LENGTH);
                    String questDescriptionKey = input.readUtf(MAX_TRANSLATION_KEY_LENGTH);
                    String iconItemId = input.readUtf(256);
                    GuideImage image = null;
                    if (input.readBoolean()) {
                        image = new GuideImage(input.readUtf(256), input.readVarInt(), input.readVarInt(), input.readUtf(256));
                    }
                    QuestPosition position = new QuestPosition(input.readInt(), input.readInt());
                    int prerequisiteCount = readSize(input, "prerequisites", MAX_PREREQUISITES_PER_QUEST);
                    List<String> prerequisites = new ArrayList<>(prerequisiteCount);
                    for (int prerequisiteIndex = 0; prerequisiteIndex < prerequisiteCount; prerequisiteIndex++) {
                        prerequisites.add(input.readUtf(STABLE_ID_LENGTH));
                    }
                    QuestDefinition.PrerequisiteMode prerequisiteMode = readType(input, QuestDefinition.PrerequisiteMode.class);
                    int taskCount = readSize(input, "tasks", MAX_ENTRIES_PER_QUEST);
                    List<TaskDefinition> tasks = new ArrayList<>(taskCount);
                    for (int i = 0; i < taskCount; i++) {
                        String taskId = input.readUtf(STABLE_ID_LENGTH);
                        TaskDefinition.Type type = readType(input, TaskDefinition.Type.class);
                        String itemId = input.readUtf(256);
                        int count = input.readVarInt();
                        String taskTitleKey = input.readUtf(MAX_TRANSLATION_KEY_LENGTH);
                        String advancementId = input.readUtf(256);
                        String criterion = input.readUtf(256);
                        tasks.add(new TaskDefinition(taskId, type, itemId.isEmpty() ? null : itemId, count,
                                taskTitleKey.isEmpty() ? null : taskTitleKey,
                                advancementId.isEmpty() ? null : advancementId, criterion.isEmpty() ? null : criterion));
                    }
                    int rewardCount = readSize(input, "rewards", MAX_ENTRIES_PER_QUEST);
                    List<RewardDefinition> rewards = new ArrayList<>(rewardCount);
                    for (int i = 0; i < rewardCount; i++) {
                        String rewardId = input.readUtf(STABLE_ID_LENGTH);
                        RewardDefinition.Type type = readType(input, RewardDefinition.Type.class);
                        String itemId = input.readUtf(256);
                        rewards.add(new RewardDefinition(rewardId, type, itemId.isEmpty() ? null : itemId, input.readVarInt()));
                    }
                    quests.add(new QuestDefinition(
                            questId, questOrder, questTitleKey, questDescriptionKey, position, prerequisites, tasks, rewards,
                            iconItemId.isEmpty() ? null : iconItemId, image, prerequisiteMode));
                }
                chapters.add(new ChapterDefinition(
                        chapterId, order, chapterTitleKey, chapterDescriptionKey, quests,
                        chapterIconItemId.isEmpty() ? null : chapterIconItemId));
            }
            guides.add(new GuideDefinition(schemaVersion, id, titleKey, descriptionKey, chapters));
        }
        try {
            return new GuideSnapshot(guides);
        } catch (IllegalArgumentException exception) {
            throw new DecoderException("Received invalid guide snapshot: " + exception.getMessage(), exception);
        }
    }

    private static <T extends Enum<T>> T readType(FriendlyByteBuf input, Class<T> type) {
        String value = input.readUtf(16);
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException exception) {
            throw new DecoderException("Unknown guide entry type: " + value, exception);
        }
    }

    private static int readSize(FriendlyByteBuf input, String label, int maximum) {
        int size = input.readVarInt();
        if (size < 0 || size > maximum) {
            throw new DecoderException("Guide snapshot " + label + " count " + size
                    + " is outside the allowed range 0-" + maximum + ".");
        }
        return size;
    }

    private static void requireSize(String label, int size, int maximum) {
        if (size < 0 || size > maximum) {
            throw new EncoderException("Guide snapshot " + label + " count " + size
                    + " is outside the allowed range 0-" + maximum + ".");
        }
    }

    private static void writeId(FriendlyByteBuf output, String value) {
        output.writeUtf(value, STABLE_ID_LENGTH);
    }

    private static void writeKey(FriendlyByteBuf output, String value) {
        output.writeUtf(value, MAX_TRANSLATION_KEY_LENGTH);
    }
}
