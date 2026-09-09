package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.model.RewardDefinition;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class RewardJournalTest {
    private static final String QUEST = "2000000000000001";
    private static final List<RewardDefinition> REWARDS = List.of(
            new RewardDefinition("4000000000000001", RewardDefinition.Type.EXPERIENCE, null, 15),
            new RewardDefinition("4000000000000002", RewardDefinition.Type.ITEM, "minecraft:bread", 1));
    @TempDir Path directory;

    @Test void reservationSurvivesReopenAndCannotBeRetried() throws Exception {
        RewardJournal journal = new RewardJournal(directory);
        assertTrue(journal.reserve(QUEST, REWARDS));
        RewardJournal reopened = new RewardJournal(directory);
        assertTrue(reopened.available());
        assertEquals(Set.of(QUEST), reopened.ids(RewardJournal.Phase.PENDING));
        assertFalse(reopened.reserve(QUEST, REWARDS));
        assertTrue(reopened.ids(RewardJournal.Phase.COMPLETE).isEmpty());
    }

    @Test void sameQuestCanBeClaimedIndependentlyInPlayerJournals() throws Exception {
        Path firstPath = directory.resolve(java.util.UUID.randomUUID().toString());
        Path secondPath = directory.resolve(java.util.UUID.randomUUID().toString());
        RewardJournal first = new RewardJournal(firstPath);
        RewardJournal second = new RewardJournal(secondPath);
        assertTrue(first.reserve(QUEST, REWARDS));
        first.complete(QUEST);
        assertTrue(second.ids(RewardJournal.Phase.COMPLETE).isEmpty());
        assertTrue(second.reserve(QUEST, REWARDS));

        RewardJournal reopenedFirst = new RewardJournal(firstPath);
        RewardJournal reopenedSecond = new RewardJournal(secondPath);
        assertEquals(Set.of(QUEST), reopenedFirst.ids(RewardJournal.Phase.COMPLETE));
        assertEquals(Set.of(QUEST), reopenedSecond.ids(RewardJournal.Phase.PENDING));
        assertTrue(reopenedSecond.ids(RewardJournal.Phase.COMPLETE).isEmpty());
        reopenedSecond.complete(QUEST);
        assertEquals(Set.of(QUEST), new RewardJournal(secondPath).ids(RewardJournal.Phase.COMPLETE));
        assertFalse(new RewardJournal(firstPath).reserve(QUEST, REWARDS));
        assertFalse(new RewardJournal(secondPath).reserve(QUEST, REWARDS));
    }

    @Test void corruptPlayerJournalDoesNotBlockAnotherPlayersClaims() throws Exception {
        Path brokenPath = directory.resolve(java.util.UUID.randomUUID().toString());
        Path healthyPath = directory.resolve(java.util.UUID.randomUUID().toString());
        Files.createDirectories(brokenPath);
        Files.writeString(brokenPath.resolve(QUEST + ".claim"), "broken");
        assertFalse(new RewardJournal(brokenPath).available());
        RewardJournal healthy = new RewardJournal(healthyPath);
        assertTrue(healthy.available());
        assertTrue(healthy.reserve(QUEST, REWARDS));
        healthy.complete(QUEST);
        assertEquals(Set.of(QUEST), new RewardJournal(healthyPath).ids(RewardJournal.Phase.COMPLETE));
        assertEquals("broken", Files.readString(brokenPath.resolve(QUEST + ".claim")));
    }

    @Test void completionSurvivesReopenAndNewRewardsDoNotReopenQuest() throws Exception {
        RewardJournal journal = new RewardJournal(directory);
        assertTrue(journal.reserve(QUEST, REWARDS));
        journal.complete(QUEST);
        RewardJournal reopened = new RewardJournal(directory);
        assertEquals(Set.of(QUEST), reopened.ids(RewardJournal.Phase.COMPLETE));
        assertFalse(reopened.reserve(QUEST, List.of(new RewardDefinition(
                "4000000000000003", RewardDefinition.Type.EXPERIENCE, null, 999))));
        assertTrue(reopened.ids(RewardJournal.Phase.PENDING).isEmpty());
    }

    @Test void copiedJournalPreservesCompletedAndInterruptedClaims() throws Exception {
        Path source = directory.resolve("source");
        Path restored = directory.resolve("restored");
        String interrupted = "2000000000000002";
        RewardJournal original = new RewardJournal(source);
        assertTrue(original.reserve(QUEST, REWARDS));
        original.complete(QUEST);
        assertTrue(original.reserve(interrupted, REWARDS));

        Files.createDirectories(restored);
        try (var files = Files.list(source)) {
            for (Path file : files.toList()) {
                Files.copy(file, restored.resolve(file.getFileName()));
            }
        }
        RewardJournal copy = new RewardJournal(restored);
        assertTrue(copy.available());
        assertEquals(Set.of(QUEST), copy.ids(RewardJournal.Phase.COMPLETE));
        assertEquals(Set.of(interrupted), copy.ids(RewardJournal.Phase.PENDING));
        assertFalse(copy.reserve(QUEST, REWARDS));
        assertFalse(copy.reserve(interrupted, REWARDS));
        assertEquals(Set.of(interrupted), new RewardJournal(source).ids(RewardJournal.Phase.PENDING));
    }

    @Test void unsupportedJournalVersionIsPreservedAndBlocksClaims() throws Exception {
        RewardJournal journal = new RewardJournal(directory);
        assertTrue(journal.reserve(QUEST, REWARDS));
        Path file = directory.resolve(QUEST + ".claim");
        String future = Files.readString(file).replace("FIRST_TORCH_CLAIM_1", "FIRST_TORCH_CLAIM_2");
        Files.writeString(file, future);
        RewardJournal reopened = new RewardJournal(directory);
        assertFalse(reopened.available());
        assertThrows(java.io.IOException.class, () -> reopened.reserve("2000000000000002", REWARDS));
        assertEquals(future, Files.readString(file));
    }

    @Test void corruptedRecordIsPreservedAndBlocksNewReservations() throws Exception {
        Path file = directory.resolve(QUEST + ".claim");
        Files.writeString(file, "broken");
        RewardJournal journal = new RewardJournal(directory);
        assertFalse(journal.available());
        assertThrows(java.io.IOException.class, () -> journal.reserve("2000000000000002", REWARDS));
        assertEquals("broken", Files.readString(file));
        assertFalse(Files.exists(directory.resolve("2000000000000002.claim")));
    }

    @Test void concurrentReservationFailsClosedWithoutReplacingOriginal() throws Exception {
        RewardJournal first = new RewardJournal(directory);
        RewardJournal stale = new RewardJournal(directory);
        first.reserve(QUEST, REWARDS);
        String original = Files.readString(directory.resolve(QUEST + ".claim"));
        assertThrows(java.io.IOException.class, () -> stale.reserve(QUEST, REWARDS));
        assertFalse(stale.available());
        assertEquals(original, Files.readString(directory.resolve(QUEST + ".claim")));
    }

    @Test void failedCompletionCannotRemoveReservation() throws Exception {
        RewardJournal journal = new RewardJournal(directory);
        journal.reserve(QUEST, REWARDS);
        Files.writeString(directory.resolve(QUEST + ".interrupted.tmp"), "partial replacement");
        RewardJournal reopened = new RewardJournal(directory);
        assertTrue(reopened.available());
        assertEquals(Set.of(QUEST), reopened.ids(RewardJournal.Phase.PENDING));
        assertFalse(reopened.reserve(QUEST, REWARDS));
    }

    @Test void completionDoesNotReplaceReservation() throws Exception {
        RewardJournal journal = new RewardJournal(directory);
        journal.reserve(QUEST, REWARDS);
        String reserved = Files.readString(directory.resolve(QUEST + ".claim"));
        journal.complete(QUEST);
        assertEquals(reserved, Files.readString(directory.resolve(QUEST + ".claim")));
        assertTrue(Files.exists(directory.resolve(QUEST + ".complete")));
        assertEquals(Set.of(QUEST), new RewardJournal(directory).ids(RewardJournal.Phase.COMPLETE));
    }

    @Test void legacyCompletedRecordStillLoads() throws Exception {
        RewardJournal journal = new RewardJournal(directory);
        journal.reserve(QUEST, REWARDS);
        Path file = directory.resolve(QUEST + ".claim");
        Files.writeString(file, Files.readString(file).replace("PENDING", "COMPLETE"));
        assertEquals(Set.of(QUEST), new RewardJournal(directory).ids(RewardJournal.Phase.COMPLETE));
    }

    @Test void partialCompletionBlocksPayoutWithoutRemovingReservation() throws Exception {
        RewardJournal journal = new RewardJournal(directory);
        journal.reserve(QUEST, REWARDS);
        Files.writeString(directory.resolve(QUEST + ".complete"), "partial");
        assertThrows(java.io.IOException.class, () -> journal.complete(QUEST));
        RewardJournal reopened = new RewardJournal(directory);
        assertFalse(reopened.available());
        assertThrows(java.io.IOException.class, () -> reopened.reserve(QUEST, REWARDS));
        assertTrue(Files.readString(directory.resolve(QUEST + ".claim")).contains("PENDING"));
    }

    @Test void orphanCompletionFailsClosed() throws Exception {
        RewardJournal journal = new RewardJournal(directory);
        journal.reserve(QUEST, REWARDS);
        journal.complete(QUEST);
        Files.delete(directory.resolve(QUEST + ".claim"));
        assertFalse(new RewardJournal(directory).available());
    }

    @Test void rewardFailurePreservesTaskProgressAndBlocksOnlyPayouts() {
        var state = new ch.minenox.firsttorch.guide.progress.ProgressState(Set.of("1000000000000001"), Set.of(QUEST));
        var progress = new ch.minenox.firsttorch.network.ProgressPayload(state, java.util.Map.of("1000000000000001", 1), true);
        var observed = ProgressObservation.withClaims(progress, Set.of("2000000000000002"), Set.of(), false,
                Set.of(QUEST, "2000000000000002"));
        assertTrue(observed.available());
        assertEquals(state, observed.state());
        assertEquals(progress.taskCounts(), observed.taskCounts());
        assertEquals(Set.of(QUEST), observed.pendingQuestIds());
        assertEquals(Set.of("2000000000000002"), observed.claimedQuestIds());
    }
}
