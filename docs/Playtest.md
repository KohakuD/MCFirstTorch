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
