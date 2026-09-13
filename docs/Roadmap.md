# Roadmap

**Release hold — 2026-09-13:** The owner identified missing coverage of major Minecraft features from 1.21 through 26.1. Complete the [curriculum coverage audit](CurriculumCoverageAudit.md) and the resulting lesson work before final release preparation. Existing test passes and UI acceptance do not establish content completeness.


## Previous release baseline: 0.13.0-beta.2 — Native course and reference library

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
- [x] Redstone practical-build acceptance in both languages
  - [x] User accepted the six-chapter practical pass; diagrams and target-version technical regressions passed; see `RedstoneAcceptance.md`
- [x] Accessibility acceptance for the documented native surfaces and tested configurations
  - [x] User accepted bilingual keyboard/narrator smoke checks for navigation, search feedback, reference paging, trophies and settings
  - [x] User accepted open/locked/completed quest narration and enlarged/quiet-mode combinations in German and English; see `AccessibilityAcceptance.md`
- [ ] Verify licences for native libraries and release assets; retain Minecraft-derived artwork only for the reviewed target version
  - [x] Native build/dependency inventory and official Minecraft terms reviewed; no bundled third-party libraries
  - [x] Runtime illustration visual acceptance: all 91 assembled diagrams render from installed assets; 14 captures remain unchanged and one owner-approved UI-free village screenshot derivative remains packaged. Owner accepted the collective review and the corrected three-air-block staircase headroom; see `LiveIllustrations.md`.
  - [ ] Remaining image permissions/provenance and screenshot/public-branding review; see `ReleaseLicensing.md`
- [x] Clean-profile release acceptance: fresh install, early guided-route smoke test, both languages, restart persistence, and native JAR verification; owner accepted on 2026-09-10, checklist in `CleanProfileAcceptance.md`
- [x] Public repository, shared English contact form, support links and owner-approved voxel logo integrated
- [ ] Publish the beta: complete the remaining asset review, review final listing/screenshots/disclaimer, then upload the production JAR. Upload material is in `CurseForgeUpload.md`.
  - [x] Owner-authorised beta upload completed on 2026-09-10: project `1689817`, file `8851077`, Java 25 and English changelog included; automatic publication after approval selected
  - Beta.1 file `8851077` archived by the owner after release-blocking preview/test-control reports; do not publish it
  - [x] Beta.2 in-game regression accepted by the owner on 2026-09-10: design-preview switch and test completion absent, welcome displayed in a new world
  - [ ] Submit the corrected beta after regression acceptance, then confirm moderation approval and public availability

## Active milestone: 0.14.0 — Multiple Minecraft versions and returning learners

Owner-agreed direction (2026-09-12): backport to Minecraft `1.21.1` first, then add `26.2`, and build a reusable version-comparison view for returning learners. Development has started as `0.14.0-alpha.1`; both `26.1.2` and an initial installable `1.21.1` edition now build; the latter has owner-confirmed early gameplay and bilingual UI acceptance; the owner confirmed the 1.21.1 Beta upload on 2026-09-12. The previous baseline is preserved at commit `029e8c4`. Existing release acceptance work above remains open as recorded.

### Phase 1: Shared foundation and Minecraft 1.21.1 backport

- [x] Preserve the verified `26.1.2` baseline and its stable guide, quest, task and reward IDs. Baseline test/build passed before the module split; guide content and IDs are unchanged.
- [ ] Complete the native API, loader and data/resource-format port assessment.
  - [x] Extract and test shared guide/parsing/validation/progression code on Java 21; retain Minecraft integration tests in the runtime module.
  - [x] Record initial API and content blockers, including the early lodestone progression issue, in `Backport1211.md`.
  - [x] Add the isolated `:mc1211` verification module, pinned to Java 21 / NeoForge 21.1.248, and compile the first native progress/welcome data adapters.
  - [x] Compile reward, task/command, reload/sync and payload adapters; reuse compatible journal/observation/service sources and validate the target through native NeoForge JUnit.
  - [x] Implement production entry/client-handler registration, native client UI/renderers and initial target-specific resources; compile/test and client startup checks pass. Live gameplay acceptance remains below.
