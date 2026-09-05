# Roadmap

The active milestone is **0.9.0 — The End**, with a deliberate Overworld curriculum interlude before outer-island exploration continues. Version **0.2.0** is the latest completed and tested release. The 0.3.0 through 0.8.0 content remains under in-game review, and the current development build is **0.9.0**.

## 0.1.0 — First Safe Night

- [x] Independent repository and reproducible pack manifest
- [x] FTB Quests JSON5 foundation
- [x] English and German quest text
- [x] Guided path: orientation, wood, crafting table, Wooden Shovel, emergency shelter, Bed, then Pickaxe, Stone, Furnace, Chest storage, Torches, permanent shelter, and first-morning check
- [x] Automated metadata and translation validation
- [x] Fresh-profile startup test on Minecraft 26.1.2
- [x] Full quest completion test in English
- [x] German text and layout review in game
- [x] Beginner playtest and wording adjustments
- [ ] First public release archive

### 0.1.1 startup fix

- [x] Remove unused KubeJS integration after its Better Advanced Tooltips dependency crashed during startup
- [x] Rebuild the minimal pack with only the required FTB quest stack
- [x] Confirm a clean CurseForge profile reaches the Minecraft title screen

### 0.1.2 quest ID fix

- [x] Confirm a clean CurseForge profile reaches the title screen and creates a world
- [x] Diagnose unnamed chapters and generic checkmark titles from screenshots
- [x] Move IDs outside the signed Java `long` range into FTB Quests' supported range
- [x] Reject out-of-range IDs during validation
- [x] Add a backed-up in-place updater for development profiles
- [x] Confirm chapter, quest, and task titles in the updated 0.1.2 profile

### 0.1.3 beginner controls and reliable quantities

- [x] Explain right-hand default, attack, use, breaking, pickup, and inventory movement explicitly
- [x] Add illustrated guides for breaking a log and crafting planks
- [x] Require eight Cobblestone and eight Torches with FTB Quests' task-level count field
- [x] Reject ignored item-stack quantities during validation
- [x] Extend the safe updater to install and enable only the pack-owned guide images
- [x] Confirm both illustrations render in the existing profile
- [x] Confirm the Cobblestone task stays incomplete at 1–7 and completes at 8

### Optional movement-controls refinement

- [x] Add a non-gating branch after the mouse-control lesson
- [x] Teach jumping onto a full block while explaining that slabs and stairs are climbed without jumping
- [x] Teach safe sneaking at a low edge, including the slab-and-stair descent exception and its other limits
- [x] Teach both default sprint inputs, Hunger use, and route safety
- [x] Finish with one controlled sprint-jump and a small food-and-XP reward
- [ ] Complete all four exercises once in survival mode and inspect both languages

### 0.1.4 illustrated crafting sequence

- [x] Fix the guide resource-pack metadata for Minecraft 26.1.2's version-pair format
- [x] Add illustrations for placing the Crafting Table, crafting a Wooden Pickaxe, crafting a Furnace, and making Charcoal
- [x] Add a Stone Axe lesson after the first Cobblestone so beginners stop breaking wood by hand
- [x] Show the stick recipe in the Crafting Table's 3 × 3 grid
- [x] Explain stone collection explicitly as the same interaction loop as collecting wood, with the pickaxe as the key difference
- [x] Confirm all six illustrations render in the existing profile

### 0.1.5 flexible beginner objectives

- [x] Accept four planks of any vanilla wood type, including mixed stacks
- [x] Pin FTB Filter System and FTB XMod Compat for item-tag objectives
- [x] Remove the premature instruction to bring food before food has been taught
- [x] Document why native CurseForge pack-version display requires a published CurseForge project
- [x] Confirm the filter modules reach the title screen in the existing profile
- [x] Confirm the plank task stays incomplete at 1–3 and completes at 4 with mixed wood types

### 0.1.6 gentle rewards

- [x] Explain how to claim rewards before the first reward appears
- [x] Give two Apples after the controls lesson and two after making Torches
- [x] Give three Bread as a bridge into the food chapter
- [x] Give one Lantern as the First Torch completion trophy
- [x] Validate exact reward items, quantities, and stable reward IDs
- [x] Confirm all four rewards display and can be claimed exactly once in game

### 0.1.7 reward quantity fix

