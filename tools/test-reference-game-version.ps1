$ErrorActionPreference = 'Stop'
$testDirectory = Join-Path ([IO.Path]::GetTempPath()) ('firsttorch-version-test-' + [guid]::NewGuid())
[IO.Directory]::CreateDirectory($testDirectory) | Out-Null
$fixtures = @()
try {
    foreach ($case in @(
        @{ Name = 'correct'; Content = '{"id":"26.1.2"}'; Accepted = $true },
        @{ Name = 'wrong'; Content = '{"id":"26.2"}'; Accepted = $false },
        @{ Name = 'missing-id'; Content = '{}'; Accepted = $false },
        @{ Name = 'malformed'; Content = '{'; Accepted = $false },
        @{ Name = 'missing-entry'; Content = $null; Accepted = $false }
    )) {
        $fixture = Join-Path $testDirectory ($case.Name + '.jar')
        $fixtures += $fixture
        $archive = [IO.Compression.ZipFile]::Open($fixture, [IO.Compression.ZipArchiveMode]::Create)
        try {
            if ($null -ne $case.Content) {
                $writer = [IO.StreamWriter]::new($archive.CreateEntry('version.json').Open())
                try { $writer.Write($case.Content) } finally { $writer.Dispose() }
            }
        } finally { $archive.Dispose() }
        $accepted = $true
        try { & (Join-Path $PSScriptRoot 'assert-reference-game-version.ps1') -MinecraftJar $fixture }
        catch { $accepted = $false }
        if ($accepted -ne $case.Accepted) { throw "Unexpected version-check result: $($case.Name)" }
    }
    Write-Output 'All five reference version-gate tests passed.'
} finally {
    # Only exact fixture files created above; no recursive deletion or user files.
    foreach ($fixture in $fixtures) { if (Test-Path -LiteralPath $fixture) { Remove-Item -LiteralPath $fixture } }
    [IO.Directory]::Delete($testDirectory)
}
