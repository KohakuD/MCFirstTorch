# First Torch

**A patient, step-by-step Minecraft survival course inside the game.**

First Torch is a small NeoForge modpack built around FTB Quests. It teaches new players what to do, why it matters, how to do it, and what commonly goes wrong—without requiring an experienced player to answer every small question.

The project is deliberately separate from DistinctCraft. DistinctCraft can be installed alongside it when a compatible version is available, but First Torch works on its own and is not specific to colour vision.

## Current target

- Pack version: `0.7.0`
- Minecraft: `26.1.2`
- NeoForge: `26.1.2.84`
- Languages: English (`en_us`) and German (`de_de`)
- Current milestone: locating and securing the End Portal room without activating it

## Included foundation

- FTB Quests, FTB Library, FTB Teams, and the official FTB item-tag filter modules
- Twelve bilingual chapters covering the first safe night, independent survival routines, essential Iron equipment, finding the way home, sustainable supplies, the first deep Diamond expedition, optional safe mob drops, the first controlled Nether visit, optional Nether activities, brewing, the Stronghold search, and controlled Stronghold exploration
- Reproducible CurseForge manifest with pinned dependency file IDs

## Install a development build

1. Run `pwsh ./tools/build-pack.ps1`.
2. Import `build/First-Torch-0.7.0.zip` into CurseForge, Prism Launcher, or another launcher that supports CurseForge modpack manifests.
3. Create a new world and open FTB Quests from its key binding.

The archive contains no redistributed mod JARs. The launcher downloads dependencies from their official CurseForge entries.

## Update an existing development profile

Close Minecraft, then update only the files owned by First Torch:

```powershell
pwsh ./tools/update-instance.ps1 -InstancePath "D:\Minecraft\curseforge\minecraft\Instances\First Torch"
```

The updater creates a timestamped backup inside `.first-torch/backups/` before replacing quest definitions and the pack-owned guide images. Worlds, key bindings, screenshots, and unrelated resource packs remain untouched. It only adds the First Torch guide pack to the existing `resourcePacks` option and preserves all other player settings. Mod dependency changes still require a CurseForge profile update or a fresh import.

## Project status

Version 0.2.0 has passed its iterative in-game beginner review. The 0.3.0 through 0.6.0 content has been implemented and remains under in-game review. Version 0.7.0 prepares Eyes of Ender, teaches a staged Stronghold search, and continues through a marked labyrinth route to a secured but inactive End Portal room. Activation remains locked behind a later End-expedition checklist. Optional Bastion, Fire Resistance, and Stronghold Library paths remain independent. The detailed scope and acceptance record live in [docs/Roadmap.md](docs/Roadmap.md).

## Licence and trademarks

First Torch source content is available under the MIT License; see [LICENSE](LICENSE). Minecraft is a trademark of Microsoft. This project is not affiliated with Mojang Studios, Microsoft, FTB, CurseForge, or NeoForged.
