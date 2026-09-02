# Roadmap

The active milestone is **0.1.x — First Safe Night**. The current development build is **0.1.7**. Scope stays intentionally small until the complete path has passed an in-game beginner test.

## 0.1.0 — First Safe Night

- [x] Independent repository and reproducible pack manifest
- [x] FTB Quests JSON5 foundation
- [x] English and German quest text
- [x] Guided path: orientation, wood, crafting table, first pickaxe, stone, furnace, torches, shelter, bed, first-night check
- [x] Automated metadata and translation validation
- [ ] Fresh-profile startup test on Minecraft 26.1.2
- [ ] Full quest completion test in English
- [ ] German text and layout review in game
- [ ] Beginner playtest and wording adjustments
- [ ] First public release archive

### 0.1.1 startup fix

- [x] Remove unused KubeJS integration after its Better Advanced Tooltips dependency crashed during startup
- [x] Rebuild the minimal pack with only the required FTB quest stack
- [ ] Confirm a clean CurseForge profile reaches the Minecraft title screen

### 0.1.2 quest ID fix

- [x] Confirm a clean CurseForge profile reaches the title screen and creates a world
- [x] Diagnose unnamed chapters and generic checkmark titles from screenshots
- [x] Move IDs outside the signed Java `long` range into FTB Quests' supported range
- [x] Reject out-of-range IDs during validation
- [x] Add a backed-up in-place updater for development profiles
- [x] Confirm chapter, quest, and task titles in the updated 0.1.2 profile

### 0.1.3 beginner controls and reliable quantities

- [x] Explain right-hand default, attack, use, breaking, pickup, and inventory movement explicitly
- [x] Add illustrated guides for breaking a log and crafting planks
- [x] Require eight Cobblestone and eight Torches with FTB Quests' task-level count field
- [x] Reject ignored item-stack quantities during validation
- [x] Extend the safe updater to install and enable only the pack-owned guide images
- [ ] Confirm both illustrations render in the existing profile
- [ ] Confirm the Cobblestone task stays incomplete at 1–7 and completes at 8

### 0.1.4 illustrated crafting sequence

- [x] Fix the guide resource-pack metadata for Minecraft 26.1.2's version-pair format
- [x] Add illustrations for placing the Crafting Table, crafting a Wooden Pickaxe, crafting a Furnace, and making Charcoal
- [x] Show the stick recipe in the Crafting Table's 3 × 3 grid
- [x] Explain stone collection explicitly as the same interaction loop as collecting wood, with the pickaxe as the key difference
- [ ] Confirm all six illustrations render in the existing profile

### 0.1.5 flexible beginner objectives

- [x] Accept four planks of any vanilla wood type, including mixed stacks
- [x] Pin FTB Filter System and FTB XMod Compat for item-tag objectives
- [x] Remove the premature instruction to bring food before food has been taught
- [x] Document why native CurseForge pack-version display requires a published CurseForge project
- [ ] Confirm the filter modules reach the title screen in the existing profile
- [ ] Confirm the plank task stays incomplete at 1–3 and completes at 4 with mixed wood types

### 0.1.6 gentle rewards

- [x] Explain how to claim rewards before the first reward appears
- [x] Give two Apples after the controls lesson and two after making Torches
- [x] Give three Bread as a bridge into the food chapter
- [x] Give one Lantern as the First Torch completion trophy
- [x] Validate exact reward items, quantities, and stable reward IDs
- [ ] Confirm all four rewards display and can be claimed exactly once in game

### 0.1.7 reward quantity fix

- [x] Move Apple and Bread quantities to FTB Quests' reward-level `count` field
- [x] Keep the displayed item stack at one, as required by the 26.1 item reward format
- [x] Reject ignored quantities inside reward item stacks during validation
- [ ] Confirm a fresh claim grants 2 Apples, 2 Apples, 3 Bread, and 1 Lantern

## 0.2.0 — Becoming Independent

- [ ] Hunger, food sources, and safe cooking
- [ ] Health, armour, shields, and avoiding unnecessary fights
- [ ] Mining safely: stairs, light, water, gravel, and getting home
- [ ] Iron tools, bucket, shield, and basic armour
- [ ] Coordinates, landmarks, and a simple home routine
- [ ] Optional recap path for skills the learner already knows

## Later milestones

- `0.3.0`: farms, animals, storage, and sustainable resources
- `0.4.0`: caves, diamonds, enchanting, and preparation
- `0.5.0`: Nether preparation and first Nether trip
- CurseForge publication: native pack-version display and launcher-managed updates

No Nether content is required for the first 48-hour playable build.
