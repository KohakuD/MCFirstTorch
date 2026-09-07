package ch.minenox.firsttorch.network;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
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
                requireSize("quests", chapter.quests().size(), MAX_QUESTS_PER_CHAPTER);
                totalQuests += chapter.quests().size();
                requireSize("total quests", totalQuests, MAX_TOTAL_QUESTS);
                output.writeVarInt(chapter.quests().size());
                for (QuestDefinition quest : chapter.quests()) {
                    writeId(output, quest.id());
                    output.writeVarInt(quest.order());
                    writeKey(output, quest.titleKey());
                    writeKey(output, quest.descriptionKey());
                    output.writeInt(quest.position().x());
                    output.writeInt(quest.position().y());
                    requireSize("prerequisites", quest.prerequisiteQuestIds().size(), MAX_PREREQUISITES_PER_QUEST);
                    output.writeVarInt(quest.prerequisiteQuestIds().size());
                    for (String prerequisite : quest.prerequisiteQuestIds()) {
                        writeId(output, prerequisite);
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
                    QuestPosition position = new QuestPosition(input.readInt(), input.readInt());
                    int prerequisiteCount = readSize(input, "prerequisites", MAX_PREREQUISITES_PER_QUEST);
                    List<String> prerequisites = new ArrayList<>(prerequisiteCount);
                    for (int prerequisiteIndex = 0; prerequisiteIndex < prerequisiteCount; prerequisiteIndex++) {
                        prerequisites.add(input.readUtf(STABLE_ID_LENGTH));
                    }
                    quests.add(new QuestDefinition(
                            questId, questOrder, questTitleKey, questDescriptionKey, position, prerequisites));
                }
                chapters.add(new ChapterDefinition(
                        chapterId, order, chapterTitleKey, chapterDescriptionKey, quests));
            }
            guides.add(new GuideDefinition(schemaVersion, id, titleKey, descriptionKey, chapters));
        }
        try {
            return new GuideSnapshot(guides);
        } catch (IllegalArgumentException exception) {
            throw new DecoderException("Received invalid guide snapshot: " + exception.getMessage(), exception);
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
