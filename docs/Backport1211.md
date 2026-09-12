# Minecraft 1.21.1 backport assessment

Assessment date: 2026-09-12. This document records the first bounded compatibility review for milestone 0.14.0. It records a development backport, not a completed release acceptance.

## Current implementation boundary

The playable root project remains Minecraft 26.1.2 / NeoForge 26.1.2.84 / Java 25. The `core` module provides the Java 21 guide model, validation and progression rules.

The separate `:mc1211` module under `versions/1.21.1` pins Java 21 and NeoForge 21.1.248. It now compiles native progress/welcome storage, reward execution, task/command handlers, datapack reload/sync handlers and the five network payload/codec classes. Compatible journal, observation and welcome-service sources are reused through an explicit generated-source allowlist, not maintained as forked copies. Shared test sources likewise run against both targets.

The production mod entry, client handlers, welcome/browser UI and native 1.21.1 renderers are now implemented. The native NeoForge JUnit launcher uses real production metadata; the earlier test-only mod fixture was removed. Separate client/server run profiles and an installable `firsttorch-mc1.21.1-0.14.0-alpha.1.jar` are enabled. The client startup log confirms First Torch loading and resource/texture initialization without a launch crash. This does not establish fresh-world gameplay, actual reward delivery or visual acceptance.
## Content adaptation matrix

The initial adaptations below are implemented in target overlays and have focused automated checks; exhaustive mechanics and visual acceptance are pending. Preserve existing guide, chapter, quest, task and reward IDs wherever the same concept survives; do not regenerate identifiers. Target-specific wording must change in `en_us` and `de_de` together.

Paths in this table are relative to the repository. The main course is `src/main/resources/data/firsttorch/guides/course.json`; translations are `src/main/resources/assets/firsttorch/lang/{en_us,de_de}.json`.

| Area | Stable IDs or translation keys | Proposed 1.21.1 adaptation |
| --- | --- | --- |
| Armour lesson | Quest `18A6D3F90C754BE2`; `quest.firsttorch.protection.armour.description` | Replace Copper Chestplate icon with Iron Chestplate. Require the iron-ingot quest `3C16B9E50A724DF8`, removing the copper alternative `6C4AE8F31D957B20`. Teach the four available craftable materials rather than five. |
| Armour illustrations | `src/main/java/ch/minenox/firsttorch/client/LiveRecipePanels.java` | Use Iron Ingots and Iron armour results for all four panels, rendered with original 1.21.1 models. Copper equipment was added in 1.21.9. |
| Copper branch | `quest.firsttorch.ores.raw_copper.description`, `quest.firsttorch.ores.copper_ingot.description` | Keep the ore/smelting lessons and their IDs. Replace promises of early armour with valid uses such as building or the Brush. Check that branch completion is not presented as unlocking armour. |
| Pickaxe and sword tags | `src/main/resources/data/firsttorch/tags/item/stone_or_better_pickaxes.json`, `ordinary_swords.json` | Remove absent `minecraft:copper_pickaxe` and `minecraft:copper_sword`. |
| Tool explanations | `quest.firsttorch.mining.equipment.description`, `quest.firsttorch.iron_essentials.pickaxe.description` | Remove Copper from the tool-material comparisons. |
| Boats | `src/main/resources/data/firsttorch/tags/item/ordinary_boats.json`; `quest.firsttorch.excursions.boat.description` | Remove `minecraft:pale_oak_boat` and Pale Oak from the explanation. Keep the Bamboo Raft. |
| Bundle reward | Storage quest `6D91380C6EA4B2F5`, reward `5AEFB70D418269C3`; `quest.firsttorch.storage.routine.description` | Replace the Bundle with a modest ordinary storage reward, such as a Chest, and update the description. Bundles were added as a normal feature in 1.21.2; do not require experiments for this learning pack. |
| Bundle icons | Quests `1A62D8E30C745BF9`, `6D91380C6EA4B2F5`, `34FAC7E96152BDF8`, `52A60C48F37B915E` | Replace Bundle icons with suitable ordinary 1.21.1 items. |
| Pale Garden reference | Quest `1CA0B0C0D0E00003`, task `2CA0B0C0D0E00003`; chapter `70A7B8C9D0E1F203` | Exclude the unavailable Creaking card from this target. Keep Breeze and Bogged. Change `chapter.firsttorch.field_chambers_garden.title` to Trial Chambers, its description from three encounters to two, and the matching trophy description. Preserve the chapter ID. |
| Reference link | `src/main/resources/assets/firsttorch/quest_links.json`, source `51A0B0C0D0E00003` | Remove its link to the excluded Creaking card; keep the End reference link. Validate that no other link targets an excluded quest. |
| Lodestone recipe | Quest `59CBED086F24A137`; `quest.firsttorch.lodestone.craft.description`, `image.firsttorch.lodestone.description`; `LiveRecipeCatalog.java` | Use Netherite Ingot in the centre, not Iron Ingot. Explain the advanced material requirement. The Iron recipe arrived in 1.21.5. See the progression proposal below before enabling this branch. |
| Lodestone observation | Task `3E1045DAB479F68C` | Change advancement ID from `minecraft:adventure/use_lodestone` to `minecraft:nether/use_lodestone`; verify criterion `use_lodestone` against the target data. The advancement moved categories in 1.21.5. |
| Copper Chest references | `quest.firsttorch.bastion.brutes.description`, `quest.firsttorch.outer.0b875b8819cb3c72.description` | Remove Copper Chests from the examples. Replace the explicit 26.1.2 sentence in the Bastion card with target-correct wording and verify guarded block behaviour. |
| Version-specific visual wording | `image.firsttorch.archaeology_comparison.description`; `LiveEnchantingLayout.java` | The current text/layout explicitly refers to 26.1.2. Verify original 1.21.1 textures and enchanting-menu alignment before changing the wording to claim that target. |

