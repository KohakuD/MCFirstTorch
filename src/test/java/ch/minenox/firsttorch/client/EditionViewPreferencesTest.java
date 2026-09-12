package ch.minenox.firsttorch.client;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EditionViewPreferencesTest {
    @TempDir Path directory;

    @Test void persistsIndependentlyForEachPlayerAndTargetAndClearsSelection() throws Exception {
        var first = UUID.randomUUID();
        var second = UUID.randomUUID();
        var versions = List.of("1.21.1", "26.1.2");
        EditionViewPreferences.write(directory, first, "26.2", "26.1.2");
        EditionViewPreferences.write(directory, second, "26.2", "1.21.1");
        assertEquals("26.1.2", EditionViewPreferences.read(directory, first, "26.2", versions));
        assertNull(EditionViewPreferences.read(directory, first, "26.1.2", versions));
        EditionViewPreferences.write(directory, first, "26.2", null);
        assertNull(EditionViewPreferences.read(directory, first, "26.2", versions));
        assertEquals("1.21.1", EditionViewPreferences.read(directory, second, "26.2", versions));
    }

    @Test void ignoresUnavailableBaselineAndRejectsUnsafeTarget() throws Exception {
        var player = UUID.randomUUID();
        EditionViewPreferences.write(directory, player, "26.2", "26.3");
        assertNull(EditionViewPreferences.read(directory, player, "26.2", List.of("1.21.1")));
        assertThrows(IllegalArgumentException.class,
                () -> EditionViewPreferences.read(directory, player, "../outside", List.of()));
    }
}
