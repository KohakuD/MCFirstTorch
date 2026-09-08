# Initial creature-reference verification

Target: Minecraft Java 26.1.2, bundled native First Torch 0.13.0-alpha.1. This is a small reference selection, not an exhaustive creature or rare-drop catalogue.

## Authoritative drop data

Read directly from the target game artifact: `data/minecraft/loot_table/entities/{cow,sheep,pig,chicken,zombie,skeleton,spider,creeper,blaze,wither_skeleton,ghast,magma_cube}.json`.

- Ordinary adult Cow: 1–3 Beef, 0–2 Leather before Looting. Pig: 1–3 Porkchops. Chicken: one Chicken, 0–2 Feathers. Sheep: 1–2 Mutton, separate colour-specific Wool table only when not sheared. Meat entries have conditional furnace-smelting functions. Living-animal milk, eggs and shearing are not death-loot rewards.
- Zombie: 0–2 Rotten Flesh; the rare Iron/Carrot/Potato pool requires player credit and a chance check. Special mounted variants are outside this initial ordinary-Zombie summary.
- Skeleton: separate 0–2 Arrow and Bone entries. Equipment is separate from ordinary loot-table entries and must not be promised.
- Spider: 0–2 String; Spider Eye pool requires player credit and can yield none.
- Creeper: 0–2 Gunpowder; a separate disc pool requires an attacker in `#minecraft:skeletons`. An exploding Creeper is not a reliable Gunpowder harvest.
- Blaze: its entire Rod pool requires player credit; ordinary count is 0–1, with Looting increases. The read-only verification script reads this exact target-game resource.
- Wither Skeleton: possible Coal and Bones; Skull pool requires player credit and a rare chance check with Looting bonus. No guaranteed Skull claim.
- Ghast: possible 0–1 Tear and 0–2 Gunpowder before Looting. The target also has a conditional player-credited Fireball-kill disc; the ordinary-drop card need not enumerate every special disc.
- Magma Cube: Cream requires size at least two and a non-Frog damage source; count can be zero. Frog-source entries instead select a Froglight by Frog variant. Small cubes are not a Cream source even with Looting.

Counts above describe unmodified ordinary loot tables, not an exhaustive promise covering babies, equipment, data packs or other mods. Player-credit conditions can include credited tame-Wolf kills. Looting affects the listed death-loot functions, not milk, egg-laying or shearing. Avoid unsupported exact rates for other behaviours.

## Supporting official reading

### Witch, Goat, Trial Chambers and Pale Garden

The 26.1.2 entity loot tables confirm Witch ordinary ingredient selection (Glowstone Dust, Sugar, Spider Eye, Glass Bottle, Gunpowder, Stick) plus a separate unconditional 4–8 Redstone pool before Looting. Breeze Rods require player credit and yield 1–2 before Looting; Heavy Cores are not Breeze death loot. Bogged ordinary Arrow and Bone pools each yield 0–2 before Looting, with a separate player-credited 0–1 Poison Arrow pool. Goat and Creaking have no ordinary death-item pools. The read-only verifier covers these distinctions and checks the six requested/current icon resources, including `turtle_scute`, directly in the target archive.

Target recipes `wind_charge` and `mace` confirm Breeze Rod uses; block tag `snaps_goat_horn` defines eligible ram blocks. The 26.1.2 manifest's asset index 30 resolves the cached German language asset: Böe, Böenrute, Windkugel, Streitkolben, Sumpfskelett, Knarz, Knarzherz, Blasser Garten and Prüfungs-Spawner. Use these official names rather than translating English names literally or confusing spawners with spawn eggs.

