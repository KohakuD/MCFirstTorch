package ch.minenox.firsttorch.client;

import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.ChapterDefinition;
import ch.minenox.firsttorch.network.ProgressPayload;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Fixed, client-side course milestones derived from the live guide and saved quest completions. */
public final class TrophyCatalog {
    private static final String COURSE_GUIDE_ID = "0013F17C00000001";
    private static final Map<String, Trophy> TROPHIES = Map.ofEntries(
            Map.entry("0F91A2B3C4D5E607", new Trophy("welcome", "minecraft:book")),
            Map.entry("01F57C0E3B9D2468", new Trophy("movement", "minecraft:feather")),
            Map.entry("0C9E12A4B6D83F70", new Trophy("shelter", "minecraft:clock")),
            Map.entry("1D8F4C2A7B9E6053", new Trophy("home", "minecraft:lantern")),
            Map.entry("2A64C8E10B7D395F", new Trophy("food", "minecraft:cooked_beef")),
            Map.entry("3B75D9F21C8E406A", new Trophy("ores", "minecraft:iron_ingot")),
            Map.entry("4C86EA032D9F517B", new Trophy("protection", "minecraft:shield")),
            Map.entry("5D97FB143EA0628C", new Trophy("mining", "minecraft:stone_pickaxe")),
            Map.entry("4C86EA031D9F5B72", new Trophy("iron_essentials", "minecraft:water_bucket")),
            Map.entry("3B75D9F20C8E4A61", new Trophy("finding_home", "minecraft:compass")),
            Map.entry("6EA80C254FB1739D", new Trophy("lodestone", "minecraft:lodestone")),
            Map.entry("7FB91D365AC2840E", new Trophy("maps", "minecraft:filled_map")),
            Map.entry("5D91A7C30E624BF8", new Trophy("farming", "minecraft:wheat")),
            Map.entry("0AC82E476BD3951F", new Trophy("composting", "minecraft:composter")),
            Map.entry("1BD93F587CE4062A", new Trophy("animal_care", "minecraft:hay_block")),
            Map.entry("2CEA40698DF5173B", new Trophy("storage", "minecraft:chest")),
            Map.entry("68C1F4072D9A5BE3", new Trophy("excursions", "minecraft:oak_boat")),
            Map.entry("3DFB517A9E06284C", new Trophy("village", "minecraft:bell")),
            Map.entry("4E0C628BAF17395D", new Trophy("trading", "minecraft:emerald")),
            Map.entry("6B3D9F215E8C4A70", new Trophy("deep_mining", "minecraft:diamond_pickaxe")),
            Map.entry("5F1D739CB0284A6E", new Trophy("enchanting", "minecraft:enchanting_table")),
            Map.entry("607E84ADB1395B7F", new Trophy("library", "minecraft:bookshelf")),
            Map.entry("6138B4E07D952AC6", new Trophy("nether_portal", "minecraft:flint_and_steel")),
            Map.entry("7249C5F18EA63BD8", new Trophy("nether_equipment", "minecraft:golden_helmet")),
            Map.entry("735AD6029FB74CE9", new Trophy("nether_arrival", "minecraft:obsidian")),
            Map.entry("746BE713A0C85DFA", new Trophy("nether_safety", "minecraft:fire_charge")),
            Map.entry("7249E5AFB61C7D28", new Trophy("piglin_barter", "minecraft:gold_ingot")),
            Map.entry("757CF824B1D96E0B", new Trophy("nether_resources", "minecraft:quartz")),
            Map.entry("766D0935C2EA7F1C", new Trophy("nether_fortress", "minecraft:nether_bricks")),
            Map.entry("777EA146D3FB802D", new Trophy("fortress_return", "minecraft:brewing_stand")),
            Map.entry("735A06C29D714BE8", new Trophy("brewing", "minecraft:brewing_stand")),
            Map.entry("788FB257E40C913E", new Trophy("fire_resistance", "minecraft:fire_charge")),
            Map.entry("746B17D3AE825CF0", new Trophy("ender_eyes", "minecraft:ender_eye")),
            Map.entry("7990C368F51DA24F", new Trophy("stronghold_search", "minecraft:compass")),
            Map.entry("757C28E4BF936D0A", new Trophy("stronghold_interior", "minecraft:stone_bricks")),
            Map.entry("7AA1D479062EB350", new Trophy("portal_room", "minecraft:end_portal_frame")),
            Map.entry("7BB2E58A173FC461", new Trophy("end_preparation", "minecraft:ender_eye")),
            Map.entry("779E4A06D1B58F4E", new Trophy("end_arrival", "minecraft:end_stone")),
            Map.entry("7CC3F69B2840D572", new Trophy("crystal_removal", "minecraft:end_crystal")),
            Map.entry("7DD407AC3951E683", new Trophy("dragon_victory", "minecraft:dragon_head")),
            Map.entry("78AF5B17E2C6904D", new Trophy("outer_arrival", "minecraft:ender_pearl")),
            Map.entry("7EE518BD4A62F794", new Trophy("chorus_city", "minecraft:chorus_flower")),
            Map.entry("7FF629CE5B7308A5", new Trophy("shulker_city", "minecraft:shulker_shell")),
            Map.entry("60A73ADF6C8419B6", new Trophy("end_ship", "minecraft:elytra")),
            Map.entry("61B84BE07D952AC7", new Trophy("flight_basics", "minecraft:elytra")),
            Map.entry("62C95CF18EA63BD8", new Trophy("flight_journey", "minecraft:firework_rocket")),
            Map.entry("6E095C883A1E5D3D", new Trophy("and_now", "minecraft:compass")),
            Map.entry("702FA5D18C643BE9", new Trophy("mob_drops", "minecraft:bone")),
            Map.entry("63DA6D029FB74CE9", new Trophy("bastion", "minecraft:polished_blackstone_bricks")),
            Map.entry("64EB7E13A0C85DFA", new Trophy("redstone_basics", "minecraft:redstone_lamp")),
            Map.entry("65FC8F24B1D96E0B", new Trophy("redstone_repeaters", "minecraft:repeater")),
            Map.entry("660D9035C2EA7F1C", new Trophy("redstone_comparators", "minecraft:comparator")),
            Map.entry("671EA146D3FB802D", new Trophy("redstone_pistons", "minecraft:sticky_piston")),
            Map.entry("682FB257E40C913E", new Trophy("redstone_observers", "minecraft:observer")),
            Map.entry("6930C368F51DA24F", new Trophy("redstone_door", "minecraft:iron_door")),
            Map.entry("6A41D479062EB350", new Trophy("field_animals", "minecraft:milk_bucket")),
            Map.entry("6B52E58A173FC461", new Trophy("field_overworld", "minecraft:bone")),
            Map.entry("6C63F69B2840D572", new Trophy("field_nether", "minecraft:blaze_rod")),
            Map.entry("6D7407AC3951E683", new Trophy("field_end", "minecraft:ender_pearl")),
            Map.entry("6E8518BD4A62F794", new Trophy("field_water", "minecraft:ink_sac")),
            Map.entry("6F9629CE5B7308A5", new Trophy("field_special", "minecraft:honeycomb")),
            Map.entry("70A7B8C9D0E1F203", new Trophy("field_chambers_garden", "minecraft:breeze_rod")),
            Map.entry("71B8C9D0E1F20314", new Trophy("mechanics_blocks", "minecraft:white_concrete")),
            Map.entry("72C9D0E1F2031425", new Trophy("mechanics_names", "minecraft:name_tag")),
            Map.entry("73D0E1F203142536", new Trophy("mechanics_bees", "minecraft:honey_bottle")),
            Map.entry("74E1F20314253647", new Trophy("mechanics_archaeology", "minecraft:brush")),
            Map.entry("75F2031425364758", new Trophy("mechanics_music", "minecraft:music_disc_cat")),
            Map.entry("7603142536475869", new Trophy("mechanics_curing", "minecraft:golden_apple")),
            Map.entry("771425364758697A", new Trophy("mechanics_cauldrons", "minecraft:cauldron")),
            Map.entry("7825364758697A1B", new Trophy("mechanics_pumpkins", "minecraft:carved_pumpkin")),
            Map.entry("79364758697A1B2C", new Trophy("mechanics_anchor", "minecraft:respawn_anchor")),
            Map.entry("7A26200000000001", new Trophy("mechanics_sulfur", "minecraft:sulfur")));