- [x] Establish one repository and IntelliJ Gradle project with a Java 21 `:core` module and the existing Java 25 / `26.1.2` runtime. Future Minecraft targets get separate native modules; core classes are merged directly into each native JAR.
- [x] Provide separate dependencies, toolchains, run profiles, test directories and clearly labelled JARs for the implemented Minecraft targets. Share compatible logic, guide data and translations without forcing incompatible runtime code into a common module.
- [ ] Backport the native runtime and review every lesson against `1.21.1` mechanics, recipes, objectives and available content. Maintain English and German together and use exact target-version game assets.
  - [x] Adapt known incompatible items, Iron armour, Netherite lodestone and optional excursion prerequisites in both languages; include the owner-approved shared captures unchanged. Full mechanics/visual acceptance remains open.
  - [x] Validate crafting diagrams against original 1.21.1 recipes; validate course icons, inventory/tag objectives, item rewards and advancement criteria against native target data and default feature flags.
- [x] Record initial content availability and meaningful lesson differences between `1.21.1` and `26.1.2` as the basis for later comparisons; see `EditionDifferences.md`. Minecraft version order is independent of the date a First Torch backport is published.
- [ ] Validate and release the `1.21.1` edition before proceeding to the `26.2` port. A mod backport does not imply support for downgrading existing Minecraft worlds or importing progress across versions.

  - [x] Owner confirmed early 1.21.1 quest completion, reward collection and progress after restart. Enlarged view, search, references and trophies are also owner-confirmed in German and English.

  - [x] Owner confirmed that the existing fifteen screenshots fit 1.21.1 and 26.2; restored all sixteen backport image references and verified byte-identical packaging.

- [x] Prepare the independently versioned 1.21.1 `0.14.0-beta.1` release and English upload material; see `releases/1.21.1-0.14.0-beta.1.md`. Owner authorised publication first. Owner confirmed upload on 2026-09-12 and authorised proceeding to 26.2; moderation/public availability have not been independently verified.

### Phase 2: Minecraft 26.2 edition

- [x] Record official prerequisites and implement an initial isolated `:mc262` native build. Java 25 / NeoForge 26.2.0.86, five client adapters, all 417 reused target tests and package verification pass. Owner accepted the requested bilingual gameplay/UI smoke checks after the metadata correction. Target content review remains open. See `Port262.md`.

- [x] Port the native runtime, dependencies and resources to `26.2` while preserving support for the earlier targets.
- [ ] Review the Minecraft changes since `26.1.2`, add new lessons and update affected existing lessons in both languages.
  - [x] Add five optional bilingual Sulfur Caves cards, links and a reading trophy. Friends List guidance is implemented; new-card game acceptance remains open.
- [x] Extend the content-availability and change history with `26.2`; distinguish newly introduced lessons from substantive revisions and exclude cosmetic wording fixes from the novelty filter.

### Phase 3: Returning-learner welcome and version comparison

- [x] Implement and test the read-only shared comparison core with explicit Minecraft order, cumulative semantic changes, target availability from loaded quests and separate prerequisite context. The native 26.1.2/26.2 browser now integrates the comparison and per-player local persistence; in-game acceptance is pending.

- [x] Add a bilingual welcome question: "Do you already know First Torch from an earlier Minecraft version?" / "Kennst du First Torch bereits aus einer früheren Minecraft-Version?"
- [x] "No" opens all quests. "Yes" asks which earlier supported version the learner knows and opens the relevant new and revised lessons since that baseline.
- [x] Detect the running Minecraft target and maintain a bundled, explicitly ordered catalogue of published First Torch Minecraft editions. Offer only valid earlier comparison baselines; future targets such as `26.3` become available after their edition is implemented and published. An online catalogue refresh is optional future work, not required for this milestone.
- [x] Store per-lesson version history and target availability. Comparing `1.21.1` with `26.2` includes relevant changes from both `26.1.2` and `26.2`, shows each quest once and uses the lesson valid for the running target. Later targets extend the same model.
- [x] Add an accessible top-right icon with a bilingual accessible label, clear active state and keyboard/narrator support (popup tooltips remain disabled consistently with the existing interface) to change the baseline or return to all quests. Persist the selection per player.
- [x] Keep a single authoritative quest/reward progress state across views. Selecting prior knowledge must not complete hidden quests, grant rewards or import progress.
- [x] Define and test prerequisite access so returning learners can use the selected lessons without hidden prerequisite dead ends or silently bypassed reward rules. Provide links to required background lessons, hide empty chapters, scope counters clearly and explain empty comparison results.

