# Unusual-mechanics reference verification

Target: Minecraft Java 26.1.2, native First Torch 0.13.0-alpha.1. The first nine cards are optional reading, not required experiments or a complete secrets catalogue.

## Target data and code

- Discovery batch: target `recipe/brush.json` uses Feather/Copper Ingot/Stick vertically; `recipe/music_disc_5.json` combines nine Disc Fragment 5 items. `entities/creeper.json` selects from `creeper_drop_music_discs` only with an attacker in `skeletons`; the lesson uses a normal Skeleton's final arrow as its concrete example and never requires a combat experiment. `ZombieVillager.mobInteract` checks the ordinary Golden Apple and active Weakness, then starts conversion with 3600–6000 ticks. The lesson says several minutes and keeps the enclosure safe throughout, without promising an exact wall-clock duration. No forced infection or repeated-discount exploit is taught.

- `recipe/sponge.json`: furnace smelting converts Wet Sponge to Sponge. The lesson uses a normal Overworld Furnace and fuel, never a required Nether trip.
- `tags/block/convertable_to_mud.json` includes Dirt. `PotionItem.useOn` checks Water potion content and converts an eligible block into Mud; use the top of a ground-level Dirt block in the example.
- `ConcretePowderBlock.canSolidify` checks the Water fluid tag. The lesson places one Powder block on solid ground beside water, not a gravity trap or large build; rain and bottles do not substitute for a water block.
- `LivingEntityRenderer.isUpsideDownName` matches exact `Dinnerbone` or `Grumm`; `SheepRenderer` checks `jeb_`; `RabbitRenderer` checks `Toast` and selects the corresponding texture. These are appearance effects, not new breeds or combat benefits. Sheep wool colour is separately saved and used by colour-specific shearing tables.
- `BeehiveBlock.useItemOn` requires honey level five before Bottle/Shears harvesting. `loot_table/harvest/beehive.json` gives three Honeycombs, separate from breaking the nest. `CampfireBlock.isSmokeyPos` checks the blocks below, with a directly covered lit-campfire exception. The simple lesson setup is a lit Campfire two blocks below the nest, a Carpet directly on the fire, and no additional obstruction. This protects against contact with the fire while preserving the target Java smoke check. It is not a promise to stop Bees already attacking.

The read-only archive checks are repeatable with `pwsh ./tools/verify-mechanics-data.ps1 -MinecraftJar <26.1.2-client.jar>`. They verify recipes, harvest tables and item assets, not live world interactions. Code checks used the target client's unobfuscated classes without modifying or redistributing them.

## Supporting primary sources

- [Mojang: Brush](https://www.minecraft.net/pl-pl/article/brush) explains fragile suspicious blocks, brushing rather than mining, possible random finds and Brush durability. The cards do not promise a particular rare find.
- [Mojang: Golden Apple](https://www.minecraft.net/en-us/article/golden-apple) supports Weakness followed by a non-enchanted Golden Apple for Zombie Villager curing.
- [Mojang: Disc Fragment 5 introduction](https://www.minecraft.net/en-us/article/minecraft-snapshot-22w16a) describes the nine-fragment disc; the target recipe remains the current authority.

- [Mojang: Concrete Powder](https://www.minecraft.net/en-us/article/concrete-powder) explains water contact, gravity and the rain/bottle distinction.
- [Mojang: Mud](https://www.minecraft.net/it-it/article/block-month--mud) describes Water Bottle conversion of Dirt.
- [Mojang: Name Tag](https://www.minecraft.net/da-dk/article/taking-inventory--name-tag) explains naming through an Anvil and using the renamed tag on a creature. Target renderer code grounds the three exact cosmetic names.
- [Mojang: Buzzy Bees Java release](https://www.minecraft.net/en-us/article/buzzy-bees-out-now-in-java) documents pollination, crop growth, harvesting and Campfire calming. The target code determines the specific protected setup.

Name-tag examples use peaceful animals at home. Naming costs an Anvil operation and consumes the used tag in survival; no learner must spend it to complete a card. The bee examples never require breaking or moving an occupied nest. No optional reference task grants progression items or gates another lesson.
