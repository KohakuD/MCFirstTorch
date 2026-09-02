# Roadmap

The active milestone is **0.1.0 — First Safe Night**. Scope stays intentionally small until the complete path has passed an in-game beginner test.

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
- [ ] Confirm all chapter, quest, and task titles in a fresh 0.1.2 profile

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

No Nether content is required for the first 48-hour playable build.