### Implementation notes (2026-09-12)

The clock icon opens the returning-learner question and baseline choice. The bundled baseline catalogue contains 1.21.1 and 26.1.2; 26.2 remains a development target. Minecraft's running version selects the applicable history. 1.21.1 has no earlier baseline and keeps its normal welcome.

The filter shows new/revised cards, including locked reference cards, and hides empty chapters. Search and chapter counters use that projection. Prerequisite links open background cards with Escape returning to the comparison; all task and reward gates still use authoritative progress. Claim-all is hidden in filtered views; trophies retain their full-course scope. Preferences are local files keyed by player UUID and Minecraft target under `config/firsttorch-edition-view/`; they do not transfer between installations or alter world progress. Invalid/unsupported saved baselines fall back to all quests.

The owner explicitly accepted the 26.2 comparison controls, change summaries, version switching, background navigation and restart persistence in both languages on 2026-09-12. The 26.1.2 comparison and separate Sulfur-card/trophy checks remain pending. Implementation and automated checks do not replace those checks. Check both welcome choices, clock switching, nested background links, filtered counts, unchanged rewards and selection after restart.

### Planned reference-library progression

- [ ] Make the reference-library entry visible and usable from the beginning in every supported Minecraft target, even before any reference content is unlocked.
- [ ] Unlock each reference topic when its corresponding course chapter becomes available, rather than waiting for that chapter to be completed. Define explicit topic-to-course mappings; do not unlock the entire library through the existing single introduction quest.
- [ ] Show a friendly empty-state message until the first topic is available. Proposed player-facing copy:
  - `de_de`: "Dein Nachschlagewerk wächst mit deinem Abenteuer. Sobald du neue Kapitel freischaltest, findest du hier das passende Wissen zum Nachlesen."
  - `en_us`: "Your reference library grows with your adventure. As you unlock new chapters, their related knowledge becomes available here for you to revisit."
- [ ] Reconcile availability for existing saves from their current chapter access, preserving completed reading tasks and stable IDs. Keep only target-compatible topics and maintain both languages together.
- [ ] Review reference search, direct reading links and the returning-learner comparison so locked topics communicate their unlock condition consistently. Check the empty state, first unlock, later topic unlocks and restart persistence in each target.

This is planned work, not the current runtime behaviour. The mapping review must cover optional course branches and reference topics without an obvious matching course chapter.

### Acceptance

Release drafts and remaining manual checks: [26.1.2 and 26.2 preparation](releases/0.14.0-next-editions.md).

- [x] Run focused source/data tests, the full test/build gate and native JAR verification for each installable target artifact. Latest code baseline: `1767dba`, 1,103 tests, no failures/errors/skips; all three JAR boundary checks passed.
- [ ] Test each target with fresh profiles in English and German: welcome choices, baseline selection, cumulative comparison results, all-quests switching, prerequisite access, tasks, rewards and persistence after restart.
- [ ] Verify that each target packages only compatible content and original target-version assets, and that normal `26.1.2` behaviour remains intact.

## Scope rules

- The active runtime remains native-only. FTB Quests, FTB Library, FTB Teams, Initially, KubeJS, and related pack tooling are out of scope.
- No FTB progress importer or migration path is planned.
- Preserve stable native identifiers and existing player progress when extending guides.
- Treat learner confusion as a product issue: improve sequencing, wording, or visual explanation instead of assuming prior knowledge.

## Historical record

For the discontinued FTB pack, consult [`../archive/ftb-legacy/`](../archive/ftb-legacy/). Its historical Roadmap, Development notes, and Curriculum snapshot are retained there for provenance only.

