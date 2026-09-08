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
}
