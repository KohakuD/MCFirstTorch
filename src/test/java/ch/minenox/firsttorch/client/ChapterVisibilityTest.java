package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.minenox.firsttorch.guide.data.GuideJson;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class ChapterVisibilityTest {
    @Test void brewingUnlocksOptionalFireResistanceAndEyeSuppliesTogether() throws Exception {
        var visible = ChapterVisibility.visibleChapters(course(), available(quests("5620D39F7A4E18B5")))
                .stream().map(ChapterDefinition::id).toList();
        org.junit.jupiter.api.Assertions.assertTrue(visible.containsAll(List.of("788FB257E40C913E", "746B17D3AE825CF0")));
        org.junit.jupiter.api.Assertions.assertFalse(visible.contains("7990C368F51DA24F"));
        var prepared = ChapterVisibility.visibleChapters(course(), available(quests("361FD38F593D07B4")))
                .stream().map(ChapterDefinition::id).toList();
        org.junit.jupiter.api.Assertions.assertTrue(prepared.contains("7990C368F51DA24F"));
    }

    @Test void animalsOpenFromBreadWithoutComposting() throws Exception {
        var chapters = ChapterVisibility.visibleChapters(course(), available(quests("26EC824FB71D3590")));
        org.junit.jupiter.api.Assertions.assertTrue(chapters.stream().anyMatch(c -> c.id().equals("1BD93F587CE4062A")));
    }

    @Test void farmingOpensFromFoodReserveWithoutIronOrMaps() throws Exception {
        var chapters = ChapterVisibility.visibleChapters(course(), available(quests("6C03B5E98A417DF2")));
        org.junit.jupiter.api.Assertions.assertTrue(chapters.stream().anyMatch(c -> c.id().equals("5D91A7C30E624BF8")));
    }

    @Test void findingHomeNeedsSafeReturnButNotIronRecap() throws Exception {
        var before = ChapterVisibility.visibleChapters(course(), available(quests("3F215C6E03ABD479")));
        org.junit.jupiter.api.Assertions.assertFalse(before.stream().anyMatch(c -> c.id().equals("3B75D9F20C8E4A61")));
        var after = ChapterVisibility.visibleChapters(course(), available(quests("36CF412575EB038D")));
        org.junit.jupiter.api.Assertions.assertTrue(after.stream().anyMatch(c -> c.id().equals("3B75D9F20C8E4A61")));
    }

    @Test void ironEssentialsIsVisibleAfterIronWithoutMiningCompletion() throws Exception {
        var visible = ChapterVisibility.visibleChapters(course(), available(quests("3C16B9E50A724DF8")));
        org.junit.jupiter.api.Assertions.assertTrue(visible.stream().anyMatch(c -> c.id().equals("4C86EA031D9F5B72")));
        org.junit.jupiter.api.Assertions.assertFalse(visible.stream().anyMatch(c -> c.id().equals("5D97FB143EA0628C")));
    }

    private static final List<String> COURSE_CHAPTERS = List.of(
            "0F91A2B3C4D5E607", "01F57C0E3B9D2468", "0C9E12A4B6D83F70", "1D8F4C2A7B9E6053");
    private static final String WELCOME_READY = "17A923456789AB3C";
    private static final String HANDS = "34A8023B6ECF5791";
    private static final String CRAFTING_TABLE = "56CA245D80EB7913";
    private static final String PICKAXE_TASK = "29FD5780B31EAC46";

    @Test
    void bundledCourseStartsWithWelcomeOnly() throws Exception {
        assertVisible(course(), available(ProgressState.EMPTY), COURSE_CHAPTERS.getFirst());
    }

    @Test
    void completingWelcomeUnlocksControls() throws Exception {
        assertVisible(course(), available(quests(WELCOME_READY)), COURSE_CHAPTERS.get(0), COURSE_CHAPTERS.get(1));
    }

    @Test
    void handsUnlockShelterWithoutMovementLessons() throws Exception {
        assertVisible(course(), available(quests(HANDS)),
                COURSE_CHAPTERS.get(0), COURSE_CHAPTERS.get(1), COURSE_CHAPTERS.get(2));
    }

    @Test
    void craftingTableUnlocksSafeHome() throws Exception {
        assertVisible(course(), available(quests(CRAFTING_TABLE)),
                COURSE_CHAPTERS.get(0), COURSE_CHAPTERS.get(2), COURSE_CHAPTERS.get(3));
    }

    @Test
    void onlyMeaningfulManualProgressKeepsLaterChaptersVisibleWithoutPrerequisites() throws Exception {
        GuideDefinition guide = course();
        assertVisible(guide, available(tasks(PICKAXE_TASK)), COURSE_CHAPTERS.get(0));
        assertVisible(guide, available(quests("18EC467FA20D9B35")), COURSE_CHAPTERS.get(0), COURSE_CHAPTERS.get(3));
        assertVisible(guide, new ProgressPayload(ProgressState.EMPTY, Map.of(PICKAXE_TASK, 1), true),
                COURSE_CHAPTERS.get(0));
    }

    @Test
    void completedManualTaskKeepsItsLockedChapterVisible() {
        String manual = "3000000000000001";
        ChapterDefinition root = new ChapterDefinition("1000000000000001", 0, "root.title", "root.description",
                List.of(new QuestDefinition("2000000000000001", 0, "root.quest", "root.description",
                        new QuestPosition(0, 0), List.of(), List.of(), List.of())));
        ChapterDefinition later = new ChapterDefinition("1000000000000002", 1, "later.title", "later.description",
                List.of(new QuestDefinition("2000000000000002", 1, "later.quest", "later.description",
                        new QuestPosition(1, 0), List.of("2000000000000001"),
                        List.of(new TaskDefinition(manual, TaskDefinition.Type.MANUAL, null, 1)), List.of())));
        GuideDefinition guide = new GuideDefinition(1, "0000000000000001", "guide.title", "guide.description",
                List.of(root, later));

        assertVisible(guide, available(tasks(manual)), "1000000000000001", "1000000000000002");
    }

    @Test
    void unavailableProgressShowsOnlyTheAuthoredRoot() throws Exception {
        assertVisible(course(), null, COURSE_CHAPTERS.getFirst());
        assertVisible(course(), ProgressPayload.UNAVAILABLE, COURSE_CHAPTERS.getFirst());
    }

    @Test
    void foodChapterOpensAfterSafeMorningButNotFromPrecollectedFood() throws Exception {
        var guide = course();
        var food = guide.chapters().stream().filter(chapter -> chapter.id().equals("2A64C8E10B7D395F")).findFirst().orElseThrow();
        assertEquals(false, ChapterVisibility.visibleChapters(guide, available(tasks("09E4A72D5C813BF6"))).contains(food));
        assertEquals(true, ChapterVisibility.visibleChapters(guide, available(quests("54A8023B6EC957F1"))).contains(food));
    }

    @Test
    void oresWaitForFoodReserveEvenWhenMetalIsAlreadyOwned() throws Exception {
        var guide = course();
        var ores = guide.chapters().stream().filter(chapter -> chapter.id().equals("3B75D9F21C8E406A")).findFirst().orElseThrow();
        assertEquals(false, ChapterVisibility.visibleChapters(guide, available(tasks("61E4A8C20D935BF7"))).contains(ores));
        assertEquals(true, ChapterVisibility.visibleChapters(guide, available(quests("6C03B5E98A417DF2"))).contains(ores));
    }

    @Test
    void emptyChapterListIsHandledDefensively() {
        GuideDefinition empty = new GuideDefinition(1, "0000000000000001", "guide.test.title",
                "guide.test.description", List.of());

        assertEquals(List.of(), ChapterVisibility.visibleChapters(empty, ProgressPayload.UNAVAILABLE));
    }

    @Test
    void choosesTheFirstChapterByAuthoredOrderThenId() {
        ChapterDefinition later = chapter("1000000000000002", 1, "3000000000000002");
        ChapterDefinition firstById = chapter("1000000000000000", 0, "3000000000000000");
        ChapterDefinition firstByOrder = chapter("1000000000000001", 0, "3000000000000001");
        GuideDefinition guide = new GuideDefinition(1, "0000000000000001", "guide.test.title",
                "guide.test.description", List.of(later, firstByOrder, firstById));

        assertEquals(List.of(firstById), ChapterVisibility.visibleChapters(guide, ProgressPayload.UNAVAILABLE));
    }

    private static GuideDefinition course() throws Exception {
        try (InputStream input = ChapterVisibilityTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return GuideJson.read(input);
        }
    }

    private static ProgressPayload available(ProgressState state) {
        return new ProgressPayload(state, Map.of(), true);
    }

    private static ProgressState quests(String... ids) {
        return new ProgressState(Set.of(), Set.of(ids));
    }

    private static ProgressState tasks(String... ids) {
        return new ProgressState(Set.of(ids), Set.of());
    }

    private static void assertVisible(GuideDefinition guide, ProgressPayload progress, String... chapterIds) {
        assertEquals(List.of(chapterIds), ChapterVisibility.visibleChapters(guide, progress).stream()
                .map(ChapterDefinition::id).toList());
    }

    private static void assertVisible(GuideDefinition guide, ProgressPayload progress, List<String> chapterIds) {
        assertEquals(chapterIds, ChapterVisibility.visibleChapters(guide, progress).stream()
                .map(ChapterDefinition::id).toList());
    }

    private static ChapterDefinition chapter(String id, int order, String questId) {
        QuestDefinition quest = new QuestDefinition(questId, 0, "quest.test.title", "quest.test.description",
                new QuestPosition(0, 0), List.of("7000000000000001"));
        return new ChapterDefinition(id, order, "chapter.test.title", "chapter.test.description", List.of(quest));
    }
}
