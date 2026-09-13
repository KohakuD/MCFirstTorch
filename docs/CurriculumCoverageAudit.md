# Curriculum coverage audit — release hold

Opened 2026-09-13 after the owner identified missing Minecraft features. This is an initial gap inventory, not a completed release-note audit. Automated tests validate existing definitions; they do not establish feature coverage. The previous comparison counts (7/13/6) describe the implemented seed only.

## Original gap report (historical)

| Minecraft introduction | Topics raised by owner | Current English course inspection | Required targets |
| --- | --- | --- | --- |
| 1.21 | Crafter, Heavy Core, Mace, Ominous Bottle | Crafter absent; other topics only mentioned in existing explanations, not dedicated coverage | 1.21.1, 26.1.2, 26.2 |
| 1.21.6 | Dried Ghast, Harness, Ghastling, Happy Ghast | No dedicated lessons; Happy Ghast only mentioned in ordinary Ghast context | 26.1.2, 26.2 |
| 1.21.9 | Copper Chest, Copper Golem, Shelf | Chest caveats exist; no dedicated sorting system or Shelf lesson | 26.1.2, 26.2 |
| 1.21.11 | Nautilus armour, Spear, Nautilus, Parched, Breath of the Nautilus | No dedicated coverage found | 26.1.2, 26.2 |
| 26.1 | Golden Dandelion | No coverage found | 26.1.2, 26.2 |

Plain Bookshelf explanations are not coverage of the Shelf block. A mention of a Heavy Core in the Breeze card does not teach obtaining or using it. Confirm exact German item/entity names from each target's native language resources before authoring.

## Sources for the full audit

- [1.21.6 official notes](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-6)
- [1.21.9 official notes](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-9)
- [1.21.11 official notes](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-11)
- [26.1 official notes](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1)

Also audit the intervening 1.21.2, 1.21.4 and 1.21.5 updates and the full 1.21/26.2 notes. The owner's list is the minimum, not an exhaustive checklist. Record each learner-relevant feature as taught, only mentioned, missing, or deliberately excluded with a reason. Separate native target compatibility from actual curriculum completeness.

## Version semantics

Minecraft introduction, availability in a target, and first publication as a First Torch lesson are distinct facts. A newly authored Crafter lesson belongs in the 1.21.1 course, but must not be labelled a Minecraft innovation since 1.21.1. Features introduced between 1.21.1 and 26.1.2 belong in the cumulative comparison for that interval. Golden Dandelion belongs to 26.1/26.1.2, not to the 26.2-only changes.

Keep the selectable catalogue limited to implemented First Torch editions; recording an intermediate Minecraft introduction does not imply publishing a separate mod target. Design how returning learners discover newly added teaching of older mechanics without misrepresenting it as a Minecraft update.

## Release gate

Pause final release versioning/uploads for 26.1.2 and 26.2. Retain the accepted UI tests as evidence for existing behavior. Complete the coverage audit, implement bilingual lessons with stable IDs and native target assets, update semantic history/coverage tests, then repeat tests for affected paths. Existing 1.21.1 uploads are unchanged; any additional 1.21 material requires a separately versioned follow-up release.

## First implementation batch — 26.1 everyday mechanics

Four bilingual optional reading cards now cover Golden Dandelion growth control (including reversal and excluded babies), craftable Name Tags, direct Stonecutter options and Copper trumpet sounds. These use native item icons, no synthetic lesson artwork, no item rewards and the existing reference unlock. Golden Dandelion and Name Tag recipes were checked against both cached native 26.1.2 and 26.2 recipe files. The first uses eight Gold Nuggets surrounding a Dandelion; the second uses diagonal Paper and a metal Nugget.

Chapter `7A26100000000001`, quests `2A26100000000001`–`2A26100000000004`, tasks `3A26100000000001`–`3A26100000000004`. Available in 26.1.2/26.2 only. Explicit intermediate Minecraft ordering now permits a 26.1 history event without offering unimplemented First Torch versions in the baseline menu. Tests prove these cards appear after 1.21.1/1.21.11 but not after 26.1.2. The provisional changed-card counts are now 11 (1.21.1 → 26.1.2), 17 (1.21.1 → 26.2), and six (26.1.2 → 26.2).

