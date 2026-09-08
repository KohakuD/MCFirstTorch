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
