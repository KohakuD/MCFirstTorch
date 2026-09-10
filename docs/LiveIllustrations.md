# Runtime illustration migration

Owner approved the migration on 2026-09-10. This changes presentation and packaging,
not quest IDs, requirements, reward state, icons or owner screenshots.

## Batch 1: single-output crafting diagrams

Implemented a shared 3x3 recipe renderer using Minecraft's current item renderer.
Block ingredients/results use the native model view; frame/arrow geometry is original
code. Existing half-pane width, image aspect ratio and bilingual captions remain.
The existing image identifier selects a live diagram before the raster fallback.

Migrated: wooden pickaxe, wooden shovel, stone pickaxe, stone axe, iron pickaxe,
iron sword, furnace, chest, bucket and golden helmet. Layouts were checked against
the installed original Minecraft 26.1.2 `data/minecraft/recipe` entries. Oak planks,
cobblestone and iron ingots are representative ingredients for the relevant tags;
this is a lesson illustration, not an exhaustive recipe browser or recipe evaluator.

The ten old PNGs remain recoverable as source comparisons but are excluded by
processResources, including development classpaths and the release JAR. Tests
require each migrated raster to be absent and all remaining images to retain their
source bytes/dimensions and translated captions. New catalog entries must have a
matching build exclusion. This does not clear remaining assets for publication.

Pending visual check: inspect wooden/iron pickaxe, furnace/chest and bucket in
normal/enlarged view, scroll past them and confirm no missing-image message, clipping
or stretched item models. Compare the unchanged fortress screenshot too. No reset.

## Remaining batches

- Mixed-material and multiple-output recipes, smelting and brewing diagrams.
- Redstone circuits, directional models, wire lines and state diagrams.
- Other assembled instructional layouts, classified from their generation sources.
- Screenshots and public branding: separate provenance/permissions review, not an
  automatic replacement. No generative redraw of Minecraft assets.

Complete the visual acceptance of the shared renderer before migrating the remaining
diagrams. The current change is the first batch, not completion of the migration.
