# Roadmap

The completed milestone is **0.2.0 — Becoming Independent**. The current release build is **0.2.0**. Its lessons were reviewed iteratively in game before closure.

## 0.1.0 — First Safe Night

- [x] Independent repository and reproducible pack manifest
- [x] FTB Quests JSON5 foundation
- [x] English and German quest text
- [x] Guided path: orientation, wood, crafting table, first pickaxe, stone, furnace, chest storage, torches, shelter, bed, first-night check
- [x] Automated metadata and translation validation
- [x] Fresh-profile startup test on Minecraft 26.1.2
- [x] Full quest completion test in English
- [x] German text and layout review in game
- [x] Beginner playtest and wording adjustments
- [ ] First public release archive

### 0.1.1 startup fix

- [x] Remove unused KubeJS integration after its Better Advanced Tooltips dependency crashed during startup
- [x] Rebuild the minimal pack with only the required FTB quest stack
- [x] Confirm a clean CurseForge profile reaches the Minecraft title screen

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
- [x] Confirm both illustrations render in the existing profile
- [x] Confirm the Cobblestone task stays incomplete at 1–7 and completes at 8

### 0.1.4 illustrated crafting sequence

- [x] Fix the guide resource-pack metadata for Minecraft 26.1.2's version-pair format
- [x] Add illustrations for placing the Crafting Table, crafting a Wooden Pickaxe, crafting a Furnace, and making Charcoal
- [x] Show the stick recipe in the Crafting Table's 3 × 3 grid
- [x] Explain stone collection explicitly as the same interaction loop as collecting wood, with the pickaxe as the key difference
- [x] Confirm all six illustrations render in the existing profile

### 0.1.5 flexible beginner objectives

- [x] Accept four planks of any vanilla wood type, including mixed stacks
- [x] Pin FTB Filter System and FTB XMod Compat for item-tag objectives
- [x] Remove the premature instruction to bring food before food has been taught
- [x] Document why native CurseForge pack-version display requires a published CurseForge project
- [x] Confirm the filter modules reach the title screen in the existing profile
- [x] Confirm the plank task stays incomplete at 1–3 and completes at 4 with mixed wood types

### 0.1.6 gentle rewards

- [x] Explain how to claim rewards before the first reward appears
- [x] Give two Apples after the controls lesson and two after making Torches
- [x] Give three Bread as a bridge into the food chapter
- [x] Give one Lantern as the First Torch completion trophy
- [x] Validate exact reward items, quantities, and stable reward IDs
- [x] Confirm all four rewards display and can be claimed exactly once in game

### 0.1.7 reward quantity fix

- [x] Move Apple and Bread quantities to FTB Quests' reward-level `count` field
- [x] Keep the displayed item stack at one, as required by the 26.1 item reward format
- [x] Reject ignored quantities inside reward item stacks during validation
- [x] Confirm a fresh claim grants 2 Apples, 2 Apples, 3 Bread, and 1 Lantern

## 0.2.0 — Becoming Independent

- [x] Hunger, food sources, and safe cooking
- [x] Health, armour, shields, and avoiding unnecessary fights
- [x] Mining safely: stairs, light, water, gravel, and getting home
- [x] Iron tools, bucket, shield, and basic armour
- [x] Coordinates, landmarks, and a simple home routine
- [x] Let already-known skills complete without punishment through automatic inventory checks and non-gating practice quests

### Food foundation

- [x] Explain the ten-part Hunger bar, eating control, and sprint threshold without assuming knowledge of game modes
- [x] Detect the first consumed item automatically through the vanilla Husbandry advancement
- [x] Keep the eating lesson optional so it does not gate food collection
- [x] Accept several locally available raw food sources instead of requiring one biome-specific item
- [x] Illustrate the Furnace flow from raw Beef to cooked Steak
- [x] Explain the different burn durations of Sticks, wood, Coal, and Charcoal in the first Furnace lesson
- [x] Explain how high Hunger enables natural health regeneration
- [x] Warn explicitly about Rotten Flesh, Raw Chicken, Spider Eyes, Poisonous Potatoes, and Pufferfish
- [x] Require four cooked foods, allowing mixed supported types
- [x] Add one Apple, one Charcoal, and one Cookie as non-skipping milestone rewards
- [x] Confirm the new chapter unlocks after the safe-morning quest
- [x] Confirm both smart filters accept every listed ingredient and reject unrelated food
- [x] Confirm both new guide images render correctly
- [x] Complete the food path once in German and inspect it once in English

### Protection foundation

