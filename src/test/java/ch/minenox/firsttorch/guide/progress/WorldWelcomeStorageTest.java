package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class WorldWelcomeStorageTest {
    private static final UUID FIRST = new UUID(0, 1);
    private static final UUID SECOND = new UUID(0, 2);
    private static final UUID NEW_PLAYER = new UUID(0, 3);
    private static final SavedDataType<WorldWelcomeData> WELCOME = new SavedDataType<>(
            Identifier.parse("firsttorch:welcome"), WorldWelcomeData::new, WorldWelcomeData.CODEC);
    private static final SavedDataType<WorldProgressData> PROGRESS = new SavedDataType<>(
            Identifier.parse("firsttorch:progress"), WorldProgressData::new, WorldProgressData.CODEC);
    @TempDir Path directory;

    @BeforeAll static void version() { SharedConstants.tryDetectVersion(); }

    private SavedDataStorage open(Path path) {
        return new SavedDataStorage(null, path, null, RegistryAccess.EMPTY);
    }

    @Test void acknowledgementsRemainPerPlayerAcrossTwoReopens() {
        try (var storage = open(directory)) {
            var data = storage.computeIfAbsent(WELCOME);
            data.acknowledge(FIRST);
            assertFalse(data.isAcknowledged(SECOND));
        }
        try (var storage = open(directory)) {
            var data = storage.get(WELCOME);
            assertNotNull(data);
            assertTrue(data.isAcknowledged(FIRST));
            assertFalse(data.isAcknowledged(SECOND));
            data.acknowledge(SECOND);
            data.acknowledge(FIRST);
        }
        try (var storage = open(directory)) {
            var data = storage.get(WELCOME);
            assertNotNull(data);
            assertTrue(data.isAcknowledged(FIRST));
            assertTrue(data.isAcknowledged(SECOND));
            assertFalse(data.isAcknowledged(NEW_PLAYER));
        }
    }

    @Test void samePlayerStillReceivesWelcomeInAnotherWorld() {
        Path first = directory.resolve("first");
        Path second = directory.resolve("second");
        try (var a = open(first); var b = open(second)) {
            a.computeIfAbsent(WELCOME).acknowledge(FIRST);
            b.computeIfAbsent(WELCOME).acknowledge(SECOND);
        }
        try (var a = open(first); var b = open(second)) {
            assertTrue(a.get(WELCOME).isAcknowledged(FIRST));
            assertFalse(a.get(WELCOME).isAcknowledged(SECOND));
            assertFalse(b.get(WELCOME).isAcknowledged(FIRST));
            assertTrue(b.get(WELCOME).isAcknowledged(SECOND));
        }
    }

    @Test void welcomeAndQuestStorageDoNotReplaceOneAnother() {
        var progress = new ProgressState(Set.of("3000000000000001"), Set.of("2000000000000001"));
        try (var storage = open(directory)) {
            storage.computeIfAbsent(PROGRESS).put(FIRST, progress);
            storage.computeIfAbsent(WELCOME).acknowledge(SECOND);
        }
        try (var storage = open(directory)) {
            assertEquals(progress, storage.get(PROGRESS).get(FIRST));
            assertEquals(ProgressState.EMPTY, storage.get(PROGRESS).get(SECOND));
            assertFalse(storage.get(WELCOME).isAcknowledged(FIRST));
            assertTrue(storage.get(WELCOME).isAcknowledged(SECOND));
        }
    }
}