- [x] Move Apple and Bread quantities to FTB Quests' reward-level `count` field
- [x] Keep the displayed item stack at one, as required by the 26.1 item reward format
- [x] Reject ignored quantities inside reward item stacks during validation
- [x] Confirm a fresh claim grants 2 Apples, 2 Apples, 3 Bread, and 1 Lantern

### First-night safety refinement

- [x] Add an early Wooden Shovel lesson and distinguish soil, stone, and wood tools
- [x] Branch first-night safety from the Crafting Table so it does not wait for the complete tool route
- [x] Teach a quick, fully sealed emergency shelter before requiring a lit permanent home
- [x] Move the three-White-Wool reward and Bed lesson directly behind the emergency shelter
- [x] Rejoin the route after the learner has both a Bed and a lit shelter with protected storage
- [x] Add exact-texture guides for the Wooden Shovel recipe and emergency shelter entrance
- [ ] Complete the revised route once from a fresh survival world and inspect both languages

## 0.2.0 — Becoming Independent

- [x] Hunger, food sources, and safe cooking
- [x] Health, armour, shields, and avoiding unnecessary fights
- [x] Mining safely: stairs, light, water, gravel, and getting home
- [x] Iron tools, bucket, shield, and basic armour
- [x] Coordinates, landmarks, and a simple home routine
- [x] Let already-known skills complete without punishment through automatic inventory checks and non-gating practice quests

### Food foundation

- [x] Explain the ten-part Hunger bar, eating control, and sprint threshold without assuming knowledge of game modes
- [x] Detect the first consumed item automatically through the vanilla Husbandry advancement
- [x] Keep the eating lesson optional so it does not gate food collection
- [x] Accept several locally available raw food sources instead of requiring one biome-specific item
- [x] Illustrate the Furnace flow from raw Beef to cooked Steak
- [x] Explain the different burn durations of Sticks, wood, Coal, and Charcoal in the first Furnace lesson
- [x] Explain how high Hunger enables natural health regeneration
- [x] Warn explicitly about Rotten Flesh, Raw Chicken, Spider Eyes, Poisonous Potatoes, and Pufferfish
- [x] Require four cooked foods, allowing mixed supported types
- [x] Add one Apple, one Charcoal, and one Cookie as non-skipping milestone rewards
- [x] Confirm the new chapter unlocks after the safe-morning quest
- [x] Confirm both smart filters accept every listed ingredient and reject unrelated food
- [x] Confirm both new guide images render correctly
- [x] Complete the food path once in German and inspect it once in English

### Protection foundation

- [x] Explain damage, safe retreat, and recovery before teaching combat equipment
- [x] Teach the Stone Pickaxe and warn that a Wooden Pickaxe cannot collect Raw Iron
- [x] Guide the learner from exposed Iron Ore through smelting the first Iron Ingot
- [x] Show Iron Ore with the live in-game item model so active resource packs are respected
- [x] Illustrate the exact Shield recipe and its offhand placement with the correct left-mouse control
- [x] Illustrate all four Armour recipes in one compact, consistent 2 × 2 overview
- [x] Add a parallel Copper branch that unlocks Armour crafting after either Copper or Iron
- [x] Keep the Shield lesson exclusive to the Iron branch
- [x] Explain armour slots without requiring a full set of Iron Armour
- [x] Teach raising and facing with a Shield, including important limits
- [x] Make avoiding unnecessary fights an explicit skill
- [x] Return one Iron Ingot as a small non-skipping milestone reward
- [x] Introduce small XP-point rewards in the welcome and armour-crafting lessons
- [x] Confirm the Stone Pickaxe, Raw Iron, Iron Ingot, and Shield item tasks in game
- [x] Confirm both new guide images render correctly
- [x] Complete the protection path once in German and inspect it once in English

### Safe mining foundation

- [x] Require two suitable Pickaxes, 16 Torches, and four cooked foods before going deeper
- [x] Illustrate a walkable staircase and explicitly forbid digging straight down
- [x] Teach a consistent right-going-in, left-going-home Torch rule
- [x] Explain falling Gravel and Sand as suffocation hazards
- [x] Teach retreating and blocking the opening when Water or Lava appears
- [x] Make returning before supplies run out part of the mining routine
- [x] Add 10 XP points as a non-skipping safe-return reward
- [x] Teach Chest crafting and basic item storage before the first longer outing
- [x] Wrap the chapter into three thematic rows so the full path remains readable on smaller displays
- [x] Confirm mixed suitable Pickaxes satisfy the two-item preparation task
- [x] Confirm both mining guide images render correctly
- [x] Complete the safe-mining path once in German and inspect it once in English