The owner explicitly accepted all four cards as working and correct on 2026-09-13. The wider audit and the owner's other missing topics remain open. Initial additional search also found no Locator Bar or leash guidance, and no named Spear/Lunge, Zombie Horse or Camel Husk coverage; review against full release notes before authoring. Ordinary Saddle or Bundle mentions must be checked for actual instruction rather than counted as complete coverage.

## Current gameplay inventory — reconciled 2026-09-13

The feature lists and relevant gameplay-change sections were compared with the actual course descriptions, not only quest titles. This inventory covers the major gameplay feature groups; it is not a claim that every bug fix or technical release-note entry requires a lesson. Detailed mechanics and target-native assets must still be checked when authoring each block.

Status: **taught** means an actionable explanation exists; **partial** means a mention or incomplete explanation; **missing** means no relevant instruction was found; **accepted** means the owner tested the implemented block. Related additions are grouped so the learner is not forced through one card per decorative variant.

| Version | Feature group | Coverage and evidence | Next action |
| --- | --- | --- | --- |
| 1.21 | Crafter and automatic crafting | Taught: two optional Redstone cards, `2A121...01`–`02`, all targets | Retain stable IDs and Minecraft 1.21 history |
| 1.21 | Trial Spawner, Vault and keys | Taught: `2A121...03`–`04`, all targets | Retain personal reward and safe retreat explanations |
| 1.21 | Ominous Bottle, raid/trial effects, Ominous Vault | Taught: `2A121...05`, all targets | Retain intentional activation and consequences |
| 1.21 | Heavy Core, Mace and its enchantments | Taught: `2A121...06`–`07`, all targets | Retain optional combat and acquisition explanations |
| 1.21 | Wind Charges and Breeze Rods | Taught: `2A121...08`, all targets | Retain fall/knockback guidance |
| 1.21 | New potion effects | Taught: four independent cards `2A121...09`–`0C`, all targets | Retain brewing links and reading-only completion |
| 1.21 | Breeze and Bogged | Existing creature safety references | Retain; link expanded Trial lessons |
| 1.21 | Tuff/Copper block additions and collectibles | No systematic coverage | Group practical block uses; collections remain optional |
| 1.21.2 | Bundle | Implemented and owner-accepted: `2A212...01`–`02`, capacity, controls and S-layout | Retain modern-target boundary |
| 1.21.4 | Creaking/Heart/Pale Garden | Implemented and owner-accepted: five-card Pale Garden chapter | Retain separate Trial Chamber category |
| 1.21.4 | Resin, Eyeblossoms and Pale vegetation | Implemented and owner-accepted: `1CA0B0C0D0E00004`–`07` | Retain native recipes and plant risks |
| 1.21.5 | Warm/cold farm animals and coloured eggs | Implemented and owner-accepted: revised Cow/Pig/Chicken/Sheep cards | Retain revision reasons and original IDs |
| 1.21.5 | Firefly Bush, Wildflowers, Leaf Litter, Bush, dry grasses, Cactus Flower | Implemented and owner-accepted: four `2A215...` plant cards | Retain grouped practical uses |
| 1.21.5 | Cheaper Lodestone | Taught in existing target recipe lesson | Keep; date revision to 1.21.5 |
| 1.21.6 | Dried Ghast → Ghastling → Happy Ghast, Harness | Implemented and owner-accepted: `2A216...01`–`04` | Retain care, flight and dismount distinction |
| 1.21.6 | Saddle recipe and removal | Implemented and owner-accepted: `2A216...06` | Retain recipe and safe equipment handling |
| 1.21.6 | Lead recipe, connected transport | Implemented and owner-accepted: `2A216...05` | Retain transport and break risks |
| 1.21.6 | Locator Bar | Implemented and owner-accepted: `2A216...07` | Retain direction, limits and privacy explanation |
| 1.21.9 | Copper Chest/Golem sorting and oxidation/statues | Implemented and owner-accepted: `2A219...01`–`03` | Retain input/output example and maintenance |
| 1.21.9 | Shelf | Implemented and owner-accepted: `2A219...04`–`05` | Retain ordinary interaction and powered swaps |
| 1.21.9 | Copper equipment | Partial: armour recipes and Iron Pickaxe mining-tier contrast taught; Copper tool recipe/suitability not fully explained | Finish grouped tool guidance; do not duplicate armour recipes |
| 1.21.9 | Copper decorations | Not systematically taught | Group lighting/oxidation uses; avoid repeating every colour |
| 1.21.11 | Nautilus, armour and Breath effect | Implemented and owner-accepted: `2A2111...01`–`02` | Retain air consumption/refill distinction |
| 1.21.11 | Zombie Nautilus, Zombie Horse, Camel Husk, Parched | Implemented and owner-accepted: `2A2111...05`–`07` | Retain rider threats and riderless behaviour |
| 1.21.11 | Spear and Lunge | Implemented and owner-accepted: `2A2111...03`–`04` | Retain controls, reach and hunger limits |
| 1.21.11 | Netherite Horse Armour and ridden water movement | Implemented and owner-accepted: `2A2111...08` | Retain Skeleton Horse exception |
| 26.1 | Golden Dandelion, craftable Name Tags, Stonecutter shortcuts, trumpet | Four cards implemented and owner-accepted | Retain; already in comparison metadata |
| 26.1 | Baby-model and sound changes | Mostly visual/audio variation | Brief context where useful; no separate completion for every variant |
| 26.2 | Sulfur Caves/materials/hazards/Cube | Five cards implemented | Detailed mechanics coverage and new-card acceptance still open |
| 26.2 | Friends List | Guidance implemented | Review joining/privacy coverage separately from personal quest progress |