This targeted scan is not an exhaustive registry, recipe, loot or creature-mechanics audit. Review the remaining course and reference cards against the target before release.

## Proposed lodestone progression change

The current excursion entry quest `12E8A5C74F309BD6` requires the lodestone-binding quest `0BED012A8146C359`, as well as another existing prerequisite. Correcting only the ingredient would put an advanced Netherite requirement in front of ordinary Overworld excursions.

Proposed solution for 1.21.1: make the lodestone branch optional and replace only that excursion prerequisite with the ordinary compass lesson `2F6A91C4D8E307B5`, retaining the other prerequisite. Revise `quest.firsttorch.excursions.equipment.description` to rely on a tested landmark and recorded coordinates rather than a required Lodestone Compass. The lodestone introduction should clearly explain that it can be revisited later.

This progression change is implemented and covered by focused prerequisite and recipe regressions. Still verify chapter visibility, recommendation ordering, cross-references, task completion and reward eligibility with stable IDs preserved. Confirm that optional lodestone work does not become an indirect gate elsewhere in the course.

## Native runtime adaptation checklist

Implementation and remaining acceptance:

- [x] Pin the 1.21.1 verification module to NeoForge 21.1.248 and Java 21, with isolated build/test outputs.
- [x] Complete mod metadata, data/resource-pack formats, run profiles and installable artifact configuration.
- [x] Audit `Identifier` usages against 1.21.1 `ResourceLocation`, including constructors/factories, registry access and serialization. Compile every native call against the selected target rather than relying on name substitution.
- [ ] Adapt `SavedData` creation/loading/saving, NBT and registry lookup signatures. Verify welcome acknowledgement, player progress, reward journal persistence, unavailable-state handling and restart behaviour. Do not assume save-format or cross-version world compatibility.
- [ ] Check NeoForge event classes, registration and lifecycle ordering for reload, login, respawn, disconnect, tick and client setup. Preserve server-authoritative observations and per-player state.
- [ ] Adapt payload registration, codecs/buffers, handler threading and client/server dispatch. Verify snapshot, progress and welcome synchronization and malformed/unavailable-state handling.
- [ ] Adapt screen input callbacks, key bindings, mouse/keyboard handling, narration and GUI rendering. Verify clipping, transforms, item/block/entity rendering, recipe panels, tooltips and target GUI textures in both languages.
- [ ] Verify reward execution, inventory-tag observations, advancement criteria and dimension/location checks against real 1.21.1 registries and data.
- [ ] Review every packaged capture and illustration for exact target-version assets. Runtime-rendered models require visual verification too; successful compilation does not prove visual accuracy.
- [ ] Add target-specific regression coverage while preserving current 26.1.2 expectations. Existing tests deliberately assert Copper armour, Iron lodestone, Copper tool tags and the Creaking card; do not weaken those baseline checks to make a backport pass.
- [ ] Run focused tests, full target test/build and adapted native JAR verification before handing off an installable build. Perform fresh-profile in-game acceptance for both languages, affected progression, rewards and restart persistence.

A client startup smoke check has now been performed; no world was created or modified by the agent. A mod backport does not support downgrading an existing world or importing progress across Minecraft versions.

## Foundation verification (2026-09-12)

