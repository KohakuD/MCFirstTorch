# Playtest log

## 2026-09-09 — Migration design boundary

Quest-state narration is accepted by the user. Documented the offline migration
design and unresolved source-format, team and partial-reward cases. This delivery
changes documentation only; no new in-game test or world conversion is required.

## 2026-09-09 — Quest state narration

Earlier Redstone visibility and plain-panel presentation are accepted by the user. Added bilingual state announcements before quest descriptions when quest nodes receive narrator focus. Shared reward eligibility prevents announcing pending, claimed or locked rewards as available. No definitions, IDs, progress or network changes.

Pending: enable Minecraft's UI narrator and Tab to open, locked, completed and reward-ready quest nodes in both languages. Confirm the title and state precede the lesson description, and claiming a reward removes the reward-ready announcement. No progress reset is needed.

## 2026-09-09 — Earlier optional Redstone access

Moved only the first Redstone quest's prerequisite from the final independent-exploration introduction to `1C4EA0627FB38D59` (Ready for the Depths). All six Redstone chapters retain their display order and internal/follow-up chain. No mandatory course quest depends on the Redstone branch. Existing completions, IDs, tasks and rewards are unchanged. Added visibility regression coverage before/after the mining-preparation quest. Pending: finish that preparation quest and confirm Redstone Basics appears before finding Diamonds; follow-up chapters should still require their existing Redstone milestones. No reset needed for existing progress.

## 2026-09-09 — Chapter order follow-up

Moved composting immediately after farming, Iron Essentials after ores, and all six Redstone chapters after Bookshelves. Redstone prerequisites remain after the existing course introduction to independent exploration; earlier display order is not earlier availability. Renamed the Piglin chapter in both languages to Nether Plans and Piglin Bartering and clarified that only its introductory reading gates the Fortress, not the optional barter. Stable IDs, quest prerequisites, tasks and rewards are unchanged. Recommendation tests reflect the new order. Pending: inspect chapter sorting and the renamed Nether chapter in game.

## 2026-09-09 — Early chapter display order

User requested farming, animal care, storage and composting directly after food, ahead of ores, and Iron Essentials before Safe Mining. Changed only eleven chapter order values; stable IDs, prerequisites, quests, tasks and rewards remain unchanged. Chapters still appear when their own prerequisites allow them, rather than unlocking in display order. Regenerated the chapter reference image with updated numbers. Pending: inspect early chapter sorting in game; no progress reset required.

## 2026-09-09 — Optional plain panels

The user accepted enlarged-view routing after the height-cutoff fix. Added the bilingual, persistent `quietSurfaces` option under Accessibility: flat dark reading/panel surfaces without decorative gradients or scratches. Brass action buttons keep bright fills and dark text. No quest data or progress changes.

Pending: toggle plain panels via the person icon, inspect a long lesson, selected chapter and gold action button, then disable to compare the original metal treatment. Check the setting survives restart. This is a visual preference, not a completed accessibility certification.

## 2026-09-09 — Enlarged-view graph regression

User review exposed the Welcome fork/join map falling back to authored positions below a fixed 240-pixel map height. Eligibility now uses actual node count and minimum row spacing; enlarged mode reserves additional reading-toolbar height. The compact header progress bar stops before guide navigation. A regression exercises normal/enlarged overview/reading at four window sizes, checking consistent vertical routing, node bounds, non-overlap and connections outside node interiors. No quest state or dependencies changed. Pending: reopen Welcome with enlarged view enabled and check the map and header; compare overview and reading mode.

## 2026-09-09 — Optional enlarged quest-browser view

Added an off-by-default client setting under Accessibility in both languages. It enlarges the browser's text, icons and controls together by up to 25 percent; constrained windows reduce or suppress extra zoom. Five added viewport tests protect unchanged defaults, bounded size, small-window fallback and pointer-coordinate conversion. Search/reference modals and vanilla options keep their existing scaling. No quest definitions, server protocol or player progress changed.

Pending in-game: enable the larger view via the person icon, return to a long lesson and check text, scrolling, chapter selection and task/reward buttons. Check overview and expanded reading layouts, reduce the window size, then turn the setting off to restore the original view. Restart once to verify the chosen preference persists. No progress reset is needed. Full contrast/text-only scaling and screen-reader acceptance remain separate roadmap work.

## 2026-09-09 — Player storage isolation regressions

Added four automated tests using temporary directories only: two players completing the same task/quest IDs independently, one player's later history update surviving two reopen cycles without affecting the other, one UUID in separate world directories, and separate reward journals retaining independent claims even when one journal is corrupt. These verify storage components, not live player-action/network/payout routing. No real world, player progress or runtime code changed.

Next integration test requires two distinct player accounts on a disposable dedicated test world with the same native build. Do not reset the existing single-player world:

1. Join as A and B. Each should receive their own first-join prompt; dismissing A's prompt must not dismiss B's. Reconnect each once to check it does not repeat.
2. A confirms a Welcome task while B leaves it open. Check each quest book and completion notification. Then B completes the same task independently.
3. A claims its reward; check exact XP/item changes on A only. B must still be able to claim its own reward once. Include claim-all with more than one eligible reward and verify neither recipient receives duplicate payouts.
4. Reach an automatic inventory objective on both accounts; give the required items only to A. Check counts/completion remain separate. Repeat with B.
5. Leave different progress and unclaimed rewards on A/B, stop the server normally, restart and reconnect in reverse order. Verify both histories and claim states are retained independently.

Verification: all 395 native tests and the build pass; whitespace checks pass. Pending: this two-player dedicated-server acceptance. Local-owner development test completion is not evidence for automatic objectives or remote-player permissions.

## 2026-09-09 — Native artifact boundary and CI