### Scope decisions

- No compulsory quests for menu panoramas, language additions, soundtrack changes, decorative variants alone or animation tweaks. Mention these in optional discovery references where useful.
- Graphics APIs, server management protocols, commands, resource/data-pack internals and experimental renderer settings remain developer documentation, outside this survival curriculum.
- New recipe/safety/interaction rules are not dismissed as cosmetic changes, even when the item already existed.
- Patch releases remain part of target compatibility review; a patch label alone is neither a new lesson nor proof that no mechanic changed.

### Implementation order

1. Complete the 1.21 foundation: Trial systems, Mace/Wind Charges, Crafter and potion effects in all three targets. These are course backfills, not post-1.21.1 Minecraft innovations.
2. Add Bundle and Pale Garden/plant coverage, then the 1.21.5 animal/plant changes.
3. Add the full 1.21.6 travel block, including the Saddle/Lead/Locator changes omitted from the owner's initial list.
4. Add Copper sorting/Shelf, then mounted combat and aquatic travel from 1.21.11.
5. Recheck 26.2 completeness and all revised instructions; only then resume final release preparation.

Official sources: [1.21](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21), [1.21.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-2), [1.21.4](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-4), [1.21.5](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-5), [1.21.6](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-6), [1.21.9](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-9), [1.21.11](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-11), [26.1](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1), [26.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-2).

### History correction

Existing Creaking and Pale Oak events now use 1.21.4; the Lodestone revision and route context use 1.21.5; Copper equipment/chest revisions and context use 1.21.9. Previously they were all attributed to the First Torch target 26.1.2. Boundary tests prevent that conflation. The available player selections and their cumulative results remain unchanged.

## Second implementation batch — 1.21 crafting and trials

Eight bilingual optional lessons now cover the Crafter recipe, a manual-button Stick example, Trial Spawners, personal Vault access and keys, deliberate ominous-event activation, Heavy Core/Mace crafting, Mace enchantment choices and Wind Charges. Chapters `7A12100000000001` (Trials) and `7A12100000000002` (Redstone: The Crafter), quests `2A12100000000001`–`2A12100000000008`, tasks `3A12100000000001`–`3A12100000000008`.

The block is included in all three targets, with unchanged IDs and no item rewards. The reference introduction unlocks the six Trials cards. The two Crafter lessons form an optional Redstone chapter: the first follows the final Redstone Basics lesson, and the second follows the first. The original course never depends on these new cards. The Trials entries link to the existing Trial Chamber introduction. It is a reading block with optional safe practice, not an automatic certification of crafting or combat.

Crafter, Mace and Wind Charge recipes were read from each target's original client JAR; the shared recipes agree. German terminology was checked in cached native language assets (including Boeenrute/Boe, Pruefungs-Spawner and the omen effect names; runtime strings use their proper Unicode spelling). Official 1.21 notes supplied spawner, Vault and combat behavior.

History records these as Minecraft 1.21 features. A shared test runs in all three modules to ensure they are present yet absent from the post-1.21.1 novelty view; existing prerequisites and rewards are preserved. This does not yet implement a separate 'newly authored First Torch lessons' view. The six reference cards appear under Trials / Pruefungen; the two Crafter lessons appear in the normal course under Redstone: The Crafter / Redstone: Der Werker. Quest and task IDs, completed progress and Minecraft introduction dates remain unchanged by the move.