- [Mojang: Java Tricky Trials release](https://feedback.minecraft.net/hc/en-us/articles/27547857163917-Minecraft-Java-Edition-1-21-Tricky-Trials) documents Trial Spawners, Breeze movement/projectiles and Bogged poison attacks. These are reading references, not a required Trial Chamber expedition.
- [Mojang: Java The Garden Awakens release](https://feedback.minecraft.net/hc/en-us/articles/32385811139085-Minecraft-Java-Edition-1-21-4-The-Garden-Awakens) distinguishes naturally heart-linked Creakings from spawn-egg variants, eye-contact freezing, heart destruction and separate Resin/Heart block mechanics. The card does not promise Heart or Resin as mob death loot.
- [Mojang: The Wild Update Java release](https://www.minecraft.net/en-us/article/the-wild-update-out-today-java) documents living Goat ramming for Horns and using Horns as instruments, not killing Goats for Horn drops.

All five additions remain optional manual reading. No ram, capture, kill, loot collection or night-time Heart search is required. Existing completed reading IDs remain intact; the derived Animals and Overworld trophies require their newly added card as well.

### End, aquatic and special-encounter batch

Read target entity tables for Enderman, Shulker, Silverfish, Endermite, Squid, Glow Squid, Dolphin, Turtle, Bee, Fox, Frog and Allay. Enderman has 0–1 Pearls plus Looting. Shulker has a single-shell chance (50% without Looting), with a Looting chance increase rather than a multi-shell count function. Neither table has a player-only pool condition. Squid variants have 1–3 corresponding Ink Sacs plus Looting. Dolphin has 0–1 Cod plus Looting and conditional cooking. Turtle ordinary death loot is 0–2 Seagrass plus Looting, with a separate lightning-only Bowl entry; Turtle Scute is not death loot. The other six named tables have no ordinary item pools; carried equipment is separate. The read-only archive verifier covers key conditions.

- [Mojang: Turtle Shell](https://www.minecraft.net/en-us/article/taking-inventory-turtle-shell) distinguishes growing Turtle Scutes from killing Turtles.
- [Mojang: Meet the Dolphin](https://www.minecraft.net/pl-pl/article/meet-dolphin) explains the need for both water and surface air.
- [Mojang: Allay introduction](https://www.minecraft.net/en-us/article/minecraft-snapshot-22w13a) documents held-item collection and returning the held item, not mining or item duplication.

The cards are intentionally short references, not instructions to build mob farms or capture rare creatures. Further unusual-mechanic experiments belong to 0.12.0; the remaining Redstone illustration work is not marked complete by this batch.

### Animal expansion

The same target archive contains `entities/wolf.json`, `cat.json`, `horse.json`, `camel.json` and `axolotl.json`. Wolf, Camel and Axolotl define no ordinary item-drop pools. Cat gives 0–2 String with no Looting increase function; Horse gives 0–2 Leather with Looting increases. Equipment is not part of these ordinary-drop summaries. The verification script now checks these resources too.

Target item tags confirm `camel_food` = Cactus, `cat_food` = raw Cod/Salmon and `axolotl_food` = Bucket of Tropical Fish. Do not substitute a loose fish for the Axolotl breeding item. Camel content is present in 26.1.2, not a proposed future-version addition.

- [Mojang: Camel](https://www.minecraft.net/en-us/article/camel) and [Java 1.20 release notes](https://feedback.minecraft.net/hc/en-us/articles/16499677456781-Minecraft-Java-Edition-1-20-Trails-Tales) support two riders, saddles and Cactus feeding.
- [Mojang: Wolf](https://www.minecraft.net/en-us/article/wolf) supports Bone taming; the companion remains a Wolf, not a separate vanilla Dog species.
- [Mojang: Cat](https://www.minecraft.net/pl-pl/article/cat) supports the slow raw-fish approach and sitting companion behaviour; Ocelots are separate animals.
- [Mojang: Meet the Horse](https://www.minecraft.net/en-us/article/meet-horse) supports repeated empty-hand mounting for taming. The card describes ordinary Horses, not undead variants.
- [Mojang: Bucket of Axolotl](https://www.minecraft.net/pt-br/article/taking-inventory--bucket-axolotl) supports water-bucket transport and aquatic care.

Repeat the key conditional-drop checks with `pwsh ./tools/verify-creature-loot.ps1 -MinecraftJar <path-to-26.1.2-client.jar>`. Vanilla data resources are not exposed on the unit-test runtime classpath, so this explicit archive check is separate from the content tests and does not copy game assets into the repository.

- [Mojang: Cow](https://www.minecraft.net/fr-fr/article/cow) — renewable milk and everyday Cow context.
- [Mojang: Music Disc](https://www.minecraft.net/en-us/article/taking-inventory--music-disc) — Skeleton-killed Creeper disc example; use the target table for the precise current attacker tag.
- [Mojang: Blaze](https://www.minecraft.net/de-de/article/blaze) — Fortress and fire-projectile context. Target Java data takes precedence over older articles and other editions.

Detailed course safety lessons remain the practical instructions; reference cards point back to their existing translated titles. No new encounter is required to tick a reading card. Physical encounter playtesting is not claimed by the data checks.
