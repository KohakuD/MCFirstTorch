package ch.minenox.firsttorch.guide.data;

import static org.junit.jupiter.api.Assertions.*;
import ch.minenox.firsttorch.guide.GuideSnapshot;
import ch.minenox.firsttorch.guide.model.*;
import ch.minenox.firsttorch.network.GuideSnapshotWireCodec;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

final class QuestIconTest {
    @Test void turtleReferenceUsesCurrentScuteIdentifier() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var turtle = GuideJson.read(input).chapters().stream().flatMap(c -> c.quests().stream())
                    .filter(q -> q.id().equals("1AA0B0C0D0E00004")).findFirst().orElseThrow();
            assertEquals("minecraft:turtle_scute", turtle.iconItemId());
        }
    }

    @Test void bundledIconsUseOriginalVanillaItemsAndRoundTrip() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var guide = GuideJson.read(input);
            assertEquals(List.of("minecraft:book", "minecraft:torch", "minecraft:white_bed", "minecraft:lantern", "minecraft:bread", "minecraft:iron_ore", "minecraft:shield", "minecraft:stone_pickaxe", "minecraft:water_bucket", "minecraft:compass", "minecraft:lodestone", "minecraft:filled_map", "minecraft:wheat", "minecraft:composter", "minecraft:hay_block", "minecraft:chest", "minecraft:oak_boat", "minecraft:bell", "minecraft:emerald", "minecraft:diamond_pickaxe", "minecraft:enchanting_table", "minecraft:bookshelf", "minecraft:flint_and_steel", "minecraft:golden_helmet", "minecraft:obsidian", "minecraft:fire_charge", "minecraft:gold_ingot", "minecraft:quartz", "minecraft:nether_bricks", "minecraft:brewing_stand", "minecraft:brewing_stand", "minecraft:fire_charge", "minecraft:ender_eye", "minecraft:compass", "minecraft:stone_bricks", "minecraft:end_portal_frame", "minecraft:ender_eye", "minecraft:end_stone", "minecraft:end_crystal", "minecraft:dragon_head", "minecraft:ender_pearl", "minecraft:chorus_flower", "minecraft:shulker_shell", "minecraft:elytra", "minecraft:elytra", "minecraft:firework_rocket", "minecraft:compass", "minecraft:bone", "minecraft:polished_blackstone_bricks", "minecraft:redstone", "minecraft:repeater", "minecraft:comparator", "minecraft:sticky_piston", "minecraft:observer", "minecraft:iron_door", "minecraft:milk_bucket", "minecraft:bone", "minecraft:blaze_rod", "minecraft:ender_pearl", "minecraft:ink_sac", "minecraft:honeycomb", "minecraft:breeze_rod", "minecraft:white_concrete", "minecraft:name_tag", "minecraft:honey_bottle", "minecraft:brush", "minecraft:music_disc_cat", "minecraft:golden_apple", "minecraft:cauldron", "minecraft:carved_pumpkin", "minecraft:respawn_anchor", "minecraft:sulfur"),
                    guide.chapters().stream().map(ChapterDefinition::iconItemId).toList());
            assertEquals(List.of("minecraft:book", "minecraft:map", "minecraft:spyglass", "minecraft:compass",
                    "minecraft:chest", "minecraft:experience_bottle", "minecraft:book", "minecraft:torch",
                    "minecraft:wooden_sword", "minecraft:rabbit_foot", "minecraft:leather_boots", "minecraft:sugar",
                    "minecraft:feather"), guide.chapters().stream().flatMap(c -> c.quests().stream())
                    .map(QuestDefinition::iconItemId).limit(13).toList());
            roundTrip(new GuideSnapshot(List.of(guide)));
        }
    }

    @Test void omittedIconsRemainCompatibleWithOlderData() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/getting_started.json")) {
            var guide = GuideJson.read(input);
            assertNull(guide.chapters().getFirst().quests().getFirst().iconItemId());
            assertNull(guide.chapters().getFirst().iconItemId());
            roundTrip(new GuideSnapshot(List.of(guide)));
        }
    }

    @Test void rejectsMalformedOrOversizedIconIdentifiers() {
        for (String id : List.of("", "minecraft:Bad", "missing_namespace", "minecraft:" + "a".repeat(256))) {
            var quest = new QuestDefinition("2000000000000001", 0, "quest.test.title", "quest.test.description",
                    new QuestPosition(0, 0), List.of(), List.of(), List.of(), id);
            var chapter = new ChapterDefinition("1000000000000001", 0, "chapter.test.title", "chapter.test.description", List.of(quest));
            assertThrows(IllegalArgumentException.class, () -> new GuideSnapshot(List.of(new GuideDefinition(
                    1, "0000000000000001", "guide.test.title", "guide.test.description", List.of(chapter)))));
        }
    }

    @Test void rejectsMalformedChapterIconIdentifiers() {
        for (String id : List.of("", "minecraft:Bad", "missing_namespace", "minecraft:" + "a".repeat(256))) {
            var quest = new QuestDefinition("2000000000000001", 0, "quest.test.title", "quest.test.description",
                    new QuestPosition(0, 0), List.of());
            var chapter = new ChapterDefinition("1000000000000001", 0, "chapter.test.title",
                    "chapter.test.description", List.of(quest), id);
            assertThrows(IllegalArgumentException.class, () -> new GuideSnapshot(List.of(new GuideDefinition(
                    1, "0000000000000001", "guide.test.title", "guide.test.description", List.of(chapter)))));
        }
    }

    private static void roundTrip(GuideSnapshot snapshot) {
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            GuideSnapshotWireCodec.encode(buffer, snapshot);
            assertEquals(snapshot, GuideSnapshotWireCodec.decode(buffer));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }
}