Manual bilingual game acceptance remains open. The four potion-effect lessons and the other audit blocks remain unimplemented; this batch is not a declaration that all 1.21 content is complete. The published 1.21.1 Beta is unchanged; the locally rebuilt artifact is a development test state requiring a new release version before any follow-up publication.

## Third implementation batch — 1.21 potion effects

The Trials reference chapter now includes four independent reading cards: Weaving, Oozing, Infestation and Wind Charging. Quests `2A12100000000009`–`2A1210000000000C` and matching `3A121...` tasks are included in all three targets. They explain Awkward Potion ingredients, effect triggers, retreat decisions and Milk clearing useful effects too. No damage, death, brewing or drinking is required for completion. Reading links lead back to the existing Brewing chapter.

The official 1.21 status-effect notes and cached native PotionBrewing sources agree on Cobweb, Slime Block, Stone and Breeze Rod inputs. German potion/effect names were checked against cached native language assets. Existing lessons and progress IDs remain intact; comparison history dates the four additions to Minecraft 1.21, excluding them from changes since 1.21.1. Automated checks cover independent manual completion, translations, links and all target builds; in-game layout acceptance remains pending.

## Reference organisation — Trial Chamber and Pale Garden

The Trial Chamber chapter (`7A12100000000001`) now owns the existing Breeze and Bogged cards (`1CA0B0C0D0E00001`/`02`) alongside the ten trial-system, equipment and potion cards. Four compact rows group trials/keys, enemies, equipment and effects within a 6-by-6 coordinate span, replacing the single 18-unit vertical column. Existing quest/task IDs and prerequisites remain unchanged; reading links use the destination chapter's current title.

Chapter `70A7B8C9D0E1F203` is now Pale Garden, retaining the Creaking card in 26.1.2 and 26.2. The empty former mixed chapter is removed in 1.21.1, which has neither the biome nor Creaking. Heart, Resin, Eyeblossom and vegetation additions remain part of the planned Pale Garden remediation block; this reorganisation does not claim them as complete.

## Pale Garden expansion

Four independent bilingual readings extend the preserved Creaking card to a five-card compact grid: wood/moss, Creaking Heart, Resin and Eyeblossoms. Quest IDs `1CA0B0C0D0E00004`–`07` and task IDs `2CA0B0C0D0E00004`–`07` are present only in 26.1.2/26.2, with history attributed to Minecraft 1.21.4. All cards link back to Creaking safety and require reading only. Native target JAR recipes verified the Heart's vertical log/resin/log pattern, nine-clump storage block and clump smelting. The official 1.21.4 release notes supply the biome, Heart, moss and flower behaviour. Layout/reading acceptance remains open; the owner accepted the preceding category split and Trial Chamber grid.

## Bundle teaching

Two optional Storage lessons (`2A21200000000001`/`02`, tasks `3A21200000000001`/`02`) follow the existing return-home routine in 26.1.2/26.2. They teach the string-over-leather recipe, shared stack capacity and a safe two-material inventory exercise. Original storage rewards and prerequisite IDs remain intact; later course chapters do not depend on the extension. The chapter uses a compact three-column grid. These lessons are absent in 1.21.1 and dated to Minecraft 1.21.2 for cumulative comparison.

Recipe data was read from both target client JARs. Cached native BundleItem source verifies insertion by primary click with a carried item and extraction by secondary click with an empty cursor; instructions deliberately specify that the Bundle stays in its inventory slot. Existing reward possession cannot auto-complete the manual practice. Owner acceptance of the preceding Pale Garden block is recorded; Bundle controls/layout still need in-game acceptance.

## Spring to Life teaching

The existing Cow, Pig, Chicken and Sheep cards now explain the 1.21.5 variants, inherited appearances, coloured eggs and biome-dependent wool frequencies. Their IDs are preserved and their comparison events are revisions, with bilingual change summaries. The 1.21.1 text remains unchanged.

