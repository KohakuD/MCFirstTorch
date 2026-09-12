# First Torch Repository Instructions

## Scope and repository boundary

- This repository is the independent source of truth for the **First Torch** Minecraft learning pack.
- Other Minecraft repositories may be read for conventions and compatibility, but must not be modified as part of First Torch work.
- DistinctCraft is a separate, optional companion project. Never make First Torch depend on it.
- Preserve unrelated user changes and never discard work without explicit permission.

## Communication and language

- Communicate with the user in concise, friendly German (Swiss spelling is welcome).
- Repository documentation, scripts, comments, commit messages, and identifiers are written in English.
- Player-facing quest text must be maintained in `en_us` and `de_de` together. Neither language may lag behind the other.
- End implementation reports with: changed, checked, open, and any required in-game test.

## Authoritative project context

Before changing pack content, read:

1. `README.md`
2. `docs/Development.md`
3. `docs/Roadmap.md`
4. `docs/Curriculum.md` when changing lesson content

The active milestone in `docs/Roadmap.md` controls scope and versioning.

## Technical baseline

- Minecraft: `26.1.2`
- Loader: NeoForge `26.1.2.84`
- Native runtime: `0.14.0-alpha.1` (development; playable target remains 26.1.2)
- Java toolchain: `25` for the current Minecraft runtime; `21` for the shared `core` module
- Mod JARs, launcher instances, worlds, logs, and player progress never belong in Git.
- Maintain the native Java/datapack implementation; do not add FTB Quests, FTB Library, FTB Teams, KubeJS, or an FTB-progress importer.

## Workflow and verification

- Keep changes focused on the requested milestone.
- Preserve stable guide, quest, task, and reward IDs; never regenerate an existing ID casually.
- Run focused Gradle tests after source or guide-data changes, and `pwsh ./tools/verify-native-jar.ps1 -JarPath <built.jar>` after a production JAR build.
- Run `.\gradlew.bat test build` before handing off an installable build.
- Quest images that depict Minecraft items, blocks, entities, or interfaces must use the exact textures and models from the targeted Minecraft version. Never approximate or generatively redraw them. Neutral frames, arrows, labels, and other explanatory overlays may be constructed around the original game assets.
- In crafting guides, render placeable block ingredients and results with the consistent three-dimensional Minecraft block-model view used by the established guides. Keep these models straight and uniformly aligned. Use flat two-dimensional textures only when the illustration is genuinely a top-down plan, such as an Enchanting Table and Bookshelf layout.
- For guide changes, perform a fresh-profile in-game test when possible: load a new world, open First Torch, verify both languages, complete the affected path, and restart once.
- Treat the learner's confusion as a product bug: improve the explanation or sequencing rather than assuming prior knowledge.
- Multi-panel recipe illustrations must use the full reading-column width, preserve each panel's aspect ratio, and avoid empty outer-frame padding. Keep single illustrations compact; do not apply the half-width default to recipe pairs or future multi-panel guides.

## Git and releases

- The owner has permanently authorised commits and pushes for this project. Save completed, verified work periodically; never include unrelated changes.
- Never commit generated files under `build/`.
- Do not package archived FTB material as part of a native release.
