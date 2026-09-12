package ch.minenox.firsttorch.client;

import java.util.Map;

/** Explains why an existing card appears in a comparison, without changing its identity. */
final class EditionLessonChanges {
    private static final Map<String, String> REASONS = Map.ofEntries(
            Map.entry("4A28C6E10D735BF9", "copper"),
            Map.entry("6C4AE8F31D957B20", "copper"),
            Map.entry("18A6D3F90C754BE2", "copper"),
            Map.entry("59CBED086F24A137", "lodestone"),
            Map.entry("38D24F61E9DA3570", "boat"),
            Map.entry("4BBE2FCA33A44E31", "bastion"),
            Map.entry("32B4C6D8E0F21357", "friends"),
            Map.entry("1CA0B0C0D0E00003", "creaking"),
            Map.entry("2A26200000000001", "sulfur"),
            Map.entry("2A26200000000002", "sulfur"),
            Map.entry("2A26200000000003", "sulfur"),
            Map.entry("2A26200000000004", "sulfur"),
            Map.entry("2A26200000000005", "sulfur"));

    private EditionLessonChanges() {}

    static String key(String questId) {
        String reason = REASONS.get(questId);
        return reason == null ? null : "screen.firsttorch.edition.change." + reason;
    }
}