Added a separate Java 25 native test/build job to the existing pack workflow. Finished-JAR validation allows only First Torch classes/resources and reviewed metadata; it checks the two declared game/loader dependencies and required language/course files. Six isolated fixtures passed, including rejection of world data, foreign classes, nested JARs, extra dependencies and missing German resources. The current built JAR passed with 243 file entries. Temporary synthetic fixtures were removed. Whitespace checks passed. Remote execution subsequently passed: [GitHub Actions run 34394261061](https://github.com/KohakuD/MCFirstTorch/actions/runs/34394261061). No new game test is needed because no runtime/content code changed.

## 2026-09-09 — Native installation documentation

The user accepted the Jukebox/brushing hints. README now leads with the independent native mod and its actual build artifact, controls, library and development-only test mode. The previous pack workflow is retained separately in `LegacyPack.md`. Explicitly distinguish installing a native JAR from importing the old FTB ZIP and from migrating FTB progress. Inspected build configuration and built JAR metadata: declared dependencies are Minecraft/NeoForge, with no nested JARs or FTB class packages. This does not claim full release parity or licensing approval. No game files or progress changed; no new playtest required.

## 2026-09-09 — Jukebox and archaeology behaviour clarification

Reviewed target Jukebox playback/ejection and BrushableBlock completion/reset methods. Added bilingual hints to collect/reinsert an ejected disc and resume brushing safely after an interruption decreases progress. No task, reward, ID or saved progress changed. Pending: read the two updated library cards; no new expedition, reset or risky experiment is required.

Verification: native tests/build, retained-pack validation/build and whitespace checks passed.

## 2026-09-09 — Guard the reference verification baseline

Welcome wording accepted by the user. Added an explicit Minecraft 26.1.2 version gate before creature/mechanics archive verification and documented deliberate reference maintenance across upgrades. Five fixture tests passed (correct, wrong, missing ID, malformed JSON, missing entry); both verifiers also passed with the real target archive. Fixture files were removed after testing. No game code, assets, quests or saved progress changed; no in-game test is needed.

## 2026-09-09 — Refresh stale native Welcome instructions

The user accepted course/library separation. Updated three bilingual Welcome cards: tasks now describe automatic advancement observation and early task recording without bypassing prerequisites; rewards explain the conditional claim-all chest; opening explains the pause-menu alternative without requiring an inventory book. Removed obsolete alpha limitations. Existing proof actions remain equivalent, with no definition, ID, reward or progress changes.

Pending in-game: read the three revised cards in both languages for clarity and layout. Previously completed tasks need not be reset or repeated.

Verification: native tests/build, retained-pack validation/build and whitespace checks passed.

## 2026-09-09 — Separate library from course chapters

The user accepted the initial index and requested library-only reference chapters. The course column/archive now filters out field-guide and mechanics chapters while keeping them in the visible catalogue for search, links and index access. All sixteen chapter prefixes are removed in both languages. Welcome overview introduces the library, and the header uses Minecraft's original 3D Bookshelf item model. Existing progress, rewards and IDs are unchanged.

Native tests/build passed, including reference/course partition and completed-archive exclusion assertions. Pending in-game: course-only chapter/archive display, Bookshelf icon, short library titles, Welcome wording and continued search/link/return access to reference entries.

## 2026-09-09 — Compact reference index

Added a title-bar Book below the conditional reward chest. It opens an industrial modal with original chapter icons and translated titles for visible field-guide/mechanics chapters, including completed entries. No unavailable chapter is introduced. Selection rechecks visibility and stores the previous browser location in the existing return stack. No tasks, rewards, definitions or progress changed.

Pending in-game: open the Book with unlocked references; scroll and use keyboard navigation; select an archived chapter, read a card and return to the prior lesson; cancel with Escape without changing selection. Check both languages and small-window layout. Before references unlock, the Book must be absent. The reward chest must remain independently visible only when claims exist.

Verification: 391 native tests and mod build passed; retained-pack validation/build and whitespace checks passed. Two index tests cover all sixteen chapters/62 cards, ordering and exclusion of chapters outside the supplied visible list. Live modal interaction remains pending.

## 2026-09-09 — Mechanics instructional-structure audit

Read all 27 mechanics descriptions in both languages and recorded the trigger/result/safety/optional-observation coverage by chapter in `MechanicsReferenceSources.md`. Closed the selected-topic coverage and instructional-structure roadmap items, not the separate full target-version behaviour audit. Corrected German Jukebox grammar and English Pumpkin carving-step wording. No task, reward, ID, image or progress changes. No new interaction playtest is required for these wording corrections.

Verification: native tests/build and retained-pack validation/build passed; whitespace check passed.

## 2026-09-09 — Reference verification reconciliation

The user accepted the four reused Redstone image placements and renewed standing push authorization. Reconciled the remaining Rabbit and twelve-card reading acceptance entries with the user's explicit standing confirmation of previous delivered batches. Both target-archive verification scripts passed again. `ReferenceVerification.md` separates accepted reading/UI work from physical circuit and behaviour verification; no broad survival or interaction test is claimed. This documentation-only batch changes no gameplay or player data and requires no new in-game test.

## 2026-09-09 — Complete practical Redstone image placement

The user accepted chapter scrolling. Four existing follow-up cards now reuse matching original-asset diagrams: Comparator display, Observer changes, Door site and Door safe finish. All seventeen practical circuit cards now have illustrations; material-only cards and generic bench preparation are excluded intentionally. The Door-site caption explicitly leaves the plates for the next lesson. No objectives, rewards or saved progress change.

Pending in-game: inspect the four placements in the three chapters, especially the Door preview wording. Existing circuits need not be rebuilt for this visual check. This closes image placement coverage, not a fresh full survival playthrough.

Verification: all 389 native tests and the mod build passed. Retained-pack validation/build and whitespace checks passed. The new coverage test checks seventeen practical cards across six chapters; image packaging checks cover 111 placements.

## 2026-09-09 — Scrollable chapter navigation

The user accepted the original-component artwork revision. Replaced chapter page arrows with whole-row scrolling, an amber draggable scrollbar and focused-card Page Up/Down/Home/End controls. Scroll offset is clamped to current visible/archive rows, and navigation targets are revealed without changing quest selection while browsing. Reference history now stores a row offset rather than a page number. No gameplay or persisted data changes.

Pending in-game: scroll a long expanded chapter archive with wheel and thumb, in overview and compact reading widths; collapse/expand the archive; search for a distant chapter; follow a reference and return to the old scroll position. Try keyboard paging on a focused chapter card. The detail pane must keep its independent scrolling and chapter scrolling must not select quests automatically. Guide/trophy paging is not part of this change.

Verification: all 388 native tests and build passed, including five new scroll-geometry tests and existing reference-history tests. A separate code review found no concrete event/rebuild/navigation defect. Retained-pack validation/build and whitespace checks passed. Live UI interaction remains pending.

## 2026-09-09 — Original components instead of letter placeholders

At the user's request, replaced component letters across all eight affected Redstone diagrams: Repeater direction/range/delay, Comparator reading, short wire, wire limit, input comparison and Iron Door. The other five current Redstone images already contain original components or comparison/state labels, so no component substitution is needed there. Original models/textures replace Lever/Chest/Button/Plate/Door letters; straight Dust uses original wire texture and power tint. Counted wire symbols are explicitly not to scale. Comparison letters and position numbers remain intentionally.

Pending in-game: inspect recognisability and half-pane readability across the eight revised diagrams, especially the Lever, Plate, continuous wire and ×15/×3 quantities. The preceding container/pulse state-image acceptance also remains open. No reset or replay of completed tasks is necessary. Quest/task/reward data and player files are untouched.

Verification: all eight revised images inspected outside Minecraft; five component icons and all sixteen wire strengths passed the artwork verifier (edge continuity, colour progression and cache isolation). All 383 native tests/build, retained-pack validation/build and whitespace checks passed. Image placement count remains 107. Live visual acceptance remains pending.

## 2026-09-09 — Input, container and pulse state diagrams

The user accepted the basic wire/range/Piston comparisons. Three additional state diagrams explain the existing input comparison, 0/64/128-Cobblestone Chest checks and single Observer pulse. Both captions and letters distinguish repeated states from a physical building plan. Artwork uses original target Lamp/Cobblestone textures with neutral annotations. No tasks, rewards or saved progress are changed.

Pending in-game: inspect these three illustrations and captions at half-pane width in both languages. In particular, distinguish a manually switched-off Lever from an automatically ending Button pulse, and read the three Chest quantities. This is image acceptance, not a claim that every survival circuit has been freshly retested.

Verification: all 382 native tests and build passed, including 107 image placements, packaged dimensions and bilingual captions. Retained-pack validation/build and whitespace checks passed. A structural comparison against HEAD confirmed that course definitions are unchanged apart from image fields. All three PNGs were visually inspected outside Minecraft.

## 2026-09-09 — Basic wire and Piston comparison artwork

User accepted the range/delay/door diagrams. Added short-wire on/off, 15/16-Dust boundary and ordinary/sticky settled-state comparisons with bilingual captions. The limit plan explicitly groups fifteen Dust positions rather than suggesting one block; the Piston plan distinguishes final positions without pretending to render an extended Piston.

Pending in-game: check the three A/B images and their legends in the short-line, basic-range and sticky-pull lessons. Verify readable captions/scrolling and that final block positions are understandable. No progress reset or changed gameplay requirement is introduced. Other circuit artwork remains open.

Verification: all three images were visually inspected outside Minecraft. Native tests/build passed with 104 packaged image placements, bilingual captions and unchanged half-pane sizing. Retained-pack validation/build and whitespace checks passed. Live circuit behaviour was not tested by the agent.

## 2026-09-09 — Range, delay and doorway diagrams

The user accepted the Repeater/Comparator/Piston plans. Three additional illustrations now explain the long Repeater range line, short/long delay settings and two-sided Iron Door practice site. Original model textures are used for visible Repeaters/Lamps; lettered placeholders and condensed Dust groups are explicitly explained in both languages. Visual inspection outside Minecraft checked directions, spacing and labels.

Pending in-game: inspect the range, delay and door-walking lessons. Confirm that the 15-/3-Dust groups are understood as multiple consecutive positions, A/B as settings of one build, and P/T/P as plate/door/plate. Check caption wrapping and scrolling. Existing gameplay requirements and player progress are unchanged; other circuit artwork remains open.

Verification: all 382 native tests/build passed, including 101 packaged image placements and bilingual captions. Retained-pack validation/build and whitespace checks passed. No live circuit test was performed by the agent.

## 2026-09-09 — Repeater, Comparator and Piston plans

The user accepted the Observer orientation image. Added three direction/layout images to existing lessons, with bilingual legends separating schematic placeholders from original component views. Original lesson instructions, progress and rewards remain unchanged.

Pending in-game: inspect the Repeater direction, Comparator reading and Piston push images at half-pane width; check direction, numbered positions and captions. If rebuilding the existing exercises, close the book before observing power, container signal or movement. Other circuit illustrations remain open.

Verification: all three generated images were visually inspected. Model-top projection uses negative Z as north before clockwise rotation to east; cells touch without physical gaps. All 382 native tests/build, image packaging/dimensions/captions, retained-pack validation/build and whitespace checks passed. No in-game execution is claimed for this delivery.

## 2026-09-09 — Observer orientation illustration

The user accepted mechanics category cues and safe reading exercises. Added a 1672 × 941 original-asset top-down plan to the Observer watching lesson: contiguous positions 0/1/2 plus watching-face/output-face swatches A/B. Both captions distinguish explanatory arrows from actual wiring. The image was generated and visually inspected outside Minecraft; in-game readability and circuit behaviour remain pending.

Pending: open the Observer watching lesson, read the half-pane image/caption and identify which face touches Cobblestone and which touches the Lamp. If the practice build is available, remove and replace block 0 with the book closed and a dark pause between changes; each change should flash the Lamp. No progression reset is needed.

Verification: all 382 native tests and build passed, including packaged image dimensions, source-byte identity, bilingual caption presence and half-pane sizing. Retained-pack validation/build and whitespace checks passed. Live visual/circuit review remains pending.

## 2026-09-09 — Remaining reference audit and reading cues

Reviewed the remaining fifteen creature cards and all 27 mechanics readings in both languages against documented target evidence. No further loot-data or documented-mechanics contradiction was identified. Extended target archive checks for five more ordinary item entries, Shulker Looting chance and Turtle lightning loot. Added practical/discovery labels to mechanics descriptions and safe, non-activating reading exercises for archaeology recognition and all three Anchor cards. Runtime, quest definitions and progress remain unchanged.

Pending in-game: sample a practical mechanics card and a name/music discovery; check the archaeology comparison exercise and three Anchor comparisons for readable wrapping, scrolling and reachable confirmation/reference buttons. No new world, encounter, crafting, activation or death is required. Physical Redstone circuit verification and artwork remain separate open work.

Verification: native tests/build, both target-archive verifiers, retained-pack validation/build and whitespace checks passed. The new regression checks all 27 bilingual category cues and the safe reading prompts; this does not replace visual acceptance.

## 2026-09-09 — Creature drop regression expansion

The user accepted the final Respawn Anchor links and return navigation. Extended the read-only target-JAR verifier for ordinary animal/Overworld/Nether counts, conditional meat cooking, Zombie rare-drop gates and Frog-dependent Magma Cube outputs. The expanded verifier passes against Minecraft 26.1.2. No new game behaviour or player-state changes are introduced; no repeat in-game test is required for these script assertions. Full prose/interaction coverage remains a separate roadmap item.

Reviewed the twenty Animals/Overworld/Nether reference descriptions in both languages against the documented sources and target loot tables. Corrected Chicken's unconditional raw-meat wording: fire can yield cooked meat. No other concrete loot-data contradictions were identified in this bounded review. Optional visual check: the Chicken reading card in either language; no kill or progress reset needed.

## 2026-09-09 — Final Respawn Anchor references

Verification: all 381 tests and native build pass. Catalogue coverage now matches every one of the 62 current reference quests; all 59 previous entries remain unchanged. The Bed destination explicitly explains the respawn point in its existing text. Target mechanics checks and retained-pack validation/build pass. Live review remains pending.

The user accepted the curing/Cauldron/Pumpkin links. The three existing Respawn Anchor readings now link to bartering, safe Nether arrival and the Overworld Bed lesson. All 62 current library cards have reference destinations; this does not declare the entire reference/release roadmap complete. No original lesson text, progression, rewards or player files are changed.

Pending in-game: follow all three Anchor links and return to the reading position. No crafting, charging, spawn change or death test is needed.

## 2026-09-09 — Curing, Cauldron and Pumpkin links

Verification: all 381 tests and native build pass. The catalogue regression validates all 59 linked cards; a dedicated test protects both Pumpkin creature destinations and the Creaking return link. Retained-pack validation/build pass. In-game acceptance of this batch remains pending.

The user accepted the Bee/archaeology/music link batch. Nine existing cards now link to related Village/brewing, water/Bottle, Sheep and lighting lessons. Pumpkin headgear has separate Enderman and Creaking destinations; the latter already links back to headgear, so the return history can be checked without changing completion. Original reading chains, text, rewards and saved progress are unchanged.

Pending in-game: inspect these three archived/current chapters, follow links and return. In particular, check both headgear destinations and Creaking → headgear → back. No cure, washing, carving or dangerous encounter is required.

## 2026-09-09 — Bee, archaeology and music references

Verification: all 380 tests and native build pass. The reference regression checks all 50 source cards and their bilingual destination labels. All 41 prior catalogue entries are unchanged. Target mechanics-data verification and retained-pack validation/build pass. In-game review of this batch remains pending.

The user accepted the encounter/transformation/name links. Nine cards in the existing Bee Mechanics, Archaeology and Music chapters now link to related course or creature information. The Honey Bottle card provides both bottle preparation and the Bee reference. Original text, task definitions, dependency chains, rewards and progress remain unchanged.

Pending in-game: open these three chapters (also available from the Completed archive), check destination lessons and use return navigation. In particular, try Honey Bottle → Bee → Bee Mechanics and return twice, and Music Disc/Creeper → Creeper. No harvest, excavation, combat or progress reset is needed.

## 2026-09-09 — Encounters, transformations and names links

Verification: 380 tests and native build pass, including all 41 catalogue source cards and the real Toast/Rabbit/names return sequence. Exact practical destination titles were checked against the German course strings. Retained-pack validation/build pass. Live in-game review of this batch remains pending.

The user accepted the Animals/Overworld/Nether link batch. Nine further existing cards now link to related safety, Pumpkin headgear, water/Bottle/Furnace and animal lessons. Jeb and Toast point directly to the Sheep and Rabbit cards, rather than the start of Animals. Existing completion states and all gameplay definitions remain untouched. The name/Rabbit references also provide a real nested-link return path.

Pending in-game: review the three existing chapters Trial Chambers/Pale Garden, Block Transformations and Special Names (including the Completed archive). Try Toast → Rabbit → Special Names and return twice, checking both reading positions. Check that block guides land on the relevant practical lesson. No experiment, item collection or reset is required.

## 2026-09-09 — Animal, Overworld and Nether reference links

Verification: all 379 tests and native build pass. The expanded regression validates all 32 source cards, every destination ID and bilingual chapter label, and duplicate destinations. All twelve prior catalogue entries remain unchanged. Target creature-loot verification and retained-pack validation/build pass. No live in-game check was performed for this batch.

The user accepted clickable references and return navigation. The next batch extends the existing catalogue to twenty cards in three existing chapters: Animals, Overworld monsters and Nether creatures. Old prose pointers are replaced by gold destination buttons; creature facts, quest definitions, rewards and player data are unchanged. Completed chapters remain in the archive rather than becoming new quests.

Pending in-game: review the destinations in these three chapters, including Rabbit and Witch, and return to the original reading position. No new completion or creature interaction is needed.

## 2026-09-09 — Clickable quest references and return navigation

Verification: 379 automated tests and native build pass, including stable reference destinations, bilingual labels, visibility protection, nested history and exact reading-location restoration. Retained-pack validation/build pass. The user subsequently confirmed all in-game checks as correct.

The user located the updated archived chapters and requested direct links. Twelve bundled references now target fixed quest IDs, using the destination chapter title as the bilingual button label. Links appear below the description; a footer return button restores the prior selection, detail scroll, reading layout and chapter-list position. Completed targets remain reachable. Hidden or missing targets cannot be opened by a link and do not unlock anything. History is session-only and supports nested jumps; definition replacement and preview switching clear it. No quest completions, rewards, protocol or player files are changed.

Pending in-game: open Silverfish → Inside the Stronghold from the completed archive, return to the same reading position, review long labels and keyboard activation in both languages, and verify an unavailable destination remains disabled. Check narrow and wide layouts, scrolling past the links, and ordinary task/reward controls.

## 2026-09-09 — Creature reference reading pointers

Verification: all 376 tests and native build pass, including exact destination-title checks in both languages. Target creature-loot verification and retained-pack validation/build pass. No live in-game test was performed for these pointers.

The user accepted the reordered Pumpkin chain and original screenshot. The next focused batch adds exact-title bilingual reading pointers to twelve existing cards in the End, aquatic and special-encounter chapters. These are plain-text navigation hints, not clickable links; the chapter list includes completed chapters, while later course chapters may require progress before appearing. No quest definitions, gates, rewards or player files change in this reference batch.

Pending in-game: review the pointers in all three chapters and both languages, and locate an already completed destination through the Completed group. No creature interaction or progress reset is needed.

## 2026-09-09 — Pumpkin sequence and original screenshot

Verification: all 375 tests and native build pass. Pixel comparison confirms that every pixel outside the two HUD masks is unchanged. Original source capture remains untouched; packaged dimensions and bilingual caption pass resource tests.

The user accepted Cauldrons and Respawn Anchors and clarified the Pumpkin order: carving, wearing, then making the lantern. Existing IDs/completions are preserved while the dependency chain and bilingual transitions change. The owner's pumpkin-overlay capture is included with the hotbar/crosshair masked on black backgrounds; remaining pixels are unchanged. The user subsequently accepted this reordered chapter and screenshot in game.

## 2026-09-09 — Cauldrons, Pumpkins and Respawn Anchors

Verification: 375 tests and native build pass; target mechanics verifier and retained-pack validation/build pass. Previous chapter definitions are unchanged apart from the already accepted archaeology/curing follow-up. German Anchor naming uses the original target language asset (Seelenanker). No player files were touched.

The user accepted the ordered archaeology/curing follow-up and original block comparison. The next batch adds three separate three-step reference chapters with nine manual readings, no item/XP payouts and three book-only trophies. Pending in-game: ordered arrows/confirmation, both languages, Anchor warnings, optional experiment wording, trophies and restart persistence. Existing progress must not be reset.

## 2026-09-09 — Ordered archaeology and curing

Verification: all 373 tests and native build pass, including ordered confirmation gates and packaged image dimensions/translations. Retained-pack build also passes. In-game acceptance remains pending.

User requested two coherent, separate three-step questlines rather than independent cards. Archaeology now progresses from recognition and the original-block comparison to safe brushing and finds; Zombie Villager curing progresses from preparation to treatment and recovery. Music remains independent. Pending: check arrows, attempts to confirm later steps early, normal progression, bilingual image caption and old completions. Reading still suffices; no excavation or cure is required. Image uses target 26.1.2 textures/models and was visually inspected outside the game.

## 2026-09-09 — Archaeology, music and curing references

Verification: 373 tests and native build pass; target recipe/icon checks and retained-pack validation/build pass. All previous 65 chapter definitions match the pre-batch state. In-game review remains pending.

User accepted the restored separate Bee entries and preceding mechanics batch. New review pending: three chapters with nine independent reading cards covering archaeology, music discs and Zombie Villager curing. Verify English/German text, icons, three book trophies and restart persistence. Do not excavate, stage Creeper combat or cure a Zombie Villager merely to test completion. Existing Rabbit and Bee IDs remain unchanged; no world files are edited.

## 2026-09-09 — Restore separate Bee mechanics

Verification: all 370 tests and native build pass; retained-pack validation/build pass. Restored chapter definitions match the original mechanics batch, with Rabbit retained in Animals. No live in-game test was performed.

User revised the organisation request: restore the original Bee creature card and separate three-card Bee Mechanics chapter/trophy using original IDs. Rabbit remains the eleventh Animals card. Pending in-game review: both Bee entries are separate, all three mechanics cards/trophy appear, and Rabbit remains available. No progress files are changed.

## 2026-09-09 — Rabbit and consolidated Bee reference

Verification: 370 tests and native build pass. Target Rabbit loot checks and retained-pack validation/build pass. Existing ten animal definitions and all other retained chapter definitions are unchanged. No live in-game test was performed.

Pending in-game review: Rabbit appears as the eleventh Animals card, with a Rabbit Hide icon, bilingual drop distinctions and independent reading confirmation. Existing ten animal readings remain; the derived Animals trophy now also requires Rabbit. Pollination and Honey Bottle/Honeycomb harvesting appear directly in the existing Bee card; its ID and completion remain unchanged. The separate Bee Mechanics chapter/trophy is removed. No player files are modified; old removed-card IDs are not reused. Check the maps and both languages after restart. The previous unusual-mechanics cards remain awaiting user acceptance.

## 2026-09-09 — First unusual-mechanics batch

Automated verification: all 369 tests and the native mod build pass; retained-pack validation/build pass separately. Target 26.1.2 recipe, harvest and icon checks pass. All 62 previous chapter definitions are structurally unchanged. The course now has 65 chapters, 327 quests and 414 tasks. In-game review of the new cards and physical experiments remains pending.

User accepted the five requested creature cards and Turtle Scute icon. New pending batch: block transformations (Concrete Powder, Mud, Wet Sponge), name effects (Dinnerbone/Grumm, jeb_, Toast), Bees/Honey (pollination, Honey Bottle, Honeycomb). Check all nine English/German reading cards, three compact maps and book-only trophies. Each card must be independently readable after the common course introduction; the optional experiment is never required for the checkmark. Do not spend Name Tags or provoke Bees just to test completion. If voluntarily testing the experiments, use safe home-ground setups; preserve original Sheep colour and exact name casing. Existing readings and reward claims must survive reopening and restart. No player/world files are modified.

## 2026-09-09 — Requested creature additions and Turtle icon

Verification passed: 365 automated tests, native build, retained-pack validation/build and target-archive loot/icon checks. All previous 313 quest definitions remain unchanged. Tests cover independent manual reading and expanded/new trophy eligibility; German names were checked against the target language asset. In-game review remains pending.

Pending user review: Goat in Animals, Witch in Overworld monsters, and Breeze/Bogged/Creaking in the new Trial Chambers/Pale Garden chapter. Check both languages, map spacing, five icons and independent manual reading without items or combat. Old reading marks remain; Animals and Overworld reappear as unfinished until their new card is read. Check all three affected book trophies, collapsed chapters and persistence after restart. Also verify the previously corrected Turtle Scute icon; the user explicitly deferred this test. No world or player files are modified. The previous twelve-card batch remains pending explicit acceptance.

## 2026-09-09 — End, water and special encounters

Verification passed: 362 automated tests, native build, retained-pack validation/build and target-version creature loot checks. All 58 existing chapter definitions compare unchanged; the three new chapters follow them in order. No world or player files were modified. In-game acceptance of these twelve new cards remains pending.

The user accepted the initial reference batch and expanded Animals chapter. New batch: End/approach (Enderman, Shulker, Silverfish, Endermite), aquatic life (Squid, Glow Squid, Dolphin, Turtle) and special encounters (Bee, Fox, Frog, Allay). Review all twelve cards in both languages, independent selection and readable four-node maps. Confirming a card records reading only; no fight, creature transport or collected item is required. Finish any one of the three chapters first and check its independent trophy, collapsed completed chapter and persistence after restart. All earlier player progress is preserved. Redstone circuit images remain pending.

## 2026-09-09 — Animal reference expanded

Verification passed: 361 automated tests, native build, retained-pack validation/build and the expanded target-loot check. All 57 unrelated chapters are unchanged. The original four animal cards retain IDs, tasks, prerequisites and rewards; only their map positions changed.

User requested Animals rather than Farm Animals, adding Wolf/dog companion, Cat, Horse, Camel and Axolotl. Test all nine animal cards in the 3-by-3 grid and both language versions. Each card must remain independently readable after the common course introduction, without kill/taming/item requirements. Existing four reading checkmarks must survive; the derived trophy now requires the five extra readings too. Check new icons, title, collapsed chapter and restart persistence. This request does not accept the prior twelve-card reference batch. No player files are modified.

## 2026-09-09 — Initial creature-reference batch

Verification passed: 359 automated tests, native build and retained-pack validation/build. The read-only target-archive check passed for the critical conditional drops. All 55 earlier native chapter definitions compare unchanged; no player files were touched.

User accepted Pistons, Observers and Automatic Door. New batch: three four-card chapters for farm animals, common Overworld monsters and Nether creatures. They become available after the existing course-completion introduction without requiring Redstone completion. Review all twelve cards in both languages. Select the fourth card first in each chapter: other cards must not lock it. Inventory possession must not complete a reading card. Confirming a card means only that it was read, not that a creature was killed or loot collected. There are no XP/item claims; the chapter trophy appears after all four cards are read. Check collapsed completed chapters, search, trophy inspection and persistence after restart. Existing progress is preserved. Further creature groups and Redstone circuit illustrations remain pending.

## Native Redstone three-chapter batch — Automatic Door added

Verification passed: 356 automated tests, native build and retained-pack validation/build. All 54 prior native chapter definitions compare unchanged. The target recipes were inspected; automated content tests cover one-versus-two plates, early inventory recognition without bypassing prerequisites, separate practical confirmations, reward and trophy eligibility.

Pistons and Observers remain pending user acceptance; test them together with Automatic Door. Check both languages and the four new lessons: one Iron Door plus two Stone Pressure Plates, free-standing placement on solid ground, one plate immediately on each side, opening while crossing in both directions and closing after stepping away. Leave room to walk around the door; never use the practice circuit as a home's only exit or mob barrier. Check support/placement and remove stray power if it stays open. Stone plates are living-entity inputs, not dropped-item detectors. Claim five XP once, inspect the book-only trophy, and restart to verify persistence. TEST completion does not verify the actual circuit.

Target Minecraft 26.1.2 recipe resources confirm six Iron Ingots produce three doors and two Stone produce one plate. Physical survival circuit tests and authentic circuit illustrations remain pending. No world or player progress is reset.

## Native Redstone motion — Pistons and Observers

Verification passed: 353 tests, native build and retained-pack validation/build. Target-game Observer state fixtures verify powered/unpowered output on every direction; the target push limit is twelve. All 52 earlier chapter definitions compare unchanged. This is not a substitute for the physical push/pull/pulse exercises in a running world.

The user accepted the Repeater/Comparator batch. New batch: two four-quest chapters after that optional path. Check both languages, component possession and distinct normal/sticky behaviour with one Cobblestone, a maintained Lever input and a clear horizontal lane. Check Observer face/output orientation, initial placement pulse settling, removing/replacing the watched block, walking without changing it, and the stable final state. Never connect the Observer exercise to the Piston yet; TEST completion does not verify the actual circuit. Claim each five-XP reward once, inspect both trophies and restart to verify progress. No world or reward data was reset. Authentic circuit illustrations remain pending.

## Native Redstone signals — Repeaters and Comparators

Verification passed: 349 automated tests, native build and retained-pack validation/build. A target-game SimpleContainer fixture confirms signals at 0, 1, 64, 123, 124 and 128 Cobblestone; the tutorial distinguishes the actual 124-item threshold from the convenient 128-item test. All 50 earlier chapter definitions compare unchanged. Practical in-game circuit verification is pending.

The user accepted the first six Redstone lessons. New batch: two compact four-quest chapters following that optional branch. Verify both languages, automatic two-Repeater/material counts and the manual direction/range/delay exercises. Two Repeaters at minimum/maximum delay should show the stated nominal timing while the world runs; no free-running clock is built. For the Comparator use one ordinary single Chest, comparison mode and no side inputs: empty, one Cobblestone, 64 Cobblestone, then 128 in two full stacks. The last state powers the Lamp beyond two Dust; removing all items turns it off. Check reward claiming, both trophies and restart persistence. TEST completion cannot verify the built circuit. Circuit illustrations remain pending. No player data was reset.

## Native Redstone foundations — first six lessons

Verification: 345 automated tests and native build passed, including compact-map arrow clearance, inventory thresholds, optional dependencies and trophy eligibility. Retained-pack validation/build passed; all earlier 49 native chapters compare unchanged. Target 26.1.2 recipe JSON and RedstoneWireEvaluator/BasePressurePlateBlock code ground the recipes, one-level-per-Dust decay and adjacent Lamp exercise. No in-game circuit verification is claimed.

The user accepted the task-group icon correction. New optional Redstone lessons unlock after the final course introduction, independently of the optional mob/Bastion and exploration cards. Check both languages, small-map layout, 19 versus 20 Dust, Lever/Lamp and Button/Plate possession, and separate manual circuit confirmations. Physically build the isolated Lever–three-Dust–Lamp row; extend to 16 Dust with the Lamp beyond it (off), then shorten to 15 Dust (on). Compare maintained Lever power, a Button pulse, and stepping on/off the Stone Pressure Plate with all other sources removed. Close the quest book to let the world run while observing. Claim both five-XP rewards once, inspect the trophy and restart. TEST completion does not verify a working circuit. Survival verification and authentic circuit illustrations remain pending; no player progress was reset.

## 2026-09-08 — Final retained optional chapters

Automated verification passed: 342 tests, native Mod build, retained-pack validation/build, exact coverage of all 258 source quest IDs and unchanged image dimensions. In-game acceptance remains pending.

New batch: seven mob-drop and seven Bastion quests. Review both languages, six image placements and two compact maps. The attack-indicator illustration/explanation now belongs to the Zombie card; the preceding overview points to it. Check Copper/ordinary Sword acceptance, Gold exclusion, two mixed cooked foods including Kelp, and 16 substantial foods excluding Kelp for Bastions. Verify 63/64 Cobblestone, 2/3 Bone Meal, early sticky item/advancement checks and separate worn-armour/retreat/loot/return confirmations. Genuine Bastion entry must not be confused with Fortress entry or a TEST completion. Rewards remain four Torches, two Bread, five XP and ten XP; claim once and restart to check persistence. These branches must remain optional while normal course recommendations continue. No progress or reward claims were reset.

All 258 retained quest IDs are represented after this batch. User-approved next sequence: Redstone foundations, creature/drop reference, then unusual mechanics. Curriculum coverage does not complete the separate multiplayer, migration and release-readiness checklist.

## 2026-09-08 — Native flight training and course completion

Milk preparation correction accepted by the user. New batch: five first-flight lessons, four maintenance/travel lessons and seven closing/reference cards. Review both languages, two unchanged half-width images and all three compact maps. Check 63/64 Cobblestone, 7/8 Ladders, ingredients before crafting, 2/3 Rockets and 15/16 Rockets for travel. Item completion must never auto-confirm safe Rocket components, the built practice course, flight/landing or equipment routines. Rocket safety remains a manual check as explicitly described in the lesson. Verify the five original XP payouts (5/5/10/5/10), three independent quest-book trophies, and progress/claims after restart. The six optional reference cards open together after And Now; none requires a structure visit or blocks another card. Development completion accelerates content review but cannot validate the actual practice routine.

After this migration only fourteen retained quests remain: seven optional mob-drop lessons and seven optional Bastion lessons. Native multiplayer parity and other release requirements remain separate from curriculum migration.

Verified: 337 native tests and native build pass. Retained-pack validation/build pass with 739 stable source object IDs and six pinned dependencies. Regression tests cover original IDs/rewards, material and Rocket thresholds, manual safety separation, independent final reading, recommendation priority, trophies and packaged image dimensions. In-game acceptance of this batch remains pending.

## 2026-09-08 — Pack milk before the second End journey

User found that milk was first required at the End City, too late for normal preparation. Added an automatic Milk Bucket objective to the earlier second-visit packing lesson in native and retained content, with bilingual separate-Bucket/cow instructions. Existing completed packing quests and claims remain untouched. Pending: on an unfinished packing quest, verify that Pearls and End Stone alone are insufficient, a filled Milk Bucket completes only its item objective, and manual packing confirmation is still separate. Inspect the revised End City fallback to the Overworld; no cow is implied on the main End island. No quest progress was reset.

## 2026-09-08 — Native End City three-chapter batch

The five outer-arrival lessons are user-accepted. This supersedes their pending note below. User requests multi-chapter deliveries until all retained quests are migrated.

Pending: review seventeen lessons across Chorus/search (4), Shulker/city (8) and End Ship/home return (5), in both languages and pane widths. Inspect seven half-width image placements and three independent quest-book trophies. Inventory examples: 7/8 Chorus Fruit, 0/1 Flower, 63/64 End Stone for island travel, 1/2 Shulker Shells, 127/128 and 255/256 End Stone for ship preparation/bridge work. Early possession must remain recorded without bypassing manual practice or prerequisites. Test genuine End City discovery separately from test completion, then confirm the marked safe route separately. Elytra possession does not prove return to the Overworld or storage at home. Claim each original XP reward once; restart and verify previous progress and claims are intact. The local TEST button can accelerate reading review but does not validate genuine automatic detection.

Source-ID audit after this batch leaves thirty retained quests: nine flight-training lessons, seven final exploration lessons, seven optional mob-drop lessons and seven optional Bastion lessons.

Verified: 332 native tests and native build pass; retained-pack validator/build pass (738 source object IDs, six pinned dependencies). Tests cover source IDs/rewards, material thresholds, sticky observation, genuine End City criterion, manual separation, recommendation priority, three trophies, and packaged image dimensions/translations. No in-game acceptance is claimed for this batch yet.

## 2026-09-08 — Native outer-island arrival

Dragon-victory batch accepted by the user. Pending: review five new lessons and both images in both languages and widths. Test three/four Pearls and 63/64 End Stone; packing must still require its manual check. Test genuine Gateway entry separately from ordinary End entry and development completion. Automatic entry must not certify access construction or secured arrival. Check continuation without the optional Dragon Egg, the new trophy, and progress after restart.

## 2026-09-08 — Native Dragon victory and return

The preceding End-arrival/Crystal batch and owner's arrival screenshot are user-accepted. New acceptance pending: review six lessons and four images in both languages and map widths; verify actual Dragon defeat and Egg pickup detection separately from development test completion; verify earlier advancements stick but cannot bypass manual combat practice. Confirm return without retrieving the optional Egg, inspect the chapter trophy after all six lessons, and reconnect to check preserved progress and reward claims. No extra quest payouts are added to the source's vanilla victory rewards.

## 2026-09-08 — Owner's End arrival capture

Replaced the top-down arrival diagram with the approved unchanged capture. Pending in-game check: open Reach Safe End Stone, inspect half-width presentation and aspect ratio, and read the updated route-width/railing guidance. Progress, objectives and rewards are unchanged.

## 2026-09-08 — Native End arrival and Crystal removal

Previous Stronghold/Portal/End-preparation batch and owner screenshots accepted by the user. New in-game acceptance is pending: review all nine lessons and five images in both languages and pane widths; verify early End-entry detection, 63 versus 64 End Stone, sticky inventory completion, separate cage safety confirmation, optional roof bypass, two trophies and original one-time Cobblestone/Arrow/XP rewards. Restart and verify existing progress and claims. Use development completion for content review; test automatic detection separately without bypassing its objectives.

## 2026-09-08 — Owner's Iron Door and End Portal captures

- Marked the real Stone Button to the right of the Iron Door using an orange frame/arrow; no scene repainting or brightness changes. Original captures are unchanged.
- Replaced the Portal Room and final-activation diagrams with the owner's missing-Eye/Spawner and active/no-Spawner captures. The separate frame-state comparison remains.
- Native and retained languages now describe the real scenes; the active-portal caption explicitly distinguishes Spawner removal from the still-open side Lava.
- Pending in-game: inspect the three images at half width, button visibility, captions and scrolling. No quest/task/reward identifiers or player data changed.
- Verified: 312 native tests and native build pass; retained-pack validator/build pass with the new asset references. Image packaging checks confirm actual dimensions and bilingual captions. Both portal copies have the same SHA-256 as their originals; the Door overlay was visually checked against the real Button.

## 2026-09-08 — Stronghold interior and End preparation batch

- User accepted all sixteen Fire Resistance/Eye Supplies/Stronghold Search lessons and requested another multi-quest batch. Their previous pending acceptance notes are superseded.
- Pending: inspect sixteen new lessons in three compact maps (5/4/7), both languages, six half-width images and three independent trophies.
- Continue through portal-room discovery while the optional Library is unfinished. Portal Room opens after hazard awareness; End Preparation follows the separately confirmed home return.
- Test one versus two Stone Buttons, 31 versus 32 Arrows, 63 versus 64 Cobblestone and 15 versus 16 supported cooked foods. Iron-or-better tools must match the existing exact tags. Early inventory completion must not bypass prerequisites, current armour checks or manual exercises.
- Check the marked return, secured Spawner/Lava, twelve-frame count, home storage and the new Overworld Bed/reserve Chest separately. Old sleep advancement progress must not auto-confirm this new return point.
- Claim the Golden Apple/ten XP, 32 Arrows/five XP, four Bread and final ten XP once at their respective milestones. Restart and check persistence.
- Final lesson activates the portal from a safe outside position without entering; End arrival is deliberately a later batch. No player data, settings or reward journals are reset.
- Automated verification passed: 312 tests, native build and retained-pack build/validation. Checks cover source IDs and quantities, exact item-tag alternatives, early/sticky inventory completion, manual safety gates, optional navigation, separate trophies, image packaging/aspect ratios and arrow clearance in both map widths. All prior 34 chapter definitions are unchanged; all sixteen new source descriptions are preserved in both languages apart from extracted image directives.

## 2026-09-08 — Fire Resistance and Stronghold search batch

- User accepted Brewing Foundations and requested larger content batches. Prior brewing acceptance notes are superseded.
- Pending: review all 16 new lessons, both languages, five images, compact graphs and three trophies together.
- Fire Resistance: one Magma Cream counts automatically; brewing/name/duration/storage checks remain manual. Verify the five-XP reward.
- Eye Supplies: test 15 versus 16 Pearls, Powder and Eyes; both material branches must precede Eye completion. Keep observed materials long enough before crafting. Confirm physical storage separately; claim two Eyes and ten XP once.
- Search: continue even with Fire Resistance, barter/resources and Bastions unfinished. Check first Eye throw, route narrowing and marked stair start separately. Prior/new Stronghold advancement must not confirm the secured entrance/surface return. Claim sixteen Torches and ten XP once after that exercise.
- Restart and inspect preserved prior progress and reward state. No saves, settings or claim journals were reset.
- Automated verification passed: 304 tests and native build, retained-pack validation, bilingual key/image checks, preserved historical IDs, and arrow clearance in both map widths. The Eye material fork is vertical to retain arrow spacing in reading mode.

## 2026-09-08 — Brewing foundations (native)

- User accepted the Fortress-return slice and the two improved screenshots; earlier pending notes for those deliveries are superseded.
- Pending: inspect seven compact lessons, both languages, five half-width illustrations and the thirty-first trophy.
- Check one/two Blaze Powder, zero/one Brewing Stand and two/three empty Glass Bottles. Keep the Stand and bottles in inventory long enough for observation before placing/filling them.
- With no previous brewing advancement, take a finished potion out to trigger detection, then reinsert it for Strength. Existing vanilla progress must count without bypassing prerequisite/manual exercises.
- Confirm water placement and drinking/inspecting Strength separately; claim three Redstone and ten XP once, restart and check persistence. Optional Nether branches remain optional; no saves were reset.

## 2026-09-08 — Nether Wart and Blaze Spawner screenshots

- Replaced the Spawner schematic and added the first Nether Wart garden image using the owner's new in-game captures. Applied only deterministic shadow brightening; source captures and player data are unchanged.
- Both languages reference the full 2277 × 1353 assets; retained pack dimensions are 300 × 178. Native images stay half-width.
- Pending: inspect brightness, captions, aspect ratio and scrolling in both affected quests after restarting the client.

## 2026-09-08 — Fortress resources and return (native)

- User accepted the previous Fortress slice, approved screenshot and five-XP discovery reward. Earlier pending notes for those deliveries are superseded.
- Pending: inspect the three-quest chapter in both languages and widths, including its illustration and thirtieth trophy.
- Test one versus two Blaze Rods, three versus four Nether Wart/Soul Sand, and early item detection without bypassing prerequisites. Item possession must not confirm the separate safe return and planted farm.
- Claim 16 Cobblestone and ten XP once after returning; verify restart persistence and continuation despite unfinished optional Nether branches. No world data was reset.

## 2026-09-08 — Fortress discovery reward

- User accepted the brightened screenshot and requested a small reward for discovery.
- Added five XP points to the native discovery quest. Pending: claim from a newly or previously completed quest, verify five points and no repeat payout. No world data changed.

## 2026-09-08 — Approved Fortress screenshot

- User selected the brightened original screenshot without added creatures, replacing the schematic Fortress illustration.
- Original capture: `2026-09-08_15.13.05.png`, 2277 × 1353. Original screenshot and world data remain untouched.
- Pending in-game: inspect the half-width image and its caption in the Fortress search lesson; check scrolling and undistorted proportions.

## 2026-09-08 — Fortress preparation (native)

- Optional Nether resources accepted by the user. No world data changed.
- New acceptance pending: inspect both languages, original Fortress/enemy artwork, and entry directly from the activity introduction without optional branches.
- Check exact equipment thresholds (64 Cobblestone, 16 eligible foods, Iron-or-better Sword/Pickaxe and Shield); holding equipment must not complete physical preparation, Fortress search, retreat cover or enemy-safety checks. Check the twenty-ninth trophy and saved state after restart.

## 2026-09-08 — First Nether resources (native)

- New slice awaiting user acceptance; the retrospective acceptance below applies only to earlier deliveries.
- Check independent access after the activity introduction without Piglin barter. Fifteen Netherrack/three Quartz are insufficient; sixteen/four count automatically even while locked.
- Confirm safe planning and return separately, inspect both languages, claim eight Cobblestone and five XP once and inspect the twenty-eighth trophy. No player data changed.

## 2026-09-08 — Retrospective user acceptance

The user explicitly confirms that all prior "correct/good, continue" replies mean the delivered changes were tested successfully. This supersedes the historical pending single-player acceptance notes below through the Piglin-barter slice. The corresponding native roadmap checks are now complete. Separate multiplayer/player-isolation and failure-injection coverage is not inferred from ordinary single-player acceptance. New slices still await confirmation.

## 2026-09-08 — Optional Piglin barter (native)

- Previous Nether-safety slice accepted by the user; no world data changed.
- Pending: inspect both languages and optional branch labelling. A Gold Ingot counts automatically, Gold Nuggets do not; possession does not confirm a safe place or actual barter.
- Pending: confirm one exchange regardless of the random result, claim one Gold Ingot and five XP once, inspect the twenty-seventh trophy, and verify restart persistence.

## 2026-09-08 — Nether safety (native)

- First Nether arrival slice accepted by the user; no player data changed.
- Pending in-game: review Piglin/Gold/Ghast guidance and illustrations in both languages.
- Pending in-game: five Cobblestone/two Torches must be insufficient, six/three must count automatically even before unlock. Materials alone must not complete marker placement or return.
- Pending in-game: confirm the marked route and return separately, claim four Torches/eight Cobblestone/two Bread/ten XP once at their respective quests, inspect the twenty-sixth trophy and restart to verify persistence.

## 2026-09-08 — First Nether arrival (native)

- Previous equipment slice accepted by the user; no player data changed.
- Pending in-game: automatic Nether entry plus separate stay-near-portal confirmation. Prior entry must count without bypassing the prerequisite gate.
- Pending in-game: inspect both languages and the route illustration, confirm observation/shelter/return separately, claim original rewards once and inspect the twenty-fifth trophy. Reopen/restart to verify persistence.

## Native 0.13.0 alpha

### 2026-09-08 — Nether equipment and pre-entry checks

- Added six source lessons after the unlit frame, through Gold, worn Helmet, supplies, controlled ignition, Overworld safety and the departure check. User reported the preceding frame slice correct.
- Pending in-game: four/five Gold Ingots, Helmet possession versus manual wearing check, accepted Iron-or-better swords/Pickaxes, 32 Cobblestone, 16 Torches and eight supported foods. Dried Kelp must not count for this trip.
- Check three original pictures in both languages, sticky objectives and the explicit physical recheck before entry. Ignition, securing the area and the final check remain manual; do not enter yet.
- Check original Coal/Gold/Bread/Torch/XP rewards and twenty-fourth trophy. Existing progress and reward records were not changed.

### 2026-09-08 — Nether portal preparation

- Added five original lessons through the unlit portal frame. The first enchantment opens the chapter; unfinished Bookshelves must not block it.
- Pending in-game: Flint/Flint and Steel detection, nine versus ten normal Obsidian, rejection of Crying Obsidian, manual site/frame checks and both original illustrations in both languages.
- Confirm the Iron refund, five-XP material reward, eight Torches plus five XP after frame completion, and the preparation-only trophy. Do not light or enter the portal yet; equipment and safe-entry lessons follow separately.
- No player data changed. User reported the preceding Library slice correct.

### 2026-09-08 — Library expansion

- Added three original Bookshelf lessons after the first enchantment, with recipe and top-down layout illustrations and a cosmetic twenty-second trophy.
- Pending in-game: one shelf completes the first inventory objective; fourteen do not complete the fifteen-shelf objective; fifteen do. Placing the shelves afterwards must not erase that completion.
- The final confirmation remains manual: inspect the spacing and see a level-30 offer, without needing to buy it. Review both pictures/languages, claim three Books and later three Lapis plus ten XP, and reopen/restart.
- No player data changed. The user reported the preceding enchanting slice was good; detailed survival edge cases remain part of full acceptance testing.

### 2026-09-08 — Enchanting foundations

- Added six source lessons after the Diamond Pickaxe, with parallel Obsidian/Sugar Cane/Lapis branches and three unchanged half-width illustrations.
- Pending in-game: four Obsidian, three Sugar Cane, one Book, three Lapis and one Enchanting Table; prior/new enchanting advancement detection must not bypass prerequisite quests.
- Check Paper/Book, Table recipe and interface images in both languages and reading widths; check original Leather/XP/Lapis rewards, twenty-first trophy and opening recommendation despite unfinished optional excursions.
- No world, progress, reward journal or key binding was changed for this slice.

### 2026-09-08 — Reward journal recovery follow-up

- User reported successful behaviour after the backed-up recovery and separate completion-marker fix. Full-inventory and multiplayer edge cases remain part of the broader acceptance checklist.

### Bulk reward collection

- Pending in-game: gold Chest appears at the top right only with eligible rewards, including archived chapters; disappears after collection; remains for inventory-full batches.
- Test mixed item/XP rewards, repeated clicks, restart, a full inventory, preview mode and another player. Locked/pending/claimed rewards must not pay again. Check header spacing at small GUI widths and XP sound.
- No player progress was reset. Server collection reuses the existing durable per-quest payout.

### 2026-09-08 — Your First Diamonds

- Added six original lessons after storage and Iron Essentials. Optional Village work is not required.
- Pending in-game: two Iron-or-better Pickaxes, Water Bucket, Shield, 32 Torches, eight cooked foods, 16 Cobbled Deepslate, three Diamonds, manual safety checks and Diamond Pickaxe.
- Check original four Bread, eight Torches, two five-XP rewards, Golden Apple and twentieth trophy in both languages. No player data changed.

### 2026-09-08 — Trading and returning home

- Added six original lessons after respectful Village behaviour, with the unchanged half-width trading illustration.
- Pending in-game: chapter reveal, profession/offer text, automatic first trade (including prior vanilla progress), manual return checks, four Torches plus five XP, three Bread plus five XP, and nineteenth trophy in both languages.
- No saves, reward journals or main-path gates were changed.

### 2026-09-08 — Discover a Village

- Added four optional source lessons after the Boat return, with original IDs, five XP and the unchanged Village overview diagram.
- Pending in-game: chapter reveal after returning to the same shore, sequential practical confirmations, both languages, half-width image, reward and eighteenth trophy.
- No player progress or reward journal was changed. Trading remains the next slice.

### 2026-09-08 — Optional excursions and Boats

- Added six source lessons, two original half-width diagrams and a cosmetic trophy. No player data was changed.
- Pending in-game: complete Compass binding/return and protection recap to reveal the chapter; check Shield, eight cooked foods, 16 Torches, 32 Cobblestone, ordinary Boat/Raft detection, manual preparation/travel, five XP and both languages.
- The chapter does not gate the main course. Village exploration remains separate.

### 2026-09-08 — Storage and organisation

- Added five source lessons after animal care, preserving identifiers and rewards. The Sign description now matches mixed-wood tag counting.
- Pending in-game: two versus three Chests/Signs, separate practical confirmations, three Item Frames, one Bundle, five XP and the sixteenth trophy in both languages.
- No world data was changed; optional composting does not gate the chapter.

### 2026-09-08 — Your First Animals

- Added six bilingual source lessons after Bread, independent of optional composting. The original Fence/Gate diagram remains half-width.
- Pending in-game: 14 versus 15 mixed wooden Fences plus one Gate, two feed items, practical pen/luring checks, automatic breeding including prior vanilla progress, four Torches, one Lead, five XP and the fifteenth trophy.
- Existing player progress is unchanged. Storage remains the next slice.

### 2026-09-08 — Optional composting

- Added three source lessons with stable IDs; the branch opens after Wheat planting and does not gate the course.
- Pending in-game: six versus seven mixed wooden Slabs, automatic Composter possession, exactly 32 Seeds, manual Bone Meal collection, five XP and the fourteenth trophy in both languages.
- Existing saves and reward journals were not changed.

### 2026-09-08 — First Wheat field

- Added six original field-to-Bread lessons with three original half-width illustrations; optional composting and animal care are deferred.
- Pending in-game: two versus three Seeds/Wheat, automatic Wheat planting (including prior vanilla progress), manual Farmland confirmation, three Bone Meal and five-XP claims, all images and thirteenth trophy.
- The field opens from the food reserve, not Iron completion. Instructions include nearby natural Water for players without a Bucket. No saves or reward journals were changed.

### 2026-09-08 — Optional maps

- Added the two source map lessons as an explicitly optional chapter, without making them a prerequisite for the course.
- Pending in-game: an Empty Map alone must not satisfy Filled Map possession; inspect both half-width diagrams; obtain a Cartography Table and confirm the separate expansion exercise; verify the twelfth trophy.
- The inventory objective does not prove where a map was opened. The text distinguishes possession detection from the recommended at-home exercise. No progress or reward state was reset.

### 2026-09-08 — Lodestone slice

- Added four source lessons in a separate compact chapter after Compass basics, retaining item counts, task IDs, binding criterion and five-XP reward.
- Pending in-game: Stonecutter, seven versus eight Chiseled Stone Bricks, Lodestone recipe, automatic Compass binding and separate manual return; inspect both half-width images and the eleventh trophy in both languages.
- Prior binding should count without repeating it, while the return exercise and reward prerequisite gate remain enforced. No world state was reset.

### 2026-09-08 — Smaller images accepted; Finding Home basics

- User confirmed the half-width, centred image presentation and asked to continue with Minecraft closed.
- Added five orientation lessons; Lodestone and optional mapping are deferred to separate compact maps.
- Pending in-game: safe-return chapter reveal, all five manual steps, exactly five XP and two Compasses after the route test, tenth trophy and both languages. No world progress or reward journal was modified.

### 2026-09-08 — Iron Essentials migration

- Continued with six source lessons, preserving the early first-Iron prerequisite, original IDs, four automatic possession tasks, two manual exercises and five-XP reward.
- Added the original Iron Pickaxe, Bucket and renewable-water illustrations plus the ninth quest-book trophy.
- Pending in-game: five versus six Ingots, parallel Pickaxe/Bucket route, Water Bucket detection, both water-source layouts, final two-branch gate, images and languages. The TEST completion shortcut remains available; no world state was reset.

### 2026-09-08 — Protection accepted; Safe Underground migration

- User accepted the previous Shield and Armour slice and asked to continue. Full parity edge cases remain separate from this general acceptance.
- Added six underground lessons preserving source IDs and rewards, plus the original staircase and Torch-route illustrations.
- Pending in-game: inspect the six-node map in both widths and languages; check two mixed suitable Pickaxes, 16 Torches and four mixed cooked foods; confirm manual safety exercises remain separate; claim ten XP once and inspect the eighth trophy.
- No world progress or reward claims were reset. The development completion button remains available for faster review.

### 2026-09-08 — Shield and Armour migration

- Added five source lessons as a compact seventh chapter, retaining task/reward IDs and both original illustrations.
- Armour accepts either completed Iron Ingot or Copper Ingot; Shield remains Iron-only. The shared gate is used by server completion and reward claims as well as the browser.
- In-game review pending: inspect both images and languages, check Copper-only armour access versus Iron-only Shield access, complete both practice branches and claim the final single Iron Ingot. Confirm the seventh quest-book trophy and chapter celebration.
- The development completion shortcut remains available; it satisfies one alternative path instead of completing both. It changes saved progress permanently but never claims rewards automatically.

### 2026-09-08 — Ores accepted and opt-in testing shortcut

- The user accepted the ore quests and requested a faster way to complete quests without meeting objectives.
- The IntelliJ/Gradle development client now opts into a local-owner-only test completion utility. It completes the selected quest and prerequisite closure; optional unrelated branches and reward claim records remain unchanged. No world is modified until the user activates it.
- In-game check: use TEST: Complete on a locked quest, inspect completed prerequisites, claim rewards separately, and verify ordinary completion still works. Existing test progress is deliberately persistent; this is not a temporary preview.

### 2026-09-08 — Food accepted; first ores migration

- The user reported the native food chapter as correct and requested continued work.
- Next slice: a separate six-quest First Ores chapter after the food reserve, including retreat planning, Stone Pickaxe and parallel Iron/Copper routes. Preserve existing identifiers, source objectives and the original Stone Pickaxe illustration; add a quest-book-only milestone.
- Remaining in-game checks for this slice: food gate, manual retreat check, automatic item recognition (including pre-owned items), two visible branches, recipe illustration and sixth trophy. No world progress reset.

### 2026-09-08 — Food chapter migration

- The user accepted the corrected physical-key welcome wording and requested continued roadmap work.
- Next native slice: six food/hunger lessons after the safe morning, preserving source quest/task/reward IDs and keeping the eating exercise non-gating. Original raw/cooked food alternatives become native item tags; no FTB filter dependency is added.
- In-game acceptance: chapter reveal, optional eating detection, raw food alternatives, three versus four mixed cooked foods, manual safety checks, three original small rewards, both unchanged images, fifth quest-book trophy, and restart persistence. No existing world progress is reset.

### 2026-09-08 — Welcome accepted, physical key wording corrected

- The user confirmed the welcome behaviour as correct. The rendered shortcut was blank, so both welcome translations now describe the physical key below ESC as the default binding and retain the pause-menu alternative. No bindings or player progress were changed.

### 2026-09-08 — Follow-up acceptance and first-join introduction

- The user confirmed all four follow-up fixes as correct in game: celebration caption, main-path opening selection, automatic completion notifications and pre-unlock inventory objectives with locked rewards.
- Next implementation: a one-time, dismissible first-join introduction. Acceptance remains open for a fresh world, Open/Later/Escape, rejoin, another player, remapped controls and both languages. Established quest progress must remain untouched and must not trigger a retroactive introduction.

### 2026-09-08 — User acceptance and four follow-up fixes

- The user reported all other requested checks as correct, including the pause-menu entry and its centred label. This is user-reported acceptance, not an automated or independently observed playtest.
- Four remaining cases: chapter celebration must not show an unexpanded `%s`; opening the book must recommend the next available main-path quest; automatic quest completion must notify the player and mention claimable rewards; automatic objectives must count before prerequisites while quest completion and rewards remain locked.
- Re-test these four cases after the fixes, including one restart and simultaneous vanilla notifications. Existing player progress was not reset.

### 2026-09-08 — Trophy collection and chapter navigation

- The user accepted the title-bar trophy collection and requested continued roadmap work. This confirms the general presentation, not every language/reconnect/small-window acceptance case.
- Progressive chapter navigation implemented next: Welcome initially, then individual prerequisite-based reveals, retaining saved progress and optional movement branches.
- Fresh-world reveals, search restrictions and existing-save navigation remain to be reviewed in game; no player progress was reset.

## 0.9.0

### 2026-09-05 — Beginner first-night safety refinement

- A laptop beginner playtest exposed two missing assumptions: the learner used a Pickaxe on Dirt because the Shovel had never been taught, and the first night arrived before the original shelter sequence could be completed.
- Added a Wooden Shovel immediately after the Crafting Table, with an exact Minecraft 26.1.2 recipe guide and a direct comparison of soil, stone, and wood tools.
- Split shelter building into an early emergency branch and a later permanent upgrade. The emergency shelter may be a small dirt or hillside room, but its two-block-high entrance must be sealed completely before night.
- Moved the three White Wool and Bed lesson behind that emergency shelter. The learner can continue the parallel Pickaxe route during daylight and return to the Bed before dark.
- The later upgrade requires the already-taught Chest and Torches, protected storage, a lit interior and entrance, and either a Wooden Door or solid temporary entrance blocks.
- Generated both new guides exclusively from exact textures in the official, SHA-1-verified Minecraft 26.1.2 client JAR. The revised map flow, text fit, and completion behaviour still require a fresh survival-mode test.

### 2026-09-05 — Overworld expansion planning and hostile-mob overview

- Paused further outer-End expansion so common Overworld systems can be taught before the learner is likely to encounter them without context.
- Added a separate Overworld roadmap for safe excursions, Boats, Villages, Villager professions and trading, Village safety, and a deliberate return home. The route is planned as an independent chapter and will not gate Nether or End progress.
- Deferred Raids, Zombie Villager curing, Villager breeding, trading optimisation, and specialised structures into optional later branches.
- Added a language-neutral four-panel overview to the existing mob-drop introduction. Zombie, Skeleton, Spider, and Creeper use exact Minecraft 26.1.2 entity textures and are paired with exact textures for Rotten Flesh, Bone, String, and Gunpowder.
- The new overview still requires an in-game size and active-resource-pack check in German and English.

### 2026-09-05 — Overworld excursion and Boat foundation

- Added a fifteenth bilingual chapter, `What to Do in the Overworld?`, between the optional mob-drop material and the Nether chapters in the chapter list.
- The chapter unlocks only after both the personal Lodestone Compass test and the basic protection recap, but remains completely independent from Nether, Stronghold, and End progression.
- Added a deliberate day-trip checklist covering the Bed left at home, recorded coordinates, suitable tools, Iron-or-better Armour, Shield, eight cooked foods, 16 Torches, 32 Cobblestone, and eight free inventory slots.
- Added a short daylight walking rehearsal before water travel, including thunderstorms, changing landmarks, intermediate coordinates, and an explicit early-return threshold.
- Added the exact five-Plank Boat recipe, support for every ordinary Boat variant plus the Bamboo Raft, all basic controls, safe dismounting, retrieval, and a 100-block same-shore practice trip.
- Added an exact-texture recipe guide with straight three-dimensional Planks and a language-neutral top-down controls guide. Both images and the full chapter still require in-game review in German and English.
- Beginner review moved `Safe Mob Drops` and `What to Do in the Overworld?` ahead of `Mining Deeper`. Practical exploration now appears before Diamonds and Enchanting in the chapter list without changing any quest dependency.

## 0.1.0

### 2026-09-02 — CurseForge 0.1.1 smoke test

- Minecraft reached the title screen and created a new survival world.
- FTB Quests opened and displayed the complete dependency path.
- German quest descriptions and automatic item task labels loaded.
- The chapter appeared as `Unbenannt`, and objects whose IDs started with `8` through `F` fell back to generic titles such as `Haken`.
- Root cause: those IDs exceed the positive signed Java `long` range used by FTB Quests. Version 0.1.2 migrates them and adds a validator regression check.

Status: startup and world creation passed; 0.1.2 translation retest pending.

### 2026-09-02 — In-place update to 0.1.2

- Updated the existing CurseForge profile with `tools/update-instance.ps1`.
- Backed up the previous quest definitions before replacement.
- Verified all copied quest files by SHA-256.
- Confirmed the updater did not target the existing world, `options.txt`, screenshots, or resource packs.
- In-game screenshots confirm that the chapter, quest, and task titles now resolve correctly.

### 2026-09-02 — Beginner feedback for 0.1.3

- The overall quest path is understandable, but the first interaction and crafting steps still required spoken help.
- Added an explicit controls lesson and illustrated instructions for the default right hand, left-click breaking, automatic pickup, log placement in the 2 × 2 grid, and moving the result into the inventory.
- A task labelled for eight Cobblestone completed with one item. The root cause is an item-stack `count` value that FTB Quests ignores for task completion; 0.1.3 moves quantities to the task-level field for Cobblestone and Torches and adds regression validation.
- In-game image rendering and the corrected 1–7/8 Cobblestone threshold remain to be tested.

### 2026-09-02 — Image loading diagnosis for 0.1.4

- Both guide images rendered as the pink-and-black missing-texture pattern.
- `latest.log` confirmed that Minecraft removed `file/first_torch_guides` as incompatible before resource loading.
- The installed client's `version.json` reports resource format 84.0. Modern packs require `min_format` and `max_format` version pairs; the guide pack incorrectly used only the legacy `pack_format` field.
- Updated the metadata to `[84, 0]`, added a regression check, and expanded the illustrated sequence through Crafting Table placement, Wooden Pickaxe, Furnace, and Charcoal.

### 2026-09-03 — First-steps Stone Axe refinement

- Inserted a Stone Axe lesson between the first eight Cobblestone and the Furnace so a beginner learns the efficient tool for logs and common wooden blocks before hand-breaking becomes a habit.
- Requires both the Stone Axe item and a manual confirmation after breaking one reachable log. The lesson distinguishes Axe and Pickaxe use, explains durability, and warns that right-clicking many logs strips their bark instead of breaking them.
- Added an exact Minecraft 26.1.2 recipe guide with straight three-dimensional Cobblestone models and original Stick and Stone Axe item textures.
- Rewards the three consumed Cobblestone after the practical check, preserving all eight previously collected blocks for the following Furnace without skipping the Axe lesson.

### 2026-09-02 — Flexible planks and CurseForge version diagnosis for 0.1.5

- The first wood objective accepted only Oak Planks. It now requires four items matching `#minecraft:planks`, so all vanilla plank types and mixed stacks count.
- Added the official FTB Filter System and FTB XMod Compat dependencies required by the tag filter.
- Removed the final quest's premature suggestion to carry food; the text now warns against a long trip and previews food as the next chapter.
- Techopolis 3 displays its pack release because its instance is linked to a published CurseForge project through `installedModpack.installedFile`. First Torch is currently a local manifest import, so CurseForge leaves `installedModpack` empty and falls back to showing the Minecraft version. Native pack-version display therefore belongs to the CurseForge publication step rather than local profile metadata.

### 2026-09-02 — Gentle rewards for 0.1.6

- Added a reward-claim explanation to the welcome quest.
- Added two Apples after learning the controls and two after crafting eight Torches. These are small hunger buffers and do not replace a taught recipe.
- Added three Bread after the safe morning as a bridge into the food chapter and one Lantern as a visible chapter trophy.
- Kept manual reward claiming enabled so the interaction is learned explicitly. In-game display and single-claim behaviour remain to be tested.

### 2026-09-02 — Reward quantity diagnosis for 0.1.7

- In-game claims granted one Apple and one Bread despite larger configured item stacks.
- Inspection of FTB Quests 26.1.2.7's `ItemReward` implementation confirmed that reward quantity is read from the reward-level `count` field. The item stack itself is normalised to one.
- Moved all multi-item quantities to the supported field and added a regression check for the ignored form.
- Existing claimed-reward state is intentionally preserved; the corrected quantities require an unclaimed reward or a fresh test world for verification.
- Reset only the backed-up FTB Quests progress for the test world and repeated the claims. In-game verification confirmed both 2-Apple rewards, 3 Bread, and 1 Lantern are granted correctly and only once.
- Follow-up inspection confirmed that the safe-shelter reward also uses the supported reward-level count of three. Because FTB keeps the embedded icon stack at one, the Wool reward now has an explicit bilingual `3 ×` title so the intended quantity remains unambiguous in its tooltip without changing payout behaviour.

Record each test with date, Minecraft/NeoForge versions, language, fresh or existing world, confusing moments, technical errors, and the resulting change.

## 0.2.0

### 2026-09-02 — Food foundation implementation

- Added a six-quest path covering the Hunger bar, flexible local food sources, Furnace cooking, health regeneration, risky foods, and a four-item cooked reserve.
- Added illustrated guides for recognising low Hunger and for cooking raw Beef with Charcoal.
- The first lesson explicitly handles full Hunger and Peaceful difficulty so the manual task cannot block a learner who is unable to eat.
- Static validation and archive construction passed. Chapter unlocking, smart-filter acceptance, image rendering, and both-language layout remain to be tested in game.
- Beginner review found that the initial Hunger illustration showed two inventory-like rows, making the selected Bread appear to come from an ambiguous inventory area. The final illustration now shows exactly one nine-slot hotbar in each panel, with Bread selected in its first slot.
- Follow-up review replaced the manual eating checkmark with automatic detection of the vanilla `minecraft:husbandry/root` `consumed_item` criterion. The optional eating quest no longer gates the food-source path.
- Removed the unexplained Creative-mode reference, documented concrete Furnace fuel durations in the first chapter, and added an Apple, Charcoal, and Cookie as small milestone rewards.

### 2026-09-02 — Protection foundation implementation

- Added an eight-quest continuation covering retreat, the Stone Pickaxe, first Iron, Shield crafting, armour slots, Shield use, and avoiding unnecessary fights.
- Added illustrated guides for the Stone Pickaxe recipe and the Shield recipe/offhand flow.
- Item tasks verify the Stone Pickaxe, Raw Iron, Iron Ingot, and Shield automatically; understanding and physical practice remain explicit checkmarks.
- Added one Iron Ingot after the protection recap as a partial replacement for the Shield material and a bridge into later equipment.
- Static validation and archive construction passed. Image rendering, both-language layout, and the full in-game path remain to be checked.
- Beginner review replaced the fixed Raw Iron quest icon with the live Iron Ore item model, so installed resource packs change its appearance automatically.
- Corrected the Shield illustration and text to show left-click dragging into the offhand slot; the following Shield-use quest no longer repeats equipping.
- Inserted an Armour crafting lesson before the armour-slot lesson, covering the four shared recipe shapes, common materials, and the Chainmail/Netherite exceptions.
- Added two deliberately small rewards of 5 XP points: one in the welcome quest and one after the Armour recipe explanation.
- Replaced the symbolic Armour patterns with four focused 3 × 3 recipe images using Copper Ingots as the accessible example material.
- Follow-up review added a grey crafting arrow and the matching Copper Armour result beside every 3 × 3 recipe while keeping the ingredient layouts unchanged.
- In-game review showed that four full-width images created inconsistent colours, sizes, and excessive scrolling. Replaced them with one uniform 2 × 2 overview displayed at 300 × 169 pixels.

### 2026-09-02 — Safe mining foundation implementation

- Added six lessons covering preparation, staircase mining, route lighting, falling blocks, Water/Lava, and a deliberate safe return.
- The preparation task accepts mixed Stone, Copper, Iron, Diamond, or Netherite Pickaxes and requires two total, plus 16 Torches and four cooked foods.
- Added compact illustrations contrasting straight-down digging with a walkable staircase and showing the right-going-in, left-going-home Torch rule.
- Added 10 XP points after the first deliberate safe return. Static validation passed; archive construction, image rendering, task acceptance, and both-language layout remain to be checked.
- Added Raw Copper and Copper Ingot lessons parallel to the Iron branch. The Armour lesson uses FTB Quests' `one_completed` dependency rule and unlocks after either ingot; the Shield remains Iron-only.

### 2026-09-02 — Small-screen layout and storage prerequisite

- In-game review on a wide monitor showed that the single 45-unit horizontal path would require excessive scrolling on smaller displays.
- Wrapped Becoming Independent into three connected rows for food, protection, and safe mining without changing any existing object IDs or progress state. First Steps now also wraps after the Furnace instead of growing into another long line.
- The safe-return lesson referred to storing finds in a Chest before Chest use had been introduced. Added a short illustrated Chest lesson between the Furnace and Torches, including crafting, placement, opening, transferring an item, retrieving it, and the warning that breaking a filled Chest drops its contents.
- Reviewed 20 user-provided Survival Steps screenshots as a product reference. Useful ideas were the separation into topic-sized sections and treating a home/storage routine as an early skill; First Torch adopts those ideas in its own slower, illustrated teaching style.
- Deliberately did not copy Survival Steps text, assets, whole-level XP rewards, or large material refunds. The Chest lesson grants five XP points, keeping First Torch's rewards small and non-skipping.

### 2026-09-02 — Finding home foundation implementation

- Added a dedicated four-quest chapter for a visible landmark, reading X/Y/Z, recording the player's real home coordinates, and testing a short daylight return route.
- Kept the new chapter separate from the mining map so adding content does not undo the small-screen layout improvement.
- The route lesson uses the landmark first and coordinates as a backup. It explicitly warns that coordinates do not identify a safe path around hazards.
- Added five XP points after the practice return. Static validation, debug-screen wording, chapter unlocking, and the complete route still require verification.
- Added one Compass to the route-test reward and a short follow-up lesson that distinguishes world spawn from the player's Bed respawn point. The text keeps landmarks and recorded coordinates as the reliable home method when the shelter is elsewhere.
- Extended that lesson into a compact Lodestone path: craft a Stonecutter, cut eight Stone directly into Chiseled Stone Bricks, craft the 26.1.2 Lodestone with one Iron Ingot, bind the rewarded Compass at home, and test it safely. Two compact recipe images cover the Stonecutter and Lodestone recipes.

### 2026-09-02 — Bed onboarding refinement

- Replaced the optional one-paragraph Bed note with an explicit crafting, placement, sleeping, and respawn-point lesson.
- Added three White Wool as the shelter reward. This removes the luck of finding Sheep while preserving the Vanilla crafting step and the need to supply Wooden Planks.
- Added a compact White Bed recipe guide and made the Bed lesson follow the shelter lesson so the required reward is available first.
- Clarified in the roadmap that a normal Compass points to world spawn rather than the player's Bed; it must not be presented as a home finder.

### 2026-09-02 — Iron essentials foundation implementation

- Added a dedicated five-quest chapter that collects six Iron Ingots, branches into an Iron Pickaxe and Bucket, teaches filling and retrieving Water, and rejoins in a short safety recap.
- Verified the 26.1.2 recipes and mining tiers directly from the installed Vanilla client JAR. Copper and Stone tools are marked incorrect for Iron-tier blocks; Iron is suitable for Diamond, Gold, Redstone, and Emerald ores but not Diamond-tier blocks.
- Beginner review moved the recipes out of the six-Ingot preparation quest. Each recipe now has its own 300 × 169 image in the matching Iron Pickaxe or Bucket quest.
- The six-Ingot quest now unlocks immediately after the first Iron Ingot. Its in-map link uses a diamond shape instead of a normal round quest outline, and the opened quest tells the learner to select Iron Essentials from the chapter list on the left.
- Removed repeated slot-by-slot Pickaxe instructions; the text now points back to the already-learned recipe shape and explains only the material change.
- Water practice explicitly uses a source block, takes place outdoors away from storage, and does not encourage dangerous falling or Lava tricks.
- Added five XP points after the recap. Task thresholds, image rendering, both-language layout, and Water interaction remain to be tested in game.

### 2026-09-02 — 0.2.0 acceptance

- The user confirmed that each development increment had been tested in game and that the final quest text, layouts, images, quantities, rewards, navigation, and interactions behaved correctly.
- The complete 0.2.0 content scope is accepted. Automatic validation and the final release archive build remain mandatory before the release commit.

## 0.3.0

### 2026-09-02 — First Wheat farm implementation

- Added a compact six-quest chapter after the cooked-food reserve: collect three Wheat Seeds, craft a Stone Hoe, prepare watered Farmland, plant Wheat, wait for three mature harvests, and craft Bread.
- Planting uses the Vanilla `minecraft:husbandry/plant_seed` advancement's `wheat` criterion. Field preparation remains a manual observation because Vanilla exposes no equally precise learner-safe criterion for the intended small watered layout.
- Two compact recipe images cover the Stone Hoe and Bread. The path explicitly distinguishes short grass from the ground block, warns that Seed drops are random, explains mature golden Wheat, and establishes replanting plus spare-Seed storage as the core renewable-resource habit.
- Added a 9 × 9 hydration diagram rendered with the original Minecraft 26.1.2 moist-Farmland and tinted Water textures: one central Water source reaches four blocks in each horizontal direction, including diagonals, and can support up to 80 Farmland blocks. The lesson still asks for only three adjacent blocks initially.

### 2026-09-02 — Basic animal care implementation

- Added a six-quest continuation after the first home-grown Bread: craft pen parts, verify a closed pen, choose matching animals and food, lure a pair home, breed it, and protect the renewable breeding pair.
- The path accepts common wooden Fence and Fence Gate variants and supports Wheat, Wheat Seeds, Carrots, Potatoes, or Beetroot so the learner can use Cows, Sheep, Chickens, or Pigs found nearby.
- Successful breeding uses the Vanilla `minecraft:husbandry/breed_an_animal` advancement's `bred` criterion. Pen safety, animal placement, and the long-term care routine remain manual observations because they depend on the learner's actual build and choices.
- The final lesson establishes three non-destructive habits: keep two adults alive, store matching food, and expand the pen before it becomes crowded.
- Kept the established 1672 × 941 guide style for the Stone Hoe, Bread, Fence, and Fence Gate recipes. Minecraft content uses the targeted game's textures and models; only neutral instructional frames, arrows, counts, and measurements are constructed by First Torch.

### 2026-09-02 — Simple home storage implementation

- Added a five-quest continuation after animal care: prepare three Chests, build distinct storage areas, craft three Signs, label and sort each category, and practise a return-home routine.
- The categories remain deliberately broad—building materials, food and farming supplies, and equipment—so a beginner can remember them without maintaining a complex item taxonomy.
- Sign tasks accept every Vanilla wooden Sign through the `minecraft:signs` item tag. Physical placement and sorting remain manual observations because they depend on the learner's home layout.
- Reuses the already-taught Chest recipe and inventory controls, so no new guide image is needed.

### 2026-09-02 — Sustainable-supplies reward pass

- Removed the unescaped ampersand from the suggested German and English storage labels after it produced an FTB Quests formatting error.
- Added small, practical milestone rewards across the chapter: three Bone Meal, four Torches, one Lead, three Item Frames, and one Bundle.
- Kept the existing 5-XP rewards at the Wheat, animal-care, and storage milestones.
- Added an explicit bilingual Bone Meal usage lesson: right-click young Wheat, recognise the green particles, and expect a random rather than guaranteed full growth increase.
- Extended the lesson with the one-Bone-to-three-Bone-Meal inventory recipe, a Composter alternative, and a preview of the optional safe mob-drop path planned for 0.4.0.

### 2026-09-02 — Optional Composter sidequest implementation

- Added a three-quest branch from the first planted Wheat: collect seven wooden Slabs, craft a Composter, then fill and empty it successfully.
- The branch remains optional and does not gate the continuing Wheat, animal-care, or storage lessons.
- The wooden-Slab task accepts the shared Vanilla item tag. The Composter lesson supplies 32 Wheat Seeds for a robust first experiment and ends with 5 XP.
- Kept the recipes text-based for now; all visible quest icons use the live target-version Minecraft item models.

## 0.4.0

### 2026-09-03 — Optional movement-controls implementation

- Added a four-quest optional branch after the first mouse-control lesson covering jumping, sneaking, sprinting, and one controlled sprint-jump.
- The main First Steps path does not depend on the branch, so existing progression remains valid and learners who already know the controls can ignore it.
- Clarified that full blocks require jumping while slabs and stairs are climbed automatically, and that sneaking reduces accidental edge falls without providing complete protection or stopping descent over slabs and stairs.
- Used manual checkmarks because the exercises depend on safe, deliberate practice rather than a reliably exposed item or advancement criterion.
- Added two Apples and 5 XP points after the final exercise without bypassing a later lesson.

### 2026-09-02 — First deep Diamond expedition implementation

- Added the bilingual `Mining Deeper` chapter, gated by both the Iron-essentials recap and the sustainable-storage endpoint.
- The six-quest path covers an expanded supply check, reading and recording coordinates, reaching Deepslate, preparing a lit search tunnel near Y −53, collecting three Diamonds safely, and crafting a Diamond Pickaxe.
- Added a diamond-shaped navigation link from the sustainable-supplies map. The chapter grants four Bread, eight replacement Torches, 10 total XP, and one Golden Apple without replacing the mining task itself.
- Recipe guidance remains textual where an exact target-version recipe image has not yet been prepared; all chapter icons use live Minecraft item models.

### 2026-09-02 — First enchanting loop implementation

- Extended `Mining Deeper` with three parallel branches after the Diamond Pickaxe: safely create four Obsidian, grow and preserve Sugar Cane for a Book, and collect three Lapis Lazuli.
- The Sugar Cane milestone grants one Leather so this lesson does not ask the learner to kill a protected breeding animal. The three branches rejoin at the Enchanting Table recipe.
- The final task uses the verified Minecraft 26.1.2 `minecraft:story/enchant_item` advancement and its `enchanted_item` criterion.
- Added 25 XP points across the preparation milestones, plus three replacement Lapis Lazuli after the first successful enchantment.
- The initial iteration used text-only recipe guidance while keeping all visible icons on Minecraft's live target-version item models.
- Added compact 1672 × 941 guides for Paper and Book crafting, the Enchanting Table recipe, and the Enchanting Table interface. Minecraft content comes from the installed 26.1.2 assets or its in-game rendered model; only instructional frames, arrows, and numbered highlights are constructed.
- Added Cows and Horses as common Leather sources, while explicitly protecting the learner's final breeding pair and supplying the first Leather as a non-destructive bridge reward.

### 2026-09-03 — Bookshelf expansion implementation

- Continued the enchanting path with one Bookshelf, a gradual 15-shelf collection milestone, and a final placement check.
- Added exact 26.1.2 Bookshelf recipe artwork and a top-down 15-shelf station plan using the original Bookshelf and Enchanting Table textures.
- Revised the recipe artwork after in-game review so Wooden Planks and the Bookshelf use the established straight three-dimensional block-model view; the genuine top-down station plan deliberately remains two-dimensional.
- Explained the one-block empty gap, valid shelf heights, 15-shelf maximum, open entrance, and the difference between requiring level 30 and consuming only three levels.
- Rewarded three Books after the first shelf, then 10 XP points and three Lapis Lazuli after the completed station without replacing the full material-gathering lesson.

### 2026-09-03 — Optional safe mob-drop path implementation

- Added a dedicated optional chapter after the existing avoid-danger lesson, with a visible cross-chapter link from the peaceful Composter endpoint.
- Requires a Sword, Shield, and two cooked foods before presenting separate Zombie, Skeleton, Spider, and Creeper branches; none of those branches gates the main curriculum.
- Keeps the Creeper task fully isolated and explicitly prioritises retreat over Gunpowder, while the Skeleton branch continues to the useful Bone Meal lesson.
- Added exact 26.1.2 recipe guides for an Iron Sword and the one-Bone-to-three-Bone-Meal inventory recipe.
- Added a two-state attack-indicator guide directly from Minecraft 26.1.2's crosshair and HUD sprites, showing a partially recovered attack beside the fully ready target indicator.
- Explains attack recovery, cover, Shield direction, each enemy's distinct danger, common drops, unsafe foods, and the peaceful renewable Composter alternative.
- Adds four Torches, two Bread, and 5 XP points as small safety and completion rewards.

## 0.5.0

### 2026-09-03 — Safe Overworld portal preparation implementation

- Began the Nether milestone after the first enchantment without requiring either optional Bookshelf completion or the safe mob-drop chapter.
- Added six lessons covering dimensional hazards, a portal site with clear access, Flint from falling Gravel, Flint and Steel, ten ordinary Obsidian, and an unlit minimum frame.
- Added an exact 26.1.2 guide for the shapeless Flint and Steel recipe and replaced the schematic frame guide with an authentic in-world screenshot of a 4 × 5 frame. Its ten Obsidian and four temporary Dirt corner blocks are explained explicitly.
- Prepared a matching authentic screenshot of the activated frame for the following portal-activation lesson.
- Added bilingual instructions for extinguishing a single fire block with a left click and no tool while remaining on safe ground.
- Clarified that flammable blocks matter only around the ignition point until activation succeeds; an active portal is not itself a fire-spread hazard.
- Explicitly distinguishes Crying Obsidian, keeps the portal unlit until the Nether equipment checklist exists, and reserves clear ground on both sides.
- Reduced the attack-indicator guide display to 250 × 141 pixels following in-game review; this will be checked with the next profile update.

### 2026-09-03 — Gold equipment and controlled portal activation

- Added four lessons after the unlit frame: smelt five Gold Ingots, craft and wear a Golden Helmet, assemble the first-visit equipment, and activate the portal without entering.
- Verified the Golden Helmet recipe and all four entries in `minecraft:piglin_safe_armor` directly against the local Minecraft 26.1.2 JAR.
- Added an exact Golden Helmet recipe guide using the target-version item textures and reused the previously reviewed Iron Sword guide for the equipment checklist.
- The equipment task accepts an Iron, Diamond, or Netherite Sword and Pickaxe, then also requires a Shield, Flint and Steel, 32 Cobblestone, 16 Torches, and eight substantial cooked foods at the same time.
- Added a Silk Touch reminder that points back to random Enchanting Table offers without making the enchantment necessary for collecting or smelting Gold.
- Ordinary Piglins, anger-provoking actions, and always-hostile Piglin Brutes are distinguished before entry; the detailed behaviour lesson remains part of the first Nether scouting route.
- Activated-portal artwork uses the matching authentic in-world screenshot and explicitly keeps the learner outside until the Nether-side securing routine exists.

### 2026-09-03 — Final departure check and first Nether entry

- Added three lessons after activation: secure and light both approaches on the Overworld side, perform a final manual departure check, and make the first controlled dimension change.
- The final check repeats the Bed respawn point, records the Overworld portal coordinates, verifies full health and Hunger, stores unnecessary valuables, and arranges the already-detected equipment.
- The first entry uses the exact `entered_nether` criterion from Minecraft 26.1.2's `minecraft:nether/root` advancement plus a manual confirmation that the learner stopped beside the visible portal.
- Exploration remains locked behind the next Nether-side securing lesson. The entry text tells the learner to return immediately through the same portal if the arrival point presents Lava, a drop, or an enemy.
- Added eight Torches and 5 XP for the Overworld access, two Bread for the departure reserve, then 16 Cobblestone and 5 XP for the Nether-side shelter.

### 2026-09-03 — Nether-side portal shelter and return proof

- Added three lessons immediately after the first entry: assess the arrival area before moving away, build a compact Cobblestone shelter around the portal, and mark its coordinates before testing the return route.
- The arrival check separates ordinary Piglins from Piglin Brutes and warns against attacking, opening unfamiliar containers, or mining Gold before the detailed behaviour lesson.
- The shelter starts with safe footing, closes nearby drops, then adds Cobblestone walls, a roof, lighting, a clear two-block-high portal space, and one protected exit. It does not claim that Torches make the Nether fully spawn-proof.
- Explains that Water cannot be placed normally, Beds explode, an ordinary Compass is unreliable without a Lodestone, and one horizontal Nether block corresponds to roughly eight Overworld blocks.
- Ends with a deliberate trip back through the original portal and leaves the learner in the Overworld. Exploration remains locked for the following short scouting route.
- Uses manual checkmarks for the visual safety inspection, shelter quality, recorded coordinates, visible marker, and return proof. Rewards add 40 Cobblestone, four Torches, four Bread, and 10 XP across the three steps.
- Added a language-neutral route-marker guide made from the exact Minecraft 26.1.2 Cobblestone, Torch, Obsidian, and Nether Portal textures. Two stacked Cobblestone blocks make the marker visible, while the Torch side consistently points back toward the portal.

### 2026-09-03 — First marked Nether scouting route

- Added five lessons after the first return: re-enter the secured shelter with a fresh equipment check, distinguish three Piglin reactions, assess Ghast, fire, and Lava hazards from cover, walk a maximum three-marker practice route, and return through the original portal.
- Verified the target-version `piglin_safe_armor` and `guarded_by_piglins` tags directly in the Minecraft 26.1.2 JAR. The text distinguishes ordinary Piglins from always-hostile Piglin Brutes and group-reactive Zombified Piglins without asking the learner to provoke any of them.
- The hazard lesson prioritises a closed Cobblestone wall and roof over fighting a Ghast, repeats safe left-click fire removal, explains faster and farther Nether Lava flow, and explicitly forbids the first open-Lava bridge.
- The route requires six Cobblestone and three Torches before departure, then uses at most three two-block markers with the Torch side facing the portal. A route that becomes unsafe ends early and still counts after a successful retreat.
- The learner returns first to the Nether shelter and then through the original portal to the Overworld. Small rewards provide four Torches, eight Cobblestone, two Bread, and 10 XP without replacing any later lesson.

### 2026-09-03 — Optional first Piglin barter

- Added a separate bilingual `What to Do in the Nether?` chapter after the completed first Nether route. A cross-chapter link opens it from Nether preparation without extending the already complete preparation map.
- Moved the existing three-quest optional Piglin branch with all quest, task, and reward IDs unchanged: prepare one Gold Ingot, assess a safe bartering place, then perform one barter and return to the Overworld.
- Verified `minecraft:nether/distract_piglin` directly in the Minecraft 26.1.2 JAR. Its conditions require the player to wear no Piglin-safe Gold Armour, so the lesson deliberately uses a manual check and keeps the Golden Helmet equipped.
- Verified the target-version Piglin bartering loot table. The player is told that the result is random and may include supplies such as Blackstone, Gravel, Obsidian, Quartz, Iron Nuggets, Ender Pearls, or a Fire Resistance Potion without promising any one result.
- Requires an adult ordinary Piglin, level ground, nearby Cobblestone cover, the existing marked retreat, and no nearby Bastion, Lava, guarded block, Baby Piglin, Piglin Brute, or Zombified Piglin.
- Teaches one deliberate right-click interaction with one Gold Ingot, safe collection, and immediate return. One Gold Ingot and 5 XP replace the first payment and reward the completed optional exercise.
- Added a language-neutral four-panel comparison built from the exact Minecraft 26.1.2 Piglin textures and model proportions. The adult ordinary Piglin remains clear and green-framed; the Piglin Brute, Baby Piglin, and Zombified Piglin carry translucent red rejection marks.
- The chapter introduction reserves distinct later paths for safe Nether resources, the progression-relevant Nether Fortress, and the substantially more dangerous optional Bastion Remnant.

### 2026-09-03 — Safe first Nether resources

- Added a second independent branch to `What to Do in the Nether?` for a short resource trip that does not require a particular Nether biome.
- The learner checks a nearby mining place along the existing marked route, gathers 16 Netherrack and four Nether Quartz, then returns both samples through the original portal.
- Netherrack instructions cover its high breaking speed, blind upward and downward digging, and indefinitely burning fire. Quartz instructions distinguish ordinary mining from Silk Touch and explain the irreversible four-Quartz Block recipe.
- Gold Ore, high-ceiling Glowstone, unfamiliar structures, and route expansion remain deliberately outside this first trip. Eight Cobblestone and 5 XP reward the safe return without replacing later lessons.

### 2026-09-03 — Prepared Nether Fortress path

- Added a seven-quest progression path covering an Iron-or-better equipment check, safe Fortress search, protected entrance, threat recognition, two Blaze Rods, Nether Wart and Soul Sand, and the deliberate return home.
- The search distinguishes dark-red Nether Brick bridges from Blackstone Bastion Remnants, preserves the Cobblestone-and-Torch marker rule, permits several journeys, and rejects open-Lava bridging or forced shortcuts.
- The entrance lesson adds a roofed Cobblestone retreat room and a passage with only two blocks of open height. The text states its limits against Blaze fire and smaller enemies instead of presenting it as complete protection.
- Combat guidance uses full-block line-of-sight cover for Blazes, the low passage for Wither Skeleton retreat, fully charged Sword attacks, and an optional Bow. Blaze Spawners remain intact for later controlled use.
- The material path requires two Blaze Rods plus four Nether Wart and four Soul Sand, explains mature Wart and biome-independent farming, and ends with safe storage and a four-block home farm.
- Sixteen Cobblestone and 10 XP replace part of the expedition supplies without crafting the future Brewing Stand or bypassing its lesson.
- Rearranged the chapter map so the introduction sits on the left and the Piglin, first-resource, and Fortress paths begin in three separate rows. None of the three opening quests visually appears to depend on another branch.
- Added three language-neutral 300 × 169 guides built from exact Minecraft 26.1.2 textures and model UVs: a Nether Brick bridge-and-pillar segment, Blaze/Wither Skeleton/Magma Cube identification, and a Blaze Spawner on a raised Nether Brick platform.
- Clarified in the Blaze Rod lesson that a raised Shield can block incoming Blaze fireballs from the direction the player faces, while full-block cover remains safer against multiple angles.

### 2026-09-03 — Advanced optional Bastion Remnant path

- Added a fourth independent path that explicitly states Bastion Remnants are not required for brewing materials or later progression and may remain incomplete permanently.
- The seven lessons cover the voluntary decision, Iron-or-better equipment with Diamond and ranged-combat recommendations, visual identification, a closed outer retreat room, the exact Piglin Brute and guarded-block rules, at most one fully secured Chest, and deliberate return to the Overworld.
- Verified the `find_bastion` advancement criterion and the complete `guarded_by_piglins` block tag directly against the Minecraft 26.1.2 JAR. The player is warned about Gold and Raw Gold Blocks, Gilded Blackstone, Gold Ores, Barrels, Ender Chests, ordinary/Trapped/Copper Chests, and Shulker Boxes.
- The first controlled entry combines the Vanilla Bastion-location criterion with a manual confirmation that the learner returned to the closed Cobblestone room. Loot remains manual because every Bastion table is random and safety cannot be inferred from an item.
- Added a language-neutral Bastion silhouette built from the exact target-version Blackstone, Polished and Cracked Polished Blackstone Brick, and Gilded Blackstone textures. The existing four-Piglin comparison is repeated at the Brute lesson.
- The final reward is 10 XP only; no Bastion loot or later crafting material is granted.

## 0.6.0

### 2026-09-03 — First complete brewing sequence

- Added a seven-quest bilingual chapter that unlocks directly from the safe Nether Fortress return, has a diamond-shaped map link there, and does not require the optional Bastion branch.
- The learner deliberately converts one of two Blaze Rods into two Blaze Powder and preserves the other for a Brewing Stand. Fuel and ingredient uses are explained separately.
- The path crafts and places a Brewing Stand, crafts three reusable Glass Bottles, fills all three with Water in the Overworld, and shows their exact interface slots.
- The first Nether-Wart brew produces Awkward Potions and uses the verified `minecraft:nether/brew_potion` criterion `potion` for automatic completion.
- A second brew produces three-minute Potions of Strength. The manual final check includes drinking one, reading its effect, and understanding that Strength does not replace defensive equipment.
- Three Redstone Dust and 10 XP reward the completed sequence. Redstone duration, Glowstone strength, and the later Magma-Cream path to Fire Resistance are explained without requiring those extensions now.
- Added five language-neutral guides from exact Minecraft 26.1.2 textures, models, and the original Brewing Stand interface. Placeable Glass, Cobblestone, and the Brewing Stand use 3D model views in recipe images.

### 2026-09-03 — Brewing guide rendering correction

- Corrected the isometric face selection so all placeable recipe ingredients and results render as closed 3D models rather than disconnected faces.
- Verified the Brewing Stand menu directly from the Minecraft 26.1.2 classes: the fuel slot is at the upper left, while the filled fuel state is rendered as the exact yellow horizontal fuel sprite above the left bottle.
- Removed the incorrect Blaze Powder icon from the flame area in both brewing guides and clarified that loading fuel consumes the item immediately while storing 20 charges in the yellow bar.

### 2026-09-03 — Beginner brewing playtest and optional Fire Resistance

- The user confirmed the first complete brewing sequence after the corrected recipe models and fuel display; the concrete item tasks and full German survival path now count as tested.
- Added a four-quest optional branch for Fire Resistance without making it a prerequisite for later progression.
- Magma Cream may come from the verified shapeless Slimeball-and-Blaze-Powder recipe or from a medium or large Magma Cube. The lesson states that small Cubes cannot drop it and larger drops are not guaranteed.
- The branch brews three-minute Fire Resistance from Awkward Potions and Magma Cream, then uses one of the previously rewarded Redstone Dust to extend the duration to eight minutes.
- Safety text explains the Potion's specific protection and its limits, forbids deliberate Lava testing, and gives a clear retreat response for an accidental fall.
- Added three language-neutral guides using the exact Minecraft 26.1.2 item textures, potion tint, Brewing Stand interface, and filled fuel sprite.

## 0.7.0

### 2026-09-03 — First Eye of Ender preparation

- Added a six-quest bilingual chapter that unlocks directly from the completed Strength lesson; optional Fire Resistance does not gate progression.
- Ender Pearl and Blaze Powder supplies form two parallel branches before rejoining at the shapeless Eye of Ender recipe.
- The Pearl lesson explains that Endermen remain neutral until directly looked in the face or attacked, then offers safe two-block-roof combat, random Piglin bartering, and an experienced Cleric trade as alternatives without promising a particular random result. Looting is identified explicitly as a Sword enchantment that raises the possible maximum without guaranteeing a drop.
- Verified against Minecraft 26.1.2 that Endermen drop zero to one Pearl before Looting, Piglin barter returns two to four Pearls when selected, and a Cleric's relevant offer costs five Emeralds.
- Warns that a thrown Ender Pearl costs two and a half hearts and has a five-percent Endermite chance.
- Requires sixteen Pearls and sixteen Blaze Powder, explains that eight Blaze Rods make the Powder, and then requires sixteen completed Eyes.
- Explains the twelve portal frames and the twenty-percent chance that a thrown search Eye shatters. The learner pauses a later search whenever fewer than twelve Eyes remain.
- Added an exact target-version recipe guide from the original Ender Pearl, Blaze Powder, and Eye of Ender textures. Two bonus Eyes and 10 XP are awarded only after the initial sixteen have been crafted and safely stored.

### 2026-09-03 — Controlled Stronghold search

- Continued the chapter with six lessons for an Overworld expedition loadout, one controlled first throw, documented travel stages, surface triangulation, a safe descending staircase, and a verified return to the surface.
- The route repeats the twelve-Eye abort threshold, separates world-spawn Compass behaviour from Bed and home coordinates, and permits multiple daylight journeys instead of encouraging one uninterrupted chase.
- Eye throws are limited to clear safe areas and observed until recovery or shattering. Reversing direction narrows the previous interval; a downward flight marks the local target area.
- Straight-down digging is forbidden. The learner approaches beside the target via a lit staircase, closes unknown side caves, and opens a suspected Stronghold wall from a protected standing place.
- The exact `minecraft:story/follow_ender_eye` advancement criterion `in_stronghold` detects arrival automatically. The portal room remains outside this implementation.
- Added a three-stage, language-neutral search guide using only the original 26.1.2 Eye, terrain, Stone, and Stone Brick textures. Sixteen Torches and 10 XP reward the secured entrance and proven surface return.

### 2026-09-03 — Controlled Stronghold and portal-room exploration

- Added a separate eight-quest bilingual chapter after the proven surface return so the search map remains compact and portal-room work cannot be confused with locating the structure.
- The learner leaves Eyes outside the exploration inventory, establishes a stopping rule, and uses right-wall Torches plus coordinates and junction markers to handle loops and changing elevation.
- Separates ordinary mobs in dark corridors from Silverfish hidden in visually ordinary Stone variants. Unnecessary wall mining is discouraged, and a narrow lit retreat is prioritised if Silverfish appear.
- Stronghold Libraries are an optional side branch because they are not required for progression and may be absent or truncated by structure generation.
- The main path identifies the portal room through twelve frames, Lava, and the Silverfish Spawner, then requires an external retreat room before the Spawner is destroyed and reachable Lava is controlled.
- Existing and missing Eyes are counted across all twelve frames without carrying or inserting Eyes. The learner returns home with the written count before any activation lesson appears.
- Added three language-neutral guides from exact Minecraft 26.1.2 assets: three straight 3D Stone Brick variants beside the segmented Silverfish model, a top-down portal-room orientation plan, and top-down empty/filled frame states.
- One Golden Apple and 10 XP reward the completed exploration without activating the portal or bypassing the later End equipment lesson.

### 2026-09-03 — Stronghold Iron Door lesson

- Inserted a dedicated lesson between the first marked corridor and the hazard explanation for the Iron Doors a beginner may first encounter inside a Stronghold.
- Contrasts direct right-click Wooden Doors with Iron Doors that require a Redstone signal, explains the nearby Stone Button's short opening interval, and keeps the learner out of the doorway while it closes.
- Requires two Stone Buttons automatically so a missing control can be replaced from either side before the learner proceeds. The exact 26.1.2 recipe is one ordinary Stone per Button after smelting Cobblestone.
- Added a combined language-neutral guide with a straight 3D Stone ingredient and Stone Button result beside the exact Iron Door, Stone Button, and Stone Brick wall textures.

### 2026-09-05 — First Village discovery

- Continued the Overworld chapter after the completed Boat return with three lessons for a controlled daylight search, recognising a Village, and recording a safe return point.
- The search uses repeated daytime legs, food and Torch stopping thresholds, landmarks, and intermediate coordinates rather than encouraging directionless travel.
- The recognition lesson identifies paths, several buildings, Villagers, Beds, workstations, farms, a Bell, and the possible Iron Golem while explaining biome-dependent layouts.
- The learner records exact X/Y/Z coordinates outside the game, adds a sparse marker without blocking doors or paths, and leaves Chests, Beds, and workstations untouched for the later Villager lesson.
- Added a language-neutral in-world-style overview generated from the user's Minecraft 26.1.2 Village screenshot and approved separately before installation. The original screenshot remains beside the selected illustration as provenance. Five XP rewards the secured location without replacing a future trade.

### 2026-09-05 — Villager professions and first trade

- Added four bilingual lessons after the secured Village location: respectful behaviour, recognising professions and workstations, reading the trading screen, and completing a first trade.
- Warns against attacking Villagers or Iron Golems, explains a safe retreat from an angered Golem, and leaves Beds, workstations, Chests, fields, doors, and paths undisturbed.
- Introduces adult employed Villagers, babies, Nitwits, four representative workstation mappings, and the profession lock created by a completed trade without requiring the learner to rearrange the Village.
- Added a language-neutral trading guide assembled from the exact Minecraft 26.1.2 Villager GUI, Wheat, Emerald, trade-arrow, and profession-experience textures. Its 20-Wheat example is explicitly separated from the offers actually present in the learner's world.
- The exact `minecraft:adventure/trade` advancement criterion `traded` detects the first completed trade automatically. Four Torches and 5 XP support the return trip without replacing its cost or granting valuable equipment.

### 2026-09-05 — Automatic first sleep

- Replaced the manual checkmark in `Your First Bed` with the exact Minecraft 26.1.2 `minecraft:adventure/sleep_in_bed` advancement criterion `slept_in_bed`.
- The lesson now completes only after a successful sleep rather than after merely crafting or placing a Bed. The existing task ID remains unchanged to preserve stored quest references.

### 2026-09-05 — Safe end to the first Village visit

- Added three bilingual lessons after the first Villager trade: leaving the Village without obstructing its inhabitants, returning home through the recorded route, and storing supplies with a complete Village note.
- Limited Village changes to a few useful Torches and closing exposed Wooden Doors. Doors, paths, Beds, workstations, Chests, and fields remain intact and reachable.
- Explains that ordinary hostile mobs threaten Villagers at night while explicitly removing any expectation that a beginner must fight a group to rescue the Village. Waiting safely for daylight is a valid fallback.
- Requires the learner to use Village and home coordinates, landmarks, and any marked Boat landing on the return rather than simply checking the task while still in the Village.
- The final recap stores gathered resources, prepares a future travel kit, and records coordinates, route details, landing place, and a useful trade. Three Bread and 5 XP support the next trip without granting valuable equipment.

### 2026-09-05 — Optional home map and Cartography Table

- Added a two-lesson optional branch after the normal Compass explanation in `Finding Home`; neither lesson gates the Lodestone route or later chapters.
- The tested route-home reward now contains two Compasses. One remains available for the Compass and Lodestone lessons while the second is consumed by the Empty Map recipe.
- Teaches Sugar Cane to Paper, the eight-Paper-and-Compass Empty Map recipe, activation at home, cardinal orientation, updating while held, and the limits of a fixed mapped area.
- Adds the Cartography Table recipe and its three target-version operations: Filled Map plus Paper expands, plus Empty Map clones, and plus Glass Pane irreversibly locks.
- Added two language-neutral 300 × 169 guides built from exact Minecraft 26.1.2 item, block, and Cartography Table interface textures. The Cartography Table is shown as a straight 3D block model while flat items remain in their normal 2D form.

### 2026-09-05 — First Chorus harvest and controlled teleport

- Continued `What to Do in the End?` after the secured outer-island arrival with a two-lesson bilingual Chorus sequence.
- Requires eight Chorus Fruit and one directly harvested Chorus Flower simultaneously. The flower is preserved for later cultivation on End Stone rather than lost by destroying its supporting plant first.
- The first Fruit is tested only on broad level End Stone with at least eight blocks of solid room in every direction and a recommended ten-block margin from the void. Full health, hands-off teleporting, immediate Sneak, and reorientation towards the marked return Gateway are explicit.
- Explains that Chorus Fruit restores hunger, remains edible at full hunger, teleports randomly up to eight blocks, and is not a reliable escape or navigation tool.
- Added two language-neutral guides from exact target-version End Stone, Chorus, Chorus Fruit, and player textures: a 3D harvest distinction and a top-down safe-area plan.

## 0.9.1

### 2026-09-06 — Quest-book onboarding review

- The first in-game review showed that one long Welcome quest produced an overly dense reading column and hid the structure of the instructions.
- The literal paragraph-sign key label was interpreted as formatting by FTB Quests and displayed an invalid-formatting error.
- Replaced the single page with a dedicated bilingual Welcome chapter: one short introduction, six compact information quests, and one final confirmation.
- `First Steps` now stays hidden until every onboarding point and the final confirmation are complete.
- Reworded the suggested key as the physical paragraph-sign key below Escape and left of 1 without including the formatting-sensitive symbol.
- Enabled FTB Quests' pack-wide pause setting so ordinary single-player worlds stop while the book is open; multiplayer and LAN limitations are explained in the Welcome chapter.
- The revised layout, chapter reveal, reward flow, and both languages still require an in-game review.