- The pre-change `26.1.2` baseline passed `test build` at commit `029e8c4`.
- Eighteen production files and nine test files were moved into `core` without changing their contents. Course data, translations and stable IDs are unchanged.
- After extraction, `clean test build` passed: 44 Java 21 core tests and 416 Minecraft-runtime tests, with no failures, errors or skips. The final dependency/publication correction also passed `test build`.
- All seven native JAR boundary fixtures passed, including rejection of a missing core class. The previously stale valid fixture now includes the required licence files.
- Native verification passed for `firsttorch-mc26.1.2-0.14.0-alpha.1.jar` (197 entries); all 24 compiled core class files are included directly, with no nested JAR or new mod dependency.
- Development launch preparation and its task graph were checked. Maven and Gradle publication metadata were generated locally and contain no separately required core artifact; nothing was published to a package registry.
- No in-game test has been performed for the module split. Before accepting the development build, reload Gradle in IntelliJ, launch the existing 26.1.2 client, then check welcome, quest completion, rewards, both languages and restart persistence in a fresh test world.

## Native storage implementation

The backport adapters retain schema version 1 and the existing validation rules. Their native `SavedData.save` and `load` methods use the same codecs through NBT; unsupported or incomplete documents fail loading rather than exposing partial state. The shared regression tests are compiled directly from the original test sources, without weakening the 26.1.2 expectations.

The 1.21.1 repositories use the Overworld `DimensionDataStorage`, with legal filenames `firsttorch_progress.dat` and `firsttorch_welcome.dat`. They keep progress and welcome acknowledgement separate, retain historical IDs and protect unreadable existing files from replacement. These are files inside a 1.21.1 world's data directory, not a cross-version world or progress importer. Automated storage tests use temporary directories only.

