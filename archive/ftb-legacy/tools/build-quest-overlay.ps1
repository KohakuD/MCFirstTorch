[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot 'validate-pack.ps1')

$manifest = Get-Content -LiteralPath (Join-Path $repoRoot 'manifest.json') -Raw | ConvertFrom-Json
$buildRoot = Join-Path $repoRoot 'build'
$stageRoot = Join-Path $buildRoot 'quest-overlay-stage'
$archivePath = Join-Path $buildRoot ("First-Torch-{0}-Quest-Overlay.zip" -f $manifest.version)
$overridesRoot = Join-Path $repoRoot 'overrides'

$resolvedRepo = [System.IO.Path]::GetFullPath($repoRoot).TrimEnd('\', '/')
$resolvedBuild = [System.IO.Path]::GetFullPath($buildRoot).TrimEnd('\', '/')
$resolvedStage = [System.IO.Path]::GetFullPath($stageRoot)
if (-not $resolvedBuild.StartsWith($resolvedRepo + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
    throw 'Refusing to use a build directory outside the repository.'
}
if (-not $resolvedStage.StartsWith($resolvedBuild + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) {
    throw 'Refusing to clean a staging directory outside the build directory.'
}

if (Test-Path -LiteralPath $stageRoot) {
    Remove-Item -LiteralPath $stageRoot -Recurse -Force
}
if (Test-Path -LiteralPath $archivePath) {
    Remove-Item -LiteralPath $archivePath -Force
}

$managedPaths = @(
    'config/ftbquests/quests',
    'config/initially',
    'resourcepacks/first_torch_guides'
)

New-Item -ItemType Directory -Path $stageRoot -Force | Out-Null
foreach ($relativePath in $managedPaths) {
    $sourcePath = Join-Path $overridesRoot $relativePath
    $targetPath = Join-Path $stageRoot $relativePath
    if (-not (Test-Path -LiteralPath $sourcePath -PathType Container)) {
        throw "Quest-overlay source directory is missing: $sourcePath"
    }

    New-Item -ItemType Directory -Path (Split-Path -Parent $targetPath) -Force | Out-Null
    Copy-Item -LiteralPath $sourcePath -Destination (Split-Path -Parent $targetPath) -Recurse
}

Copy-Item -LiteralPath (Join-Path $repoRoot 'docs/QuestOverlayInstall.txt') -Destination (Join-Path $stageRoot 'FIRST-TORCH-INSTALL.txt')
Compress-Archive -Path (Join-Path $stageRoot '*') -DestinationPath $archivePath -CompressionLevel Optimal
Remove-Item -LiteralPath $stageRoot -Recurse -Force

$archiveEntries = @(tar -tf $archivePath)
$archiveRoots = @($archiveEntries | ForEach-Object { ($_ -split '/')[0] } | Sort-Object -Unique)
$expectedRoots = @('FIRST-TORCH-INSTALL.txt', 'config', 'resourcepacks')
if ($null -ne (Compare-Object -ReferenceObject $expectedRoots -DifferenceObject $archiveRoots)) {
    throw "Unexpected quest-overlay roots: $($archiveRoots -join ', ')"
}
if (@($archiveEntries | Where-Object { $_ -like '*.jar' }).Count -ne 0) {
    throw 'Quest overlay must not contain mod JARs.'
}

Write-Host "Built $archivePath"
