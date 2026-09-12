# Minecraft 26.2 port preparation

This is the prerequisite assessment for the next roadmap phase, not an implemented or released 26.2 edition. The 1.21.1 public release remains pending under the existing phase order.

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