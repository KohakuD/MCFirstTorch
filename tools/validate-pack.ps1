[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$manifestPath = Join-Path $repoRoot 'manifest.json'
$questRoot = Join-Path $repoRoot 'overrides/config/ftbquests/quests'

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
$idMatches = [regex]::Matches($allDefinitionText, '\bid:\s*"([0-9A-F]{16})"')
$ids = @($idMatches | ForEach-Object { $_.Groups[1].Value })
$duplicateIds = $ids | Group-Object | Where-Object Count -gt 1
Assert-True ($ids.Count -gt 0) 'No FTB Quest object IDs were found.'
Assert-True ($duplicateIds.Count -eq 0) ('Duplicate FTB Quest IDs: ' + (($duplicateIds.Name) -join ', '))

$hexLikeIds = [regex]::Matches($allDefinitionText, '\bid:\s*"([^":]+)"') |
    ForEach-Object { $_.Groups[1].Value }
$invalidObjectIds = @($hexLikeIds | Where-Object { $_ -notmatch '^[0-9A-F]{16}$' })
Assert-True ($invalidObjectIds.Count -eq 0) ('Invalid FTB Quest object IDs: ' + ($invalidObjectIds -join ', '))

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
