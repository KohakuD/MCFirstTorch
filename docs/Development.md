# Development

## Active baseline

First Torch is a native NeoForge mod. The active target is Minecraft Java `26.1.2`, NeoForge `26.1.2.84`, Java `25`, and First Torch `0.14.0-alpha.1`.

The runtime is independent of FTB Quests, FTB Library, FTB Teams, Initially, KubeJS, and their companion modules. Do not add those dependencies or an FTB-progress importer. Historical FTB pack material is isolated in [`../archive/ftb-legacy/`](../archive/ftb-legacy/) and is not part of normal development.

## Repository layout

- `build.gradle`, `settings.gradle`, `gradle.properties`: native NeoForge build
- `core/src/main/java/`: Minecraft-independent guide model, parsing, validation and progression rules (Java 21)
- `core/src/test/java/`: shared quest-rule tests on Java 21 with Gson 2.10.1
- `src/main/java/`: Minecraft 26.1.2 native runtime and client interface (Java 25)
- `versions/1.21.1/`: native Java 21 / NeoForge 21.1.248 backport adapters and tests (`:mc1211`, no installable artifact yet)
- `src/main/resources/data/firsttorch/guides/`: server guide definitions
- `src/main/resources/assets/firsttorch/`: translations, icons, and guide illustrations
- `src/main/templates/`: generated NeoForge metadata templates
- `.run/`: shared IntelliJ IDEA run configurations
- `tools/`: native validation and packaging checks
- `docs/`: active project documentation
- `archive/ftb-legacy/`: unsupported historical FTB pack snapshot
- `build/`: generated output; ignored by Git

Never commit mod JARs, worlds, logs, player progress, or generated build output.

## Build and verification

Use Java 25. For local development, import the repository as a Gradle project and launch **First Torch Client**, or run:

```powershell
.\gradlew.bat runClient
```

For an installable native JAR:

```powershell
.\gradlew.bat test build
pwsh .\tools\verify-native-jar.ps1 -JarPath .\build\libs\firsttorch-mc26.1.2-0.14.0-alpha.1.jar
```

`verify-native-jar.ps1` enforces the native package and resource boundary. It rejects nested JARs, unexpected classes, world/config data, duplicate entries, and missing required resources. It is a packaging check, not proof of dependency licensing, accessibility, or multiplayer parity.

After guide, translation, progression, or interface changes, use a fresh test world where practical. Check English and German, automatic and manual task completion, prerequisite gating, rewards, persistence after restart, and the affected reading/image layout. Development-only completion controls do not prove that automatic objectives work in survival.

## Multiple-version development

Open the repository root once in IntelliJ and reload Gradle after pulling the module split. The root project remains the Minecraft `26.1.2` runtime; `:core` is a plain Java library, not a separately installed mod. Gradle selects Java 21 for the shared core and Java 25 for the current runtime. No per-Minecraft IntelliJ project is required.

The root mod declares both source sets for development launches and includes core classes directly in its JAR. Do not install the core JAR, add a nested library JAR, or duplicate shared sources into each target. The root `check` task depends on `:core:check` and `:mc1211:check`, so both `test build` and an explicit `:build` retain the shared-rule gate. Use `:core:test` for changes confined to pure quest logic; use the Minecraft test suite for runtime integrations.

The existing `First Torch Client` run configuration still launches `26.1.2`. The `:mc1211` module now pins NeoForge `21.1.248` and Java 21 and implements native storage/reward/server adapters and payload codecs. Run `./gradlew :mc1211:test` (Windows: `.\gradlew.bat :mc1211:test`) for the shared data-contract and target-specific storage tests. A test-only mod fixture enables the native NeoForge JUnit registry context. The module has no production mod entry point, client handlers/UI, client run configuration or target curriculum yet; JAR generation is disabled until the native runtime is complete. There is no installable `1.21.1` artifact. See [Backport1211.md](Backport1211.md) for the API/content assessment and pending acceptance.

Artifacts now include their Minecraft target in the filename. First Torch release numbers and Minecraft version numbers remain separate; publishing a backport later does not change Minecraft version ordering.

## Native guide and progress model

Guide definitions are datapack resources under `data/firsttorch/guides/`. Reloading validates the complete guide set before it atomically replaces the server snapshot; an invalid reload leaves the previously published definitions active. Keep player-facing text in `en_us` and `de_de` together.

Progress is server-authoritative and keyed to the player. Definitions and the executing player's live progress are synchronised on login and reload. Storage or transport failures must present an unavailable state rather than stale progress. Preserve stable guide, quest, task, and reward identifiers: existing worlds may contain their completion and reward state.

See [Progress safety](ProgressSafety.md) for the stopped-world backup boundary,
upgrade constraints, reward crash limitations and pending respawn checks.

Automatic inventory, advancement, and location objectives are observations; manual confirmations remain explicit learner actions. A task may be observed before prerequisites unlock, but a quest completes only when its requirements and prerequisite quests are satisfied. Rewards remain separately claimable under the shared eligibility policy.

## Interface and accessibility

The native browser provides chapter navigation, progressive visibility, completed-course archiving, search, guide images, reference links, manual tasks, rewards, and a reference library. Reference links use `assets/firsttorch/quest_links.json`; preserve target IDs and bilingual labels when changing them.

`enlargedView` and `quietSurfaces` are opt-in client preferences. The enlarged view scales the native quest browser while retaining a usable logical canvas; quiet surfaces remove decorative gradients while preserving controls and selection feedback. Node narration announces availability, completion, and reward state before lesson text. These features help accessibility but do not replace the remaining accessibility acceptance work in the roadmap.

## Assets and safety

Minecraft-derived images must use exact assets from the targeted Minecraft version. Never generatively redraw Minecraft items, blocks, entities, or interfaces. Keep neutral explanatory overlays distinct from the original assets. For crafting guides, use the established straight, consistent three-dimensional block-model view unless the guide is genuinely a top-down plan.

Never copy player data into the repository. Do not make a release or test workflow modify saves without explicit user authorisation and a recoverable backup. The historical archive is read-only reference material: changes to active native content must not recreate an active FTB workflow.

Compatible runtime sources and shared tests for 1.21.1 are selected explicitly in its Gradle build and copied under its generated build directory. Edit the original shared files, never generated copies. Keep version-dependent adapters under the target source directory and preserve the full baseline test expectations.
