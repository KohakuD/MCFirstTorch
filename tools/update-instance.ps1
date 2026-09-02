[CmdletBinding(SupportsShouldProcess = $true, ConfirmImpact = 'Medium')]
param(
    [Parameter(Mandatory = $true)]
    [ValidateNotNullOrEmpty()]
    [string]$InstancePath
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$overridesRoot = Join-Path $repoRoot 'overrides'
$manifestPath = Join-Path $repoRoot 'manifest.json'

function Assert-ContainedPath {
    param(
        [string]$Root,
        [string]$Candidate,
        [string]$Label
    )

    $resolvedRoot = [System.IO.Path]::GetFullPath($Root).TrimEnd('\', '/')
    $resolvedCandidate = [System.IO.Path]::GetFullPath($Candidate)
    $rootPrefix = $resolvedRoot + [System.IO.Path]::DirectorySeparatorChar
    if (-not $resolvedCandidate.StartsWith($rootPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "$Label is outside the expected root: $resolvedCandidate"
    }
}

function Assert-TreeMatches {
    param(
        [string]$Source,
        [string]$Target
    )

    $sourceFiles = @(Get-ChildItem -LiteralPath $Source -Recurse -File)
    $targetFiles = @(Get-ChildItem -LiteralPath $Target -Recurse -File)
    if ($sourceFiles.Count -ne $targetFiles.Count) {
        throw "Copied file count differs for $Target."
    }

    foreach ($sourceFile in $sourceFiles) {
        $relativePath = [System.IO.Path]::GetRelativePath($Source, $sourceFile.FullName)
        $targetFile = Join-Path $Target $relativePath
        if (-not (Test-Path -LiteralPath $targetFile -PathType Leaf)) {
            throw "Copied file is missing: $targetFile"
        }

        $sourceHash = (Get-FileHash -LiteralPath $sourceFile.FullName -Algorithm SHA256).Hash
        $targetHash = (Get-FileHash -LiteralPath $targetFile -Algorithm SHA256).Hash
        if ($sourceHash -ne $targetHash) {
            throw "Copied file failed SHA-256 verification: $targetFile"
        }
    }
}

& (Join-Path $PSScriptRoot 'validate-pack.ps1')

$instanceRoot = [System.IO.Path]::GetFullPath($InstancePath).TrimEnd('\', '/')
if (-not (Test-Path -LiteralPath $instanceRoot -PathType Container)) {
    throw "Minecraft instance does not exist: $instanceRoot"
}
if (-not (Test-Path -LiteralPath (Join-Path $instanceRoot 'minecraftinstance.json') -PathType Leaf)) {
    throw "The selected directory is not a CurseForge Minecraft instance: $instanceRoot"
}
if ($instanceRoot -eq [System.IO.Path]::GetPathRoot($instanceRoot).TrimEnd('\', '/')) {
    throw 'Refusing to update a filesystem root.'
}

$manifest = Get-Content -LiteralPath $manifestPath -Raw | ConvertFrom-Json
$managedPaths = @(
    'config/ftbquests/quests',
    'resourcepacks/first_torch_guides'
)
$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$stateRoot = Join-Path $instanceRoot '.first-torch'
$backupRoot = Join-Path $stateRoot (Join-Path 'backups' $timestamp)
$updatedPaths = [System.Collections.Generic.List[string]]::new()

foreach ($relativePath in $managedPaths) {
    $sourcePath = Join-Path $overridesRoot $relativePath
    $targetPath = Join-Path $instanceRoot $relativePath
    $backupPath = Join-Path $backupRoot $relativePath

    Assert-ContainedPath -Root $overridesRoot -Candidate $sourcePath -Label 'Managed source path'
    Assert-ContainedPath -Root $instanceRoot -Candidate $targetPath -Label 'Managed target path'
    Assert-ContainedPath -Root $instanceRoot -Candidate $backupPath -Label 'Backup path'

    if (-not (Test-Path -LiteralPath $sourcePath -PathType Container)) {
        throw "Managed source directory is missing: $sourcePath"
    }

    if ($PSCmdlet.ShouldProcess($targetPath, "Back up and install First Torch $($manifest.version)")) {
        if (Test-Path -LiteralPath $targetPath) {
            $backupParent = Split-Path -Parent $backupPath
            New-Item -ItemType Directory -Path $backupParent -Force | Out-Null
            Copy-Item -LiteralPath $targetPath -Destination $backupParent -Recurse -Force
            Remove-Item -LiteralPath $targetPath -Recurse -Force
        }

        $targetParent = Split-Path -Parent $targetPath
        New-Item -ItemType Directory -Path $targetParent -Force | Out-Null
        Copy-Item -LiteralPath $sourcePath -Destination $targetParent -Recurse -Force
        Assert-TreeMatches -Source $sourcePath -Target $targetPath
        $updatedPaths.Add($relativePath)
    }
}

if ($updatedPaths.Count -gt 0) {
    $optionsPath = Join-Path $instanceRoot 'options.txt'
    $resourcePackName = 'file/first_torch_guides'
    if (Test-Path -LiteralPath $optionsPath -PathType Leaf) {
        $optionsBackupPath = Join-Path $backupRoot 'options.txt'
        New-Item -ItemType Directory -Path (Split-Path -Parent $optionsBackupPath) -Force | Out-Null
        Copy-Item -LiteralPath $optionsPath -Destination $optionsBackupPath -Force

        $optionLines = @(Get-Content -LiteralPath $optionsPath)
        $resourcePackLine = $optionLines | Where-Object { $_ -match '^resourcePacks:' } | Select-Object -First 1
        $resourcePacks = @()
        if ($resourcePackLine) {
            $resourcePacks = @($resourcePackLine.Substring('resourcePacks:'.Length) | ConvertFrom-Json)
        }
        if ($resourcePacks -notcontains $resourcePackName) {
            $resourcePacks += $resourcePackName
            $newResourcePackLine = 'resourcePacks:' + (ConvertTo-Json -InputObject ([object[]]$resourcePacks) -Compress)
            if ($resourcePackLine) {
                $optionLines = @($optionLines | ForEach-Object {
                    if ($_ -match '^resourcePacks:') { $newResourcePackLine } else { $_ }
                })
            } else {
                $optionLines += $newResourcePackLine
            }
            Set-Content -LiteralPath $optionsPath -Value $optionLines -Encoding utf8
        }
    }

    New-Item -ItemType Directory -Path $stateRoot -Force | Out-Null
    $state = [ordered]@{
        name = $manifest.name
        version = $manifest.version
        updatedAt = (Get-Date).ToString('o')
        managedPaths = $managedPaths
        dependencies = @($manifest.files | ForEach-Object {
            [ordered]@{
                projectID = $_.projectID
                fileID = $_.fileID
            }
        })
    }
    $state | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath (Join-Path $stateRoot 'state.json') -Encoding utf8

    Write-Host "Updated First Torch to $($manifest.version) in $instanceRoot"
    Write-Host "Verified $($updatedPaths.Count) managed path(s) by SHA-256."
    Write-Host "Backup: $backupRoot"
    Write-Host 'Worlds and player settings were preserved; only the First Torch guide resource pack was enabled in options.txt.'
}
