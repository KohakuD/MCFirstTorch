# Minecraft 26.2 port preparation

The native 26.2 port, five Sulfur reference cards, Friends List guidance and returning-learner comparison are implemented. Initial gameplay and the creature-preview fix are owner-accepted; final comparison and new-card acceptance remain open. See [current release preparation](releases/0.14.0-next-editions.md). The dated sections below retain the implementation history. The owner confirmed the 1.21.1 Beta upload on 2026-09-12 and authorised starting this phase.

## Verified target

- Minecraft `26.2` requires Java `25` according to its [official version metadata](https://piston-meta.mojang.com/v1/packages/bc42e43dfe43d65a2f6c2c1dbb322c75134e51fe/26.2.json). The existing root toolchain can be reused.
- Stable NeoForge `26.2.0.87` is available in the [official Maven repository](https://maven.neoforged.net/releases/net/neoforged/neoforge/26.2.0.87/). Treat this as the initial candidate, not an already tested dependency. Availability was checked against Maven metadata during this assessment.
- Mojang lists data-pack version `107.1` and resource-pack version `88.0`. Rendering changes include a reversed depth buffer, experimental Vulkan, and block models for beds/signs in place of their previous entity-model paths. See the [official 26.2 notes](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-2).

## Implementation sequence

1. Add an isolated `:mc262` module with its own run directory, explicit IntelliJ task and labelled JAR, while retaining both existing targets. Reuse the shared core and only compile-proven compatible native sources.
2. Compile server storage, observations, rewards and network handlers before declaring compatibility. Preserve IDs, schema validation and claim protection.
3. Check GUI extraction, depth/layers, item models, live scenes and pause-menu integration against actual 26.2 sources. Verify default rendering first; treat Vulkan as a separate optional acceptance configuration.
4. Run existing rule/protocol regressions against the target. Validate recipe, item-tag and advancement references against original 26.2 data and preserve English/German parity.
5. Include the fifteen shared screenshots already approved by the owner. Reuse live illustration identifiers without reintroducing archived PNGs.

## Lesson candidates to investigate

The release adds Sulfur Caves, Sulfur Cubes, Sulfur/Cinnabar blocks and sulfur hazards, plus a Friends List. These justify investigating a safe cave-exploration branch, a creature reference and revised multiplayer guidance. This is a curriculum proposal, not a complete feature inventory or approved quest text. Confirm mechanics, original assets and translated in-game names before authoring the lessons.

Record new and substantively revised lesson IDs in the edition history. Cosmetic changes, renderer adaptations, reward substitutions and screenshot reuse must not populate the returning-learner filter. The future cumulative view must retain one authoritative progress/reward state.
## Initial native port — 2026-09-12

The isolated `:mc262` module uses Java 25 and NeoForge `26.2.0.86`. Candidate `.87` fails while recompiling Minecraft (`HolderSet.contents` access mismatch), before compiling First Torch; `.86` passes. The pin records the tested version, not a claim that no newer release exists.

Compatible root sources compile against the actual 26.2 API. Five explicit client adapters cover the moved GUI screen/toast accessors and the native `Model.Simple` player preview, following Vanilla PlayerSkinWidget. The shared core is merged directly into the target JAR. Existing guide IDs, bilingual resources and the fifteen approved rasters are reused. No new 26.2 lessons or comparison filter are included yet.

Full `test build` passed: 417 tests on 26.1.2, 44 core tests, 182 on 1.21.1 and 417 on 26.2 (1,060 total, no failures, errors or skips). All seven packaging fixtures and all three native JAR boundary checks passed. CI now includes the third artifact. Live-scene report output is isolated per target to avoid concurrent writes.

The client startup reached native mod/resource and texture-atlas initialization using its separate `versions/26.2/run/client` profile. This is startup evidence, not visual acceptance. The owner has been asked to test a fresh world, welcome, pause access, first quest/reward, illustrations and enlarged view in both languages. Vulkan, multiplayer, whole-course mechanics and changed native model behaviour remain unverified.

Use `:mc262:runClient` or the **First Torch Client 26.2** IntelliJ configuration. Artifact: `versions/26.2/build/libs/firsttorch-mc26.2-0.14.0-alpha.1.jar`. Do not install it into a different Minecraft target or treat it as the finished 26.2 release.

The owner reported the loader warning about deprecated `logoFile`. The 26.2 metadata now uses `iconFile` for the existing square logo through a target-local template. Earlier targets retain their compatible metadata. A client restart is required to re-read this metadata.

The owner confirmed that all requested initial 26.2 smoke checks work after the metadata correction (2026-09-12): welcome/pause entry, early quest and reward flow, illustrations and enlarged view in German and English. The mod-list screenshot additionally confirms the client running in a world. New 26.2 lessons, exhaustive mechanics review and returning-learner comparison remain separate work.

The mod-details header now has a separate wide First Torch banner via `bannerFile`, while `iconFile` retains the square list icon. NeoForge fits this header within 250 x 50 GUI pixels, so the banner uses a compact horizontal wordmark. Restart the client to reload mod metadata and verify the header in the mod list.

## Sulfur reference batch

The owner accepted the banner. Five bilingual safety cards now form a target-only reference chapter with native item icons, mining-context links and a reading trophy. Names were checked against installed 26.2 English/German language assets. Mechanics were reviewed against official 26.2 notes and Vanilla sources/data. All earlier 71 chapters remain equal after parsing; no old quest gains a prerequisite.

Full test/build and all three JAR verifiers pass. Target tests retain explicit expanded counts; earlier target expectations are unchanged. In-game acceptance of the five new cards, links and trophy remains pending. Friends List guidance and returning-learner filtering remain open.

## Entity preview crash fix

The owner reported a 26.2 client crash on 2026-09-12 at 22:39 while rendering a Piglin illustration. The stack trace reaches Entity.getId through ItemModelResolver and LivingEntityRenderer: the unspawned client preview has ID zero. Cached preview models now receive distinct negative render-only IDs before state extraction, without being added to the world. Full test/build and JAR verification pass; reopening the affected Piglin scene remains the required in-game regression. The crash shutdown log reports all dimensions saved; this is not an independent save-integrity check.

## Accepted crash fix and Friends List guidance

The owner confirmed the corrected creature preview works. After the Codex restart, file access works again. The 26.2 reading introduction now explains where to open Friends List, its default O key, Online Options, and the distinction from personal First Torch progress/rewards. Facts were checked against the official 26.2 release notes linked above. No friendship or multiplayer settings were changed.

## Returning-learner acceptance

The owner explicitly accepted the corrected Clock, change summaries, version switching, background links and return, and persisted choice after restart in German and English on 2026-09-12. Separate Sulfur-card/trophy acceptance remains pending; see the current release checklist.