    private TrophyCatalog() {
    }

    /**
     * Returns the loaded native-course milestones in their authored chapter order.
     * Preview definitions never match the native course guide and therefore cannot earn trophies.
     */
    public static List<Entry> entries(GuideSnapshot guides, ProgressPayload progress) {
        if (guides == null) return List.of();
        Set<String> completed = progress != null && progress.available()
                ? progress.state().completedQuestIds()
                : Set.of();
        return guides.guides().stream()
                .filter(guide -> COURSE_GUIDE_ID.equals(guide.id()))
                .flatMap(guide -> guide.chapters().stream())
                .filter(chapter -> TROPHIES.containsKey(chapter.id()))
                .sorted(Comparator.comparingInt(ChapterDefinition::order))
                .map(chapter -> entry(chapter, completed))
                .toList();
    }

    private static Entry entry(ChapterDefinition chapter, Set<String> completed) {
        Trophy trophy = TROPHIES.get(chapter.id());
        boolean earned = !chapter.quests().isEmpty()
                && chapter.quests().stream().map(quest -> quest.id()).allMatch(completed::contains);
        String prefix = "trophy.firsttorch." + trophy.key();
        return new Entry(chapter.id(), chapter.titleKey(), prefix + ".title", prefix + ".description",
                trophy.iconItemId(), earned);
    }

    public record Entry(String chapterId, String chapterTitleKey, String titleKey,
            String descriptionKey, String iconItemId, boolean earned) {
    }

    private record Trophy(String key, String iconItemId) {
    }
}