### Finding home foundation

- [x] Keep the topic in a short dedicated chapter instead of extending the already dense mining map
- [x] Test a visible home landmark from a safe distance
- [x] Explain X, Y, and Z, including minus signs and laptop function keys
- [x] Record the real home coordinates outside the world
- [x] Practise a short daylight return using the landmark first and coordinates as a backup
- [x] Explain that coordinates point toward the goal but do not guarantee a safe route
- [x] Add 5 XP points as a non-skipping route-test reward
- [x] Give one Compass after the tested route without replacing landmarks or coordinates
- [x] Explain that a normal Compass points to the world spawn, not to a Bed or shelter
- [x] Teach the Stonecutter and use eight Stone to make Chiseled Stone Bricks efficiently
- [x] Craft a Lodestone with the current eight-brick and one-Iron recipe
- [x] Detect binding the rewarded Compass to a Lodestone automatically, then test it from a safe distance
- [x] Confirm the chapter unlocks after the safe-return quest
- [x] Check the F3 wording against the Minecraft 26.1.2 debug screen in German and English
- [x] Complete the practice route once in survival mode
- [x] Confirm the Stonecutter, eight-brick, Lodestone, and binding steps in survival mode
- [x] Confirm both new recipe images render correctly

### Iron essentials foundation

- [x] Keep Iron Pickaxe and Bucket training in a short dedicated chapter
- [x] Unlock the chapter from the first Iron Ingot and add a diamond-shaped in-map link with navigation guidance
- [x] Require six Iron Ingots without consuming them before the two recipes branch
- [x] Refer back to the shared Pickaxe shape without repeating already-learned slot instructions
- [x] Explain that Stone and Copper Pickaxes cannot collect Iron-tier ores such as Diamond Ore
- [x] Teach the three-Ingot Bucket recipe
- [x] Require a Water Bucket and practise placing and retrieving a source block safely
- [x] Teach both 2 × 2 and 1 × 3 infinite Water sources directly after the first Water Bucket exercise
- [x] Explain diagonal source placement for 2 × 2 and centre-only collection for 1 × 3
- [x] Add a language-neutral top-down guide made from exact Minecraft 26.1.2 Water and Bucket textures
- [x] Rejoin both branches in a short safety recap with a 5 XP reward
- [x] Confirm the six-Ingot task remains incomplete at 1–5 and completes at 6
- [x] Confirm the Iron Pickaxe, Bucket, and Water Bucket tasks in game
- [x] Confirm the separate Iron Pickaxe and Bucket recipe images render in their matching quests
- [x] Practise placing and retrieving Water once in survival mode
- [ ] Build and collect twice from both infinite Water-source layouts in survival mode

### Bed onboarding refinement

- [x] Grant three White Wool after the emergency-shelter lesson instead of giving a finished Bed
- [x] Teach the White Bed recipe, placement, sleeping, and the respawn point explicitly
- [x] Keep the learner responsible for crafting the Wooden Planks and Bed
- [x] Confirm the three-Wool reward quantity and Bed guide image in game
- [x] Sleep once and confirm the Bed becomes the player's respawn point

## 0.3.0 — Sustainable Supplies

- [ ] Renewable crops and a dependable food routine
- [ ] Basic animal care and breeding
- [ ] Better storage and simple organisation
- [ ] Renewable materials near home

### First Wheat farm

- [x] Collect three Wheat Seeds from short grass without implying every tuft drops one
- [x] Craft a Stone Hoe with an exact compact recipe guide
- [x] Prepare a small watered field beside home and warn against trampling Farmland
- [x] Illustrate that one central Water block hydrates a 9 × 9 area with up to 80 Farmland blocks
- [x] Detect planting Wheat automatically through the Vanilla Husbandry advancement
- [x] Explain growth stages and require three Wheat from mature crops
- [x] Craft the first home-grown Bread with an exact compact recipe guide
- [x] Reinforce replanting and keeping spare Seeds as the renewable-resource habit
- [ ] Confirm the chapter unlocks after the cooked-food reserve
- [ ] Confirm the three-Seed and three-Wheat quantities in survival mode
- [ ] Confirm planting completes automatically only after Wheat Seeds are planted
- [ ] Confirm both recipe images render correctly
- [ ] Complete the first farm path once in German and inspect it once in English

