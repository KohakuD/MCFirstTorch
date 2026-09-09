# Unusual-mechanics reference verification

Target: Minecraft Java 26.1.2, native First Torch 0.13.0-alpha.1. The first nine cards are optional reading, not required experiments or a complete secrets catalogue.

## Target data and code

### Jukebox and brushing code follow-up (2026-09-09)

Read the original 26.1.2 client methods with `javap -c -p`. `JukeboxSongPlayer.tick` calls `stop` when `JukeboxSong.hasFinished` succeeds, rather than restarting the song. `JukeboxBlock.useWithoutItem` calls `JukeboxBlockEntity.popOutTheItem`, which removes the stored stack and creates a copy as an ItemEntity with pickup delay. The card now explicitly tells the learner to collect and reinsert that disc.

`BrushableBlockEntity.brush` increments accepted strokes until completion; `brushingCompleted` drops content and replaces the block using `BrushableBlock.getTurnsInto`. `checkReset` decreases accumulated progress after an interruption. The card now explains safe resumption instead of mining a seemingly stalled block. `BrushableBlock.tick` starts a falling block with drops disabled when its support is absent. These checks support the existing warnings; they do not constitute a physical ruin excavation test or verify every site loot possibility.

### Completed bilingual instructional-structure audit (2026-09-09)

All 27 current descriptions were read in English and German. Each has a trigger or preparation, an observable result, a relevant safety limitation and an optional experiment or safe observation. The table records the actual observation route, not a claim that the interaction was performed. Sequential Archaeology, Cauldron and Pumpkin prose does not need extra repeated headings.

| Chapter (three cards each) | Trigger/result coverage | Safe optional observations |
| --- | --- | --- |
| Block transformations | Water contact to Concrete; Water Bottle to Mud; Furnace drying | One powder comparison; one Dirt block; shallow-water Sponge and Furnace |
| Special names | Exact Anvil names and their cosmetic outcomes | Calm home animal; Sheep colour cycle; safely enclosed Rabbit |
| Bee mechanics | Pollen and crops; full hive with Bottle or Shears | Watch an open flight path; protected single harvests without breaking the hive |
| Archaeology | Recognise suspicious texture; Brush until extraction; interpret random find | Compare the original illustration; brush only an already safe site; inspect an existing find |
| Music | Jukebox playback; Skeleton final hit; nine Disc 5 fragments | Home playback; inspect an owned disc without staging combat; view recipe |
| Curing | Safe containment; Weakness then ordinary Golden Apple; delayed recovery | Identify an already contained patient; compare apples; listen safely, never force infection |
| Cauldrons | Bucket fill; single bottle levels; dye removal | Home fill; remove/return one bottle; wash only unwanted colour |
| Pumpkins | Carve; wear with limits; craft light | One home Pumpkin; inspect head-slot item without Endermen; view/craft recipe |
| Respawn Anchors | Recipe; Nether charge/spawn; charge consumption | Compare described recipe; distinguish Glowstone block from Dust; recall linked Bed lesson, no activation/death test |

Minor wording corrections: German Jukebox gender, and English Jack o'Lantern refers to the carving step rather than a crafting recipe for carving. No new gameplay claim, objective or reward was introduced. Broader target-code verification remains separate.

Reading audit (2026-09-09): all 27 bilingual mechanics cards were compared with the evidence recorded below, with no new factual contradiction identified. Practical versus optional-discovery cues now introduce each card. Archaeology starts with a safe comparison of the existing illustration; Anchor cards offer inventory/recipe/linked-lesson checks without activation, travel or death. These are deliberately safe reading exercises, not claims of completed physical tests. The archive verifier still does not cover every interaction.

- Interaction batch: `recipe/respawn_anchor.json` specifies six Crying Obsidian around three Glowstone blocks; `dimension_type/the_nether.json` enables `gameplay/respawn_anchor_works`. `RespawnAnchorBlock` accepts `Items.GLOWSTONE`, limits charges to four, and checks the environment attribute before setting spawn or exploding. The reading warns against activation in the Overworld/End and never requires a death test. `recipe/jack_o_lantern.json` places Carved Pumpkin over Torch; `loot_table/carve/pumpkin.json` gives four Seeds. `PumpkinBlock` checks Shears. `EnderMan.isBeingStaredBy` uses the disguise predicate, whose item tag contains Carved Pumpkin. `CauldronInteractions` fills water to level three, permits bucket retrieval only at that level, checks `Potions.WATER` for bottle input and removes a water level on bottle retrieval or dye washing. `cauldron_can_remove_dye` includes Leather Chestplate. These are Java instructions, not Bedrock potion/dyed-water behaviour.

