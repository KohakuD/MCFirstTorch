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
- Quest system: FTB Quests `26.1.2.7`
- FTB Quests 26.1+ uses JSON5. Do not add or restore legacy SNBT quest definitions.
- Mod JARs, launcher instances, worlds, logs, and player progress never belong in Git.
- Prefer configuration and quest content over custom Java code. Add KubeJS only when FTB Quests cannot express the behaviour cleanly.
- KubeJS is intentionally absent from the 0.1.x pack because its Better Advanced Tooltips dependency crashes during NeoForge startup. Reintroduce scripting only after its complete dependency chain passes a clean-profile startup test.

## Workflow and verification

- Keep changes focused on the requested milestone.
- Use stable 16-character uppercase hexadecimal IDs for FTB quest objects. The first character must be `0` through `7` so the value fits FTB Quests' positive signed Java `long`; never regenerate an existing ID casually.
- Run `pwsh ./tools/validate-pack.ps1` after pack metadata or quest changes.
- Run `pwsh ./tools/build-pack.ps1` before handing off an installable build.
- For quest changes, perform a fresh-profile in-game test when possible: load a new world, open the quest book, verify both languages, complete the affected path, and restart once.
- Treat the learner's confusion as a product bug: improve the explanation or sequencing rather than assuming prior knowledge.

## Git and releases

- Do not commit or push unless explicitly requested. Creating the initial repository and push is authorised by the user's project-creation request.
- Never commit generated files under `build/`.
- Release archives must contain only `manifest.json` and `overrides/` at their root.
- Pin dependency file IDs for reproducible builds; version updates are deliberate changes with a validation pass.