### Basic animal care

- [x] Require a closed starter pen before any animal is lured home
- [x] Teach the exact wooden Fence and Fence Gate recipes together
- [x] Explain matching food for Cows, Sheep, Chickens, and Pigs
- [x] Allow several common farm-animal choices without requiring a biome-specific species
- [x] Teach slow, reliable luring and closing the Gate after each animal
- [x] Detect the first successful breeding automatically through the Vanilla Husbandry advancement
- [x] Protect the renewable breeding pair and establish a stored-food routine
- [ ] Confirm mixed wooden Fence types satisfy the 15-Fence task
- [ ] Confirm a wooden Fence Gate satisfies the Gate task
- [ ] Confirm the food task accepts each documented food and rejects unrelated items
- [ ] Confirm breeding completes automatically only after a baby is produced
- [x] Confirm the combined Fence and Fence Gate recipe image renders correctly
- [ ] Complete the animal-care path once in German and inspect it once in English

### Simple home storage

- [x] Expand the known Chest recipe into three dedicated storage areas
- [x] Explain separate Chests and optional Large Chests without forcing one layout
- [x] Craft three matching Signs and label the storage categories clearly
- [x] Sort building materials, food and farming supplies, and equipment separately
- [x] Establish a repeatable unload, refill, and free-inventory-space routine
- [ ] Confirm the three-Chest quantity in survival mode
- [ ] Confirm all Vanilla wooden Sign variants satisfy the three-Sign task
- [ ] Confirm every placed Chest opens with its intended layout
- [ ] Complete the storage path once in German and inspect it once in English

### Optional Composter sidequest

- [x] Branch from the first planted Wheat without blocking the main farm path
- [x] Teach crafting seven wooden Slabs and one Composter
- [x] Accept all Vanilla wooden Slab variants through the shared item tag
- [x] Provide 32 Wheat Seeds for a robust first composting test without harvesting the young field
- [x] Explain successful fill feedback, failed fill attempts, maturity, collection, and reuse
- [x] Reward the completed peaceful Bone Meal loop with 5 XP
- [ ] Confirm mixed wooden Slabs satisfy the seven-Slab task
- [ ] Confirm 32 Wheat Seeds plus the learner's ordinary plant leftovers are sufficient in survival play
- [ ] Complete the sidequest once in German and inspect it once in English

## 0.4.0 — Caves, Diamonds, and Enchanting

### First deep Diamond expedition

- [x] Unlock only after both the Iron essentials and sustainable-storage milestones
- [x] Require two Iron-or-better Pickaxes, a Water Bucket, a Shield, 32 Torches, and eight cooked foods
- [x] Teach reading X Y Z coordinates and recording the safe return entrance
- [x] Reach the Deepslate layer and collect 16 Cobbled Deepslate
- [x] Explain a practical search depth near Y −53 without promising an immediate find
- [x] Teach a lit branch tunnel, the existing right-wall Torch rule, and cautious Lava checks
- [x] Require three Diamonds before crafting the first Diamond Pickaxe
- [x] Add small supply rewards, 10 total XP, and one Golden Apple for emergencies
- [x] Add a diamond-shaped chapter link from the sustainable-storage endpoint
- [ ] Confirm the complete preparation checklist in survival mode
- [ ] Confirm the chapter remains locked until both prerequisite paths are complete
- [ ] Confirm Cobbled Deepslate and Diamond quantities complete automatically
- [ ] Inspect the suggested Y level and safety wording against Minecraft 26.1.2 world generation
- [ ] Complete the path once in German and inspect it once in English

### Planned 0.4.0 continuations

