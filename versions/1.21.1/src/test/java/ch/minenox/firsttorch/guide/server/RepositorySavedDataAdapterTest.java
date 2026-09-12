package ch.minenox.firsttorch.guide.server;

import static org.junit.jupiter.api.Assertions.*;

import ch.minenox.firsttorch.guide.progress.ProgressState;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.common.IOUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class RepositorySavedDataAdapterTest {
    private static final UUID FIRST = new UUID(0, 1);
    private static final UUID SECOND = new UUID(0, 2);
    private static final HolderLookup.Provider REGISTRIES = HolderLookup.Provider.create(java.util.stream.Stream.empty());
    @TempDir Path directory;

    @BeforeAll
    static void initializeVersion() {
        SharedConstants.tryDetectVersion();
    }

    private DimensionDataStorage open() {
        // Both mod factories opt out of vanilla data fixing.
        return new DimensionDataStorage(directory.toFile(), null, REGISTRIES);
    }

    private static void save(DimensionDataStorage storage) {
        storage.save();
        IOUtilities.waitUntilIOWorkerComplete();
    }

    @Test
    void realSaveAndReopenPreservePlayersHistoricalIdsAndDirtyRules() throws Exception {
        var storage = open();
        var progress = ServerProgressRepository.get(storage, directory);
        var welcome = ServerWelcomeRepository.get(storage, directory);
        assertFalse(progress.isDirty());
        assertFalse(welcome.isDirty());
        save(storage);
        assertFalse(Files.exists(directory.resolve("firsttorch_progress.dat")));
        assertFalse(Files.exists(directory.resolve("firsttorch_welcome.dat")));
        var historic = new ProgressState(Set.of("1000000000000001"), Set.of("700000000000000F"));
        var later = new ProgressState(Set.of("1000000000000002"), Set.of());
        ServerProgressRepository.remember(progress, FIRST, historic);
        ServerProgressRepository.remember(progress, FIRST, later);
        ServerProgressRepository.remember(progress, SECOND, later);
        welcome.acknowledge(FIRST);
        assertTrue(progress.isDirty());
        assertTrue(welcome.isDirty());
        save(storage);
        assertFalse(progress.isDirty());
        assertFalse(welcome.isDirty());
        assertTrue(Files.isRegularFile(directory.resolve("firsttorch_progress.dat")));
        assertTrue(Files.isRegularFile(directory.resolve("firsttorch_welcome.dat")));

        var restarted = open();
        var restored = ServerProgressRepository.get(restarted, directory);
        var restoredWelcome = ServerWelcomeRepository.get(restarted, directory);
        assertEquals(Set.of("1000000000000001", "1000000000000002"), restored.get(FIRST).completedTaskIds());
        assertEquals(historic.completedQuestIds(), restored.get(FIRST).completedQuestIds());
        assertEquals(later, restored.get(SECOND));
        assertTrue(restoredWelcome.isAcknowledged(FIRST));
        assertFalse(restoredWelcome.isAcknowledged(SECOND));
        assertFalse(restored.isDirty());
        assertFalse(restoredWelcome.isDirty());
        ServerProgressRepository.remember(restored, FIRST, later);
        restoredWelcome.acknowledge(FIRST);
        assertFalse(restored.isDirty());
        assertFalse(restoredWelcome.isDirty());
        IOUtilities.waitUntilIOWorkerComplete();
    }

    @Test
    void corruptProgressRemainsUntouchedAndBlockedUntilStorageRestart() throws Exception {
        var path = directory.resolve("firsttorch_progress.dat");
        byte[] corrupt = {1, 2, 3, 4};
        Files.write(path, corrupt);
        var storage = open();
        assertThrows(IllegalStateException.class, () -> ServerProgressRepository.get(storage, directory));
        save(storage);
        assertArrayEquals(corrupt, Files.readAllBytes(path));
        // Removing the broken file still must not unlock a running world's cached failure.
        Files.delete(path);
        assertThrows(IllegalStateException.class, () -> ServerProgressRepository.get(storage, directory));
        assertNotNull(ServerProgressRepository.get(open(), directory));
        assertNotNull(ServerWelcomeRepository.get(storage, directory));
    }

    @Test
    void corruptWelcomeRemainsUntouchedWithoutBlockingProgress() throws Exception {
        var path = directory.resolve("firsttorch_welcome.dat");
        byte[] corrupt = {1, 2, 3, 4};
        Files.write(path, corrupt);
        var storage = open();
        assertThrows(IllegalStateException.class, () -> ServerWelcomeRepository.get(storage, directory));
        save(storage);
        assertArrayEquals(corrupt, Files.readAllBytes(path));
        Files.delete(path);
        assertThrows(IllegalStateException.class, () -> ServerWelcomeRepository.get(storage, directory));
        assertNotNull(ServerWelcomeRepository.get(open(), directory));
        assertNotNull(ServerProgressRepository.get(storage, directory));
    }
}
