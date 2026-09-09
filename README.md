# First Torch

**A patient, step-by-step Minecraft survival course inside the game.**

First Torch is an independent NeoForge mod in active alpha development. It teaches what to do, why it matters, and how to practise safely. Its native runtime does not require FTB Quests, FTB Library, FTB Teams, their filter modules, Initially, or DistinctCraft.

## Current target

- Native mod: `0.13.0-alpha.1`
- Minecraft Java: `26.1.2`
- Tested NeoForge: `26.1.2.84`
- Java toolchain: `25`
- Languages: English (`en_us`) and German (`de_de`)

The guided course covers early survival through the Nether, End and independent exploration, with optional Redstone lessons. The separate reference library covers creatures and unusual mechanics. Native features include automatic/manual tasks, rewards, trophies, search, original-game illustrations and returnable reading links.

This is not a publication-ready release. Dedicated multiplayer, full parity, progress migration and release/licensing checks remain open in the [Roadmap](docs/Roadmap.md). See also [Reference verification](docs/ReferenceVerification.md).

## Develop in IntelliJ IDEA

Import this repository as a Gradle project and use **First Torch Client**, or run:

```powershell
.\gradlew.bat runClient
```

The development run enables the local-owner test-completion control and left-two-thirds window layout. These are run-configuration properties, not defaults enabled by installing the JAR normally. Test completion does not prove automatic objectives work in survival.

## Build and install the native alpha

```powershell
.\gradlew.bat test build
```

Output: `build/libs/firsttorch-0.13.0-alpha.1.jar`.

1. Use a separate Minecraft **26.1.2** profile with NeoForge **26.1.2.84** and a compatible Java 25 runtime.
2. Close Minecraft. Back up existing test worlds before changing installed mods.
3. Place the JAR in that profile's `mods` folder. Keep only one First Torch version installed; do not remove unrelated mods.
4. Start a fresh test world. Open First Torch with the key directly below Escape and left of 1, or through its pause-menu entry. Remap the key in Controls if needed.
5. Once references unlock, the Bookshelf icon opens the reference library. No physical quest-book item is needed.

Illustrations and translations are included in the native JAR; no separate First Torch guide resource pack is needed. Progress belongs to the world/player, not the JAR. Installing this mod does **not** import FTB Quests progress. Replacing the old pack is not a progress migration.

## Retained FTB pack

The older `0.9.1` pack remains in `manifest.json` and `overrides/` as a curriculum/migration source. Its scripts produce a **different artifact**:

- `tools/build-pack.ps1` → `build/First-Torch-0.9.1.zip` (FTB-based launcher pack)
- `tools/build-quest-overlay.ps1` → FTB quest overlay, not a native mod
- `tools/update-instance.ps1` → managed retained-pack paths, not the native JAR

See [Retained pack instructions](docs/LegacyPack.md). Do not import that ZIP when intending to test the independent mod.

## Development references

- [Development workflow](docs/Development.md)
- [Curriculum principles](docs/Curriculum.md)
- [Roadmap and acceptance status](docs/Roadmap.md)
- [Playtest log](docs/Playtest.md)
- [Progress migration design (not yet implemented)](docs/ProgressMigration.md)

Generated builds, worlds, logs and player data do not belong in Git.

## Licence and trademarks

First Torch source content uses the MIT License; see [LICENSE](LICENSE) and [NOTICE.md](NOTICE.md). Release asset/licensing review remains open in the roadmap. Minecraft is a trademark of Microsoft. This project is not affiliated with Mojang Studios, Microsoft, FTB, CurseForge, or NeoForged.
