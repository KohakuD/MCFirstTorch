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
}
foreach ($taskId in $expectedTaskCounts.Keys) {
    $expectedCount = $expectedTaskCounts[$taskId]
    $taskPattern = 'id:\s*"' + $taskId + '"[\s\S]{0,180}?type:\s*"item"[\s\S]{0,180}?count:\s*' + $expectedCount + '\s*,[\s\S]{0,180}?item:'
    Assert-True ([regex]::IsMatch($allDefinitionText, $taskPattern)) "Task $taskId must require $expectedCount items with a task-level count field."
}

$plankFilterPattern = 'id:\s*"45B9134C7FDA6802"[\s\S]{0,500}?"ftbfiltersystem:filter":\s*"ftbfiltersystem:item_tag\(minecraft:planks\)"'
Assert-True ([regex]::IsMatch($allDefinitionText, $plankFilterPattern)) 'The first wood task must accept the minecraft:planks item tag through FTB Filter System.'

$expectedItemRewards = @{
    '2C8E51A70D4B639F' = @{ Item = 'minecraft:apple'; Count = 2 }
    '61D304B8A7CE295F' = @{ Item = 'minecraft:apple'; Count = 2 }
    '37AF205CE961B4D8' = @{ Item = 'minecraft:bread'; Count = 3 }
    '5E18C7D042AB936F' = @{ Item = 'minecraft:lantern'; Count = 1 }
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

$rewardArrayText = ([regex]::Matches($allDefinitionText, 'rewards:\s*\[(?<body>[\s\S]*?)\]\s*,') | ForEach-Object { $_.Groups['body'].Value }) -join "`n"
$ignoredRewardStackCounts = [regex]::Matches($rewardArrayText, 'item:\s*\{[^}]*\bcount:\s*([2-9][0-9]*)', [System.Text.RegularExpressions.RegexOptions]::Singleline)
Assert-True ($ignoredRewardStackCounts.Count -eq 0) 'Item reward quantities above one must use the reward-level count field; a count inside item is ignored by FTB Quests.'

$packMetadataPath = Join-Path $guidePackRoot 'pack.mcmeta'
Assert-True (Test-Path -LiteralPath $packMetadataPath -PathType Leaf) 'First Torch guide resource pack metadata is missing.'
$packMetadata = Get-Content -LiteralPath $packMetadataPath -Raw | ConvertFrom-Json
Assert-True ($packMetadata.pack.min_format[0] -eq 84 -and $packMetadata.pack.min_format[1] -eq 0) 'Guide resource pack min_format must be [84, 0] for Minecraft 26.1.2.'
Assert-True ($packMetadata.pack.max_format[0] -eq 84 -and $packMetadata.pack.max_format[1] -eq 0) 'Guide resource pack max_format must be [84, 0] for Minecraft 26.1.2.'
foreach ($imageName in @('attack_and_break.png', 'log_to_planks.png', 'place_crafting_table.png', 'wooden_pickaxe.png', 'furnace.png', 'charcoal.png')) {
    $imagePath = Join-Path $guidePackRoot (Join-Path 'assets/firsttorch/textures/questpics' $imageName)
    Assert-True (Test-Path -LiteralPath $imagePath -PathType Leaf) "Missing quest guide image: $imageName"
    Assert-True ((Get-Item -LiteralPath $imagePath).Length -gt 0) "Quest guide image is empty: $imageName"
}

$localeRoot = Join-Path $questRoot 'lang'
foreach ($relativePath in @('chapter.json5', 'chapters/first_steps.json5')) {
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