- [x] Continue from the Diamond Pickaxe through Obsidian, Books, Lapis Lazuli, and the Enchanting Table
- [x] Split Obsidian, renewable Sugar Cane and Books, and Lapis Lazuli into parallel preparation paths
- [x] Provide one Leather so the first Book never requires killing a protected breeding animal
- [x] Rejoin all three material paths before crafting the Enchanting Table
- [x] Detect the first successful enchantment with the exact Vanilla advancement criterion
- [x] Explain level requirements separately from the one-to-three levels and Lapis actually consumed
- [x] Add compact recipe guides for Paper and a Book, the Enchanting Table, and its original target-version interface
- [x] Explain Cows and Horses as common Leather sources while preserving the final breeding pair
- [x] Reward the preparation and first enchantment with practical materials and 25 total XP points
- [ ] Confirm Water converts Lava sources to Obsidian and flowing Lava to Cobblestone as described
- [ ] Confirm the Sugar Cane, Obsidian, Lapis Lazuli, Book, and Enchanting Table item tasks in survival mode
- [ ] Confirm the first enchantment completes automatically only after an item is enchanted
- [ ] Inspect the complete three-way branch in German and English
- [x] Add the Bookshelf expansion and explain the one-block air gap, 15-shelf maximum, and level-30 offers
- [ ] Confirm the Bookshelf recipe, 15-item threshold, layout guide, and level-30 offer in survival mode
- [x] Add an optional safe mob-drop branch covering common hostile-mob drops, including Bones and their Bone Meal use
- [x] Connect the hostile-mob branch back to the existing peaceful Composter route
- [ ] Confirm all preparation filters, four drop tasks, recipe images, and both chapter links in survival mode
- [ ] Complete the Skeleton-to-Bone-Meal path once in German and inspect it once in English

## 0.5.0 — Nether Preparation and First Visit

### Safe portal preparation in the Overworld

- [x] Unlock from the first successful enchantment without requiring the optional 15-Bookshelf expansion or mob-drop paths
- [x] Explain the Nether's lack of normal daytime, evaporating placed Water, and dangerous Bed behaviour before entry
- [x] Choose a portal site with clear access, keeping the immediate ignition area free of flammable blocks until activation succeeds
- [x] Collect Flint from Gravel without standing beneath falling blocks
- [x] Craft Flint and Steel with an exact target-version recipe guide and refund the Iron Ingot
- [x] Require ten ordinary Obsidian and distinguish unusable Crying Obsidian
- [x] Build an unlit minimum 4 × 5 frame with a 2 × 3 opening and optional corners
- [x] Illustrate the unlit frame and its activated state with matching authentic in-world screenshots
- [ ] Confirm the Flint, Flint and Steel, and ten-Obsidian item tasks in survival mode
- [ ] Confirm all five recipe and portal images plus the chapter link in German and English
- [x] Build and inspect both the unlit and activated frame at a safe Overworld site

### Planned 0.5.0 continuations

- [x] Prepare Nether-specific equipment, including one worn piece of Gold Armour
- [x] Light the portal only after the equipment check and keep the learner outside
- [x] Secure the Overworld side and enter only after a final checklist
- [x] Secure and mark the Nether-side portal before exploring
- [x] Teach the first short Nether scouting route, Piglin behaviour, Ghasts, fire, Lava, and safe retreat
- [x] Return through the original portal with the route intact

### What to Do in the Nether chapter foundation

- [x] Separate completed Nether preparation from later activities in a dedicated ninth bilingual chapter
- [x] Preserve the existing Piglin-barter quest, task, and reward IDs while moving the branch
- [x] Add a cross-chapter navigation link after the first successful marked return
- [x] Explain that an adult ordinary Piglin exchanges one Gold Ingot for one random result without a trading menu
- [x] Keep Gold Armour equipped and use a manual completion check instead of the unsafe no-Gold-Armour Vanilla advancement condition
- [x] Require a level place, Cobblestone cover, and an intact marked retreat before the first barter
- [x] Distinguish adult Piglins from babies, Piglin Brutes, and Zombified Piglins as trading partners
- [x] Illustrate the four Piglin variants with exact target-version textures and translucent rejection marks
- [x] Return the first Gold Ingot after a completed barter without making the optional branch gate later progress
- [ ] Complete one safe barter in survival mode and inspect both languages
- [x] Add a safe first-resources branch without requiring one particular Nether biome
- [x] Add a prepared Nether Fortress branch for Blazes, Blaze Rods, and Nether Wart
- [x] Arrange Piglin barter, first resources, and Nether Fortress as three visibly parallel branches
- [x] Illustrate the Fortress silhouette, its three principal enemies, and a Blaze Spawner with exact target-version assets
- [x] Add a separate advanced optional Bastion Remnant branch with explicit Piglin Brute and guarded-container rules

## 0.6.0 — Brewing and Potions

### First complete brewing sequence

