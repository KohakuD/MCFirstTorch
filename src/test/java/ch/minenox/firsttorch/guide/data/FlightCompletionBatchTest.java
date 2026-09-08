package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.TaskDefinition;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.TaskEvaluator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class FlightCompletionBatchTest {
    private static final List<String> FLIGHT = List.of("7E16D835774FBB8B", "0966D49700AA5E61", "1B3D290417098234",
            "2725D027767CBDA7", "169041F4BCBCB2EB", "3EDDD04CF6EEFE1D", "2489570FA33801E3", "58AE2C09C8263482", "3E4D819BA0DFF522");
    private static final List<String> READING = List.of("3C3122DF5C0EA192", "009D0C9674747314", "419FC369AD620958",
            "5AFE972108C13B48", "0BF75245596E6948", "30C7BD69EB7BE641", "23B4200152D3EAE9");

    @Test void preservesSourcePathAndRewardsAcrossThreeSmallChapters() throws Exception {
        var chapters = snapshot().guides().getFirst().chapters().subList(44, 47);
        assertEquals(List.of("61B84BE07D952AC7", "62C95CF18EA63BD8", "6E095C883A1E5D3D"), chapters.stream().map(c -> c.id()).toList());
        assertEquals(List.of(5, 4, 7), chapters.stream().map(c -> c.quests().size()).toList());
        var flight = chapters.subList(0, 2).stream().flatMap(c -> c.quests().stream()).toList();
        assertEquals(FLIGHT, flight.stream().map(q -> q.id()).toList());
        assertEquals(List.of("7AC2CFDD99167DA0"), flight.getFirst().prerequisiteQuestIds());
        for (int i = 1; i < flight.size(); i++) assertEquals(List.of(FLIGHT.get(i - 1)), flight.get(i).prerequisiteQuestIds());
        assertEquals(List.of("1131CB8F5101928D", "4439E39CB10982EB", "322BF357452EA2DD", "25F213727211CB07", "17E6B24BE366ABE6",
                "25F226B6211434B8", "48D3A3F10CC107A4", "6727BD3897F03FDD", "231E117827EFC5B6", "44ED893D56A87827", "5BAE0E619D599CCE",
                "5391A84DD985F62A", "0FD7748EE22DE66E", "326B559804AA02CA", "133EB2F483665786", "721821D64C148FEF", "57F32A6DFEB3F6D4",
                "795D3A95140190CF", "2BD6EDE9D13FB430", "32BF3B243563E663"), flight.stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        var rewards = flight.stream().flatMap(q -> q.rewards().stream()).toList();
        assertEquals(List.of("4225EAE33895B2F2", "528687F6180B2406", "01F6ED8E1B608E1B", "2865665A360306C8", "24C312051DF1D742"), rewards.stream().map(r -> r.id()).toList());
        assertEquals(List.of(5, 5, 10, 5, 10), rewards.stream().map(r -> r.amount()).toList());
        assertTrue(rewards.stream().allMatch(r -> r.type() == RewardDefinition.Type.EXPERIENCE));
    }

    @Test void suppliesCountButDoNotCertifySafeRocketsOrFlightPractice() throws Exception {
        var snapshot = snapshot();
        var basics = snapshot.guides().getFirst().chapters().get(44).quests();
        var packing = basics.getFirst();
        assertEquals(List.of(1, 64, 8, 1, 1), packing.tasks().stream().map(t -> t.count()).toList());
        var low = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY, key -> key.equals("minecraft:cobblestone") ? 63 : key.equals("minecraft:ladder") ? 7 : 0);
        assertFalse(low.completedTaskIds().contains("4439E39CB10982EB"));
        assertFalse(low.completedTaskIds().contains("322BF357452EA2DD"));
        var ready = TaskEvaluator.evaluate(snapshot, new ProgressState(Set.of(), Set.of("7AC2CFDD99167DA0")), key -> 64);
        assertFalse(ready.completedQuestIds().contains(packing.id()));
        assertFalse(ready.completedTaskIds().contains("17E6B24BE366ABE6"));
        var rockets = basics.get(3);
        var twoRockets = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                key -> key.equals("minecraft:firework_rocket") ? 2 : 0);
        assertFalse(twoRockets.completedTaskIds().contains("44ED893D56A87827"));
        assertEquals(List.of("minecraft:paper", "minecraft:gunpowder", "minecraft:firework_rocket"), rockets.tasks().subList(0, 3).stream().map(t -> t.itemId()).toList());
        var counted = TaskEvaluator.evaluate(snapshot, new ProgressState(Set.of(), Set.of(FLIGHT.get(2))), key -> 3);
        assertTrue(counted.completedTaskIds().contains("44ED893D56A87827"));
        assertFalse(counted.completedQuestIds().contains(rockets.id()));
        assertEquals(TaskDefinition.Type.MANUAL, rockets.tasks().getLast().type());
        assertTrue(TaskEvaluator.confirm(snapshot, counted, rockets.id(), "5BAE0E619D599CCE", key -> 0).completedQuestIds().contains(rockets.id()));
        assertTrue(basics.get(4).tasks().stream().allMatch(t -> t.type() == TaskDefinition.Type.MANUAL));
        var journey = snapshot.guides().getFirst().chapters().get(45).quests();
        assertEquals(List.of(16, 8, 1, 1), journey.get(1).tasks().stream().map(t -> t.count()).toList());
        var shortTrip = TaskEvaluator.evaluate(snapshot, ProgressState.EMPTY,
                key -> key.equals("minecraft:firework_rocket") ? 15 : key.equals("minecraft:bread") ? 7 : 0);
        assertFalse(shortTrip.completedTaskIds().contains("133EB2F483665786"));
        assertFalse(shortTrip.completedTaskIds().contains("721821D64C148FEF"));
        var tripReady = TaskEvaluator.evaluate(snapshot, shortTrip,
                key -> key.equals("minecraft:firework_rocket") ? 16 : key.equals("minecraft:bread") ? 8 : 0);
        assertTrue(tripReady.completedTaskIds().containsAll(Set.of("133EB2F483665786", "721821D64C148FEF")));
        assertFalse(tripReady.completedTaskIds().contains("795D3A95140190CF"));
    }

    @Test void finalCardsAreIndependentReadingNotRequiredExpeditions() throws Exception {
        var snapshot = snapshot();
        var cards = snapshot.guides().getFirst().chapters().get(46).quests();
        assertEquals(READING, cards.stream().map(q -> q.id()).toList());
        assertEquals(List.of("3E4D819BA0DFF522"), cards.getFirst().prerequisiteQuestIds());
        assertEquals(List.of("72C95CCAFC3F3A16", "25B14938B9673B9F", "7AFF2F0410BF29E9", "50225BFAFB16F710", "37C1C4C9A054B116", "0AB3D6E206DBD2DD", "0ACB79CC7F3DEFEF"), cards.stream().flatMap(q -> q.tasks().stream()).map(t -> t.id()).toList());
        for (var card : cards) {
            assertEquals(1, card.tasks().size());
            assertEquals(TaskDefinition.Type.MANUAL, card.tasks().getFirst().type());
            assertTrue(card.rewards().isEmpty());
        }
        for (var card : cards.subList(1, cards.size())) assertEquals(List.of(READING.getFirst()), card.prerequisiteQuestIds());
        assertTrue(snapshot.guides().getFirst().chapters().stream().flatMap(c -> c.quests().stream())
                .flatMap(q -> q.prerequisiteQuestIds().stream()).noneMatch(READING.subList(1, 7)::contains));
        var state = TaskEvaluator.confirm(snapshot, new ProgressState(Set.of(), Set.of(FLIGHT.getLast())), READING.getFirst(), "72C95CCAFC3F3A16", key -> 0);
        for (var card : cards.subList(1, cards.size())) {
            assertTrue(card.prerequisitesMet(state.completedQuestIds()));
            assertFalse(state.completedQuestIds().contains(card.id()));
        }
    }

    private static GuideSnapshot snapshot() throws Exception {
        try (var input = FlightCompletionBatchTest.class.getResourceAsStream("/data/firsttorch/guides/course.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        }
    }
}
