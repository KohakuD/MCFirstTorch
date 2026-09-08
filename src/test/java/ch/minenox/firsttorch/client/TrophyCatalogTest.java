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

    @Test void endMilestonesRemainIndependentAndRequireTheirOwnLessons() {
        var guides = snapshot(chapter("779E4A06D1B58F4E", 37, "4EA3F5B18C604B09", "0CB72D9F6A4E29E8"),
                chapter("7CC3F69B2840D572", 38, "71C628E4BF937E3D"));
        var entries = TrophyCatalog.entries(guides, progress("4EA3F5B18C604B09", "71C628E4BF937E3D"));
        assertEquals(2, entries.size());
        assertFalse(entries.getFirst().earned());
        assertTrue(entries.getLast().earned());
        assertTrue(TrophyCatalog.entries(guides, progress("4EA3F5B18C604B09", "0CB72D9F6A4E29E8")).getFirst().earned());
    }

    @Test void dragonVictoryTrophyRequiresAllSixLessonsIncludingTheOptionalEgg() {
        String[] victory = {"12D739F5C0A48F4E", "34F95B17E2C6B170", "561B7D3904E8D392",
                "013E8F5B26A0F5B5", "2350AB7D48C217D7", "4572CD9F6AE439F9"};
        var guides = snapshot(chapter("7DD407AC3951E683", 39, victory));
        assertFalse(TrophyCatalog.entries(guides, progress(java.util.Arrays.copyOf(victory, 5))).getFirst().earned());
        assertFalse(TrophyCatalog.entries(guides, progress(victory[0], victory[1], victory[2], victory[3], victory[5])).getFirst().earned());
        assertTrue(TrophyCatalog.entries(guides, progress(victory)).getFirst().earned());
    }

    @Test void gatewayArrivalTrophyRequiresAllFiveLessons() {
        String[] gateway = {"6794EFB18C065B1B", "3BD823F5C04A9F5F", "5DFA4517E26CB171",
                "7F1C6739048ED393", "213E895B26A0F5B5"};
        var guides = snapshot(chapter("78AF5B17E2C6904D", 40, gateway));
        assertFalse(TrophyCatalog.entries(guides, progress(java.util.Arrays.copyOf(gateway, 4))).getFirst().earned());
        assertTrue(TrophyCatalog.entries(guides, progress(gateway)).getFirst().earned());
    }

    @Test void endCityTrophiesAreIndependentAndRequireEveryOwnLesson() {
        String[] chorus = {"435FA7C29D816E3B", "7682DAF5C0B4916E", "1A8C05F7D3E692B1", "6FD15A4C283BE706"};
        String[] shulker = {"58A57A615D0EF444", "6FEC02D106FBA924", "4DF0AA017358AFED", "42E183B7F772D99C",
                "6C6318A949755526", "1A6EFC93E3474D60", "1239D170F79C0405", "0B875B8819CB3C72"};
        String[] ship = {"78C3992F3DF1AAB1", "4DA52C070AC3CC11", "36ED20D0FACD3629", "115C8A2BF37D0635", "7AC2CFDD99167DA0"};
        var guides = snapshot(chapter("7EE518BD4A62F794", 41, chorus),
                chapter("7FF629CE5B7308A5", 42, shulker), chapter("60A73ADF6C8419B6", 43, ship));
        var partial = TrophyCatalog.entries(guides, progress(chorus[0], chorus[1], chorus[2], shulker[0], ship[0]));
        assertEquals(3, partial.size());
        assertEquals(List.of(false, false, false), partial.stream().map(TrophyCatalog.Entry::earned).toList());
        var all = new java.util.ArrayList<String>();
        all.addAll(List.of(chorus));
        all.addAll(List.of(shulker));
        all.addAll(List.of(ship));
        assertTrue(TrophyCatalog.entries(guides, progress(all.toArray(String[]::new))).stream().allMatch(TrophyCatalog.Entry::earned));
    }

    @Test void flightAndReadingTrophiesRequireOnlyTheirOwnChapters() {
        var guides = snapshot(chapter("61B84BE07D952AC7", 44, "1000000000000001"),
                chapter("62C95CF18EA63BD8", 45, "1000000000000002"),
                chapter("6E095C883A1E5D3D", 46, "1000000000000003", "1000000000000004"));
        assertEquals(List.of(true, true, false), TrophyCatalog.entries(guides,
                progress("1000000000000001", "1000000000000002", "1000000000000003"))
                .stream().map(TrophyCatalog.Entry::earned).toList());
        assertTrue(TrophyCatalog.entries(guides, progress("1000000000000001", "1000000000000002",
                "1000000000000003", "1000000000000004")).stream().allMatch(TrophyCatalog.Entry::earned));
    }

    @Test void optionalMobAndBastionTrophiesAreIndependent() {
        var guides = snapshot(chapter("702FA5D18C643BE9", 47, "1000000000000011", "1000000000000012"),
                chapter("63DA6D029FB74CE9", 48, "1000000000000013"));
        assertEquals(List.of(false, true), TrophyCatalog.entries(guides,
                progress("1000000000000011", "1000000000000013")).stream().map(TrophyCatalog.Entry::earned).toList());
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