The earlier GitHub failure for `029e8c4` was in the obsolete valid packaging fixture (`Unexpected boundary result: valid`), after a successful build. The fixture correction already shipped in `0ddd4c5`, whose [GitHub validation passed](https://github.com/KohakuD/MCFirstTorch/actions/runs/34700571311). CI now explicitly provisions both Java 21 and Java 25, and the root build gate also requires the native backport tests.

Native adapter verification passed on 2026-09-12: 16 tests under Minecraft 1.21.1, plus the existing 44 core and 416 Minecraft 26.1.2 tests (476 total). The full `test build`, seven packaging fixtures and the 26.1.2 native JAR verification passed. No in-game launch or installed world was used for this adapter-only step.

## Reward, server and protocol implementation

- The durable `RewardJournal`, bulk-claim policy, inventory counting, progress observation, advancement observation, server guide repository and welcome service are shared from the original sources through `syncSharedRuntimeSources`. Only the explicitly listed compatible files are copied to the target build directory.
- Native adapters use the 1.21.1 `ResourceLocation`, item registry and world-data paths. Reservation precedes item/XP mutation; an interrupted reservation remains blocked instead of automatically retrying payment. The reward journal lives at `data/firsttorch/reward_claims/<UUID>` inside the target world.
- The 1.21.1 reload handler uses `AddReloadListenerEvent`. A failed guide preparation retains the last valid snapshot, with recovery checked after a subsequent valid reload.
- Guide snapshots, progress, welcome and welcome acknowledgement retain their existing payload IDs, validation and protocol version `12`. Codec tests exercise valid round trips and malformed data; the actual client-handler registration is now implemented on the mod event bus with main-thread handling.
- Sixty-two 1.21.1 tests pass, including 46 added checks beyond the storage checkpoint. Tests cover reservation/completion reopen, corrupt journals, independent players, inventory planning/stack limits, bulk-claim selection, reload failure recovery and wire validation. Actual running-server command/tick/payout acceptance remains pending.

## Official sources

- [Minecraft Java Edition 1.21.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-2): Bundles became a normal feature; also records changed Redstone Torch visuals.
- [Minecraft Java Edition 1.21.4: The Garden Awakens](https://feedback.minecraft.net/hc/en-us/articles/32385811139085-Minecraft-Java-Edition-1-21-4-The-Garden-Awakens): Pale Garden, Pale Oak and Creaking additions.
- [Minecraft Java Edition 1.21.5](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-5): Iron-based lodestone recipe and movement of its advancement from Nether to Adventure.
- [Minecraft Java Edition 1.21.9](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-9): Copper armour, tools and other Copper additions.

## Initial target overlays and capture acceptance

The initial 1.21.1 target now has course, bilingual language, reference-link and affected item-tag overlays, plus target recipe catalogs. They implement Iron armour, the Netherite lodestone recipe and original Nether advancement, an optional lodestone branch, ordinary Chest rewards, and exclusion of unavailable Creaking/Pale Oak/Copper equipment content. The target resource JAR confirms the lodestone recipe, advancement criterion, Iron armour models and guarded Piglin block tag. These checks are narrower than whole-course or visual acceptance.

The owner confirmed that the following 15 existing screenshots also fit 1.21.1 and 26.2. All 16 backport image references are restored and the files are packaged unchanged. Replacement screenshots are no longer required:

- `416BD8FA3072CE95`: `village_overview.png`
- `73B2B39D1C674728`: `nether_fortress.png`
- `5B0F14AD63E74BD3`: `blaze_spawner.png`
- `15F17869891E4CE0`: `nether_wart_garden.png`
- `3C906D28F3D7A15E`: `stronghold_iron_door_capture.png`
- `10E5B17D482CF6A3`: `end_portal_room_capture.png`
- `6F54A06C371BF6A3`: `end_portal_active_capture.png`
- `2C81D39F6A4E29E7`: `end_arrival_platform_capture.png`
- `4EA3F5B18C604B09`: `end_battlefield.png`
- `013E8F5B26A0F5B5`: `end_exit_portal.png`
- `5DFA4517E26CB171`: `end_gateway_access.png`
- `435FA7C29D816E3B`: `chorus_harvest.png`
- `1A8C05F7D3E692B1`: `end_island_crossing.png`
- `6FD15A4C283BE706`: `end_city_search.png`
- `78C3992F3DF1AAB1`: `end_city_search.png`
- `51A0B0C0D0E00003`: `pumpkin_view_capture.png`

Runtime-rendered diagrams use the target game assets, but their layout and appearance still require in-game acceptance. Screenshot reuse is based on the owner's explicit cross-version confirmation.

## Initial installable alpha verification (before screenshot approval)

- Full `.\gradlew.bat test build` passed: 416 Minecraft 26.1.2 tests, 44 shared-core tests and 176 Minecraft 1.21.1 tests (636 total), without failures, errors or skips.
- All seven packaging fixtures passed. Both native artifacts passed the unchanged boundary verifier: 197 entries for 26.1.2 and 187 for 1.21.1.
- The 1.21.1 curriculum contains 345 quests and 95 image references. A target regression requires every retained image to have a native renderer and a nonblank caption in both languages, with no old raster packaged. The 26.1.2 capture regression remains unchanged in its original suite.
- `:mc1211:runClient` started with the real production mod, Java 21, NeoForge 21.1.248 and native resources initialized. Automated Windows UI control failed before any input because its sandbox helper could not initialize, so there is no claimed visual or fresh-world gameplay acceptance.
- Reload Gradle in IntelliJ, select **First Torch Client 1.21.1**, and start a fresh test world. Verify welcome, pause-menu/key access, both languages, first manual/automatic quests, reward delivery, enlarged-view clipping, search/reference/trophy screens, and restart persistence. Restart an already-running development client after pulling the final renderer changes.
- This alpha does not yet implement the 26.2 edition or returning-learner baseline/version filter. At that checkpoint, capture approval and complete target-mechanics review remained open; capture approval has since been received.

## Native content compatibility gate

The target now checks its crafting catalogs directly against the original 1.21.1 recipe and item-tag data produced by ModDevGradle. The check includes shaped placement (with valid translations/mirroring), shapeless ingredient counts, nested tag membership and output amounts. It also covers the hand-crafting plank step and crafting the Cartography Table. The special flight-one rocket is matched and assembled by Minecraft's own Java recipe, including its three-item output and absence of explosion stars.

Every course chapter/quest icon, inventory objective and item reward resolves to a real item enabled by the default feature flags. Inventory tags and their nested members resolve, and all advancement objectives reference an existing advancement and criterion in the original target data. These are automated compatibility gates, not checks of every prose claim, all processing behaviour, cartography operations, visual layout or survival progression.

The local play log additionally confirms guide loading, an integrated 1.21.1 world start, player login and clean save/shutdown. This supports runtime integration but does not establish welcome, quest/reward or restart-persistence acceptance. No worlds were changed by this audit.

## Further acceptance and native processing checks

The owner confirmed the requested early gameplay smoke test: first quests, collecting rewards and retained progress after restart. This confirmation does not establish both-language visual coverage, every lesson or complete multiplayer acceptance.

Native compatibility tests now also cover both furnace diagrams against original smelting data, sufficient fuel time for the illustrated input, and all four brewing steps through Minecraft's PotionBrewing implementation. Each brewing result must be the intended ordinary potion, including the extended Fire Resistance variant. Full-world timing, actual brewing interactions, cartography operations and rendering remain separate acceptance work.

The initial availability and substantive-change inventory is recorded in [EditionDifferences.md](EditionDifferences.md). It separates genuinely revised lessons from reward/icon substitutions, adapter changes and missing captures; it does not enable a comparison filter yet.

## Shared screenshot approval

The owner explicitly approved reuse of the existing screenshots for Minecraft 1.21.1 and 26.2. The backport now includes all 15 original files at 16 quest references, yielding 111 total image references. Automated checks require byte-for-byte identity with the shared source, matching dimensions, and nonblank bilingual captions. Runtime-rendered illustrations still use the installed target assets. The future 26.2 build should use the same approved screenshot set; its runtime port is not implemented by this change.