- [x] Unlock only after the safe Nether Fortress return, without requiring the optional Bastion branch
- [x] Add a diamond-shaped map link from the Fortress return into the new chapter
- [x] Preserve exactly one Blaze Rod for the Brewing Stand and process the other into two Blaze Powder
- [x] Explain Blaze Powder separately as Brewing Stand fuel and as a potion ingredient
- [x] Craft and place a Brewing Stand with an exact target-version recipe guide using 3D block models
- [x] Smelt Glass when needed and craft three reusable Glass Bottles with an exact recipe guide
- [x] Fill three Water Bottles in the Overworld and place all three into the Brewing Stand
- [x] Brew three Awkward Potions from Nether Wart and detect the first completed brew automatically
- [x] Brew and drink the first Potion of Strength while explaining its limits
- [x] Explain Redstone duration, Glowstone strength, and the later Magma-Cream route to Fire Resistance
- [x] Add exact Minecraft 26.1.2 recipe and Brewing Stand interface images for every new visual step
- [ ] Confirm the chapter unlocks after the Fortress return but not through the Bastion path
- [x] Confirm the Blaze Powder, Brewing Stand, Glass Bottle, and first-brew tasks in survival mode
- [ ] Inspect all five new guide images in English after the completed German review
- [x] Complete the full first brewing sequence once in survival mode

### Optional Fire Resistance extension

- [x] Keep the branch completely optional and explain that a Potion never replaces ordinary Nether safety
- [x] Accept Magma Cream from either its shapeless Slimeball-and-Blaze-Powder recipe or a suitable Magma Cube drop
- [x] Explain Magma Cube splitting, unreliable drops, and a controlled retreat before combat
- [x] Brew up to three three-minute Potions of Fire Resistance from Awkward Potions and one Magma Cream
- [x] Use the earned Redstone Dust to extend Fire Resistance from three to eight minutes
- [x] Warn against deliberate Lava testing and explain the safe response to an accidental fall
- [x] Add exact recipe and Brewing Stand images for Magma Cream, Fire Resistance, and its extension
- [ ] Confirm Magma Cream detection and both manual brewing checks in survival mode
- [ ] Inspect all three new images in German and English

## 0.7.0 — Eyes of Ender and the Stronghold

### Preparing the Eye supply

- [x] Unlock directly from the completed first Strength sequence without requiring optional Fire Resistance
- [x] Separate Ender Pearl and Blaze Powder gathering into parallel preparation branches
- [x] Explain safe Enderman shelter combat, random Piglin bartering, and the experienced-Cleric trade as alternative Pearl sources
- [x] Warn that thrown Ender Pearls deal two and a half hearts of damage and can rarely spawn an Endermite
- [x] Require sixteen Ender Pearls and sixteen Blaze Powder before crafting sixteen Eyes of Ender
- [x] Add an exact Minecraft 26.1.2 shapeless recipe guide using the original item textures
- [x] Explain the twelve portal frames, the twenty-percent Eye break chance, and the rule to pause searching below twelve remaining Eyes
- [x] Reward two additional Eyes as a search reserve only after the full preparation task is complete
- [ ] Confirm all three sixteen-item thresholds and reward quantities in survival mode
- [ ] Inspect the chapter map, recipe image, and full text in German and English

### Planned 0.7.0 continuation

- [x] Prepare a complete Overworld expedition loadout before throwing the first Eye
- [x] Teach controlled Eye throws, recovery, route notes, and a safe return threshold
- [x] Locate the Stronghold without unsafe straight-down digging
- [x] Illustrate rising, forward-leading, and downward-diving Eyes with a language-neutral guide made from exact target-version textures
- [x] Detect the first Stronghold entry with the exact Vanilla advancement criterion
- [x] Require a secured entrance, recorded depth, and a successful return to the surface before exploration
- [x] Split controlled Stronghold exploration into a separate twelfth bilingual chapter
- [x] Teach a right-wall Torch rule while explaining why loops still require coordinates and junction markers
- [x] Explain that Stronghold Iron Doors require a short Redstone signal, show the nearby Stone Button, and require two spare Buttons for the return route
- [x] Add a combined exact-texture guide for the shapeless Stone Button recipe and an Iron Door in its Stronghold wall
- [x] Explain ordinary dark-room enemies separately from visually indistinguishable infested blocks and Silverfish
- [x] Keep Stronghold Libraries optional because generation can omit or truncate them
- [x] Identify the portal room through twelve frames, Lava, and its Silverfish Spawner
- [x] Secure a retreat, destroy the Spawner, light the room, and control reachable Lava before approaching the frames
- [x] Count existing and missing Eyes across all twelve frames without inserting any
- [x] Add exact-texture guides for Silverfish risk, the top-down portal-room arrangement, and empty versus filled frames
- [x] Return home with the frame count and Eyes stored together before activation
- [x] Activate the End Portal only after a separate End-expedition checklist
- [ ] Inspect the Stronghold chapter layout and all three new images in German and English
- [ ] Complete the marked labyrinth route, Spawner removal, twelve-frame count, and home return in survival mode

