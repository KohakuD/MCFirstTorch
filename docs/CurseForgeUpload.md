# CurseForge upload preparation

Current upload: [Minecraft 1.21.1 / 0.14.0-beta.1](releases/1.21.1-0.14.0-beta.1.md). The instructions below describe the older 26.1.2 release. Do not use its JAR or Java version for the backport.

Prepared for First Torch `0.13.0-beta.1` on Minecraft `26.1.2` / NeoForge
`26.1.2.84`. This document prepares a manual CurseForge upload; it never uploads
files or changes the project page itself.

## Upload file

**Publication on hold:** the owner archived file `8851077` after finding the
design-preview switch and development test controls. The replacement is
`build/libs/firsttorch-0.13.0-beta.2.jar`; it also fixes premature welcome
acknowledgement. Do not re-upload until the three in-game regressions are accepted.
The beta.1 submission details below are historical, not a publishable recommendation.

CurseForge project ID: `1689817` (created; moderation pending).
Beta file `8851077` was uploaded successfully on 2026-09-10. The observed file
status was **Baking**; this is processing, not publication approval. Automatic
publication after approval is selected. Metadata: Client, NeoForge, Minecraft
26.1.2, Java 25, Beta, with an English Markdown changelog.
Author status: https://authors.curseforge.com/#/projects/1689817/files/8851077
Choose **Custom License** and use the repository `LICENSE` text. Code and text
use MIT; original visual assets use CC BY 4.0, with Minecraft content excluded.

Project logo: `src/main/resources/assets/firsttorch/branding/logo.png`.
The owner-approved voxel logo is also embedded in the mod metadata for the Mods screen.

Upload only:

`build/libs/firsttorch-0.13.0-beta.1.jar`

Select **Minecraft 26.1.2** and **NeoForge**. Use the **Beta** file type. Beta
files require the Beta files filter or a direct project link according to the
upload form. New projects remain unavailable until moderator approval.

Do not upload a development run directory, a world, `options.txt`, a resource pack,
the historical FTB archive, a Minecraft JAR or a NeoForge JAR.

## Suggested project description

### English

First Torch is a patient, step-by-step Minecraft survival course inside the game.
It is an independent NeoForge mod with a native quest browser: guided lessons,
automatic and manual learning checks, optional Redstone practice, a reference
library, rewards, search and English/German text.

Requirements: Minecraft Java 26.1.2, NeoForge 26.1.2.84 and Java 25.
First Torch does not require FTB Quests, FTB Library, FTB Teams, KubeJS or
DistinctCraft.

Open First Torch with the key directly below Escape and left of 1, or through the
pause-menu entry. The development-only test-completion button is not included in
this release JAR.

NOT AN OFFICIAL MINECRAFT MOD. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR
MICROSOFT.

Feedback and bug reports: [GitHub Issues](https://github.com/KohakuD/MCFirstTorch/issues).

Voluntary support: [Buy Me a Coffee](https://buymeacoffee.com/KohakuD).

Contact: [KohakuD Mod Contact & Feedback](https://docs.google.com/forms/d/e/1FAIpQLSee3Rtf4uvSOGDUpd_bGpUMKWNM90tvHh15vOi5UA3tLneGuA/viewform?usp=publish-editor).

### Deutsch

First Torch ist ein ruhiger, schrittweiser Minecraft-Überlebenskurs direkt im Spiel.
Der unabhängige NeoForge-Mod bietet geführte Lektionen, automatische und manuelle
Lernschritte, optionale Redstone-Übungen, eine Nachschlagebibliothek, Belohnungen,
Suche sowie deutsche und englische Texte.

Benötigt werden Minecraft Java 26.1.2, NeoForge 26.1.2.84 und Java 25. First Torch
braucht weder FTB Quests noch FTB Library, FTB Teams, KubeJS oder DistinctCraft.

First Torch öffnest du mit der Taste direkt unter Escape und links von 1 oder über
den Eintrag im Pausemenü. Die Entwicklungs-Testabschlüsse sind nicht im Release-JAR.

NOT AN OFFICIAL MINECRAFT MOD. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR
MICROSOFT.

Fehler und Rückmeldungen: [GitHub Issues](https://github.com/KohakuD/MCFirstTorch/issues).

Freiwillige Unterstützung: [Buy Me a Coffee](https://buymeacoffee.com/KohakuD).

Kontakt: [KohakuD Mod Contact & Feedback](https://docs.google.com/forms/d/e/1FAIpQLSee3Rtf4uvSOGDUpd_bGpUMKWNM90tvHh15vOi5UA3tLneGuA/viewform?usp=publish-editor).

## Pre-upload checks completed

- Clean CurseForge-profile acceptance: user accepted on 2026-09-10.
- `./gradlew.bat test build`: passed.
- Native JAR boundary verifier: passed; the JAR has no Minecraft, NeoForge, FTB,
  world or configuration payload.
- 91 runtime-native diagrams and 15 approved screenshot-derived images accepted.
- LAN, accessibility and practical Redstone acceptance completed within the current
  project scope. Dedicated-server testing remains explicitly waived, not passed.

## Owner decisions still required before publishing

1. Review the final listing, screenshots and disclaimer against the current
   Minecraft Usage Guidelines and CurseForge submission form immediately before
   upload. This project documentation is an engineering record, not legal advice.

## Sources checked on 2026-09-10

- [Minecraft Usage Guidelines](https://www.minecraft.net/en-us/usage-guidelines):
  mods must be original and not contain a substantial part of Minecraft's code or
  content; use of Minecraft branding/assets requires a prominent non-official
  disclaimer and a responsible publisher/contact.
- [Minecraft EULA](https://www.minecraft.net/en-us/eula): the Usage Guidelines
  apply to mods and may change.
- [CurseForge file types and additional fields](https://support.curseforge.com/support/solutions/articles/9000197242): Alpha files are opt-in in the CurseForge app; new projects need an approved Release or Beta file to sync to the app.
