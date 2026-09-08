package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.guide.model.GuideDefinition;
import ch.minenox.firsttorch.guide.model.QuestDefinition;
import ch.minenox.firsttorch.guide.model.QuestPosition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class TrophyCatalogTest {
    @Test void portalBatchAddsThreeIndependentCosmeticMilestones() {
        var guides = snapshot(chapter("757C28E4BF936D0A", 34, "086D39F5C0A47E2B", "6EC39F5B260AD481"),
                chapter("7AA1D479062EB350", 35, "764B17D3AE825C09"),
                chapter("7BB2E58A173FC461", 36, "6F54A06C371BF6A3"));
        var entries = TrophyCatalog.entries(guides, progress("764B17D3AE825C09"));
        assertEquals(3, entries.size());
        assertEquals(List.of(false, true, false), entries.stream().map(TrophyCatalog.Entry::earned).toList());
        assertTrue(TrophyCatalog.entries(guides, progress("086D39F5C0A47E2B", "6EC39F5B260AD481", "764B17D3AE825C09", "6F54A06C371BF6A3")).stream().allMatch(TrophyCatalog.Entry::earned));
    }

    private static final String COURSE_ID = "0013F17C00000001";
    private static final String WELCOME = "0F91A2B3C4D5E607";
    private static final String MOVEMENT = "01F57C0E3B9D2468";
    private static final String SHELTER = "0C9E12A4B6D83F70";
    private static final String HOME = "1D8F4C2A7B9E6053";
    private static final String WELCOME_ONE = "12E6801F4CAD3579";
    private static final String WELCOME_TWO = "21A3B5C7D9E10246";

    @Test
    void includesOnlyLoadedCourseMilestonesAndUsesTheirMetadata() {
        GuideSnapshot guides = snapshot(chapter(WELCOME, 0, WELCOME_ONE), chapter("2F91A2B3C4D5E607", 1, "32B4C6D8E0F21357"));

        List<TrophyCatalog.Entry> entries = TrophyCatalog.entries(guides, ProgressPayload.UNAVAILABLE);

        assertEquals(1, entries.size());
        TrophyCatalog.Entry entry = entries.getFirst();
        assertEquals(WELCOME, entry.chapterId());
        assertEquals("chapter.test.title", entry.chapterTitleKey());
        assertEquals("trophy.firsttorch.welcome.title", entry.titleKey());
        assertEquals("trophy.firsttorch.welcome.description", entry.descriptionKey());
        assertEquals("minecraft:book", entry.iconItemId());
        assertFalse(entry.earned());
    }

    @Test
    void remainsLockedUntilEveryQuestInANonemptyChapterIsCompleted() {
        GuideSnapshot guides = snapshot(chapter(WELCOME, 0, WELCOME_ONE, WELCOME_TWO));

        assertFalse(TrophyCatalog.entries(guides, progress(WELCOME_ONE)).getFirst().earned());
        assertTrue(TrophyCatalog.entries(guides, progress(WELCOME_ONE, WELCOME_TWO)).getFirst().earned());
    }

    @Test
    void unavailableProgressAndPreviewSchemaNeverEarnTrophies() {
        GuideSnapshot guides = snapshot(chapter(WELCOME, 0, WELCOME_ONE));

        assertFalse(TrophyCatalog.entries(guides, ProgressPayload.UNAVAILABLE).getFirst().earned());
        assertTrue(TrophyCatalog.entries(DesignPreview.snapshot(), progress(WELCOME_ONE)).isEmpty());
    }

    @Test
    void reconnectWithTheSameSavedCompletionStateKeepsTrophyEarned() {
        GuideSnapshot guides = snapshot(chapter(WELCOME, 0, WELCOME_ONE));
        ProgressPayload savedState = progress(WELCOME_ONE);

        assertTrue(TrophyCatalog.entries(guides, savedState).getFirst().earned());
        assertTrue(TrophyCatalog.entries(guides, savedState).getFirst().earned());
    }

    @Test
    void exposesAllTwentyNineKnownMilestonesWhenTheyAreLoaded() {
        GuideSnapshot guides = snapshot(
                chapter(WELCOME, 0, WELCOME_ONE),
                chapter(MOVEMENT, 1, "34A8023B6ECF5791"),
                chapter(SHELTER, 2, "7B83D5F920C4160E"),
                chapter(HOME, 3, "18EC467FA20D9B35"),
                chapter("2A64C8E10B7D395F", 4, "6C03B5E98A417DF2"),
                chapter("3B75D9F21C8E406A", 5, "3C16B9E50A724DF8"),
                chapter("4C86EA032D9F517B", 6, "07B5E9C31D864AF2"),
                chapter("5D97FB143EA0628C", 7, "36CF412575EB038D"),
                chapter("4C86EA031D9F5B72", 8, "3F215C6E03ABD479"),
                chapter("3B75D9F20C8E4A61", 9, "2F6A91C4D8E307B5"),
                chapter("6EA80C254FB1739D", 10, "0BED012A8146C359"),
                chapter("7FB91D365AC2840E", 11, "65BA4B07D83C9E2F"),
                chapter("5D91A7C30E624BF8", 12, "26EC824FB71D3590"),
                chapter("0AC82E476BD3951F", 13, "5E04AF62D97B183C"),
                chapter("1BD93F587CE4062A", 14, "42F68D51B39E074C"),
                chapter("2CEA40698DF5173B", 15, "6D91380C6EA4B2F5"),
                chapter("68C1F4072D9A5BE3", 16, "7C1683A52D1E79B4"),
                chapter("3DFB517A9E06284C", 17, "14C9E72A5B603DF8"),
                chapter("4E0C628BAF17395D", 18, "0FB4E5A172D6093C"),
                chapter("6B3D9F215E8C4A70", 19, "7A2C84E05D916B37"),
                chapter("5F1D739CB0284A6E", 20, "6A2C84E05D916B37"),
                chapter("607E84ADB1395B7F", 21, "4093C7EBF526DA81"),
                chapter("6138B4E07D952AC6", 22, "2CE36F9B2840D571"),
                chapter("7249C5F18EA63BD8", 23, "1249E56A3FD70C81"),
                chapter("735AD6029FB74CE9", 24, "1249E5AFB61C7D28"),
                chapter("746BE713A0C85DFA", 25, "5D65A27B83E94AF6"),
                chapter("7249E5AFB61C7D28", 26, "33CB08D1E94FA05C"),
                chapter("757CF824B1D96E0B", 27, "72E9F608A3C4D7D1"),
                chapter("766D0935C2EA7F1C", 28, "676BFD158DEB4F7F"));

        assertEquals(List.of(WELCOME, MOVEMENT, SHELTER, HOME, "2A64C8E10B7D395F", "3B75D9F21C8E406A", "4C86EA032D9F517B", "5D97FB143EA0628C", "4C86EA031D9F5B72", "3B75D9F20C8E4A61", "6EA80C254FB1739D", "7FB91D365AC2840E", "5D91A7C30E624BF8", "0AC82E476BD3951F", "1BD93F587CE4062A", "2CEA40698DF5173B", "68C1F4072D9A5BE3", "3DFB517A9E06284C", "4E0C628BAF17395D", "6B3D9F215E8C4A70", "5F1D739CB0284A6E", "607E84ADB1395B7F", "6138B4E07D952AC6", "7249C5F18EA63BD8", "735AD6029FB74CE9", "746BE713A0C85DFA", "7249E5AFB61C7D28", "757CF824B1D96E0B", "766D0935C2EA7F1C"),
                TrophyCatalog.entries(guides, ProgressPayload.UNAVAILABLE).stream()
                        .map(TrophyCatalog.Entry::chapterId).toList());
    }

    private static ProgressPayload progress(String... completedQuests) {
        return new ProgressPayload(new ProgressState(Set.of(), Set.of(completedQuests)), Map.of(), true);
    }

    @Test
    void newSearchBatchExposesThreeIndependentCosmeticTrophies() {
        var guides = snapshot(chapter("788FB257E40C913E", 31, "73FACD5A0D792B46"),
                chapter("746B17D3AE825CF0", 32, "361FD38F593D07B4"),
                chapter("7990C368F51DA24F", 33, "74DB8F5B15F9C370"));
        var entries = TrophyCatalog.entries(guides, progress("361FD38F593D07B4", "74DB8F5B15F9C370"));
        assertEquals(3, entries.size());
        assertFalse(entries.getFirst().earned());
        assertTrue(entries.get(1).earned());
        assertTrue(entries.getLast().earned());
    }

    @Test
    void brewingTrophyIsAvailableOnlyAfterItsChapterIsComplete() {
        var guides = snapshot(chapter("735A06C29D714BE8", 30, "0A6417D3BE825CF9", "5620D39F7A4E18B5"));
        assertEquals(1, TrophyCatalog.entries(guides, ProgressPayload.UNAVAILABLE).size());
        assertFalse(TrophyCatalog.entries(guides, progress("0A6417D3BE825CF9")).getFirst().earned());
        assertTrue(TrophyCatalog.entries(guides, progress("0A6417D3BE825CF9", "5620D39F7A4E18B5")).getFirst().earned());
    }

    @Test
    void fortressReturnTrophyNeedsAllThreeLessonsButNoRewardClaim() {
        GuideSnapshot guides = snapshot(chapter("777EA146D3FB802D", 29,
                "5B0F14AD63E74BD3", "15F17869891E4CE0", "734140DAA3E544D2"));
        var partial = TrophyCatalog.entries(guides, progress("5B0F14AD63E74BD3", "15F17869891E4CE0"));
        assertEquals(1, partial.size());
        assertFalse(partial.getFirst().earned());
        assertTrue(TrophyCatalog.entries(guides, progress("5B0F14AD63E74BD3", "15F17869891E4CE0", "734140DAA3E544D2")).getFirst().earned());
    }

    private static GuideSnapshot snapshot(ChapterDefinition... chapters) {
        return new GuideSnapshot(List.of(new GuideDefinition(
                1, COURSE_ID, "guide.test.title", "guide.test.description", List.of(chapters))));
    }

    private static ChapterDefinition chapter(String id, int order, String... questIds) {
        List<QuestDefinition> quests = java.util.stream.IntStream.range(0, questIds.length)
                .mapToObj(index -> new QuestDefinition(questIds[index], index,
                        "quest.test.title", "quest.test.description",
                        new QuestPosition(index, 0), List.of()))
                .toList();
        return new ChapterDefinition(id, order, "chapter.test.title", "chapter.test.description", quests);
    }
}
