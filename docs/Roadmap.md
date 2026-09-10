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

- [x] LAN multiplayer acceptance: independent player progress, reward recipients and reconnect/host-restart persistence
  - [x] Native storage regressions for welcome acknowledgements across two players, two reopen cycles, separate worlds and independent quest storage
  - [x] Two-account LAN smoke test: independent welcomes and quest progress, reward recipients, duplicate claims, reconnect/restart persistence and claim-all isolation confirmed by the user (see `MultiplayerAcceptance.md`)
  - Dedicated-server two-client acceptance and lifecycle checks are explicitly waived by the owner (2026-09-10), not passed; dedicated-server operation remains unverified and is not a release gate for the current scope
- [x] Multiplayer safety review for backups, deaths, version upgrades, and any future team semantics (known crash limitations documented, not a crash-recovery guarantee)
  - [x] Storage/identity source review and backup/upgrade policy documented in `ProgressSafety.md`; journal copy and unsupported-version regressions passed
  - [x] In-game death/respawn acceptance: completion and reward states retained, other player unchanged, reconnect and acknowledged welcome correct
  - [x] Synthetic closed-storage snapshot recovery: native progress, welcome and reward journals restored together without later-state merging
  - [x] User acceptance of clean-save whole-world copy: quest state, inventory, XP and claim protection retained
  - [x] Interrupted-welcome in-game acceptance: after respawn, choosing Later persists acknowledgement across reconnect
  - [x] Reload-listener regression: malformed/unreadable resources preserve the last published snapshot, followed by successful recovery
  - [x] Normal in-game `/reload` accepted; abrupt-save recovery remains an explicitly unverified limitation (see `ProgressSafety.md`)
- [x] User acceptance of the delivered End City/End Ship and optional mob-drop/Bastion content; separate clean-profile survival verification remains below
- [ ] Complete Redstone practical-build and illustration acceptance in both languages
- [ ] Accessibility acceptance beyond the existing foundations, including narrator behaviour and keyboard-only navigation in all relevant native surfaces
  - [x] User accepted bilingual keyboard/narrator smoke checks for navigation, search feedback, reference paging, trophies and settings
  - [ ] Remaining quest-state narration and enlarged/quiet-mode combinations in `AccessibilityAcceptance.md`
- [ ] Verify licences for native libraries and release assets; retain Minecraft-derived artwork only for the reviewed target version
  - [x] Native build/dependency inventory and official Minecraft terms reviewed; no bundled third-party libraries
  - [ ] Runtime illustration migration: 42 diagrams (28 crafting, 4 brewing, 5 recipe composites, 2 smelting composites, 1 small-grid guide, 1 block comparison, 1 enchanting interface) render from installed assets and exclude their PNGs; batches 1–9 accepted, archaeology and enchanting visuals pending in `LiveIllustrations.md`
  - [ ] Remaining image permissions/provenance and screenshot/public-branding review; see `ReleaseLicensing.md`
- [ ] Clean-profile release acceptance: fresh install, complete guided route, both languages, restart persistence, and native JAR verification
- [ ] Decide publication and launcher-update workflow for the native mod

## Scope rules

- The active runtime remains native-only. FTB Quests, FTB Library, FTB Teams, Initially, KubeJS, and related pack tooling are out of scope.
- No FTB progress importer or migration path is planned.
- Preserve stable native identifiers and existing player progress when extending guides.
- Treat learner confusion as a product issue: improve sequencing, wording, or visual explanation instead of assuming prior knowledge.

## Historical record

For the discontinued FTB pack, consult [`../archive/ftb-legacy/`](../archive/ftb-legacy/). Its historical Roadmap, Development notes, and Curriculum snapshot are retained there for provenance only.
