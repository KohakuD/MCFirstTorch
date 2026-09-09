param([Parameter(Mandatory = $true)][string]$MinecraftJar)

$ErrorActionPreference = 'Stop'
$archive = [IO.Compression.ZipFile]::OpenRead((Resolve-Path -LiteralPath $MinecraftJar).Path)
function Read-GameJson([string]$path) {
    $entry = $archive.GetEntry($path)
    if ($null -eq $entry) { throw "Missing game resource: $path" }
    $reader = [IO.StreamReader]::new($entry.Open())
    try { return $reader.ReadToEnd() | ConvertFrom-Json } finally { $reader.Dispose() }
}
function Assert-Mechanic([bool]$condition, [string]$message) {
    if (-not $condition) { throw $message }
}
try {
    $brush = Read-GameJson 'data/minecraft/recipe/brush.json'
    Assert-Mechanic ($brush.result.id -eq 'minecraft:brush' -and ($brush.pattern -join ',') -eq 'X,#,I') 'Brush recipe pattern changed'
    Assert-Mechanic ($brush.key.X -eq 'minecraft:feather' -and $brush.key.'#' -eq 'minecraft:copper_ingot' -and $brush.key.I -eq 'minecraft:stick') 'Brush ingredients changed'
    $disc = Read-GameJson 'data/minecraft/recipe/music_disc_5.json'
    Assert-Mechanic ($disc.result.id -eq 'minecraft:music_disc_5' -and $disc.ingredients.Count -eq 9 -and @($disc.ingredients | Where-Object { $_ -ne 'minecraft:disc_fragment_5' }).Count -eq 0) 'Disc 5 fragment recipe changed'
    $creeperDisc = (Read-GameJson 'data/minecraft/loot_table/entities/creeper.json').pools[1]
    Assert-Mechanic ($creeperDisc.conditions[0].entity -eq 'attacker' -and $creeperDisc.conditions[0].predicate.type -eq '#minecraft:skeletons') 'Creeper disc attacker condition changed'
    Assert-Mechanic ($creeperDisc.entries[0].name -eq 'minecraft:creeper_drop_music_discs') 'Creeper disc selection changed'
    foreach ($item in @('brush', 'suspicious_gravel', 'angler_pottery_sherd', 'jukebox', 'music_disc_cat', 'disc_fragment_5', 'golden_apple', 'splash_potion', 'apple')) {
        Assert-Mechanic ($null -ne $archive.GetEntry("assets/minecraft/items/$item.json")) "Missing discovery icon: $item"
    }
    $sponge = Read-GameJson 'data/minecraft/recipe/sponge.json'
    Assert-Mechanic ($sponge.type -eq 'minecraft:smelting' -and $sponge.ingredient -eq 'minecraft:wet_sponge' -and $sponge.result.id -eq 'minecraft:sponge') 'Wet Sponge drying recipe changed'
    $mud = Read-GameJson 'data/minecraft/tags/block/convertable_to_mud.json'
    Assert-Mechanic ($mud.values -contains 'minecraft:dirt') 'Dirt is no longer eligible for Mud conversion'
    $honeycomb = (Read-GameJson 'data/minecraft/loot_table/harvest/beehive.json').pools[0].entries[0]
    Assert-Mechanic ($honeycomb.name -eq 'minecraft:honeycomb' -and $honeycomb.functions[0].count -eq 3) 'Honeycomb harvest changed'
    foreach ($colour in @('white', 'red', 'blue')) {
        $wool = (Read-GameJson "data/minecraft/loot_table/shearing/sheep/$colour.json").pools[0].entries[0]
        Assert-Mechanic ($wool.name -eq "minecraft:${colour}_wool") 'Sheep shearing colour changed'
    }
    foreach ($item in @('white_concrete', 'mud', 'sponge', 'name_tag', 'white_wool', 'rabbit_hide', 'wheat', 'honey_bottle', 'honeycomb')) {
        Assert-Mechanic ($null -ne $archive.GetEntry("assets/minecraft/items/$item.json")) "Missing reference icon: $item"
    }
    Write-Output 'Target unusual-mechanics recipe, harvest and icon checks passed. Physical experiments still require in-game review.'
} finally { $archive.Dispose() }
