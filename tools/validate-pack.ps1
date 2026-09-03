[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$manifestPath = Join-Path $repoRoot 'manifest.json'
$questRoot = Join-Path $repoRoot 'overrides/config/ftbquests/quests'
$guidePackRoot = Join-Path $repoRoot 'overrides/resourcepacks/first_torch_guides'

function Assert-True {
    param([bool]$Condition, [string]$Message)
    if (-not $Condition) {
        throw $Message
    }
}

Assert-True (Test-Path -LiteralPath $manifestPath -PathType Leaf) 'manifest.json is missing.'
$manifest = Get-Content -LiteralPath $manifestPath -Raw | ConvertFrom-Json
Assert-True ($manifest.manifestType -eq 'minecraftModpack') 'Unexpected manifest type.'
Assert-True ($manifest.manifestVersion -eq 1) 'Unexpected manifest version.'
Assert-True ($manifest.minecraft.version -eq '26.1.2') 'Minecraft must remain pinned to 26.1.2.'
Assert-True ($manifest.minecraft.modLoaders[0].id -eq 'neoforge-26.1.2.84') 'NeoForge must remain pinned to 26.1.2.84.'
Assert-True ($manifest.overrides -eq 'overrides') 'Manifest overrides directory must be "overrides".'
Assert-True ($manifest.version -eq '0.7.0') 'The development pack version must be 0.7.0.'

$expectedFiles = @{
    '289412' = 8730542
    '404465' = 8574542
    '404468' = 8074003
    '943925' = 8300191
    '889915' = 8678090
}
Assert-True ($manifest.files.Count -eq $expectedFiles.Count) 'The pinned dependency count changed unexpectedly.'
foreach ($file in $manifest.files) {
    $projectKey = [string]$file.projectID
    Assert-True ($expectedFiles.ContainsKey($projectKey)) "Unexpected CurseForge project ID: $projectKey"
    Assert-True ($file.fileID -eq $expectedFiles[$projectKey]) "Unexpected file ID for CurseForge project $projectKey."
    Assert-True ($file.required -eq $true) "CurseForge project $projectKey must be required."
}

Assert-True (Test-Path -LiteralPath $questRoot -PathType Container) 'FTB Quests directory is missing.'
$legacyFiles = Get-ChildItem -LiteralPath $questRoot -Recurse -File -Filter '*.snbt'
Assert-True ($legacyFiles.Count -eq 0) 'Legacy SNBT quest files are not supported on Minecraft 26.1.2.'

$jarFiles = Get-ChildItem -LiteralPath (Join-Path $repoRoot 'overrides') -Recurse -File -Filter '*.jar'
Assert-True ($jarFiles.Count -eq 0) 'Mod JARs must not be distributed in overrides.'

$definitionFiles = Get-ChildItem -LiteralPath $questRoot -Recurse -File -Filter '*.json5' |
    Where-Object { $_.FullName -notmatch '[\\/]lang[\\/]' }
$allDefinitionText = ($definitionFiles | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"
$allLanguageText = (Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' |
    ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"
$invalidFormattingAmpersands = [regex]::Matches($allLanguageText, '(?<!\\)&\s')
Assert-True ($invalidFormattingAmpersands.Count -eq 0) 'Quest text contains an ampersand followed by whitespace; FTB Quests treats this as an invalid formatting code.'
$idMatches = [regex]::Matches($allDefinitionText, '\bid:\s*"([0-7][0-9A-F]{15})"')
$ids = @($idMatches | ForEach-Object { $_.Groups[1].Value })
$duplicateIds = $ids | Group-Object | Where-Object Count -gt 1
Assert-True ($ids.Count -gt 0) 'No FTB Quest object IDs were found.'
Assert-True ($duplicateIds.Count -eq 0) ('Duplicate FTB Quest IDs: ' + (($duplicateIds.Name) -join ', '))

$hexLikeIds = [regex]::Matches($allDefinitionText, '\bid:\s*"([^":]+)"') |
    ForEach-Object { $_.Groups[1].Value }
$invalidObjectIds = @($hexLikeIds | Where-Object { $_ -notmatch '^[0-7][0-9A-F]{15}$' })
Assert-True ($invalidObjectIds.Count -eq 0) ('Invalid FTB Quest object IDs; IDs must fit a positive signed Java long: ' + ($invalidObjectIds -join ', '))

$taskArrayText = ([regex]::Matches($allDefinitionText, 'tasks:\s*\[(?<body>[\s\S]*?)\]\s*,') | ForEach-Object { $_.Groups['body'].Value }) -join "`n"
$ignoredItemStackCounts = [regex]::Matches($taskArrayText, 'item:\s*\{[^}]*\bcount:\s*([2-9][0-9]*)', [System.Text.RegularExpressions.RegexOptions]::Singleline)
Assert-True ($ignoredItemStackCounts.Count -eq 0) 'Item task quantities above one must use the task-level count field; a count inside item is ignored by FTB Quests.'

$expectedTaskCounts = @{
    '45B9134C7FDA6802' = 4
    '4B1F79A2D530CE68' = 8
    '0F53BDE6197402AC' = 8
    '3D85F1C70A624BE9' = 4
    '2B73E9F40D856AC1' = 2
    '3C84FA150E967BD2' = 16
    '4D950B261FA78CE3' = 4
    '38BAE5F71C946D02' = 6
    '1D53F9B62E84AC07' = 3
    '15DB713EA60C248F' = 3
    '285CE3B719F46DA2' = 15
    '7DA1380C6E49B2F7' = 2
    '762AC195F73D4B8E' = 3
    '3A6E05D93B718FC2' = 3
    '2BD17C3FA648E509' = 7
    '2D5FB17380C49E6A' = 2
    '5082E4A6B3F7C19D' = 32
    '6193F5B7C408D2AE' = 8
    '25D739FB084C16E2' = 16
    '691B73DF4C805A26' = 3
    '2E60C28491D5AF7B' = 4
    '5193F5B7C408D2AE' = 3
    '26E840AC195D27F3' = 3
    '2A327F4850B617C3' = 6
    '3B43805961C728D4' = 3
    '3D974A06E1B58F2C' = 2
    '71DB8E4A25F9C360' = 3
    '51CA8E3A04E8B26F' = 16
    '73ECA05C260AD481' = 16
    '250EC27E482CF6A3' = 16
    '4DA17E3904E8B26F' = 2
}
foreach ($taskId in $expectedTaskCounts.Keys) {
    $expectedCount = $expectedTaskCounts[$taskId]
    $taskPattern = 'id:\s*"' + $taskId + '"[\s\S]{0,180}?type:\s*"item"[\s\S]{0,180}?count:\s*' + $expectedCount + '\s*,[\s\S]{0,180}?item:'
    Assert-True ([regex]::IsMatch($allDefinitionText, $taskPattern)) "Task $taskId must require $expectedCount items with a task-level count field."
}

$expectedSmartFilters = @{
    '45B9134C7FDA6802' = 'ftbfiltersystem:item_tag(minecraft:planks)'
    '09E4A72D5C813BF6' = 'or(item(minecraft:beef)item(minecraft:porkchop)item(minecraft:chicken)item(minecraft:mutton)item(minecraft:rabbit)item(minecraft:cod)item(minecraft:salmon)item(minecraft:potato)item(minecraft:kelp))'
    '2E73C5A09D164BF8' = 'or(item(minecraft:cooked_beef)item(minecraft:cooked_porkchop)item(minecraft:cooked_chicken)item(minecraft:cooked_mutton)item(minecraft:cooked_rabbit)item(minecraft:cooked_cod)item(minecraft:cooked_salmon)item(minecraft:baked_potato)item(minecraft:dried_kelp))'
    '3D85F1C70A624BE9' = 'or(item(minecraft:cooked_beef)item(minecraft:cooked_porkchop)item(minecraft:cooked_chicken)item(minecraft:cooked_mutton)item(minecraft:cooked_rabbit)item(minecraft:cooked_cod)item(minecraft:cooked_salmon)item(minecraft:baked_potato)item(minecraft:dried_kelp))'
    '2B73E9F40D856AC1' = 'or(item(minecraft:stone_pickaxe)item(minecraft:copper_pickaxe)item(minecraft:iron_pickaxe)item(minecraft:diamond_pickaxe)item(minecraft:netherite_pickaxe))'
    '4D950B261FA78CE3' = 'or(item(minecraft:cooked_beef)item(minecraft:cooked_porkchop)item(minecraft:cooked_chicken)item(minecraft:cooked_mutton)item(minecraft:cooked_rabbit)item(minecraft:cooked_cod)item(minecraft:cooked_salmon)item(minecraft:baked_potato)item(minecraft:dried_kelp))'
    '3A6E05D93B718FC2' = 'ftbfiltersystem:item_tag(minecraft:signs)'
    '2BD17C3FA648E509' = 'ftbfiltersystem:item_tag(minecraft:wooden_slabs)'
    '2D5FB17380C49E6A' = 'or(item(minecraft:iron_pickaxe)item(minecraft:diamond_pickaxe)item(minecraft:netherite_pickaxe))'
    '6193F5B7C408D2AE' = 'or(item(minecraft:cooked_beef)item(minecraft:cooked_porkchop)item(minecraft:cooked_chicken)item(minecraft:cooked_mutton)item(minecraft:cooked_rabbit)item(minecraft:cooked_cod)item(minecraft:cooked_salmon)item(minecraft:baked_potato)item(minecraft:dried_kelp))'
}
foreach ($taskId in $expectedSmartFilters.Keys) {
    $filterPattern = 'id:\s*"' + $taskId + '"[\s\S]{0,700}?"ftbfiltersystem:filter":\s*"' + [regex]::Escape($expectedSmartFilters[$taskId]) + '"'
    Assert-True ([regex]::IsMatch($allDefinitionText, $filterPattern)) "Task $taskId does not contain its expected FTB smart filter."
}

$firstEatingPattern = 'id:\s*"6F20B4D98C315EA7"[\s\S]{0,250}?type:\s*"advancement"[\s\S]{0,250}?advancement:\s*"minecraft:husbandry/root"[\s\S]{0,150}?criterion:\s*"consumed_item"'
Assert-True ([regex]::IsMatch($allDefinitionText, $firstEatingPattern)) 'The first eating task must automatically detect the vanilla Husbandry root advancement.'
$foodSourceQuestPattern = 'id:\s*"35B7E10C9A624DF8"[\s\S]{0,180}?dependencies:\s*\["54A8023B6EC957F1"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $foodSourceQuestPattern)) 'The food-source path must unlock from the safe morning without depending on the optional eating quest.'
$wheatFarmDependencyPattern = 'id:\s*"0C42E8A51D739BF6"[\s\S]{0,180}?dependencies:\s*\["6C03B5E98A417DF2"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $wheatFarmDependencyPattern)) 'The first Wheat farm must unlock after the cooked-food reserve.'
$plantWheatPattern = 'id:\s*"73B95F1C84EA026D"[\s\S]{0,250}?type:\s*"advancement"[\s\S]{0,250}?advancement:\s*"minecraft:husbandry/plant_seed"[\s\S]{0,150}?criterion:\s*"wheat"'
Assert-True ([regex]::IsMatch($allDefinitionText, $plantWheatPattern)) 'Planting Wheat must be detected through the Vanilla Wheat criterion.'
$breedAnimalPattern = 'id:\s*"31E57C40A28DF63B"[\s\S]{0,250}?type:\s*"advancement"[\s\S]{0,250}?advancement:\s*"minecraft:husbandry/breed_an_animal"[\s\S]{0,150}?criterion:\s*"bred"'
Assert-True ([regex]::IsMatch($allDefinitionText, $breedAnimalPattern)) 'Breeding must be detected through the Vanilla bred-animal criterion.'
$firstEnchantmentPattern = 'id:\s*"7B3D95F16EA27C48"[\s\S]{0,250}?type:\s*"advancement"[\s\S]{0,250}?advancement:\s*"minecraft:story/enchant_item"[\s\S]{0,150}?criterion:\s*"enchanted_item"'
Assert-True ([regex]::IsMatch($allDefinitionText, $firstEnchantmentPattern)) 'The first enchantment must use the exact Vanilla enchanted-item criterion.'
$woodenFencePattern = 'id:\s*"285CE3B719F46DA2"[\s\S]{0,400}?ftbfiltersystem:item_tag\(minecraft:wooden_fences\)'
Assert-True ([regex]::IsMatch($allDefinitionText, $woodenFencePattern)) 'The pen task must accept the Vanilla wooden Fences item tag.'
$fenceGatePattern = 'id:\s*"396DF4C82A057EB3"[\s\S]{0,400}?ftbfiltersystem:item_tag\(minecraft:fence_gates\)'
Assert-True ([regex]::IsMatch($allDefinitionText, $fenceGatePattern)) 'The pen task must accept the Vanilla Fence Gates item tag.'
$animalFoodPattern = 'id:\s*"7DA1380C6E49B2F7"[\s\S]{0,500}?or\(item\(minecraft:wheat\)item\(minecraft:wheat_seeds\)item\(minecraft:carrot\)item\(minecraft:potato\)item\(minecraft:beetroot\)\)'
Assert-True ([regex]::IsMatch($allDefinitionText, $animalFoodPattern)) 'The animal-food task must accept every food documented for the supported farm animals.'
$composterSidePathPattern = 'id:\s*"1AC06B2E9537D4F8"[\s\S]{0,180}?dependencies:\s*\["62A84E0B73D9F15C"\][\s\S]*?id:\s*"3CE28D40B759F61A"[\s\S]{0,180}?dependencies:\s*\["1AC06B2E9537D4F8"\][\s\S]*?id:\s*"5E04AF62D97B183C"[\s\S]{0,180}?dependencies:\s*\["3CE28D40B759F61A"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $composterSidePathPattern)) 'The optional Composter path must branch from planting Wheat and remain internally ordered.'

$expectedSingleItemTasks = @{
    '1B35E9042D826CF0' = 'minecraft:chest'
    '57A0D4C91E326BF8' = 'minecraft:stone_pickaxe'
    '0D39A7E25C614BF8' = 'minecraft:raw_iron'
    '61E4A8C20D935BF7' = 'minecraft:iron_ingot'
    '12A6E3D80C754BF9' = 'minecraft:shield'
    '5B39D7F20E846AC1' = 'minecraft:raw_copper'
    '7D5BF9042EA68C31' = 'minecraft:copper_ingot'
    '5ADC07193EB68F24' = 'minecraft:iron_pickaxe'
    '7CFE293B50D8A146' = 'minecraft:bucket'
    '2E104B5D72FAC368' = 'minecraft:water_bucket'
    '3F751BD840A6CE29' = 'minecraft:stone_hoe'
    '37FD9350C82E46A1' = 'minecraft:bread'
    '4DF39E51C86A072B' = 'minecraft:composter'
    '3E60C28491D5AF7B' = 'minecraft:water_bucket'
    '4F71D395A2E6B08C' = 'minecraft:shield'
    '0B3D95F16EA27C48' = 'minecraft:diamond_pickaxe'
    '04C628EAF73B05D1' = 'minecraft:book'
    '591B73DF4C805A26' = 'minecraft:enchanting_table'
}
foreach ($taskId in $expectedSingleItemTasks.Keys) {
    $itemId = $expectedSingleItemTasks[$taskId]
    $taskPattern = 'id:\s*"' + $taskId + '"[\s\S]{0,180}?type:\s*"item"[\s\S]{0,180}?item:\s*\{\s*id:\s*"' + [regex]::Escape($itemId) + '"\s*,\s*count:\s*1\s*\}'
    Assert-True ([regex]::IsMatch($allDefinitionText, $taskPattern)) "Task $taskId must require one $itemId."
}

$protectionFinalPattern = 'id:\s*"07B5E9C31D864AF2"[\s\S]{0,220}?dependencies:\s*\[[\s\S]{0,100}?"5E27A4D90B836CF1"[\s\S]{0,100}?"6F40D8A21C953BE7"'
Assert-True ([regex]::IsMatch($allDefinitionText, $protectionFinalPattern)) 'The protection recap must depend on both the armour-slot and shield-use lessons.'
$ironOreIconPattern = 'id:\s*"2B84F1C60D735AE9"[\s\S]{0,180}?icon:\s*\{\s*id:\s*"minecraft:iron_ore"'
Assert-True ([regex]::IsMatch($allDefinitionText, $ironOreIconPattern)) 'The Raw Iron lesson must use the live Iron Ore item model as its icon.'
$armourSlotDependencyPattern = 'id:\s*"5E27A4D90B836CF1"[\s\S]{0,180}?dependencies:\s*\["18A6D3F90C754BE2"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $armourSlotDependencyPattern)) 'The armour-slot lesson must follow the armour-crafting explanation.'
$armourUnlockPattern = 'id:\s*"18A6D3F90C754BE2"[\s\S]{0,220}?dependencies:\s*\[[\s\S]{0,80}?"3C16B9E50A724DF8"[\s\S]{0,80}?"6C4AE8F31D957B20"[\s\S]{0,80}?\][\s\S]{0,80}?dependency_requirement:\s*"one_completed"'
Assert-True ([regex]::IsMatch($allDefinitionText, $armourUnlockPattern)) 'The armour-crafting lesson must unlock after either the Iron Ingot or Copper Ingot branch.'
$shieldDependencyPattern = 'id:\s*"4D92C7A10E638BF5"[\s\S]{0,180}?dependencies:\s*\["3C16B9E50A724DF8"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $shieldDependencyPattern)) 'The Shield lesson must remain exclusive to the Iron branch.'
$chestDependencyPattern = 'id:\s*"6A24D8F30C715BE9"[\s\S]{0,180}?dependencies:\s*\["5C208AB3E641DF79"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $chestDependencyPattern)) 'The Chest lesson must follow the Furnace lesson.'
$torchDependencyPattern = 'id:\s*"7E42ACD50863F19B"[\s\S]{0,180}?dependencies:\s*\["6A24D8F30C715BE9"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $torchDependencyPattern)) 'The Torch lesson must follow the Chest lesson.'
$bedDependencyPattern = 'id:\s*"3286E0194CA735DF"[\s\S]{0,180}?dependencies:\s*\["1064CEF72A8513BD"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $bedDependencyPattern)) 'The Bed lesson must follow the shelter that grants its Wool.'
$findingHomeDependencyPattern = 'id:\s*"16C8E2A50D739BF4"[\s\S]{0,180}?dependencies:\s*\["36CF412575EB038D"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $findingHomeDependencyPattern)) 'The Finding Home chapter must unlock after the safe-return lesson.'
$compassLessonPattern = 'id:\s*"2F6A91C4D8E307B5"[\s\S]{0,180}?dependencies:\s*\["7C2E480B63D9F15A"\][\s\S]{0,180}?icon:\s*\{\s*id:\s*"minecraft:compass"'
Assert-True ([regex]::IsMatch($allDefinitionText, $compassLessonPattern)) 'The Compass explanation must follow the tested route home.'
$lodestonePathPattern = 'id:\s*"15E7C9A42B806DF3"[\s\S]{0,180}?dependencies:\s*\["2F6A91C4D8E307B5"\][\s\S]*?id:\s*"37A9EBC64D028F15"[\s\S]{0,180}?dependencies:\s*\["15E7C9A42B806DF3"\][\s\S]*?id:\s*"59CBED086F24A137"[\s\S]{0,180}?dependencies:\s*\["37A9EBC64D028F15"\][\s\S]*?id:\s*"0BED012A8146C359"[\s\S]{0,180}?dependencies:\s*\["59CBED086F24A137"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $lodestonePathPattern)) 'The Lodestone path must follow the Compass explanation in the intended order.'
$chiseledStoneCountPattern = 'id:\s*"48BADCF75E139026"[\s\S]{0,180}?type:\s*"item"[\s\S]{0,180}?item:\s*\{\s*id:\s*"minecraft:chiseled_stone_bricks"[\s\S]{0,100}?count:\s*8\s*,'
Assert-True ([regex]::IsMatch($allDefinitionText, $chiseledStoneCountPattern)) 'The Lodestone path must require eight Chiseled Stone Bricks.'
$useLodestoneAdvancementPattern = 'id:\s*"3E1045DAB479F68C"[\s\S]{0,180}?type:\s*"advancement"[\s\S]{0,120}?advancement:\s*"minecraft:adventure/use_lodestone"[\s\S]{0,120}?criterion:\s*"use_lodestone"'
Assert-True ([regex]::IsMatch($allDefinitionText, $useLodestoneAdvancementPattern)) 'Binding the Compass to the Lodestone must be detected automatically.'
$ironEssentialsDependencyPattern = 'id:\s*"27A9D4E60B835CF1"[\s\S]{0,180}?dependencies:\s*\["3C16B9E50A724DF8"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $ironEssentialsDependencyPattern)) 'The Iron Essentials chapter must unlock after the first Iron Ingot.'
$ironEssentialsLinkPattern = 'id:\s*"62448F9136DEA7C0"[\s\S]{0,180}?linked_quest:\s*"27A9D4E60B835CF1"[\s\S]{0,180}?shape:\s*"diamond"'
Assert-True ([regex]::IsMatch($allDefinitionText, $ironEssentialsLinkPattern)) 'The first Iron Ingot path must show a diamond-shaped in-map link to Iron Essentials.'
$ironEssentialsFinalPattern = 'id:\s*"3F215C6E03ABD479"[\s\S]{0,220}?dependencies:\s*\[[\s\S]{0,100}?"49CBF6082DA57E13"[\s\S]{0,100}?"1D0F3A4C61E9B257"'
Assert-True ([regex]::IsMatch($allDefinitionText, $ironEssentialsFinalPattern)) 'The Iron Essentials recap must depend on both the Iron Pickaxe and Water Bucket paths.'
$deepMiningUnlockPattern = 'id:\s*"1C4EA0627FB38D59"[\s\S]{0,220}?dependencies:\s*\[[\s\S]{0,100}?"6D91380C6EA4B2F5"[\s\S]{0,100}?"3F215C6E03ABD479"'
Assert-True ([regex]::IsMatch($allDefinitionText, $deepMiningUnlockPattern)) 'Deep Mining must require both sustainable storage and Iron Essentials.'
$deepMiningLinkPattern = 'id:\s*"3F7B15D9C2E604A8"[\s\S]{0,180}?linked_quest:\s*"1C4EA0627FB38D59"[\s\S]{0,180}?shape:\s*"diamond"'
Assert-True ([regex]::IsMatch($allDefinitionText, $deepMiningLinkPattern)) 'Sustainable Supplies must show a diamond-shaped link to Deep Mining.'
$enchantingJoinPattern = 'id:\s*"480A62CE3B7F4915"[\s\S]{0,260}?dependencies:\s*\[[\s\S]{0,100}?"1D5FB17380C49E6A"[\s\S]{0,100}?"73B517D9E62AF4C0"[\s\S]{0,100}?"15D739FB084C16E2"'
Assert-True ([regex]::IsMatch($allDefinitionText, $enchantingJoinPattern)) 'The Enchanting Table must wait for the Obsidian, Book, and Lapis paths.'
$brewingUnlockPattern = 'id:\s*"0A6417D3BE825CF9"[\s\S]{0,180}?dependencies:\s*\["734140DAA3E544D2"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $brewingUnlockPattern)) 'Brewing must unlock from the safe Fortress return rather than the optional Bastion branch.'
$brewingLinkPattern = 'id:\s*"7842F5B19C603AD7"[\s\S]{0,180}?linked_quest:\s*"0A6417D3BE825CF9"[\s\S]{0,180}?shape:\s*"diamond"'
Assert-True ([regex]::IsMatch($allDefinitionText, $brewingLinkPattern)) 'The Nether activities map must show a diamond-shaped link to Brewing.'
$firstBrewPattern = 'id:\s*"451FC28E693D07A4"[\s\S]{0,180}?type:\s*"advancement"[\s\S]{0,120}?advancement:\s*"minecraft:nether/brew_potion"[\s\S]{0,120}?criterion:\s*"potion"'
Assert-True ([regex]::IsMatch($allDefinitionText, $firstBrewPattern)) 'The first completed brewing operation must use the exact Vanilla advancement criterion.'
$fireResistancePathPattern = 'id:\s*"1D9468F4A713C5E0"[\s\S]{0,180}?dependencies:\s*\["5620D39F7A4E18B5"\][\s\S]*?id:\s*"3FB68A16C935E702"[\s\S]{0,180}?dependencies:\s*\["1D9468F4A713C5E0"\][\s\S]*?id:\s*"51D8AC38EB570924"[\s\S]{0,180}?dependencies:\s*\["3FB68A16C935E702"\][\s\S]*?id:\s*"73FACD5A0D792B46"[\s\S]{0,180}?dependencies:\s*\["51D8AC38EB570924"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $fireResistancePathPattern)) 'The optional Fire Resistance lessons must follow the completed first Strength sequence in order.'
$enderEyesUnlockPattern = 'id:\s*"0C7539E5BF936D1A"[\s\S]{0,180}?dependencies:\s*\["5620D39F7A4E18B5"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $enderEyesUnlockPattern)) 'Eyes of Ender must unlock from Strength without requiring optional Fire Resistance.'
$enderEyesLinkPattern = 'id:\s*"6A3127D39D714BF8"[\s\S]{0,180}?linked_quest:\s*"0C7539E5BF936D1A"[\s\S]{0,180}?shape:\s*"diamond"'
Assert-True ([regex]::IsMatch($allDefinitionText, $enderEyesLinkPattern)) 'Brewing must show a diamond-shaped link to Eye preparation.'
$enderEyesJoinPattern = 'id:\s*"14FDB16D371BE592"[\s\S]{0,260}?dependencies:\s*\[[\s\S]{0,100}?"40B97D29F3D7A15E"[\s\S]{0,100}?"62DB9F4B15F9C370"'
Assert-True ([regex]::IsMatch($allDefinitionText, $enderEyesJoinPattern)) 'Eye crafting must wait for both parallel material supplies.'
$strongholdAdvancementPattern = 'id:\s*"63CA7E4A04E8B26F"[\s\S]{0,180}?type:\s*"advancement"[\s\S]{0,120}?advancement:\s*"minecraft:story/follow_ender_eye"[\s\S]{0,120}?criterion:\s*"in_stronghold"'
Assert-True ([regex]::IsMatch($allDefinitionText, $strongholdAdvancementPattern)) 'Stronghold arrival must use the exact Vanilla in-stronghold criterion.'
$strongholdSearchPathPattern = 'id:\s*"5A31E5B17B5F29D6"[\s\S]*?id:\s*"7C5307D39D714BF8"[\s\S]{0,180}?dependencies:\s*\["5A31E5B17B5F29D6"\][\s\S]*?id:\s*"1E7529F5BF936D1A"[\s\S]{0,180}?dependencies:\s*\["7C5307D39D714BF8"\][\s\S]*?id:\s*"30974B17D1B58F3C"[\s\S]{0,180}?dependencies:\s*\["1E7529F5BF936D1A"\][\s\S]*?id:\s*"52B96D39F3D7A15E"[\s\S]{0,180}?dependencies:\s*\["30974B17D1B58F3C"\][\s\S]*?id:\s*"74DB8F5B15F9C370"[\s\S]{0,180}?dependencies:\s*\["52B96D39F3D7A15E"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $strongholdSearchPathPattern)) 'The Stronghold search must remain ordered through the secured surface return.'
$strongholdLinkPattern = 'id:\s*"3A8F5B17E2C6904D"[\s\S]{0,180}?linked_quest:\s*"086D39F5C0A47E2B"[\s\S]{0,180}?shape:\s*"diamond"'
Assert-True ([regex]::IsMatch($allDefinitionText, $strongholdLinkPattern)) 'Eye preparation must show a diamond-shaped link to Stronghold exploration.'
$strongholdExplorationPathPattern = 'id:\s*"086D39F5C0A47E2B"[\s\S]{0,180}?dependencies:\s*\["74DB8F5B15F9C370"\][\s\S]*?id:\s*"2A8F5B17E2C6904D"[\s\S]{0,180}?dependencies:\s*\["086D39F5C0A47E2B"\][\s\S]*?id:\s*"3C906D28F3D7A15E"[\s\S]{0,180}?dependencies:\s*\["2A8F5B17E2C6904D"\][\s\S]*?id:\s*"4CA17D3904E8B26F"[\s\S]{0,180}?dependencies:\s*\["3C906D28F3D7A15E"\][\s\S]*?id:\s*"10E5B17D482CF6A3"[\s\S]{0,180}?dependencies:\s*\["4CA17D3904E8B26F"\][\s\S]*?id:\s*"3207D39F6A4E18C5"[\s\S]{0,180}?dependencies:\s*\["10E5B17D482CF6A3"\][\s\S]*?id:\s*"5429F5B18C603AE7"[\s\S]{0,180}?dependencies:\s*\["3207D39F6A4E18C5"\][\s\S]*?id:\s*"764B17D3AE825C09"[\s\S]{0,180}?dependencies:\s*\["5429F5B18C603AE7"\]'
Assert-True ([regex]::IsMatch($allDefinitionText, $strongholdExplorationPathPattern)) 'The main Stronghold exploration path must remain ordered and independent of the optional Library lesson.'

$becomingChapterText = Get-Content -LiteralPath (Join-Path $questRoot 'chapters/becoming_independent.json5') -Raw
$becomingXValues = @([regex]::Matches($becomingChapterText, '(?m)^\s+x:\s*(-?[0-9]+(?:\.[0-9]+)?)\s*,') | ForEach-Object { [double]$_.Groups[1].Value })
Assert-True ($becomingXValues.Count -gt 0) 'The Becoming Independent chapter has no quest coordinates.'
$becomingXExtent = (($becomingXValues | Measure-Object -Maximum).Maximum - ($becomingXValues | Measure-Object -Minimum).Minimum)
Assert-True ($becomingXExtent -le 15.0) 'The Becoming Independent layout must remain compact enough for smaller displays.'

$expectedItemRewards = @{
    '27C3D4E5F6071829' = @{ Item = 'minecraft:cobblestone'; Count = 3 }
    '2C8E51A70D4B639F' = @{ Item = 'minecraft:apple'; Count = 2 }
    '61D304B8A7CE295F' = @{ Item = 'minecraft:apple'; Count = 2 }
    '2F517B3E160C248D' = @{ Item = 'minecraft:white_wool'; Count = 3 }
    '37AF205CE961B4D8' = @{ Item = 'minecraft:bread'; Count = 3 }
    '5E18C7D042AB936F' = @{ Item = 'minecraft:lantern'; Count = 1 }
    '0C71E4A95B263DF8' = @{ Item = 'minecraft:apple'; Count = 1 }
    '14B8D2F60A975CE3' = @{ Item = 'minecraft:charcoal'; Count = 1 }
    '72E5A1C83D609BF4' = @{ Item = 'minecraft:cookie'; Count = 1 }
    '56E8B2C10D734AF9' = @{ Item = 'minecraft:iron_ingot'; Count = 1 }
    '6D3A80F152C7BE49' = @{ Item = 'minecraft:compass'; Count = 1 }
    '16AB73C90D4E258F' = @{ Item = 'minecraft:bone_meal'; Count = 3 }
    '27BC84DA1E5F3690' = @{ Item = 'minecraft:torch'; Count = 4 }
    '38CD95EB2F6047A1' = @{ Item = 'minecraft:lead'; Count = 1 }
    '49DEA6FC307158B2' = @{ Item = 'minecraft:item_frame'; Count = 3 }
    '5AEFB70D418269C3' = @{ Item = 'minecraft:bundle'; Count = 1 }
    '0137D2950CAE4B6F' = @{ Item = 'minecraft:wheat_seeds'; Count = 32 }
    '4A8C26E0D3F715B9' = @{ Item = 'minecraft:bread'; Count = 4 }
    '5B9D37F1E40826CA' = @{ Item = 'minecraft:torch'; Count = 8 }
    '7DBF5913062A48EC' = @{ Item = 'minecraft:golden_apple'; Count = 1 }
    '62A406C8D519E3BF' = @{ Item = 'minecraft:leather'; Count = 1 }
    '0C4EA6027FB38D59' = @{ Item = 'minecraft:lapis_lazuli'; Count = 3 }
    '21A9E6BFC72D8E3A' = @{ Item = 'minecraft:torch'; Count = 4 }
    '32BAF7C0D83E9F4B' = @{ Item = 'minecraft:cobblestone'; Count = 8 }
    '43CB08D1E94FA05C' = @{ Item = 'minecraft:bread'; Count = 2 }
    '55ED2AF30B61C27E' = @{ Item = 'minecraft:gold_ingot'; Count = 1 }
    '0B9746E2C5F183AD' = @{ Item = 'minecraft:redstone'; Count = 3 }
    '5801F5B17B5F29D6' = @{ Item = 'minecraft:ender_eye'; Count = 2 }
    '16FDA17D371BE592' = @{ Item = 'minecraft:torch'; Count = 16 }
    '186D39F5C0A47E2B' = @{ Item = 'minecraft:golden_apple'; Count = 1 }
}
foreach ($rewardId in $expectedItemRewards.Keys) {
    $reward = $expectedItemRewards[$rewardId]
    $rewardPattern = 'id:\s*"' + $rewardId + '"[\s\S]{0,180}?type:\s*"item"'
    if ($reward.Count -gt 1) {
        $rewardPattern += '[\s\S]{0,100}?count:\s*' + $reward.Count + '\s*,'
    }
    $rewardPattern += '[\s\S]{0,180}?item:\s*\{\s*id:\s*"' + [regex]::Escape($reward.Item) + '"\s*,\s*count:\s*1\s*\}'
    Assert-True ([regex]::IsMatch($allDefinitionText, $rewardPattern)) "Reward $rewardId must grant $($reward.Count) $($reward.Item)."
}

$expectedXpRewards = @{
    '4BD9A6E31F028C75' = 5
    '3D570B264FA48E12' = 5
    '3AC8F5D20E917B64' = 5
    '58E16347970D25AF' = 10
    '1E406A2D05FB137C' = 5
    '51337E8025CDF69B' = 5
    '2D0F234CA368E57B' = 5
    '480EA461D93F57B2' = 5
    '6418AF73D5B0296E' = 5
    '0FB35A2E806CD417' = 5
    '7026C184FB9D3A5E' = 5
    '6CAE4802F51937DB' = 5
    '0EC06A24173B59FD' = 5
    '3F71D395A2E6B08C' = 5
    '37F951BD2A6E3804' = 5
    '2E60C82491D5AF7B' = 5
    '1D5FB71380C49E6A' = 10
    '1098D5AEB61C7D29' = 10
    '66FE3B041C72D38F' = 5
    '1CA857F3D60294BE' = 10
    '15BCEF7C2F9B4D68' = 5
    '691206C28C603AE7' = 10
    '270EB28E482CF6A3' = 10
    '297E4A06D1B58F3C' = 10
}
foreach ($rewardId in $expectedXpRewards.Keys) {
    $xp = $expectedXpRewards[$rewardId]
    $rewardPattern = 'id:\s*"' + $rewardId + '"[\s\S]{0,120}?type:\s*"xp"[\s\S]{0,120}?xp:\s*' + $xp + '\s*,'
    Assert-True ([regex]::IsMatch($allDefinitionText, $rewardPattern)) "Reward $rewardId must grant $xp experience points."
}
$rewardArrayText = ([regex]::Matches($allDefinitionText, 'rewards:\s*\[(?<body>[\s\S]*?)\]\s*,') | ForEach-Object { $_.Groups['body'].Value }) -join "`n"
Assert-True (-not [regex]::IsMatch($rewardArrayText, 'type:\s*"xp_levels"')) 'Beginner rewards must use precise XP points rather than whole XP levels.'
$ignoredRewardStackCounts = [regex]::Matches($rewardArrayText, 'item:\s*\{[^}]*\bcount:\s*([2-9][0-9]*)', [System.Text.RegularExpressions.RegexOptions]::Singleline)
Assert-True ($ignoredRewardStackCounts.Count -eq 0) 'Item reward quantities above one must use the reward-level count field; a count inside item is ignored by FTB Quests.'

$packMetadataPath = Join-Path $guidePackRoot 'pack.mcmeta'
Assert-True (Test-Path -LiteralPath $packMetadataPath -PathType Leaf) 'First Torch guide resource pack metadata is missing.'
$packMetadata = Get-Content -LiteralPath $packMetadataPath -Raw | ConvertFrom-Json
Assert-True ($packMetadata.pack.min_format[0] -eq 84 -and $packMetadata.pack.min_format[1] -eq 0) 'Guide resource pack min_format must be [84, 0] for Minecraft 26.1.2.'
Assert-True ($packMetadata.pack.max_format[0] -eq 84 -and $packMetadata.pack.max_format[1] -eq 0) 'Guide resource pack max_format must be [84, 0] for Minecraft 26.1.2.'
foreach ($imageName in @('attack_and_break.png', 'log_to_planks.png', 'place_crafting_table.png', 'wooden_pickaxe.png', 'stone_axe.png', 'furnace.png', 'chest.png', 'charcoal.png', 'bed.png', 'hunger_and_eating.png', 'cooking_food.png', 'stone_pickaxe.png', 'shield.png', 'armour_recipes.png', 'safe_staircase.png', 'torch_route.png', 'iron_pickaxe.png', 'bucket.png', 'stonecutter.png', 'lodestone.png', 'stone_hoe.png', 'bread.png', 'farmland_9x9.png', 'fence_and_gate.png', 'paper_and_book.png', 'enchanting_table_recipe.png', 'enchanting_interface.png', 'bookshelf_recipe.png', 'enchanting_bookshelves.png', 'iron_sword.png', 'bone_meal_recipe.png', 'attack_indicator.png', 'flint_and_steel.png', 'nether_portal_frame.png', 'nether_route_marker.png', 'piglin_comparison.png', 'nether_fortress.png', 'fortress_hazards.png', 'blaze_spawner.png', 'bastion_remnant.png', 'blaze_powder_recipe.png', 'brewing_stand_recipe.png', 'glass_bottle_recipe.png', 'awkward_potion_brewing.png', 'strength_potion_brewing.png', 'magma_cream_recipe.png', 'fire_resistance_brewing.png', 'long_fire_resistance_brewing.png', 'ender_eye_recipe.png', 'ender_eye_search.png', 'stronghold_silverfish.png', 'end_portal_room.png', 'end_portal_frame_states.png', 'stronghold_iron_door.png')) {
    $imagePath = Join-Path $guidePackRoot (Join-Path 'assets/firsttorch/textures/questpics' $imageName)
    Assert-True (Test-Path -LiteralPath $imagePath -PathType Leaf) "Missing quest guide image: $imageName"
    Assert-True ((Get-Item -LiteralPath $imagePath).Length -gt 0) "Quest guide image is empty: $imageName"
}
$deepMiningLanguageText = ((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter 'deep_mining.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n")
foreach ($imageName in @('paper_and_book.png', 'enchanting_table_recipe.png', 'enchanting_interface.png', 'bookshelf_recipe.png', 'enchanting_bookshelves.png')) {
    $referenceCount = [regex]::Matches($deepMiningLanguageText, [regex]::Escape("firsttorch:textures/questpics/$imageName")).Count
    Assert-True ($referenceCount -eq 2) "The $imageName guide must be referenced once in each Deep Mining language file."
}
$mobDropLanguageText = ((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter 'mob_drops.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n")
foreach ($imageName in @('iron_sword.png', 'bone_meal_recipe.png', 'attack_indicator.png')) {
    $referenceCount = [regex]::Matches($mobDropLanguageText, [regex]::Escape("firsttorch:textures/questpics/$imageName")).Count
    Assert-True ($referenceCount -eq 2) "The $imageName guide must be referenced once in each Safe Mob Drops language file."
}
$netherLanguageText = ((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter 'nether_preparation.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n")
foreach ($imageName in @('flint_and_steel.png', 'nether_portal_frame.png', 'nether_route_marker.png')) {
    $referenceCount = [regex]::Matches($netherLanguageText, [regex]::Escape("firsttorch:textures/questpics/$imageName")).Count
    Assert-True ($referenceCount -eq 2) "The $imageName guide must be referenced once in each Nether Preparation language file."
}
$piglinComparisonReferences = [regex]::Matches($allLanguageText, 'firsttorch:textures/questpics/piglin_comparison\.png')
Assert-True ($piglinComparisonReferences.Count -eq 6) 'The Piglin comparison guide must be referenced in all three relevant quests in each language.'
foreach ($imageName in @('nether_fortress.png', 'fortress_hazards.png', 'blaze_spawner.png')) {
    $referenceCount = [regex]::Matches($allLanguageText, [regex]::Escape("firsttorch:textures/questpics/$imageName")).Count
    Assert-True ($referenceCount -eq 2) "The $imageName guide must be referenced once in each language."
}
$bastionImageReferences = [regex]::Matches($allLanguageText, 'firsttorch:textures/questpics/bastion_remnant\.png')
Assert-True ($bastionImageReferences.Count -eq 2) 'The Bastion Remnant guide must be referenced once in each language.'
$brewingLanguageText = ((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter 'brewing.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n")
foreach ($imageName in @('blaze_powder_recipe.png', 'brewing_stand_recipe.png', 'glass_bottle_recipe.png', 'awkward_potion_brewing.png', 'strength_potion_brewing.png', 'magma_cream_recipe.png', 'fire_resistance_brewing.png', 'long_fire_resistance_brewing.png')) {
    $referenceCount = [regex]::Matches($brewingLanguageText, [regex]::Escape("firsttorch:textures/questpics/$imageName")).Count
    Assert-True ($referenceCount -eq 2) "The $imageName guide must be referenced once in each Brewing language file."
}
$enderEyeLanguageText = ((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter 'ender_eyes.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n")
$enderEyeRecipeReferences = [regex]::Matches($enderEyeLanguageText, 'firsttorch:textures/questpics/ender_eye_recipe\.png')
Assert-True ($enderEyeRecipeReferences.Count -eq 2) 'The Eye of Ender recipe guide must be referenced once in each language.'
$enderEyeSearchReferences = [regex]::Matches($enderEyeLanguageText, 'firsttorch:textures/questpics/ender_eye_search\.png')
Assert-True ($enderEyeSearchReferences.Count -eq 2) 'The Eye search guide must be referenced once in each language.'
$strongholdLanguageText = ((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter 'stronghold.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n")
foreach ($imageName in @('stronghold_silverfish.png', 'end_portal_room.png', 'end_portal_frame_states.png', 'stronghold_iron_door.png')) {
    $referenceCount = [regex]::Matches($strongholdLanguageText, [regex]::Escape("firsttorch:textures/questpics/$imageName")).Count
    Assert-True ($referenceCount -eq 2) "The $imageName guide must be referenced once in each Stronghold language file."
}
$armourImageReferences = [regex]::Matches($allDefinitionText + "`n" + ((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/armour_recipes\.png')
Assert-True ($armourImageReferences.Count -eq 2) 'The compact Armour recipe overview must be referenced once in each language.'
$chestImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/chest\.png')
Assert-True ($chestImageReferences.Count -eq 2) 'The Chest recipe guide must be referenced once in each language.'
$bedImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/bed\.png')
Assert-True ($bedImageReferences.Count -eq 2) 'The Bed recipe guide must be referenced once in each language.'
$ironPickaxeImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/iron_pickaxe\.png')
Assert-True ($ironPickaxeImageReferences.Count -eq 2) 'The Iron Pickaxe recipe guide must be referenced once in each language.'
$bucketImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/bucket\.png')
Assert-True ($bucketImageReferences.Count -eq 2) 'The Bucket recipe guide must be referenced once in each language.'
$stonecutterImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/stonecutter\.png')
Assert-True ($stonecutterImageReferences.Count -eq 2) 'The Stonecutter recipe guide must be referenced once in each language.'
$lodestoneImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/lodestone\.png')
Assert-True ($lodestoneImageReferences.Count -eq 2) 'The Lodestone recipe guide must be referenced once in each language.'
$stoneHoeImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/stone_hoe\.png')
Assert-True ($stoneHoeImageReferences.Count -eq 2) 'The Stone Hoe recipe guide must be referenced once in each language.'
$stoneAxeImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/stone_axe\.png')
Assert-True ($stoneAxeImageReferences.Count -eq 2) 'The Stone Axe recipe guide must be referenced once in each language.'
$breadImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/bread\.png')
Assert-True ($breadImageReferences.Count -eq 2) 'The Bread recipe guide must be referenced once in each language.'
$farmlandImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/farmland_9x9\.png')
Assert-True ($farmlandImageReferences.Count -eq 2) 'The exact 9 by 9 Farmland hydration guide must be referenced once in each language.'
$fenceAndGateImageReferences = [regex]::Matches(((Get-ChildItem -LiteralPath (Join-Path $questRoot 'lang') -Recurse -File -Filter '*.json5' | ForEach-Object { Get-Content -LiteralPath $_.FullName -Raw }) -join "`n"), 'firsttorch:textures/questpics/fence_and_gate\.png')
Assert-True ($fenceAndGateImageReferences.Count -eq 2) 'The combined Fence and Fence Gate recipe guide must be referenced once in each language.'

$localeRoot = Join-Path $questRoot 'lang'
foreach ($relativePath in @('chapter.json5', 'chapters/first_steps.json5', 'chapters/becoming_independent.json5', 'chapters/iron_essentials.json5', 'chapters/finding_home.json5', 'chapters/sustainable_supplies.json5', 'chapters/nether_preparation.json5', 'chapters/nether_activities.json5', 'chapters/brewing.json5', 'chapters/ender_eyes.json5', 'chapters/stronghold.json5')) {
    $enPath = Join-Path (Join-Path $localeRoot 'en_us') $relativePath
    $dePath = Join-Path (Join-Path $localeRoot 'de_de') $relativePath
    Assert-True (Test-Path -LiteralPath $enPath -PathType Leaf) "Missing English translation file: $relativePath"
    Assert-True (Test-Path -LiteralPath $dePath -PathType Leaf) "Missing German translation file: $relativePath"

    $keyPattern = '(?m)^\s*"([^"]+)"\s*:'
    $enKeys = @([regex]::Matches((Get-Content -LiteralPath $enPath -Raw), $keyPattern) | ForEach-Object { $_.Groups[1].Value } | Sort-Object -Unique)
    $deKeys = @([regex]::Matches((Get-Content -LiteralPath $dePath -Raw), $keyPattern) | ForEach-Object { $_.Groups[1].Value } | Sort-Object -Unique)
    $keyDifference = Compare-Object -ReferenceObject $enKeys -DifferenceObject $deKeys
    Assert-True ($null -eq $keyDifference) "Translation keys differ between en_us and de_de for $relativePath."
}

Write-Host "First Torch validation passed: $($ids.Count) stable quest object IDs and $($manifest.files.Count) pinned dependencies."
