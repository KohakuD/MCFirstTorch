$ErrorActionPreference = 'Stop'
$directory = Join-Path ([IO.Path]::GetTempPath()) ('firsttorch-jar-test-' + [guid]::NewGuid())
[IO.Directory]::CreateDirectory($directory) | Out-Null
$fixtures = @()
try {
    foreach ($case in @('valid', 'world', 'foreign-class', 'nested-jar', 'extra-dependency', 'missing-language', 'missing-core')) {
        $path = Join-Path $directory "$case.jar"
        $fixtures += $path
        $entries = @{
            'META-INF/neoforge.mods.toml' = "[[dependencies.firsttorch]]`nmodId=`"minecraft`"`n[[dependencies.firsttorch]]`nmodId=`"neoforge`"`n"
            'META-INF/LICENSE' = 'fixture'
            'META-INF/LICENSE-CODE' = 'fixture'
            'META-INF/LICENSE-ASSETS.md' = 'fixture'
            'META-INF/NOTICE.md' = 'fixture'
            'assets/firsttorch/lang/en_us.json' = '{}'
            'assets/firsttorch/lang/de_de.json' = '{}'
            'data/firsttorch/guides/course.json' = '{}'
            'ch/minenox/firsttorch/Fixture.class' = 'boundary fixture, not executable'
            'ch/minenox/firsttorch/guide/GuideSnapshot.class' = 'fixture'
            'ch/minenox/firsttorch/guide/data/GuideJson.class' = 'fixture'
            'ch/minenox/firsttorch/guide/progress/TaskEvaluator.class' = 'fixture'
        }
        switch ($case) {
            'world' { $entries['saves/Test/level.dat'] = 'private fixture' }
            'foreign-class' { $entries['dev/ftb/quests/Fixture.class'] = 'fixture' }
            'nested-jar' { $entries['META-INF/jarjar/dependency.jar'] = 'fixture' }
            'extra-dependency' { $entries['META-INF/neoforge.mods.toml'] += "[[dependencies.firsttorch]]`nmodId=`"ftbquests`"`n" }
            'missing-core' { $entries.Remove('ch/minenox/firsttorch/guide/progress/TaskEvaluator.class') }
            'missing-language' { $entries.Remove('assets/firsttorch/lang/de_de.json') }
        }
        $archive = [IO.Compression.ZipFile]::Open($path, [IO.Compression.ZipArchiveMode]::Create)
        try {
            foreach ($entry in $entries.GetEnumerator()) {
                $writer = [IO.StreamWriter]::new($archive.CreateEntry($entry.Key).Open())
                try { $writer.Write($entry.Value) } finally { $writer.Dispose() }
            }
        } finally { $archive.Dispose() }
        $accepted = $true
        try { & (Join-Path $PSScriptRoot 'verify-native-jar.ps1') -JarPath $path | Out-Null }
        catch { $accepted = $false }
        if ($accepted -ne ($case -eq 'valid')) { throw "Unexpected boundary result: $case" }
    }
    Write-Output 'All seven native JAR boundary fixtures passed.'
} finally {
    foreach ($path in $fixtures) { if (Test-Path -LiteralPath $path) { Remove-Item -LiteralPath $path } }
    [IO.Directory]::Delete($directory)
}
