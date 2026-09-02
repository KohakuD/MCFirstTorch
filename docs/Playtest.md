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
- In-game chapter and title verification remains pending.

Record each test with date, Minecraft/NeoForge versions, language, fresh or existing world, confusing moments, technical errors, and the resulting change.
