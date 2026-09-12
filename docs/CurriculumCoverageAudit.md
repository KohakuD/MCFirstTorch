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

This batch is implemented, not manually accepted. The wider audit and the owner's other missing topics remain open. Initial additional search also found no Locator Bar or leash guidance, and no named Spear/Lunge, Zombie Horse or Camel Husk coverage; review against full release notes before authoring. Ordinary Saddle or Bundle mentions must be checked for actual instruction rather than counted as complete coverage.
