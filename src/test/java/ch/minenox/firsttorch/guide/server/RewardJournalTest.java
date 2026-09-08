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
