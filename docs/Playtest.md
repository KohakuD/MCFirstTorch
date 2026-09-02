# Playtest log

## 0.1.0

### 2026-09-02 — CurseForge 0.1.1 smoke test

- Minecraft reached the title screen and created a new survival world.
- FTB Quests opened and displayed the complete dependency path.
- German quest descriptions and automatic item task labels loaded.
- The chapter appeared as `Unbenannt`, and objects whose IDs started with `8` through `F` fell back to generic titles such as `Haken`.
- Root cause: those IDs exceed the positive signed Java `long` range used by FTB Quests. Version 0.1.2 migrates them and adds a validator regression check.

Status: startup and world creation passed; 0.1.2 translation retest pending.

### 2026-09-02 — In-place update to 0.1.2

- Updated the existing CurseForge profile with `tools/update-instance.ps1`.
- Backed up the previous quest definitions before replacement.
- Verified all copied quest files by SHA-256.
- Confirmed the updater did not target the existing world, `options.txt`, screenshots, or resource packs.
- In-game screenshots confirm that the chapter, quest, and task titles now resolve correctly.

### 2026-09-02 — Beginner feedback for 0.1.3

- The overall quest path is understandable, but the first interaction and crafting steps still required spoken help.
- Added an explicit controls lesson and illustrated instructions for the default right hand, left-click breaking, automatic pickup, log placement in the 2 × 2 grid, and moving the result into the inventory.
- A task labelled for eight Cobblestone completed with one item. The root cause is an item-stack `count` value that FTB Quests ignores for task completion; 0.1.3 moves quantities to the task-level field for Cobblestone and Torches and adds regression validation.
- In-game image rendering and the corrected 1–7/8 Cobblestone threshold remain to be tested.

### 2026-09-02 — Image loading diagnosis for 0.1.4

- Both guide images rendered as the pink-and-black missing-texture pattern.
- `latest.log` confirmed that Minecraft removed `file/first_torch_guides` as incompatible before resource loading.
- The installed client's `version.json` reports resource format 84.0. Modern packs require `min_format` and `max_format` version pairs; the guide pack incorrectly used only the legacy `pack_format` field.
- Updated the metadata to `[84, 0]`, added a regression check, and expanded the illustrated sequence through Crafting Table placement, Wooden Pickaxe, Furnace, and Charcoal.

Record each test with date, Minecraft/NeoForge versions, language, fresh or existing world, confusing moments, technical errors, and the resulting change.
