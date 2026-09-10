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

Batch 2 accepted by the user. Its visual checklist: shield (mixed grid), Glass Bottle (x3), Blaze
Powder (x2) and Eye of Ender (two shapeless ingredients). Check normal/enlarged view
and scrolling. Existing screenshots and captions remain unchanged.

## Batch 3: brewing interface illustrations

Four brewing steps now reference the installed game's brewing GUI/fuel sprite and
render actual Potion item stacks with PotionContents: Water plus Nether Wart,
Awkward plus Blaze Powder, Awkward plus Magma Cream, and Fire Resistance plus
Redstone. They show the loaded stand before conversion, as the previous generation
script did; no fabricated output colour or altered lesson sequence is introduced.
The 256x256 GUI crop and 18x4 fuel sprite dimensions were checked in the original
26.1.2 client archive. The old four raster composites are excluded from resources.
Total migrated illustrations: 26 (22 crafting, 4 brewing).

Batch 3 accepted by the user. Its visual checklist: inspect the Awkward and extended Fire Resistance guides.
Check that the three bottles and ingredient occupy their slots, the fuel bar is
visible and scrolling/enlarged view do not clip the interface. IntelliJ's restarted
First Torch Client uses these changes without copying a JAR. No progress reset.

## Batch 4: paired recipe diagrams

Migrated Fence/Gate, Paper/Map and Bow/Arrows as ordered pairs of live crafting
diagrams. Each retains both recipes, native models and result counts (three fences,
three paper, four arrows). Layouts/results were checked against the original 26.1.2
recipe JSON. Following user feedback, pairs use the full reading-column width with
height derived from the two panels, without a padded outer frame. Each individual
panel retains its aspect ratio. The three raster composites are excluded; total
migrated images: 29.

Standing layout rule for future migrations: multi-panel illustrations use the full
reading-column width and tightly fitted height; single illustrations and screenshots
remain compact. Do not squeeze multiple recipes into the half-width default.

Batch 4 accepted by the user after the full-width correction, including the paired
panels and their readability.

## Batch 5: utility crafting diagrams

Added Bed, Boat, Stone Hoe, Lodestone and Stonecutter using the existing native
single-recipe renderer. Preserved the previous ingredient placement (including
bottom-aligned two-row recipes), representative materials and single output counts.
All five recipes were verified against the original 26.1.2 client recipe JSON;
Lodestone uses an iron ingot, not the obsolete Netherite recipe. Their PNGs remain
in source for comparison but are excluded from processed resources.
Total migrated illustrations: 34 (27 single crafting, 4 brewing, 3 paired).

Batch 5 accepted by the user: Bed, Boat and Stonecutter in normal/enlarged view and
while scrolling, including native block and result models.
Quest definitions, captions and player progress remain unchanged. No reset.

## Batch 6: paper-based shapeless recipes

Migrated Paper/Book as a full-width pair and short Firework Rockets as a compact
single diagram. Shapeless ingredients are grouped in first-occurrence order with
explicit counts: three Paper plus one Leather, rather than a misleading fixed grid.
The existing two-input shapeless recipes keep their layout. Paper still shows its
required row of three Sugar Cane and output count three; Rockets show Paper plus
one Gunpowder and output count three, without a Firework Star.

Verified original 26.1.2 paper/book recipe JSON and the special rocket recipe plus
its cached game implementation (one fuel gives flight duration one; no stars means
no explosions). The two raster composites are excluded from processed resources.
Total migrated illustrations: 36 (28 single crafting, 4 brewing, 4 paired).

Batch 6 accepted by the user: Paper/Book (full width and three-Paper count), short Rockets
(three outputs), and Eye of Ender as an existing shapeless regression. Check normal
and enlarged view while scrolling. No quest, caption or progress changes.

## Batch 7: four-panel armour recipes

Migrated the Copper Helmet, Chestplate, Leggings and Boots overview as four native
recipes, preserving its material and reading order. The shared multi-panel renderer
now supports two rows of two recipes at full reading-column width. Each cell keeps
the accepted recipe aspect ratio and no padded outer frame is added. Existing pairs
retain their single-row layout. Verified all four recipes against original 26.1.2
recipe JSON (5/8/7/4 Copper Ingots). The old armour raster is excluded from resources.
Total migrated illustrations: 37 (28 single crafting, 4 brewing, 5 multi-panel).

Batch 7 accepted by the user: the armour overview in normal/enlarged view and while
scrolling; all four outputs must remain readable, with captions below the second
row. Compare Paper/Book as a two-panel regression. No progress reset or quest changes.

## Batch 8: smelting and collecting

Charcoal and Cooking Food now render two original furnace interfaces side by side
at full reading width. Input, representative fuel and output use native item models;
the second panel retains a highlighted result-to-inventory teaching arrow and result
example. These are explanatory diagrams, not live inventory state. Original GUI
dimensions (256x256; displayed 176x166), fire/arrow sprites and all item positions
were checked against 26.1.2 assets and menu/screen source. Charcoal and cooked beef
smelting recipes were checked in the original recipe JSON. Their old rasters are
excluded; total migrated illustrations: 39 (28 crafting, 4 brewing, 5 recipe-panel
composites, 2 smelting composites). Captions and quest/progress data are unchanged.