## 0.8.0 — Preparing the End Expedition

### Safe activation without entry

- [x] Continue from the completed Stronghold return inside the existing Stronghold chapter
- [x] Explain that safe return from the End requires defeating the Dragon and distinguish death from a normal return route
- [x] Explain the Void, exploding Beds, usable Water, Enderman eye contact, and the optional Carved Pumpkin
- [x] Teach the exact Bow and Arrow recipes and require a first 32-Arrow reserve
- [x] Reward another 32 Arrows only after the learner prepares the first half personally
- [x] Require an Iron-or-better Sword and Pickaxe, Shield, Water Bucket, 64 Cobblestone, and 16 cooked foods
- [x] Require a checked full Iron-or-better Armour set without forcing a long Diamond grind
- [x] Establish a slept-in Bed and reserve Chest in the Overworld Stronghold retreat room
- [x] Return over the known marked route and recheck all equipment before touching a frame
- [x] Remove only the temporary three-by-three centre cover and recount the empty frames
- [x] Insert the exact required Eyes from a safe outside position and remain outside the active portal
- [x] Add exact-texture guides for Bow and Arrow crafting and the final Eye placement
- [ ] Confirm the Bow, 32-Arrow, equipment, block, and food tasks in survival mode
- [ ] Inspect the extended Stronghold chapter layout and both new guide images in German and English
- [ ] Activate the portal from outside the frame and confirm that no accidental entry occurs
- [x] Enter the End only after a separate arrival and Dragon-fight plan

## Pre-1.0 Overworld expansion

The beginner playtest showed that practical Overworld exploration needs its own teaching space before the later End journey. The detailed sequencing, deferred topics, and acceptance criteria live in [Overworld-Roadmap.md](Overworld-Roadmap.md).

- [x] Define a separate, non-gating `What to Do in the Overworld?` curriculum
- [x] Add an exact-texture visual overview of the four common hostile mobs
- [x] Teach safe excursion preparation and a tested return routine
- [x] Teach Boat crafting, controls, recovery, and a short practice trip
- [x] Introduce Villages, Villagers, professions, workstations, and one simple trade
- [x] Explain Village safety, Iron Golems, Beds, Bells, farms, and respectful use
- [ ] Return home deliberately and record the Village for a later visit
- [ ] Complete the entire chapter and both-language visual review in survival mode

## 0.9.0 — The End

### Controlled arrival

- [x] Start a dedicated End chapter large enough to continue through Crystals, the Dragon, return, and later outer-island travel
- [x] Prepare the first ten seconds before entry with a fixed hotbar and stand-still, Sneak, look-down routine
- [x] Detect the first real End entry through the exact Vanilla entered-End advancement criterion
- [x] Explain both an End-Stone-enclosed platform and a platform floating beside the main island
- [x] Teach a diagonal staircase from an enclosed platform and a crouched two-wide bridge from a floating platform
- [x] Add a language-neutral top-down arrival guide made from exact Minecraft 26.1.2 block textures
- [x] Establish a Water retreat on safe End Stone without presenting it as protection from the Dragon or Void
- [x] Add an optional three-by-three End Stone roof with two-block clearance as a dry Enderman retreat
- [x] Show its safe centre and exact clearance in a language-neutral guide made from original target-version assets
- [x] Identify End Crystals, healing beams, Iron Bar cages, Dragon's Breath, the inactive Bedrock exit, and avoidable Endermen before combat
- [ ] Confirm the entered-End advancement task in survival mode
- [ ] Inspect the End chapter layout and arrival image in German and English
- [ ] Complete both arrival responses in controlled test worlds
- [ ] Build the optional shelter and confirm an Enderman cannot reach the player at its centre
- [x] Continue with a staged Crystal-removal plan before teaching direct Dragon attacks

### Staged Crystal removal

