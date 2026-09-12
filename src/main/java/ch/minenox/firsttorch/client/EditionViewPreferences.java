package ch.minenox.firsttorch.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/** Local reading preference, separated by player and target; never stores quest progress. */
final class EditionViewPreferences {
    private EditionViewPreferences() {}

    static String read(Path directory, UUID player, String target, List<String> baselines) throws IOException {
        Path file = file(directory, player, target);
        if (!Files.exists(file)) return null;
        String value = Files.readString(file).strip();
        return baselines.contains(value) ? value : null;
    }

    static void write(Path directory, UUID player, String target, String baseline) throws IOException {
        Path file = file(directory, player, target);
        Files.createDirectories(directory);
        Path temporary = Files.createTempFile(directory, "edition-", ".tmp");
        try {
            Files.writeString(temporary, baseline == null ? "" : baseline);
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static Path file(Path directory, UUID player, String target) {
        if (!target.matches("[0-9]+(?:\\.[0-9]+)*")) throw new IllegalArgumentException("Invalid target");
        return directory.resolve(player + "-" + target + ".txt");
    }
}