- [x] Explain damage, safe retreat, and recovery before teaching combat equipment
- [x] Teach the Stone Pickaxe and warn that a Wooden Pickaxe cannot collect Raw Iron
- [x] Guide the learner from exposed Iron Ore through smelting the first Iron Ingot
- [x] Show Iron Ore with the live in-game item model so active resource packs are respected
- [x] Illustrate the exact Shield recipe and its offhand placement with the correct left-mouse control
- [x] Illustrate all four Armour recipes in one compact, consistent 2 × 2 overview
- [x] Add a parallel Copper branch that unlocks Armour crafting after either Copper or Iron
- [x] Keep the Shield lesson exclusive to the Iron branch
- [x] Explain armour slots without requiring a full set of Iron Armour
- [x] Teach raising and facing with a Shield, including important limits
- [x] Make avoiding unnecessary fights an explicit skill
- [x] Return one Iron Ingot as a small non-skipping milestone reward
- [x] Introduce small XP-point rewards in the welcome and armour-crafting lessons
- [x] Confirm the Stone Pickaxe, Raw Iron, Iron Ingot, and Shield item tasks in game
- [x] Confirm both new guide images render correctly
- [x] Complete the protection path once in German and inspect it once in English

### Safe mining foundation

- [x] Require two suitable Pickaxes, 16 Torches, and four cooked foods before going deeper
- [x] Illustrate a walkable staircase and explicitly forbid digging straight down
- [x] Teach a consistent right-going-in, left-going-home Torch rule
- [x] Explain falling Gravel and Sand as suffocation hazards
- [x] Teach retreating and blocking the opening when Water or Lava appears
- [x] Make returning before supplies run out part of the mining routine
- [x] Add 10 XP points as a non-skipping safe-return reward
- [x] Teach Chest crafting and basic item storage before the first longer outing
- [x] Wrap the chapter into three thematic rows so the full path remains readable on smaller displays
- [x] Confirm mixed suitable Pickaxes satisfy the two-item preparation task
- [x] Confirm both mining guide images render correctly
- [x] Complete the safe-mining path once in German and inspect it once in English

### Finding home foundation

- [x] Keep the topic in a short dedicated chapter instead of extending the already dense mining map
- [x] Test a visible home landmark from a safe distance
- [x] Explain X, Y, and Z, including minus signs and laptop function keys
- [x] Record the real home coordinates outside the world
- [x] Practise a short daylight return using the landmark first and coordinates as a backup
- [x] Explain that coordinates point toward the goal but do not guarantee a safe route
- [x] Add 5 XP points as a non-skipping route-test reward
- [x] Give one Compass after the tested route without replacing landmarks or coordinates
- [x] Explain that a normal Compass points to the world spawn, not to a Bed or shelter
- [x] Teach the Stonecutter and use eight Stone to make Chiseled Stone Bricks efficiently
- [x] Craft a Lodestone with the current eight-brick and one-Iron recipe
- [x] Detect binding the rewarded Compass to a Lodestone automatically, then test it from a safe distance
- [x] Confirm the chapter unlocks after the safe-return quest
- [x] Check the F3 wording against the Minecraft 26.1.2 debug screen in German and English
- [x] Complete the practice route once in survival mode
- [x] Confirm the Stonecutter, eight-brick, Lodestone, and binding steps in survival mode
- [x] Confirm both new recipe images render correctly

### Iron essentials foundation

- [x] Keep Iron Pickaxe and Bucket training in a short dedicated chapter
- [x] Unlock the chapter from the first Iron Ingot and add a diamond-shaped in-map link with navigation guidance
- [x] Require six Iron Ingots without consuming them before the two recipes branch
- [x] Refer back to the shared Pickaxe shape without repeating already-learned slot instructions
- [x] Explain that Stone and Copper Pickaxes cannot collect Iron-tier ores such as Diamond Ore
- [x] Teach the three-Ingot Bucket recipe
- [x] Require a Water Bucket and practise placing and retrieving a source block safely
- [x] Rejoin both branches in a short safety recap with a 5 XP reward
- [x] Confirm the six-Ingot task remains incomplete at 1–5 and completes at 6
- [x] Confirm the Iron Pickaxe, Bucket, and Water Bucket tasks in game
- [x] Confirm the separate Iron Pickaxe and Bucket recipe images render in their matching quests
- [x] Practise placing and retrieving Water once in survival mode

### Bed onboarding refinement

- [x] Grant three White Wool after the safe-shelter lesson instead of giving a finished Bed
- [x] Teach the White Bed recipe, placement, sleeping, and the respawn point explicitly
- [x] Keep the learner responsible for crafting the Wooden Planks and Bed
- [x] Confirm the three-Wool reward quantity and Bed guide image in game
- [x] Sleep once and confirm the Bed becomes the player's respawn point

## Later milestones

- `0.3.0`: farms, animals, storage, and sustainable resources
- `0.4.0`: caves, diamonds, enchanting, and preparation
- `0.5.0`: Nether preparation and first Nether trip
- CurseForge publication: native pack-version display and launcher-managed updates

No Nether content is required for the first 48-hour playable build.
