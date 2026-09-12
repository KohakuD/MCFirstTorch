# Curriculum coverage audit — release hold

Opened 2026-09-13 after the owner identified missing Minecraft features. This is an initial gap inventory, not a completed release-note audit. Automated tests validate existing definitions; they do not establish feature coverage. The previous comparison counts (7/13/6) describe the implemented seed only.

## Confirmed course gaps to address

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

## Systematic gameplay inventory — 2026-09-13

The feature lists and relevant gameplay-change sections were compared with the actual course descriptions, not only quest titles. This inventory covers the major gameplay feature groups; it is not a claim that every bug fix or technical release-note entry requires a lesson. Detailed mechanics and target-native assets must still be checked when authoring each block.

Status: **taught** means an actionable explanation exists; **partial** means a mention or incomplete explanation; **missing** means no relevant instruction was found; **accepted** means the owner tested the implemented block. Related additions are grouped so the learner is not forced through one card per decorative variant.

| Version | Feature group | Coverage and evidence | Next action |
| --- | --- | --- | --- |
| 1.21 | Crafter and automatic crafting | Missing | Add safe small Redstone crafting example in all targets |
| 1.21 | Trial Spawner, Vault and keys | Partial: Trial Chambers card `0BF75245596E6948` gives retreat advice and mentions keys | Explain activation, rewards and personal Vault access |
| 1.21 | Ominous Bottle, raid/trial effects, Ominous Vault | Partial: `5AFE972108C13B48` warns against drinking near a Village | Explain intentional activation, consequences and safe alternatives |
| 1.21 | Heavy Core, Mace and its enchantments | Partial: Breeze card only names materials | Add acquisition, crafting and safe combat explanation |
| 1.21 | Wind Charges and Breeze Rods | Partial: Breeze reference, no operational lesson | Explain use and fall/knockback risk |
| 1.21 | New potion effects | Missing dedicated Weaving/Oozing/Infestation/Wind Charging guidance | Review brewing and hazardous effect interactions |
| 1.21 | Breeze and Bogged | Existing creature safety references | Retain; link expanded Trial lessons |
| 1.21 | Tuff/Copper block additions and collectibles | No systematic coverage | Group practical block uses; collections remain optional |
| 1.21.2 | Bundle | Partial: reward in `6D91380C6EA4B2F5`, one sentence on storage | Teach recipe, capacity and inserting/selecting/removing contents |
| 1.21.4 | Creaking/Heart/Pale Garden | Creaking safety taught in `1CA0B0C0D0E00003`; biome and Heart acquisition partial | Expand safe exploration and Heart mechanics |
| 1.21.4 | Resin, Eyeblossoms and Pale vegetation | Resin only mentioned; flower handling missing | Add resource/plant reference, including risks |
| 1.21.5 | Warm/cold farm animals and coloured eggs | Missing | Extend animal recognition and breeding context |
| 1.21.5 | Firefly Bush, Wildflowers, Leaf Litter, Bush, dry grasses, Cactus Flower | Missing | Group gathering, placement and useful differences |
| 1.21.5 | Cheaper Lodestone | Taught in existing target recipe lesson | Keep; date revision to 1.21.5 |
| 1.21.6 | Dried Ghast → Ghastling → Happy Ghast, Harness | Missing dedicated instruction | Add hydration, care, mounting and safe landing |
| 1.21.6 | Saddle recipe and removal | Horse/Camel riding taught; obtaining/removing Saddle incomplete | Add recipe and safe equipment removal |
| 1.21.6 | Lead recipe, connected transport | No actual recipe or leash-system instruction | Explain transport and break risks |
| 1.21.6 | Locator Bar | Missing | Explain player direction, limits and privacy controls |
| 1.21.9 | Copper Chest/Golem sorting and oxidation/statues | Chest warnings only; sorting missing | Add input/output example, maintenance and failure cases |
| 1.21.9 | Shelf | Missing; Bookshelf is a different block | Explain storage/loadout interaction |
| 1.21.9 | Copper equipment | Armour already taught; tool coverage partial | Review tool suitability, mining tiers and current explanations |
| 1.21.9 | Copper decorations | Not systematically taught | Group lighting/oxidation uses; avoid repeating every colour |
| 1.21.11 | Nautilus, armour and Breath effect | Missing | Explain care, riding, air limits and armour |
| 1.21.11 | Zombie Nautilus, Zombie Horse, Camel Husk, Parched | Missing | Add recognition, rider threats and safe distance |
| 1.21.11 | Spear and Lunge | Missing | Explain attacks and controlled practice |
| 1.21.11 | Netherite Horse Armour and ridden water movement | Missing update guidance | Extend existing mount care; preserve Skeleton Horse exception |
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
