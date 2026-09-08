param([Parameter(Mandatory = $true)][string]$MinecraftJar)

$ErrorActionPreference = 'Stop'
# Read the user's target game archive; never redistribute its loot tables.
$archive = [IO.Compression.ZipFile]::OpenRead((Resolve-Path -LiteralPath $MinecraftJar).Path)
function Read-Loot([string]$mob) {
    $entry = $archive.GetEntry("data/minecraft/loot_table/entities/$mob.json")
    if ($null -eq $entry) { throw "Missing target loot table: $mob" }
    $reader = [IO.StreamReader]::new($entry.Open())
    try { return $reader.ReadToEnd() | ConvertFrom-Json } finally { $reader.Dispose() }
}
function Assert-Loot([bool]$condition, [string]$message) {
    if (-not $condition) { throw $message }
}
try {
    $blaze = (Read-Loot 'blaze').pools[0]
    Assert-Loot ($blaze.conditions[0].condition -eq 'minecraft:killed_by_player') 'Blaze player-credit condition changed'
    Assert-Loot ($blaze.entries[0].name -eq 'minecraft:blaze_rod') 'Blaze item changed'
    Assert-Loot ($blaze.entries[0].functions[0].count.min -eq 0 -and $blaze.entries[0].functions[0].count.max -eq 1) 'Blaze base range changed'
    $skull = (Read-Loot 'wither_skeleton').pools | Where-Object { $_.entries[0].name -eq 'minecraft:wither_skeleton_skull' }
    Assert-Loot ($skull.conditions.condition -contains 'minecraft:killed_by_player') 'Skull player-credit condition changed'
    Assert-Loot ($skull.conditions.condition -contains 'minecraft:random_chance_with_enchanted_bonus') 'Skull chance condition changed'
    $cube = (Read-Loot 'magma_cube').pools[0].entries[0]
    Assert-Loot ($cube.name -eq 'minecraft:magma_cream') 'Magma Cube item changed'
    $size = $cube.conditions | Where-Object { $_.condition -eq 'minecraft:entity_properties' }
    Assert-Loot ($size.predicate.type_specific.size.min -eq 2) 'Small-cube exclusion changed'
    $eye = (Read-Loot 'spider').pools | Where-Object { $_.entries[0].name -eq 'minecraft:spider_eye' }
    Assert-Loot ($eye.conditions.condition -contains 'minecraft:killed_by_player') 'Spider Eye player-credit condition changed'
    $disc = (Read-Loot 'creeper').pools[1]
    Assert-Loot ($disc.conditions[0].predicate.type -eq '#minecraft:skeletons') 'Creeper disc attacker condition changed'
    Write-Output 'Target creature loot checks passed: Blaze, Wither Skeleton, Magma Cube, Spider and Creeper.'
} finally { $archive.Dispose() }
