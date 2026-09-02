# Development

## Baseline

First Torch targets Minecraft 26.1.2 on NeoForge 26.1.2.84. Dependency versions and CurseForge file IDs are pinned in `manifest.json`.

The 0.1.x line deliberately uses the FTB quest stack plus FTB Filter System and FTB XMod Compat for vanilla item-tag objectives. KubeJS was removed after its required Better Advanced Tooltips dependency caused a startup Mixin failure. Add scripting only when the curriculum requires it and the full dependency chain has passed a clean-profile startup test.

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

## Reward design

Beginner rewards are fixed, small, and explained before the first claim. They may provide a safety buffer or celebrate a milestone, but must not complete or bypass a later lesson. Avoid random loot, powerful equipment, and unexplained currencies. Manual claiming remains enabled so the quest book can teach the reward interaction explicitly.

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
