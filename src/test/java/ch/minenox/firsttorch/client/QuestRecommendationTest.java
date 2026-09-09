package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.data.GuideJson;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.*;
import org.junit.jupiter.api.Test;

final class QuestRecommendationTest {
    @Test void portalRouteTakesPriorityOverOptionalLibraryAndFireResistance() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(34)
                .filter(c -> c.order() != 31).flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("086D39F5C0A47E2B", QuestRecommendation.choose(snapshot, progress(done)).questId());
        snapshot.guides().getFirst().chapters().get(34).quests().stream().limit(4).forEach(q -> done.add(q.id()));
        assertEquals("10E5B17D482CF6A3", QuestRecommendation.choose(snapshot, progress(done)).questId());
        snapshot.guides().getFirst().chapters().get(35).quests().forEach(q -> done.add(q.id()));
        assertEquals("098E4A06D1B58F3D", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    private GuideSnapshot course() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }

    @Test void newPlayerStartsAtWelcome() throws Exception {
        var snapshot = course();
        var expected = snapshot.guides().getFirst().chapters().getFirst().quests().getFirst().id();
        assertEquals(expected, QuestRecommendation.choose(snapshot, progress(Set.of())).questId());
    }

    @Test void eyesAndSearchContinueWithoutFireResistanceOrOptionalNetherBranches() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(31)
                .filter(c -> c.order() != 26 && c.order() != 27)
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        done.add("07105D263E94F5A2");
        assertEquals("0C7539E5BF936D1A", QuestRecommendation.choose(snapshot, progress(done)).questId());
        snapshot.guides().getFirst().chapters().get(32).quests().forEach(q -> done.add(q.id()));
        assertEquals("5A31E5B17B5F29D6", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void brewingContinuesBeforeOptionalNetherBranches() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(30)
                .filter(c -> c.order() != 26 && c.order() != 27)
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        done.add("07105D263E94F5A2");
        assertEquals("0A6417D3BE825CF9", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void mainPathTakesPriorityOverOptionalMovement() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().getFirst().quests().forEach(q -> done.add(q.id()));
        assertEquals("34A8023B6ECF5791", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("34A8023B6ECF5791");
        assertEquals("7B83D5F920C4160E", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("7B83D5F920C4160E");
        assertEquals("56CA245D80EB7913", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void unavailableProgressDoesNotGuess() throws Exception {
        assertEquals(GuideBrowserViewModel.Selection.EMPTY, QuestRecommendation.choose(course(), null));
        assertEquals(GuideBrowserViewModel.Selection.EMPTY, QuestRecommendation.choose(course(), ProgressPayload.UNAVAILABLE));
    }

    @Test void safeMorningRecommendsFoodPreparationBeforeOptionalEatingPractice() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(4)
                .flatMap(chapter -> chapter.quests().stream()).forEach(quest -> done.add(quest.id()));
        assertEquals("35B7E10C9A624DF8", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("35B7E10C9A624DF8");
        assertEquals("4A91D6F30C7E285B", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void foodReserveRecommendsFarmingBeforeOres() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(5).flatMap(chapter -> chapter.quests().stream())
                .filter(quest -> !quest.id().equals("1D4A83C6E2057B9F"))
                .forEach(quest -> done.add(quest.id()));
        assertEquals("0C42E8A51D739BF6", QuestRecommendation.choose(snapshot, progress(done)).questId());
        completeSupplies(snapshot, done);
        assertEquals("0A73D9E14C628BF5", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("0A73D9E14C628BF5");
        assertEquals("19C5E2A70B864DF3", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("19C5E2A70B864DF3");
        assertEquals("2B84F1C60D735AE9", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    private ProgressPayload progress(Set<String> done) {
        return new ProgressPayload(new ProgressState(Set.of(), done), Map.of(), true);
    }

    private static void completeSupplies(GuideSnapshot snapshot, Set<String> done) {
        snapshot.guides().getFirst().chapters().stream()
                .filter(c -> Set.of("chapter.firsttorch.farming.title", "chapter.firsttorch.animal_care.title",
                        "chapter.firsttorch.storage.title").contains(c.titleKey()))
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
    }

    @Test void deepMiningTakesPriorityOverOptionalExcursions() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(16)
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("1C4EA0627FB38D59", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void enchantingTakesPriorityOverOptionalExcursions() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(20)
                .filter(c -> !Set.of("68C1F4072D9A5BE3", "3DFB517A9E06284C", "4E0C628BAF17395D").contains(c.id()))
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("1D5FB17380C49E6A", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void netherPreparationIsSuggestedBeforeLibraryExpansion() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(21)
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("0249C5F18EA63BD7", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void netherEquipmentContinuesBeforeUnfinishedLibrary() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(23)
                .filter(c -> !c.id().equals("607E84ADB1395B7F"))
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("14C1D6E29A5F730B", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void storageTakesPriorityOverOptionalComposting() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(15)
                .filter(c -> !c.id().equals("0AC82E476BD3951F"))
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("6519B084E62C3A7D", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void fortressReturnContinuesBeforeOptionalBarterAndResources() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(29)
                .filter(c -> c.order() != 26 && c.order() != 27)
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        done.add("07105D263E94F5A2");
        assertEquals("5B0F14AD63E74BD3", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("5B0F14AD63E74BD3");
        assertEquals("15F17869891E4CE0", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("15F17869891E4CE0");
        assertEquals("734140DAA3E544D2", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void fortressTakesPriorityOverOptionalBarterAndResources() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(26)
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        done.add("07105D263E94F5A2");
        assertEquals("78FAD3D980F349BA", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void sharedNetherActivitiesGatewayContinuesBeforeLibraryExpansion() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(26)
                .filter(c -> !c.id().equals("607E84ADB1395B7F"))
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("07105D263E94F5A2", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void netherSafetyContinuesWithoutLibraryExpansion() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(25)
                .filter(c -> !c.id().equals("607E84ADB1395B7F"))
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("236B08D1E94FA05C", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void firstNetherVisitContinuesWithoutLibraryExpansion() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(24)
                .filter(c -> !c.id().equals("607E84ADB1395B7F"))
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("457C189D620A3FB4", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void animalCareTakesPriorityOverOptionalComposting() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(13)
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("174BD2A608E35C91", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void wheatHarvestTakesPriorityOverOptionalComposting() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(12)
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        snapshot.guides().getFirst().chapters().get(12).quests().stream().limit(4)
                .forEach(q -> done.add(q.id()));
        assertEquals("04CA602D95FB137E", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void farmingTakesPriorityOverUnfinishedOptionalMaps() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(11).flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        assertEquals("0C42E8A51D739BF6", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void compassBasicsRecommendLodestoneBeforeOptionalMaps() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        completeSupplies(snapshot, done);
        snapshot.guides().getFirst().chapters().stream().limit(10).flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        assertEquals("15E7C9A42B806DF3", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void crystalRouteTakesPriorityOverOptionalEndermanRoof() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(38).flatMap(c -> c.quests().stream())
                .filter(q -> !q.id().equals("0CB72D9F6A4E29E8")).forEach(q -> done.add(q.id()));
        assertEquals("5FA406C29D715C1B", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void gatewayContinuationTakesPriorityOverTheOptionalDragonEgg() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(39).flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        snapshot.guides().getFirst().chapters().get(39).quests().stream().limit(4).forEach(q -> done.add(q.id()));
        assertEquals("4572CD9F6AE439F9", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("4572CD9F6AE439F9");
        assertEquals("6794EFB18C065B1B", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void optionalMobAndBastionBranchesNeverDisplaceTheReadyFlightCourse() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(44).flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        assertEquals("7E16D835774FBB8B", QuestRecommendation.choose(snapshot, progress(done)).questId());
        assertFalse(done.contains("0D51B7F3AE264C09"));
        assertFalse(done.contains("0756BFDE228E428E"));
    }

    @Test void endShipEndpointTakesPriorityOverTheOptionalDragonEgg() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        snapshot.guides().getFirst().chapters().stream().limit(39).flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        snapshot.guides().getFirst().chapters().get(39).quests().stream().limit(4).forEach(q -> done.add(q.id()));
        done.add("4572CD9F6AE439F9");
        snapshot.guides().getFirst().chapters().get(40).quests().forEach(q -> done.add(q.id()));
        snapshot.guides().getFirst().chapters().subList(41, 43).stream().flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        snapshot.guides().getFirst().chapters().get(43).quests().stream().limit(4).forEach(q -> done.add(q.id()));
        assertEquals("7AC2CFDD99167DA0", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("7AC2CFDD99167DA0");
        assertEquals("7E16D835774FBB8B", QuestRecommendation.choose(snapshot, progress(done)).questId());
        snapshot.guides().getFirst().chapters().subList(44, 46).stream().flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        assertEquals("3C3122DF5C0EA192", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("3C3122DF5C0EA192");
        assertEquals("2350AB7D48C217D7", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void completedIronAndMiningRecommendFindingHome() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        completeSupplies(snapshot, done);
        snapshot.guides().getFirst().chapters().stream().limit(9).flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        assertEquals("16C8E2A50D739BF4", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void completedMiningRecommendsIronBeforeUnfinishedOptionalCopper() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        completeSupplies(snapshot, done);
        snapshot.guides().getFirst().chapters().stream().limit(8).flatMap(c -> c.quests().stream())
                .filter(q -> !Set.of("4A28C6E10D735BF9", "6C4AE8F31D957B20").contains(q.id()))
                .forEach(q -> done.add(q.id()));
        assertEquals("27A9D4E60B835CF1", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void protectionCompletionRecommendsIronBeforeMining() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        completeSupplies(snapshot, done);
        snapshot.guides().getFirst().chapters().stream().limit(7).flatMap(c -> c.quests().stream())
                .forEach(q -> done.add(q.id()));
        assertEquals("27A9D4E60B835CF1", QuestRecommendation.choose(snapshot, progress(done)).questId());
        snapshot.guides().getFirst().chapters().stream()
                .filter(c -> c.titleKey().equals("chapter.firsttorch.iron_essentials.title"))
                .flatMap(c -> c.quests().stream()).forEach(q -> done.add(q.id()));
        assertEquals("1A62D8E30C745BF9", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("1A62D8E30C745BF9");
        assertEquals("2E17C9A40D638BF5", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }

    @Test void ironRouteRecommendsShieldWithoutRequiringCopper() throws Exception {
        var snapshot = course();
        Set<String> done = new HashSet<>();
        completeSupplies(snapshot, done);
        snapshot.guides().getFirst().chapters().stream().limit(6).flatMap(c -> c.quests().stream())
                .filter(q -> !Set.of("4A28C6E10D735BF9", "6C4AE8F31D957B20").contains(q.id()))
                .forEach(q -> done.add(q.id()));
        assertEquals("4D92C7A10E638BF5", QuestRecommendation.choose(snapshot, progress(done)).questId());
        done.add("4D92C7A10E638BF5");
        assertEquals("18A6D3F90C754BE2", QuestRecommendation.choose(snapshot, progress(done)).questId());
    }
}
