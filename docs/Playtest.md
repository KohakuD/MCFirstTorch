# Playtest log

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