Batch 8 accepted by the user: Charcoal and Cooking Food in normal/enlarged view and while
scrolling. Check ingredient/fuel/output placement, both steps and the collection
arrow. No reset is needed.

## Batch 9: small crafting grid and collection

Log to Planks now uses native block models in an original explanatory 2x2 grid,
followed by a highlighted collection step. Both panels use full reading width;
the four-Plank output count is shown at the result and inventory example. The
original 26.1.2 Oak Planks recipe confirms one Oak Log produces four Planks.
No workbench-sized 3x3 grid is substituted for the player's small crafting grid.
The old raster is excluded; total migrated illustrations: 40. The mixed scene in
Place Crafting Table is deliberately unchanged and remains a separate migration.

Batch 9 accepted by the user: Log to Planks, normal/enlarged and while scrolling. Check the
four small-grid slots, count four and highlighted inventory destination. No reset.

## Batch 10: archaeology block comparison

The Sand/Suspicious Sand and Gravel/Suspicious Gravel comparison now uses installed
block-item models. Verified the original 26.1.2 item definitions point to Sand,
Suspicious Sand 0, Gravel and Suspicious Gravel 0 respectively: the unbrushed states
named in the existing bilingual caption. Preserved numbers 1–4 and reading order.
Four equal panels use full reading width and identical model scale/orientation.
The raster comparison is excluded; total migrated illustrations: 41.

Batch 10 accepted by the user: Archaeology's block comparison, numbers and caption.
No changes to lessons, screenshots or progress.

## Batch 11: enchanting interface

The numbered enchanting illustration uses the installed 26.1.2 GUI texture,
offer/level sprites and native Iron Pickaxe/Lapis Lazuli items. Verified input
coordinates (15,47 and 35,47), offer rows (60,14 plus 19 per row), texture sizes
and numbered bilingual caption against the original game source/assets. Offers
remain schematic: no invented enchantment names or guaranteed costs. The single
surface stays compact, with overlays outside the slots. No quest/progress changes.
The old raster is excluded; total migrated illustrations: 42. Build/tests passed.

Batch 11 accepted by the user: enchanting interface and its three numbered regions.

## Batch 12: cartography table

Four equal full-width panels show the crafting recipe, scaling, copying and locking.
Native item rendering replaces the raster and uses the actual Glass Pane item, not
a glass-block texture. Verified the recipe (two Paper, four Planks) and operations
against the 26.1.2 recipe JSON and CartographyTableMenu. Copying produces two maps;
scaling and locking produce one. Bilingual labels identify each operation without
inventing a map preview or treating these operations as workbench recipes.
All four panels retain the established 300:169 ratio without an empty outer frame.
Total migrated illustrations: 43. Quest IDs, progress and existing caption unchanged.

Pending visual check: cartography illustration, all four panels and labels in both
languages, normal/enlarged and while scrolling. No progress reset needed.

## Collective migration: remaining instructional scenes

At the owner's request, all remaining assembled diagrams were migrated together
for one collective playtest, rather than further individual-image handoffs.
The 48 new scenes comprise 13 Redstone plans, 20 Overworld/Nether instructional
layouts, 14 End diagrams and one Bastion layout. Together with the previous 43,
all 91 assembled illustrations now render from installed Minecraft assets.

The shared scene renderer uses native items, detached entity previews, original
texture crops and directional top faces resolved from installed block models.
Redstone wire uses the original dust textures and Minecraft's per-power tint.
Reload and logout clear cached models. No entities are spawned into the world.
Explanatory labels are maintained in both languages. Plans retain safety geometry,
numbered sequences and the corresponding existing captions. Multi-panel scenes
use full reading width; single scenes remain compact.

Fifteen approved gameplay captures remain byte-identical. This includes the six
End captures without a capture suffix: battlefield, city search, exit portal,
gateway access, island crossing and chorus harvest. Historical diagram PNGs remain
in source for comparison but are excluded from packaged resources. The 106 unique
image resources still serve the same 111 course placements. No quest, task, reward
or progress IDs changed. Screenshot/branding provenance review remains separate;
this work does not itself clear publication permissions.

Verification: 457 tests passed, full production build and native-JAR boundary check
passed. All explicit scene texture, item-definition and model resources were checked
against the installed 26.1.2 JAR; none were missing. Regression tests cover scene
catalog coverage, both languages, dimensions/UVs, Redstone states and important
End safety geometry. The test emits build/reports/live-scenes.json for asset audit.

## Combined visual acceptance (pending)

Batches 1 through 11 remain accepted. Cartography and this collective migration
await the owner's in-game review; automated checks are not visual acceptance.
Restart the IntelliJ client and browse the course and reference illustrations:

- Crafting, brewing, smelting and cartography: original item models and readable panels.
- Movement, hunger, boats, shelter, farming, enchanting, trading and Nether diagrams:
  labels, native entity previews and spatial explanations.
- Redstone: wire lines/brightness, repeater/comparator direction and observer faces.
- End: portal frames, crystal removal, roof clearance, dragon previews, egg steps,
  chorus/shulker safety and Elytra course; real screenshots must remain unchanged.
- Check normal/enlarged layouts and scrolling; sample both languages and resource reload.

No new world, completion reset or reward replay is needed for this visual review.