New reference chapter `7A21500000000001` groups four independent readings in a two-by-two grid: Wildflowers, Leaf Litter, Bush/Firefly Bush, and Dry Grass/Cactus Flower. Quests `2A21500000000001`–`04` and tasks `3A21500000000001`–`04` require reading only and link to shelter preparation. They are present in 26.1.2/26.2, dated to 1.21.5 and excluded from changes since 26.1.2. Official 1.21.5 release notes supplied the mechanics; native German language assets supplied plant names. In-game acceptance is pending. The preceding Bundle layout was accepted by the owner.

## Chase the Skies teaching

Chapter `7A21600000000001` adds seven bilingual travel references in a compact grid, with quests `2A21600000000001`–`07` and tasks `3A21600000000001`–`07`. Topics cover Dried Ghast acquisition, hydration/growth, Harness crafting/equipping, flight/landing, Lead crafting/transfers, Saddle crafting/removal and the multiplayer Locator Bar. Each card is an independent reading; no Nether collection, animal transport, dangerous dismount or multiplayer session is required. Links connect the stages and existing Nether/horse guidance.

Only 26.1.2/26.2 package the cards. History dates them to 1.21.6; they appear when comparing against 1.21.1 but not against 26.1.2. Both target-native recipe files were checked for Dried Ghast, white Harness, Lead and Saddle. Cached HappyGhast source confirms forward movement follows look pitch, jumping adds ascent, and the text distinguishes sneak dismount from descent. Official 1.21.6 notes supply the growth, equipment, leash and locator rules. In-game reading/layout/control acceptance remains pending. The separately requested reference-library unlock redesign remains planned and is not implemented by this batch.

## Copper sorting and Shelf teaching

Chapter `7A21900000000001` contains five bilingual independent reading cards (`2A21900000000001`–`05`, corresponding `3A219...` tasks): Copper Chest/Golem setup, a two-destination sorting example, oxidation/statue care, ordinary Shelf interaction and powered hotbar swaps. IDs and progress of previous lessons are preserved. Content exists in 26.1.2/26.2 and is dated to Minecraft 1.21.9; no post-26.1.2 innovation is implied. Original target recipe data and the official 1.21.9 release notes underpin the explanations. In-game acceptance remains pending. The owner accepted the preceding Ghast/transport block.

## Mounts of Mayhem teaching

Chapter `7A21110000000001` adds eight bilingual independent readings, quests `2A21110000000001`–`08` and tasks `3A21110000000001`–`08`: Nautilus care, air/armor, Spear controls, Lunge, Zombie Nautilus, desert riders, Zombie Horse and equipment/water crossings. A compact grid preserves readable icons. Reading is sufficient; diving, combat and rare upgrades are optional. Relevant cards link back to Nautilus/Spear preparation or existing Horse care.

The cards are packaged only in 26.1.2/26.2 and dated to Minecraft 1.21.11. Both target-native recipes were checked for Iron Spear and Netherite Nautilus/Horse Armor upgrades. German names were checked in native language assets, including Ausfallschritt, Vertrockneter, Zombiedromedar and Atem des Nautilus. Official 1.21.11 notes supplied the behavioural rules. In-game acceptance remains pending. The owner accepted Copper/Shelf content and the alphabetical reference ordering; sorting applies to all three supported targets.

## Current remaining work after mount acceptance

The owner accepted the eight Mounts of Mayhem readings on 2026-09-13. Earlier implementation sections above retain their historical pending-test statements; the reconciled inventory and this section describe the current state. Acceptance is for the reported content checks, not an unreported fresh-profile matrix across every target.

1. Finish practical Copper tool guidance and grouped Tuff/Copper building uses, including lighting and oxidation where meaningful. Existing armour recipes and Iron Pickaxe tier guidance are already taught; avoid duplicate lessons.
2. Review the five 26.2 Sulfur cards against the complete gameplay mechanics and review Friends List joining/privacy instructions. Obtain the separate Sulfur reading/link/trophy acceptance.
3. Complete the separate 26.1.2 comparison check and remaining fresh welcome/search/count/reward checks in the release checklist.
4. Finalize release versions and changelogs only after those gates. The reference-library progression redesign remains a separately planned feature, not implemented behaviour.

Verification baseline `1767dba`: 430 root, 50 core, 191 backport and 432 Minecraft 26.2 tests (1,103 total), no failures/errors/skips. All three native JAR checks passed. The current 1.21.1 → 26.2 comparison test expects 51 changed cards; 26.1.2 → 26.2 expects six. Earlier counts in implementation notes are historical snapshots.
