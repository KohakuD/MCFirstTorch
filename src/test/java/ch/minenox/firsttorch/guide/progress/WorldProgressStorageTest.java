package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.server.ServerProgressRepository;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WorldProgressStorageTest {
    @TempDir Path directory;

    @Test void survivesClosingAndReopeningNativeWorldStorage() {
        SharedConstants.tryDetectVersion();
        var type = new SavedDataType<>(Identifier.parse("firsttorch:progress"),
                WorldProgressData::new, WorldProgressData.CODEC);
        UUID player = UUID.randomUUID();
        ProgressState expected = new ProgressState(Set.of("3000000000000001"), Set.of("2000000000000001"));
        try (var storage = new SavedDataStorage(null, directory, null, RegistryAccess.EMPTY)) {
            var data = storage.computeIfAbsent(type);
            data.put(player, expected);
        }
        try (var reopened = new SavedDataStorage(null, directory, null, RegistryAccess.EMPTY)) {
            var data = reopened.get(type);
            assertNotNull(data);
            assertEquals(expected, data.get(player));
            assertEquals(ProgressState.EMPTY, data.get(UUID.randomUUID()));
        }
    }

    @Test void retainsHistoryWhenDefinitionsAreTemporarilyAbsent() {
        var data = new WorldProgressData();
        UUID player = UUID.randomUUID();
        ProgressState historical = new ProgressState(Set.of("3000000000000001"), Set.of("2000000000000001"));
        data.put(player, historical);
        data.setDirty(false);
        ServerProgressRepository.remember(data, player, ProgressState.EMPTY);
        assertEquals(historical, data.get(player));
        assertFalse(data.isDirty());
    }

    @Test void playerHistoryRemainsIsolatedAcrossUpdatesAndReopens() {
        SharedConstants.tryDetectVersion();
        var type = new SavedDataType<>(Identifier.parse("firsttorch:progress"),
                WorldProgressData::new, WorldProgressData.CODEC);
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        ProgressState initial = new ProgressState(Set.of("3000000000000001"), Set.of("2000000000000001"));
        ProgressState next = new ProgressState(Set.of("3000000000000002"), Set.of("2000000000000002"));
        try (var storage = new SavedDataStorage(null, directory, null, RegistryAccess.EMPTY)) {
            var data = storage.computeIfAbsent(type);
            ServerProgressRepository.remember(data, first, initial);
            assertEquals(ProgressState.EMPTY, data.get(second));
            ServerProgressRepository.remember(data, second, initial);
        }
        try (var storage = new SavedDataStorage(null, directory, null, RegistryAccess.EMPTY)) {
            var data = storage.get(type);
            assertNotNull(data);
            assertEquals(initial, data.get(first));
            assertEquals(initial, data.get(second));
            ServerProgressRepository.remember(data, first, next);
            assertEquals(initial, data.get(second));
        }
        try (var storage = new SavedDataStorage(null, directory, null, RegistryAccess.EMPTY)) {
            var data = storage.get(type);
            assertNotNull(data);
            assertEquals(new ProgressState(Set.of("3000000000000001", "3000000000000002"),
                    Set.of("2000000000000001", "2000000000000002")), data.get(first));
            assertEquals(initial, data.get(second));
        }
    }

    @Test void samePlayerHasIndependentHistoryInDifferentWorlds() {
        SharedConstants.tryDetectVersion();
        var type = new SavedDataType<>(Identifier.parse("firsttorch:progress"),
                WorldProgressData::new, WorldProgressData.CODEC);
        UUID player = UUID.randomUUID();
        ProgressState expected = new ProgressState(Set.of("3000000000000001"), Set.of("2000000000000001"));
        try (var first = new SavedDataStorage(null, directory.resolve("first"), null, RegistryAccess.EMPTY);
             var second = new SavedDataStorage(null, directory.resolve("second"), null, RegistryAccess.EMPTY)) {
            ServerProgressRepository.remember(first.computeIfAbsent(type), player, expected);
            assertEquals(ProgressState.EMPTY, second.computeIfAbsent(type).get(player));
        }
        try (var first = new SavedDataStorage(null, directory.resolve("first"), null, RegistryAccess.EMPTY);
             var second = new SavedDataStorage(null, directory.resolve("second"), null, RegistryAccess.EMPTY)) {
            assertEquals(expected, first.computeIfAbsent(type).get(player));
            assertEquals(ProgressState.EMPTY, second.computeIfAbsent(type).get(player));
        }
    }
}
