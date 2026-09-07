package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.data.GuideJson;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Read-only design fixture. Never installed into the server or the live client cache. */
public final class DesignPreview {
    public static final String GUIDE_ID = "0D15000000000001";
    private static final Set<String> COMPLETED = Set.of("2D15000000000001", "2D15000000000002", "2D15000000000003");
    private static final Map<String, String> ICONS = Map.ofEntries(
            Map.entry("1D15000000000001", "minecraft:oak_log"),
            Map.entry("2D15000000000001", "minecraft:oak_log"),
            Map.entry("2D15000000000002", "minecraft:crafting_table"),
            Map.entry("2D15000000000003", "minecraft:wooden_pickaxe"),
            Map.entry("2D15000000000004", "minecraft:oak_door"),
            Map.entry("2D15000000000005", "minecraft:torch"),
            Map.entry("2D15000000000006", "minecraft:red_bed"),
            Map.entry("2D15000000000007", "minecraft:bread"),
            Map.entry("2D15000000000008", "minecraft:compass"),
            Map.entry("1D15000000000002", "minecraft:stone_pickaxe"),
            Map.entry("2D15000000000009", "minecraft:stone_pickaxe"),
            Map.entry("2D1500000000000A", "minecraft:furnace"),
            Map.entry("2D1500000000000B", "minecraft:iron_ingot"),
            Map.entry("1D15000000000003", "minecraft:oak_door"),
            Map.entry("2D1500000000000C", "minecraft:oak_door"),
            Map.entry("2D1500000000000D", "minecraft:chest"),
            Map.entry("2D1500000000000E", "minecraft:compass"),
            Map.entry("1D15000000000004", "minecraft:wheat_seeds"),
            Map.entry("2D1500000000000F", "minecraft:wheat_seeds"),
            Map.entry("2D15000000000010", "minecraft:wheat"),
            Map.entry("2D15000000000011", "minecraft:bread"),
            Map.entry("1D15000000000005", "minecraft:chest"),
            Map.entry("2D15000000000012", "minecraft:chest"),
            Map.entry("2D15000000000013", "minecraft:map"),
            Map.entry("2D15000000000014", "minecraft:oak_boat"));
    private static final GuideSnapshot SNAPSHOT = load();
    private DesignPreview() {}

    private static GuideSnapshot load() {
        try (var input = DesignPreview.class.getResourceAsStream("/assets/firsttorch/preview/guide.json")) {
            return new GuideSnapshot(List.of(GuideJson.read(input)));
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read bundled design preview", exception);
        }
    }

    public static GuideSnapshot snapshot() { return SNAPSHOT; }
    public static boolean isPreview(String guideId) { return GUIDE_ID.equals(guideId); }
    public static boolean completed(String questId) { return COMPLETED.contains(questId); }
    public static String itemId(String objectId) { return ICONS.getOrDefault(objectId, "minecraft:book"); }

    public record Requirement(String itemId, int count) {}

    /** Illustrative quantities for the design fixture, not native curriculum requirements. */
    public static List<Requirement> requirements(String questId) {
        return switch (questId) {
            case "2D15000000000004", "2D1500000000000C" -> List.of(
                    new Requirement("minecraft:oak_planks", 8),
                    new Requirement("minecraft:oak_door", 1),
                    new Requirement("minecraft:torch", 4));
            case "2D15000000000006" -> List.of(
                    new Requirement("minecraft:white_wool", 3), new Requirement("minecraft:oak_planks", 3));
            case "2D15000000000005" -> List.of(
                    new Requirement("minecraft:coal", 1), new Requirement("minecraft:stick", 1));
            case "2D15000000000007", "2D15000000000011" -> List.of(new Requirement("minecraft:wheat", 3));
            default -> List.of(new Requirement(itemId(questId), 1));
        };
    }
}
