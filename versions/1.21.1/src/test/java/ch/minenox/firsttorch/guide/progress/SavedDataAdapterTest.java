package ch.minenox.firsttorch.guide.progress;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

final class SavedDataAdapterTest {
    private static final UUID FIRST = new UUID(0, 1);
    private static final UUID SECOND = new UUID(0, 2);
    private static final HolderLookup.Provider REGISTRIES = HolderLookup.Provider.create(java.util.stream.Stream.empty());

    @Test
    void nativeSaveAndLoadRetainTwoPlayersAndHistoricalProgress() {
        var data = new WorldProgressData();
        var first = new ProgressState(Set.of("1000000000000001"), Set.of("700000000000000F"));
        var second = new ProgressState(Set.of("1000000000000002"), Set.of());
        data.put(FIRST, first);
        data.put(SECOND, second);
        var destination = new CompoundTag();
        destination.putString("unrelated", "preserved");
        assertSame(destination, data.save(destination, REGISTRIES));
        assertEquals("preserved", destination.getString("unrelated"));
        var restored = WorldProgressData.load(destination, REGISTRIES);
        assertEquals(first, restored.get(FIRST));
        assertEquals(second, restored.get(SECOND));
        assertFalse(restored.isDirty());
        assertEquals(destination, restored.save(new CompoundTag().merge(destination), REGISTRIES));
    }

    @Test
    void nativeWelcomeSaveAndLoadKeepAcknowledgementsIndependent() {
        var data = new WorldWelcomeData();
        data.acknowledge(FIRST);
        var restored = WorldWelcomeData.load(data.save(new CompoundTag(), REGISTRIES), REGISTRIES);
        assertTrue(restored.isAcknowledged(FIRST));
        assertFalse(restored.isAcknowledged(SECOND));
        assertFalse(restored.isDirty());
        restored.acknowledge(SECOND);
        var restarted = WorldWelcomeData.load(restored.save(new CompoundTag(), REGISTRIES), REGISTRIES);
        assertTrue(restarted.isAcknowledged(FIRST));
        assertTrue(restarted.isAcknowledged(SECOND));
    }

    @Test
    void nativeLoadFailsInsteadOfReplacingUnsupportedOrMalformedData() {
        for (int version : new int[]{0, 2}) {
            var progress = new WorldProgressData().save(new CompoundTag(), REGISTRIES);
            progress.putInt("version", version);
            assertThrows(RuntimeException.class, () -> WorldProgressData.load(progress, REGISTRIES));
            var welcome = new WorldWelcomeData().save(new CompoundTag(), REGISTRIES);
            welcome.putInt("version", version);
            assertThrows(RuntimeException.class, () -> WorldWelcomeData.load(welcome, REGISTRIES));
        }
        assertThrows(RuntimeException.class, () -> WorldProgressData.load(new CompoundTag(), REGISTRIES));
        assertThrows(RuntimeException.class, () -> WorldWelcomeData.load(new CompoundTag(), REGISTRIES));
    }
}
