# Development

## Baseline

First Torch targets Minecraft 26.1.2 on NeoForge 26.1.2.84. Dependency versions and CurseForge file IDs are pinned in `manifest.json`.

The current pack deliberately uses the FTB quest stack plus FTB Filter System and FTB XMod Compat for flexible vanilla item objectives. KubeJS was removed after its required Better Advanced Tooltips dependency caused a startup Mixin failure. Add scripting only when the curriculum requires it and the full dependency chain has passed a clean-profile startup test.

FTB Quests 26.1+ stores quest definitions and translations as JSON5. Legacy `.snbt` examples are not valid guidance for this project.

## Repository layout

- `manifest.json`: CurseForge pack metadata and dependency pins
- `overrides/config/ftbquests/quests/`: version-controlled quest book
- `docs/`: curriculum, development rules, and roadmap
- `tools/`: local and CI validation/build scripts
- `build/`: generated import archives; ignored by Git

Player progress belongs to a world or server instance and must never be copied into this repository.

## Editing quests

Prefer FTB Quests' in-game editor when discovering a new property or task type. Save the book, copy only the resulting definitions back into `overrides/config/ftbquests/quests/`, and review the diff. Hand-editing is appropriate for translation text, coordinates, dependencies, and already-understood structures.

Object IDs are stable public identifiers. Use exactly 16 uppercase hexadecimal characters with a first character from `0` through `7`; FTB Quests parses them as positive signed Java `long` values. Do not change existing IDs merely to reorder content.

Each player-facing change must update both:

- `lang/en_us/`
- `lang/de_de/`

Run the validator to catch missing keys.

## Guide artwork

When a guide image depicts a Minecraft item, block, entity, or interface, render it from the exact textures and models shipped with the targeted Minecraft version. Do not approximate or generatively redraw game content. Constructed frames, arrows, measurements, and other explanatory overlays are allowed around those original assets.

Crafting guides show placeable block ingredients and results with the same straight, consistently aligned three-dimensional Minecraft block-model view as the established recipe images. A flat texture is appropriate only when the image is a genuine top-down plan, such as the layout of an Enchanting Table and its Bookshelves.

The island-crossing and End City guides are unchanged Minecraft 26.1.2 screenshots supplied by the project owner:

- `end_island_crossing.png`: `2026-09-06_01.45.54.png`
- `end_city_search.png`: `2026-09-06_01.46.11.png`

Both originals are 3440 × 1369 pixels and display at 300 × 119 in both languages. The former schematic generator was removed to prevent it from overwriting the screenshots. The bridge caption distinguishes the pictured Cobblestone from the exercise's End Stone and reminds learners to add the required safety features.

Additional unchanged Minecraft 26.1.2 screenshots supplied by the project owner provide authentic End context:

- `end_battlefield.png`: `2026-09-06_00.27.29.png`
- `end_exit_portal.png`: `2026-09-06_00.32.15.png`
- `chorus_harvest.png`: `2026-09-06_00.33.39.png`
- `end_gateway_access.png`: `2026-09-06_01.49.58.png`

These 3440 × 1369 originals also display at 300 × 119. Their generators no longer overwrite the screenshot-backed files. The exact instructional diagrams remain in place for Crystal removal, Dragon Egg retrieval, Chorus Fruit safety, and securing an outer-island arrival because those lessons depend on a sequence or safety layout that a single screenshot does not fully show.

`shulker_levitation.png` uses the exact Minecraft 26.1.2 Shulker, Shulker Bullet, Levitation, Milk Bucket, and Water Bucket textures. Its closed and open Shulker views follow the dimensions and UV positions from the target version's `ShulkerModel`, while the projectile face follows `ShulkerBulletModel`. The corresponding target-version projectile code applies four base damage points and 200 ticks of Levitation only after a successful hit, which grounds the lesson's ten-second recovery sequence.

`shulker_box_recipe.png` follows the target recipe data and uses the exact Shulker Shell texture. Its result joins the exact closed lid and base faces from the 64 by 64 Shulker texture according to the target `ShulkerBoxModel` dimensions. The ordinary Chest ingredient reuses the exact special-model item render from the established Chest recipe guide.

`elytra_water_course.png` combines a genuine top-down plan and side profile made from exact Cobblestone, Ladder, Grass, Water, and Elytra textures. The raw animated Water frame receives the standard Plains water tint that Minecraft applies at render time. `firework_rocket_recipe.png` follows the target shapeless simple-Rocket recipe and uses the exact Paper, Gunpowder, and Firework Rocket textures.

## Reward design

Beginner rewards are fixed, small, and explained before the first claim. They may provide a safety buffer or celebrate a milestone, but must not complete or bypass a later lesson. Prefer small amounts of exact XP points when an item reward would be too powerful; do not grant whole XP levels for routine lessons. Avoid random loot, powerful equipment, and unexplained currencies. Manual claiming remains enabled so the quest book can teach the reward interaction explicitly.

## Build and validate

```powershell
pwsh ./tools/validate-pack.ps1
pwsh ./tools/build-pack.ps1
```

The build creates `build/First-Torch-<version>.zip`. Import the archive into a compatible launcher; the launcher downloads the pinned mods.

## Update a development instance in place

For quest and configuration iterations, close Minecraft and run:

```powershell
pwsh ./tools/update-instance.ps1 -InstancePath "D:\Minecraft\curseforge\minecraft\Instances\First Torch"
```

The updater has an explicit managed-path allowlist. It backs up the current managed files under `<instance>/.first-torch/backups/<timestamp>/`, replaces only the quest book and the First Torch guide resource pack from `overrides/`, verifies copied trees by SHA-256, and records the installed pack version in `<instance>/.first-torch/state.json`. It adds `file/first_torch_guides` to the existing `resourcePacks` option without removing other packs or changing unrelated settings.

The following stay outside updater ownership:

- `saves/`
- all `options.txt` values except adding the First Torch guide pack to `resourcePacks`
- `screenshots/`
- all resource packs except `resourcepacks/first_torch_guides/`
- logs, caches, and unrelated mod configuration

The updater does not install or remove mod JARs. If `manifest.json` changes its dependency list, update through CurseForge or perform a fresh profile import before applying overrides.

## In-game acceptance test

Use a fresh launcher profile and a new survival world.

1. Confirm Minecraft and NeoForge start with all five dependencies.
2. Open the quest book and confirm the `First Steps` chapter appears.
3. Complete the path once in English and inspect it once in German.
4. Verify automatic item tasks complete with the listed vanilla items.
5. Confirm manual checkmarks clearly describe something the player must judge.
6. Exit and reopen the world to confirm progress persists.
7. Check `latest.log` for errors from FTB Quests.

Record playtest findings in `docs/Playtest.md` before declaring a milestone complete.
