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

Batch 1 accepted by the user. Its visual checklist: inspect wooden/iron pickaxe, furnace/chest and bucket in
normal/enlarged view, scroll past them and confirm no missing-image message, clipping
or stretched item models. Compare the unchanged fortress screenshot too. No reset.

## Batch 2: mixed ingredients and output amounts

Added shield, bookshelf, bread, Shulker Box, Brewing Stand, Glass Bottle, Enchanting
Table, Blaze Powder, Bone Meal, Eye of Ender, Flint and Steel, and Magma Cream.
There are now 22 runtime illustrations; all corresponding PNGs are excluded from
processed resources. Recipes were checked against the original 26.1.2 client recipe
JSON. Three Glass Bottles, two Blaze Powder and three Bone Meal are explicitly
labelled. Shapeless recipes use ingredient slots joined by plus signs, not a fixed
crafting-grid arrangement. Native item/block rendering remains unchanged.

Pending visual check for this batch: shield (mixed grid), Glass Bottle (x3), Blaze
Powder (x2) and Eye of Ender (two shapeless ingredients). Check normal/enlarged view
and scrolling. Existing screenshots and captions remain unchanged.

## Remaining batches

- Mixed-material and multiple-output recipes, smelting and brewing diagrams.
- Redstone circuits, directional models, wire lines and state diagrams.
- Other assembled instructional layouts, classified from their generation sources.
- Screenshots and public branding: separate provenance/permissions review, not an
  automatic replacement. No generative redraw of Minecraft assets.

Batch 1 is accepted; batch 2 introduces new amount/shapeless presentation requiring
visual acceptance. Remaining diagram families are not yet migrated.