- Discovery batch: target `recipe/brush.json` uses Feather/Copper Ingot/Stick vertically; `recipe/music_disc_5.json` combines nine Disc Fragment 5 items. `entities/creeper.json` selects from `creeper_drop_music_discs` only with an attacker in `skeletons`; the lesson uses a normal Skeleton's final arrow as its concrete example and never requires a combat experiment. `ZombieVillager.mobInteract` checks the ordinary Golden Apple and active Weakness, then starts conversion with 3600–6000 ticks. The lesson says several minutes and keeps the enclosure safe throughout, without promising an exact wall-clock duration. No forced infection or repeated-discount exploit is taught.

- `recipe/sponge.json`: furnace smelting converts Wet Sponge to Sponge. The lesson uses a normal Overworld Furnace and fuel, never a required Nether trip.
- `tags/block/convertable_to_mud.json` includes Dirt. `PotionItem.useOn` checks Water potion content and converts an eligible block into Mud; use the top of a ground-level Dirt block in the example.
- `ConcretePowderBlock.canSolidify` checks the Water fluid tag. The lesson places one Powder block on solid ground beside water, not a gravity trap or large build; rain and bottles do not substitute for a water block.
- `LivingEntityRenderer.isUpsideDownName` matches exact `Dinnerbone` or `Grumm`; `SheepRenderer` checks `jeb_`; `RabbitRenderer` checks `Toast` and selects the corresponding texture. These are appearance effects, not new breeds or combat benefits. Sheep wool colour is separately saved and used by colour-specific shearing tables.
- `BeehiveBlock.useItemOn` requires honey level five before Bottle/Shears harvesting. `loot_table/harvest/beehive.json` gives three Honeycombs, separate from breaking the nest. `CampfireBlock.isSmokeyPos` checks the blocks below, with a directly covered lit-campfire exception. The simple lesson setup is a lit Campfire two blocks below the nest, a Carpet directly on the fire, and no additional obstruction. This protects against contact with the fire while preserving the target Java smoke check. It is not a promise to stop Bees already attacking.

The read-only archive checks are repeatable with `pwsh ./tools/verify-mechanics-data.ps1 -MinecraftJar <26.1.2-client.jar>`. They verify recipes, harvest tables and item assets, not live world interactions. Code checks used the target client's unobfuscated classes without modifying or redistributing them.

## Supporting primary sources

- [Mojang: Cauldron](https://www.minecraft.net/nl-nl/article/cauldron) provides context; edition-specific details are checked against the target Java interactions.
- [Mojang: Jack o'Lantern](https://www.minecraft.net/en-us/article/block-week--jack-o-lantern) describes carving and lighting; target recipes and carving loot provide exact quantities.
- [Mojang: Spawning and dying](https://www.minecraft.net/en-us/article/spawning-and-dying) explains Nether anchors, Glowstone charges and respawn safety.

- [Mojang: Brush](https://www.minecraft.net/pl-pl/article/brush) explains fragile suspicious blocks, brushing rather than mining, possible random finds and Brush durability. The cards do not promise a particular rare find.
- [Mojang: Golden Apple](https://www.minecraft.net/en-us/article/golden-apple) supports Weakness followed by a non-enchanted Golden Apple for Zombie Villager curing.
- [Mojang: Disc Fragment 5 introduction](https://www.minecraft.net/en-us/article/minecraft-snapshot-22w16a) describes the nine-fragment disc; the target recipe remains the current authority.

- [Mojang: Concrete Powder](https://www.minecraft.net/en-us/article/concrete-powder) explains water contact, gravity and the rain/bottle distinction.
- [Mojang: Mud](https://www.minecraft.net/it-it/article/block-month--mud) describes Water Bottle conversion of Dirt.
- [Mojang: Name Tag](https://www.minecraft.net/da-dk/article/taking-inventory--name-tag) explains naming through an Anvil and using the renamed tag on a creature. Target renderer code grounds the three exact cosmetic names.
- [Mojang: Buzzy Bees Java release](https://www.minecraft.net/en-us/article/buzzy-bees-out-now-in-java) documents pollination, crop growth, harvesting and Campfire calming. The target code determines the specific protected setup.

Name-tag examples use peaceful animals at home. Naming costs an Anvil operation and consumes the used tag in survival; no learner must spend it to complete a card. The bee examples never require breaking or moving an occupied nest. No optional reference task grants progression items or gates another lesson.