- [x] Destroy exposed End Crystals individually with fully drawn Bow shots from safe ground
- [x] Separate every shot with movement, hazard checks, and recovery instead of encouraging rapid fire
- [x] Require 64 End Stone for Dragon-resistant approaches to the caged Crystals
- [x] Open one cage side from an attached staircase, descend, and only then shoot from a safe distance
- [x] Verify all ten pillar tops and the absence of healing beams before regrouping
- [x] Give exposed Crystals and cages separate language-neutral guides made from exact Minecraft 26.1.2 textures
- [ ] Confirm the 64-End-Stone task and both manual completion checks in survival mode
- [ ] Inspect the Crystal guide and full lesson text in German and English
- [ ] Remove every Crystal once using the staged route and confirm that 16 replacement Arrows are granted
- [x] Continue with the flying and perched Dragon-fight phases

### Ender Dragon fight

- [x] Teach fully drawn Bow shots from broad inner ground, leading the moving Dragon, and reassessing after every shot
- [x] Separate Dragon's Breath avoidance, Enderman gaze control, and sideways dodging from the act of aiming
- [x] Teach that Arrows are ineffective while the Dragon is perched and approach from behind for recovered jumping strikes beneath its tail instead of entering the wing area
- [x] Require a controlled retreat from the Bedrock centre as soon as the Dragon takes off
- [x] Explicitly exclude explosive Bed tactics from the beginner route
- [x] Detect the first real Dragon victory through the exact Vanilla killed-Dragon advancement criterion
- [x] Add separate language-neutral flight and perched-phase guides made from exact Minecraft 26.1.2 textures
- [ ] Confirm both manual phase checks and the killed-Dragon advancement task in survival mode
- [ ] Inspect the two Dragon-fight guides and full lesson text in German and English
- [ ] Complete the full Crystal-to-Dragon sequence without using a Bed explosion
- [x] Continue with the opened exit, Dragon Egg, and safe Overworld return

### Victory and return

- [x] Separate post-fight recovery and experience collection from entering the newly opened exit portal
- [x] Identify the exit surface and unique Dragon Egg before allowing the player to choose what comes next
- [x] Add the Dragon Egg as a fully optional side branch that never gates the return
- [x] Teach the Torch retrieval method and detect the collected Egg through its exact Vanilla advancement criterion
- [x] Explain the End Poem, skippable credits, retained inventory and experience, and the return to a valid respawn point or world spawn
- [x] Add a top-down exit guide and a three-step Egg guide made from exact Minecraft 26.1.2 textures and the target-version Egg model silhouette
- [ ] Inspect the post-victory layout, both guide images, and all text in German and English
- [ ] Collect the Dragon Egg once and confirm its automatic advancement task
- [ ] Return once with and once without the optional Egg and confirm the manual completion flow
- [x] Continue with a deliberate second End visit and the route to the outer islands

### Outer-island arrival

- [x] Move every lesson from the second End visit onward into a dedicated `What to Do in the End?` chapter linked from the safe first return
- [x] Treat the outer islands as a separate expedition after the first safe return instead of extending the Dragon trip indefinitely
- [x] Require four Ender Pearls for outward travel, return, and two reserves plus 64 End Stone for safe access work
- [x] Reuse the secured Stronghold, arrival platform, and main-island landmarks during the second End entry
- [x] Identify the elevated End Gateway and require a crouched staircase, standing area, and guarded edges before throwing
- [x] Detect the first real Gateway transfer through the exact Vanilla entered-End-Gateway advancement criterion
- [x] Secure the outer-island arrival before exploration, visibly mark the return Gateway, and record its coordinates
- [x] Add separate access and arrival guides made from exact Minecraft 26.1.2 textures and align every structure, route, and marker to a shared ground level
- [ ] Confirm all preparation item tasks and manual safety checks in survival mode
- [ ] Inspect the Gateway access, arrival images, and full lesson text in German and English
- [ ] Complete an outward and return Gateway transfer while retaining at least one reserve Pearl
- [ ] Continue with Chorus plants, safe island crossings, and the first End City search
- [ ] Teach the first Elytra glide only after returning to a low, protected Overworld practice platform; introduce Firework Rockets after the unpowered glide is understood

## Later milestones

- CurseForge publication: native pack-version display and launcher-managed updates

The Nether remains a continuation beyond the original first 48-hour playable scope.
