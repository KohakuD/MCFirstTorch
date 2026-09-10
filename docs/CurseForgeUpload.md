# CurseForge upload preparation

Prepared for First Torch `0.13.0-beta.1` on Minecraft `26.1.2` / NeoForge
`26.1.2.84`. This document prepares a manual CurseForge upload; it never uploads
files or changes the project page itself.

## Upload file

Upload only:

`build/libs/firsttorch-0.13.0-beta.1.jar`

Select **Minecraft 26.1.2** and **NeoForge**. Use the **Beta** file type. Beta
files are eligible for normal CurseForge app synchronisation; Alpha files require
users to opt in.

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

Feedback and bug reports: GitHub Issues at **OWNER MUST INSERT THE PUBLIC REPOSITORY URL HERE**.

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

Fehler und Rückmeldungen: GitHub Issues unter **HIER MUSS DIE ÖFFENTLICHE REPOSITORY-URL EINGEFÜGT WERDEN**.

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

1. Supply the public repository URL for the project page. Do not invent it.
2. Review the final listing, screenshots and disclaimer against the current
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
