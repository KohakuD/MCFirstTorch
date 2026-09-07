package ch.minenox.firsttorch.guide.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import java.util.List;
import org.junit.jupiter.api.Test;

final class GuideValidatorTest {
    private static final String GUIDE_ID = "0A13F17C00000001";
    private static final String CHAPTER_ID = "1A13F17C00000001";
    private static final String QUEST_A_ID = "2A13F17C00000001";
    private static final String QUEST_B_ID = "2A13F17C00000002";

    @Test
    void acceptsValidGuide() {
        assertDoesNotThrow(() -> GuideValidator.validate(validGuide()));
    }

    @Test
    void rejectsInvalidStableId() {
        GuideDefinition guide = guideWithChapters(List.of(new ChapterDefinition(
                "8A13F17C00000001", 0, "chapter.valid.title", "chapter.valid.description", List.of(questA()))));

        assertInvalid(guide, "16 uppercase hexadecimal");
    }

    @Test
    void rejectsDuplicateObjectId() {
        GuideDefinition guide = guideWithChapters(List.of(new ChapterDefinition(
                GUIDE_ID, 0, "chapter.valid.title", "chapter.valid.description", List.of(questA()))));

        assertInvalid(guide, "Duplicate guide object ID");
    }

    @Test
    void rejectsMissingPrerequisite() {
        QuestDefinition quest = quest(QUEST_B_ID, 1, 2, 0, List.of("3A13F17C00000009"));
        GuideDefinition guide = guideWithQuests(questA(), quest);

        assertInvalid(guide, "missing prerequisite");
    }

    @Test
    void rejectsSelfReference() {
        QuestDefinition quest = quest(QUEST_A_ID, 0, 0, 0, List.of(QUEST_A_ID));
        GuideDefinition guide = guideWithQuests(quest);

        assertInvalid(guide, "must not depend on itself");
    }

    @Test
    void rejectsPrerequisiteCycle() {
        QuestDefinition questA = quest(QUEST_A_ID, 0, 0, 0, List.of(QUEST_B_ID));
        QuestDefinition questB = quest(QUEST_B_ID, 1, 2, 0, List.of(QUEST_A_ID));
        GuideDefinition guide = guideWithQuests(questA, questB);

        assertInvalid(guide, "cycle");
    }

    @Test
    void rejectsDuplicatePositionWithinChapter() {
        QuestDefinition questB = quest(QUEST_B_ID, 1, 0, 0, List.of());
        GuideDefinition guide = guideWithQuests(questA(), questB);

        assertInvalid(guide, "duplicate quest position");
    }

    @Test
    void rejectsNonTranslationText() {
        QuestDefinition quest = new QuestDefinition(
                QUEST_A_ID,
                0,
                "Visible title instead of a key",
                "quest.valid.description",
                new QuestPosition(0, 0),
                List.of());
        GuideDefinition guide = guideWithQuests(quest);

        assertInvalid(guide, "translation key");
    }

    @Test
    void rejectsNegativeOrder() {
        QuestDefinition quest = quest(QUEST_A_ID, -1, 0, 0, List.of());
        GuideDefinition guide = guideWithQuests(quest);

        assertInvalid(guide, "order must not be negative");
    }

    @Test
    void rejectsDuplicateSiblingOrder() {
        QuestDefinition questB = quest(QUEST_B_ID, 0, 2, 0, List.of());
        GuideDefinition guide = guideWithQuests(questA(), questB);

        assertInvalid(guide, "duplicate sibling order");
    }

    @Test
    void rejectsUnreasonablePosition() {
        QuestDefinition quest = quest(QUEST_A_ID, 0, 1_000_001, 0, List.of());
        GuideDefinition guide = guideWithQuests(quest);

        assertInvalid(guide, "outside the supported range");
    }

    @Test
    void rejectsUnsupportedSchemaVersion() {
        GuideDefinition guide = new GuideDefinition(
                2,
                GUIDE_ID,
                "guide.valid.title",
                "guide.valid.description",
                validGuide().chapters());

        assertInvalid(guide, "Unsupported guide schema version");
    }

    private static GuideDefinition validGuide() {
        return guideWithQuests(
                questA(),
                quest(QUEST_B_ID, 1, 2, 0, List.of(QUEST_A_ID)));
    }

    private static GuideDefinition guideWithQuests(QuestDefinition... quests) {
        return guideWithChapters(List.of(new ChapterDefinition(
                CHAPTER_ID,
                0,
                "chapter.valid.title",
                "chapter.valid.description",
                List.of(quests))));
    }

    private static GuideDefinition guideWithChapters(List<ChapterDefinition> chapters) {
        return new GuideDefinition(
                GuideValidator.SCHEMA_VERSION,
                GUIDE_ID,
                "guide.valid.title",
                "guide.valid.description",
                chapters);
    }

    private static QuestDefinition questA() {
        return quest(QUEST_A_ID, 0, 0, 0, List.of());
    }

    private static QuestDefinition quest(
            String id,
            int order,
            int x,
            int y,
            List<String> prerequisites) {
        return new QuestDefinition(
                id,
                order,
                "quest.valid.title",
                "quest.valid.description",
                new QuestPosition(x, y),
                prerequisites);
    }

    private static void assertInvalid(GuideDefinition guide, String expectedMessage) {
        GuideValidationException exception = assertThrows(
                GuideValidationException.class,
                () -> GuideValidator.validate(guide));
        assertTrue(exception.getMessage().contains(expectedMessage), exception.getMessage());
    }
}