### Coverage remediation progress — 2026-09-13

- [x] Add the first four 26.1 reference lessons in 26.1.2/26.2: Golden Dandelion, Name Tag crafting, direct Stonecutter recipes and Copper trumpet sounds. Native recipe checks and comparison exclusion tests are included.
- [x] Owner accepted all four 26.1 cards as working and correct on 2026-09-13. Remaining coverage work and release hold remain in CurriculumCoverageAudit.md.
- [x] Inventory major gameplay additions across 1.21–26.2, distinguish missing teaching from mentions, and define ordered remediation blocks. Detailed authoring review remains per block.
- [x] Attribute existing history events to their actual Minecraft releases (1.21.4/1.21.5/1.21.9), independently of supported First Torch targets.

- [x] Add eight shared 1.21 lessons: two optional Crafter lessons in Redstone and six Trials reference cards for Trial Spawners/Vaults, ominous events, Mace and Wind Charges; keep them out of post-1.21.1 novelty results.
- [x] Add four independent 1.21 potion-effect reference cards with brewing links in all targets; preserve Minecraft introduction dates and avoid hazardous completion tasks.
- [ ] Accept the expanded Trials block in game in both languages; complete the later-update blocks before release.

- [x] Separate Pale Garden from Trial Chamber references, move Breeze/Bogged without changing quest IDs, and group Trial Chamber cards into four compact thematic rows.

- [x] Owner accepted the separated categories and compact Trial Chamber layout.
- [x] Add four Pale Garden readings (wood/moss, Heart, Resin, Eyeblossoms) in 26.1.2/26.2, dated to Minecraft 1.21.4.
- [x] Owner accepted the expanded Pale Garden readings; Bundle teaching is implemented below.

- [x] Owner accepted the expanded Pale Garden readings.
- [x] Add optional Bundle capacity and inventory-control lessons after the storage routine in 26.1.2/26.2; preserve the existing reward and date comparison entries to 1.21.2.
- [x] Owner accepted the Bundle work and requested S-layout; the animal/plant block followed.

- [x] Owner accepted the Bundle chapter S-layout.
- [x] Extend Cow/Pig/Chicken/Sheep references for 1.21.5 and add four compact plant cards in 26.1.2/26.2, with explicit comparison reasons for existing animals.
- [x] Owner accepted the animal additions and plant readings in game.
- [x] Add seven 1.21.6 travel readings: Dried Ghast, growth/care, Harness, flight/landing, Leads, Saddle and Locator Bar.
- [x] Owner accepted the travel block; Copper sorting and Shelf coverage followed.

- [x] Owner accepted the Ghast and transport block.
- [x] Add five Copper sorting/Shelf references in 26.1.2/26.2, dated to 1.21.9.
- [x] Owner accepted Copper sorting/Shelf readings; mounts and Spear coverage followed.

- [x] Owner accepted Copper/Shelf readings and alphabetical reference ordering in all targets.
- [x] Add eight 1.21.11 mount/Spear readings in 26.1.2/26.2, including air limits, undead riders, equipment and water-crossing exceptions.
- [x] Owner accepted the eight 1.21.11 readings; reconciled the coverage inventory with implemented content.
- [ ] Finish Copper tool/building-use coverage, review Sulfur/Friends mechanics, and complete remaining target-specific release checks. See CurriculumCoverageAudit.md for the current outstanding list.

- [x] Extend the Copper Ingot lesson with optional tool crafting and mining-tier guidance in 26.1.2/26.2; preserve its inventory task and the 1.21.1 text. Owner reading/layout acceptance remains pending.

- [x] Owner accepted the Copper tool explanation.
- [x] Add Building and Lighting references: three Minecraft 1.21 readings in all targets and one 1.21.9 Copper lighting/decor card in the modern targets, with explicit history and compact positions.
- [ ] Accept the Building and Lighting cards in game in both languages; continue the Sulfur/Friends detail review.

- [x] Review and expand the existing 26.2 Sulfur and Friends instructions using native data/code and official release notes.
- [ ] Accept the expanded bilingual readings, links and Sulfur trophy; live Friends joining remains unverified.
