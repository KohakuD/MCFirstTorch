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
    $rabbit = (Read-Loot 'rabbit').pools
    $hide = $rabbit[0].entries[0]
    Assert-Loot ($hide.name -eq 'minecraft:rabbit_hide' -and $hide.functions[0].count.min -eq 0 -and $hide.functions[0].count.max -eq 1) 'Rabbit Hide base range changed'
    $meat = $rabbit[1].entries[0]
    Assert-Loot ($meat.name -eq 'minecraft:rabbit' -and $meat.functions[0].count -eq 1) 'Rabbit meat base count changed'
    Assert-Loot ($meat.functions.function -contains 'minecraft:furnace_smelt') 'Rabbit cooking condition removed'
    foreach ($entry in @($hide, $meat)) {
        Assert-Loot ($entry.functions.function -contains 'minecraft:enchanted_count_increase') 'Rabbit Looting count function removed'
    }
    Assert-Loot ($rabbit[2].entries[0].name -eq 'minecraft:rabbit_foot') 'Rabbit rare drop changed'
    Assert-Loot ($rabbit[2].conditions.condition -contains 'minecraft:killed_by_player') 'Rabbit Foot player-credit condition changed'
    $chance = $rabbit[2].conditions | Where-Object { $_.condition -eq 'minecraft:random_chance_with_enchanted_bonus' }
    Assert-Loot ($chance.unenchanted_chance -eq 0.1 -and $chance.enchanted_chance.base -eq 0.13 -and $chance.enchanted_chance.per_level_above_first -eq 0.03) 'Rabbit Foot chance changed'
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
    foreach ($mob in @('wolf', 'camel', 'axolotl')) {
        $loot = Read-Loot $mob
        Assert-Loot (-not $loot.pools) "Unexpected ordinary death loot for $mob"
    }
    $cat = (Read-Loot 'cat').pools[0].entries[0]
    Assert-Loot ($cat.name -eq 'minecraft:string' -and $cat.functions[0].count.max -eq 2) 'Cat ordinary loot changed'
    Assert-Loot (-not ($cat.functions.function -contains 'minecraft:enchanted_count_increase')) 'Cat Looting rule changed'
    $horse = (Read-Loot 'horse').pools[0].entries[0]
    Assert-Loot ($horse.name -eq 'minecraft:leather' -and $horse.functions[0].count.max -eq 2) 'Horse ordinary loot changed'
    foreach ($mob in @('silverfish', 'endermite', 'bee', 'fox', 'frog', 'allay')) {
        Assert-Loot (-not (Read-Loot $mob).pools) "Unexpected natural item drop for $mob"
    }
    $shulker = (Read-Loot 'shulker').pools[0]
    Assert-Loot ($shulker.entries[0].name -eq 'minecraft:shulker_shell') 'Shulker item changed'
    Assert-Loot ($shulker.conditions[0].unenchanted_chance -eq 0.5) 'Shulker base chance changed'
    Assert-Loot (-not $shulker.entries[0].functions) 'Shulker count semantics changed'
    foreach ($mob in @('squid', 'glow_squid')) {
        $ink = (Read-Loot $mob).pools[0].entries[0]
        Assert-Loot ($ink.functions[0].count.min -eq 1 -and $ink.functions[0].count.max -eq 3) "Ink range changed for $mob"
    }
    $turtle = Read-Loot 'turtle'
    Assert-Loot ($turtle.pools[0].entries[0].name -eq 'minecraft:seagrass') 'Turtle ordinary loot changed'
    Assert-Loot (-not ($turtle.pools.entries.name -contains 'minecraft:turtle_scute')) 'Turtle Scute incorrectly treated as death loot'
    foreach ($mob in @('creaking', 'goat')) {
        Assert-Loot (-not (Read-Loot $mob).pools) "Unexpected natural item drop for $mob"
    }
    $witch = Read-Loot 'witch'
    $redstone = $witch.pools[1].entries[0]
    Assert-Loot ($redstone.name -eq 'minecraft:redstone') 'Witch guaranteed item changed'
    Assert-Loot ($redstone.functions[0].count.min -eq 4 -and $redstone.functions[0].count.max -eq 8) 'Witch Redstone range changed'
    Assert-Loot (-not $witch.pools[1].conditions) 'Witch Redstone is no longer unconditional'
    $ingredients = @('minecraft:glowstone_dust', 'minecraft:sugar', 'minecraft:spider_eye', 'minecraft:glass_bottle', 'minecraft:gunpowder', 'minecraft:stick')
    Assert-Loot (-not (Compare-Object $ingredients @($witch.pools[0].entries.name))) 'Witch ingredient selection changed'
    $breeze = (Read-Loot 'breeze').pools[0]
    Assert-Loot ($breeze.conditions.condition -contains 'minecraft:killed_by_player') 'Breeze player-credit condition changed'
    Assert-Loot ($breeze.entries[0].name -eq 'minecraft:breeze_rod') 'Breeze item changed'
    Assert-Loot ($breeze.entries[0].functions[0].count.min -eq 1 -and $breeze.entries[0].functions[0].count.max -eq 2) 'Breeze Rod base range changed'
    $bogged = Read-Loot 'bogged'
    foreach ($index in 0..1) {
        $entry = $bogged.pools[$index].entries[0]
        Assert-Loot ($entry.name -eq @('minecraft:arrow', 'minecraft:bone')[$index]) 'Bogged ordinary item changed'
        Assert-Loot ($entry.functions[0].count.min -eq 0 -and $entry.functions[0].count.max -eq 2) 'Bogged ordinary range changed'
    }
    $poison = $bogged.pools[2]
    Assert-Loot ($poison.conditions.condition -contains 'minecraft:killed_by_player') 'Bogged Poison Arrow player-credit condition changed'
    Assert-Loot ($poison.entries[0].name -eq 'minecraft:tipped_arrow') 'Bogged special item changed'
    Assert-Loot ($poison.entries[0].functions[0].count.min -eq 0 -and $poison.entries[0].functions[0].count.max -eq 1) 'Bogged Poison Arrow range changed'
    Assert-Loot ($poison.entries[0].functions[2].id -eq 'minecraft:poison') 'Bogged arrow potion changed'
    foreach ($item in @('turtle_scute', 'goat_horn', 'redstone', 'breeze_rod', 'bone', 'creaking_heart')) {
        Assert-Loot ($null -ne $archive.GetEntry("assets/minecraft/items/$item.json")) "Missing target item icon: $item"
    }
    Write-Output 'Target creature loot and requested reference-icon checks passed.'
} finally { $archive.Dispose() }
