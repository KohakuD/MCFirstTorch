# Roadmap

## Active milestone: 0.13.0-alpha.1 — Native course and reference library

First Torch is now developed only as a native NeoForge mod for Minecraft Java `26.1.2` on NeoForge `26.1.2.84`, using Java `25`. The retired FTB pack, its migration discussions, and its historical plans are archived in [`../archive/ftb-legacy/`](../archive/ftb-legacy/). They do not define active scope.

### Completed native progress

- [x] Native Java 25 NeoForge runtime with no FTB runtime dependencies
- [x] Data-driven bilingual guided course through independent exploration, including optional Redstone and reference lessons
- [x] Server-validated guide definitions, server-authoritative player progress, automatic/manual tasks, reward claims, and persisted completion state
- [x] Native browser with chapter navigation, progressive visibility, completed-course archive, search, illustrations, reference links, and returnable reading context
- [x] English and German player-facing text, original-version Minecraft assets, and native JAR boundary verification
- [x] Accessibility foundations: keyboard navigation, enlarged native browser view, quiet surfaces, and quest-node narration
- [x] Single-player content and interface acceptance across the completed guided path and reference-library organisation
- [x] Native-only archive cleanup: clean build, all guide images sourced natively, early recipe and later screenshot accepted in game

### Open acceptance and release work

- [ ] Dedicated-server and multiplayer acceptance: independent player progress, reward recipients, reconnects, disconnect/reload handling, and server lifecycle
  - [x] Native storage regressions for welcome acknowledgements across two players, two reopen cycles, separate worlds and independent quest storage
  - [ ] Two-client smoke test: independent welcomes, manual tasks and reward claims; reconnect both players (see `MultiplayerAcceptance.md`)
- [ ] Multiplayer safety review for backups, deaths, version upgrades, and any future team semantics
- [x] User acceptance of the delivered End City/End Ship and optional mob-drop/Bastion content; separate clean-profile survival verification remains below
- [ ] Complete Redstone practical-build and illustration acceptance in both languages
- [ ] Accessibility acceptance beyond the existing foundations, including narrator behaviour and keyboard-only navigation in all relevant native surfaces
- [ ] Verify licences for native libraries and release assets; retain Minecraft-derived artwork only for the reviewed target version
- [ ] Clean-profile release acceptance: fresh install, complete guided route, both languages, restart persistence, and native JAR verification
- [ ] Decide publication and launcher-update workflow for the native mod

## Scope rules

- The active runtime remains native-only. FTB Quests, FTB Library, FTB Teams, Initially, KubeJS, and related pack tooling are out of scope.
- No FTB progress importer or migration path is planned.
- Preserve stable native identifiers and existing player progress when extending guides.
- Treat learner confusion as a product issue: improve sequencing, wording, or visual explanation instead of assuming prior knowledge.

## Historical record

For the discontinued FTB pack, consult [`../archive/ftb-legacy/`](../archive/ftb-legacy/). Its historical Roadmap, Development notes, and Curriculum snapshot are retained there for provenance only.
