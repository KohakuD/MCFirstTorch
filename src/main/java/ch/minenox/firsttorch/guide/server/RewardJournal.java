package ch.minenox.firsttorch.guide.server;

import ch.minenox.firsttorch.guide.model.RewardDefinition;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Append-once reservations: interrupted payouts require manual recovery, never automatic retries. */
final class RewardJournal {
    enum Phase { PENDING, COMPLETE }
    record Entry(Phase phase, List<RewardDefinition> rewards) {}
    private final Path directory;
    private final Map<String, Entry> entries = new HashMap<>();
    private boolean available = true;

    RewardJournal(Path directory) {
        this.directory = directory;
        try {
            if (Files.notExists(directory)) return;
            try (var files = Files.list(directory)) {
                var paths = files.limit(32_769).toList();
                if (paths.size() > 32_768) throw new IOException("Too many claim files");
                Map<String, Entry> completions = new HashMap<>();
                for (Path path : paths) {
                    // A failed completion can leave a temporary file; its reservation still wins.
                    if (path.getFileName().toString().endsWith(".tmp")) continue;
                    String name = path.getFileName().toString();
                    if (!name.matches("[0-7][0-9A-F]{15}\\.(claim|complete)") || Files.size(path) > 1_000_000) {
                        throw new IOException("Invalid claim file: " + path);
                    }
                    Entry entry = decode(Files.readAllLines(path, StandardCharsets.UTF_8));
                    (name.endsWith(".complete") ? completions : entries).put(name.substring(0, 16), entry);
                }
                for (var completion : completions.entrySet()) {
                    Entry reservation = entries.get(completion.getKey());
                    if (reservation == null || completion.getValue().phase() != Phase.COMPLETE
                            || !reservation.rewards().equals(completion.getValue().rewards())) {
                        throw new IOException("Completion does not match reservation");
                    }
                    entries.put(completion.getKey(), completion.getValue());
                }
            }
        } catch (IOException | RuntimeException exception) {
            available = false;
            LogUtils.getLogger().error("First Torch reward journal cannot be loaded at {}; leaving all files untouched for manual recovery.",
                    directory, exception);
        }
    }

    boolean available() { return available; }
    Set<String> ids(Phase phase) {
        Set<String> ids = new HashSet<>();
        entries.forEach((id, entry) -> { if (entry.phase() == phase) ids.add(id); });
        return Set.copyOf(ids);
    }

    boolean reserve(String quest, List<RewardDefinition> rewards) throws IOException {
        requireAvailable(quest);
        if (entries.containsKey(quest)) return false;
        Entry entry = new Entry(Phase.PENDING, List.copyOf(rewards));
        byte[] bytes = encode(entry);
        try {
            Files.createDirectories(directory);
            writeForced(directory.resolve(quest + ".claim"), bytes);
            entries.put(quest, entry);
            return true;
        } catch (IOException | RuntimeException exception) {
            available = false;
            throw exception;
        }
    }

    void complete(String quest) throws IOException {
        requireAvailable(quest);
        Entry pending = entries.get(quest);
        if (pending == null || pending.phase() != Phase.PENDING) throw new IOException("Missing reservation");
        Entry complete = new Entry(Phase.COMPLETE, pending.rewards());
        try {
            // Never replace the reservation: Windows can reject atomic replacement of an existing file.
            // A partial completion fails closed on reload; it must never permit a second payout.
            writeForced(directory.resolve(quest + ".complete"), encode(complete));
            entries.put(quest, complete);
        } catch (IOException | RuntimeException exception) {
            available = false;
            throw exception;
        }
    }

    private void requireAvailable(String quest) throws IOException {
        if (!available || !quest.matches("[0-7][0-9A-F]{15}")) throw new IOException("Claim journal unavailable");
    }

    private static void writeForced(Path path, byte[] bytes) throws IOException {
        try (var channel = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            while (buffer.hasRemaining()) channel.write(buffer);
            channel.force(true);
        }
    }

    private static byte[] encode(Entry entry) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("FIRST_TORCH_CLAIM_1");
        lines.add(entry.phase().name());
        for (RewardDefinition reward : entry.rewards()) {
            lines.add(reward.id() + "\t" + reward.type().name() + "\t"
                    + (reward.itemId() == null ? "-" : reward.itemId()) + "\t" + reward.amount());
        }
        decode(lines);
        return (String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8);
    }

    private static Entry decode(List<String> lines) throws IOException {
        try {
            if (lines.size() < 3 || lines.size() > 34 || !lines.getFirst().equals("FIRST_TORCH_CLAIM_1")) throw new IllegalArgumentException();
            Phase phase = Phase.valueOf(lines.get(1));
            Set<String> ids = new HashSet<>();
            List<RewardDefinition> rewards = new ArrayList<>();
            for (String line : lines.subList(2, lines.size())) {
                String[] fields = line.split("\t", -1);
                if (fields.length != 4 || !fields[0].matches("[0-7][0-9A-F]{15}") || !ids.add(fields[0])) throw new IllegalArgumentException();
                var type = RewardDefinition.Type.valueOf(fields[1]);
                int amount = Integer.parseInt(fields[3]);
                if (amount <= 0 || (type == RewardDefinition.Type.EXPERIENCE ? amount > 10_000 || !fields[2].equals("-")
                        : amount > 4096 || fields[2].length() > 256
                                || !fields[2].matches("[a-z0-9_.-]+:[a-z0-9/._-]+"))) throw new IllegalArgumentException();
                rewards.add(new RewardDefinition(fields[0], type, type == RewardDefinition.Type.ITEM ? fields[2] : null, amount));
            }
            return new Entry(phase, List.copyOf(rewards));
        } catch (RuntimeException exception) { throw new IOException("Corrupt claim journal", exception); }
    }
}
