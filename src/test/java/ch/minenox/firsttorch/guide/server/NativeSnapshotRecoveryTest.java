package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.model.RewardDefinition;
import ch.minenox.firsttorch.guide.progress.ProgressState;
import ch.minenox.firsttorch.guide.progress.WorldProgressData;
import ch.minenox.firsttorch.guide.progress.WorldWelcomeData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Closed native storage snapshots, not a simulation of Minecraft player/inventory saves. */
final class NativeSnapshotRecoveryTest {
    private static final UUID FIRST = new UUID(0, 1);
    private static final UUID SECOND = new UUID(0, 2);
    private static final String QUEST = "2000000000000001";
    private static final String LATER_QUEST = "2000000000000002";
    private static final ProgressState INITIAL = new ProgressState(Set.of("3000000000000001"), Set.of(QUEST));
    private static final ProgressState LATER = new ProgressState(
            Set.of("3000000000000001", "3000000000000002"), Set.of(QUEST, LATER_QUEST));
    private static final List<RewardDefinition> REWARDS = List.of(
            new RewardDefinition("4000000000000001", RewardDefinition.Type.EXPERIENCE, null, 5));
    private static final SavedDataType<WorldProgressData> PROGRESS = new SavedDataType<>(
            Identifier.parse("firsttorch:progress"), WorldProgressData::new, WorldProgressData.CODEC);
    private static final SavedDataType<WorldWelcomeData> WELCOME = new SavedDataType<>(
            Identifier.parse("firsttorch:welcome"), WorldWelcomeData::new, WorldWelcomeData.CODEC);
    @TempDir Path directory;

    @Test void restoresOneClosedSnapshotWithoutMergingLaterState() throws Exception {
        SharedConstants.tryDetectVersion();
        Path live = directory.resolve("live");
        Path backup = directory.resolve("backup");
        Path restored = directory.resolve("restored");
        try (var storage = open(live)) {
            storage.computeIfAbsent(PROGRESS).put(FIRST, INITIAL);
            storage.computeIfAbsent(WELCOME).acknowledge(FIRST);
        }
        var first = journal(live, FIRST);
        assertTrue(first.reserve(QUEST, REWARDS));
        first.complete(QUEST);
        assertTrue(journal(live, SECOND).reserve(QUEST, REWARDS));
        copyTree(live, backup);

        // Continue the live world after the backup; none of this may leak into recovery.
        try (var storage = open(live)) {
            storage.get(PROGRESS).put(FIRST, LATER);
            storage.get(PROGRESS).put(SECOND, INITIAL);
            storage.get(WELCOME).acknowledge(SECOND);
        }
        assertTrue(first.reserve(LATER_QUEST, REWARDS));
        first.complete(LATER_QUEST);
        journal(live, SECOND).complete(QUEST);

        copyTree(backup, restored);
        assertSnapshot(restored);
        assertSnapshot(backup);
        try (var storage = open(live)) {
            assertEquals(LATER, storage.get(PROGRESS).get(FIRST));
            assertEquals(INITIAL, storage.get(PROGRESS).get(SECOND));
            assertTrue(storage.get(WELCOME).isAcknowledged(SECOND));
        }
        assertEquals(Set.of(QUEST, LATER_QUEST), journal(live, FIRST).ids(RewardJournal.Phase.COMPLETE));

        // Continuing a restored copy must leave the backup and original world alone.
        try (var storage = open(restored)) {
            storage.get(WELCOME).acknowledge(SECOND);
        }
        assertSnapshot(backup);
    }

    private static void assertSnapshot(Path root) throws IOException {
        try (var storage = open(root)) {
            assertNotNull(storage.get(PROGRESS));
            assertNotNull(storage.get(WELCOME));
            assertEquals(INITIAL, storage.get(PROGRESS).get(FIRST));
            assertEquals(ProgressState.EMPTY, storage.get(PROGRESS).get(SECOND));
            assertTrue(storage.get(WELCOME).isAcknowledged(FIRST));
            assertFalse(storage.get(WELCOME).isAcknowledged(SECOND));
        }
        var first = journal(root, FIRST);
        var second = journal(root, SECOND);
        assertTrue(first.available());
        assertTrue(second.available());
        assertEquals(Set.of(QUEST), first.ids(RewardJournal.Phase.COMPLETE));
        assertTrue(first.ids(RewardJournal.Phase.PENDING).isEmpty());
        assertEquals(Set.of(QUEST), second.ids(RewardJournal.Phase.PENDING));
        assertTrue(second.ids(RewardJournal.Phase.COMPLETE).isEmpty());
        assertFalse(first.reserve(QUEST, REWARDS));
        assertFalse(second.reserve(QUEST, REWARDS));
    }

    private static SavedDataStorage open(Path root) {
        return new SavedDataStorage(null, root.resolve("data"), null, RegistryAccess.EMPTY);
    }

    private static RewardJournal journal(Path root, UUID player) {
        return new RewardJournal(root.resolve("data/firsttorch/reward_claims").resolve(player.toString()));
    }

    private static void copyTree(Path source, Path target) throws IOException {
        try (var paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path destination = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) Files.createDirectories(destination);
                else Files.copy(path, destination);
            }
        }
    }
}
