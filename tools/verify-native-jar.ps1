param([Parameter(Mandatory = $true)][string]$JarPath)

$ErrorActionPreference = 'Stop'
$archive = [IO.Compression.ZipFile]::OpenRead((Resolve-Path -LiteralPath $JarPath).Path)
try {
    $names = @($archive.Entries | Where-Object { $_.Name } | ForEach-Object { $_.FullName })
    foreach ($name in $names) {
        if ($name -match '(^|/)\.\.(/|$)|\\' -or $name -notmatch '^(ch/minenox/firsttorch/.+\.class|assets/firsttorch/.+\.(json|png)|data/firsttorch/.+\.json|META-INF/(MANIFEST\.MF|neoforge\.mods\.toml|LICENSE|LICENSE-CODE|LICENSE-ASSETS\.md|NOTICE\.md))$') {
            throw "Unexpected native JAR content: $name"
        }
    }
    if (@($names | Sort-Object -Unique).Count -ne $names.Count) { throw 'Duplicate native JAR entries.' }
    foreach ($required in @('META-INF/neoforge.mods.toml', 'META-INF/LICENSE', 'META-INF/LICENSE-CODE', 'META-INF/LICENSE-ASSETS.md', 'META-INF/NOTICE.md',
        'assets/firsttorch/lang/en_us.json', 'assets/firsttorch/lang/de_de.json', 'data/firsttorch/guides/course.json')) {
        if ($names -cnotcontains $required) { throw "Missing native JAR resource: $required" }
    }
    if (-not @($names | Where-Object { $_ -cmatch '^ch/minenox/firsttorch/.+\.class$' }).Count) { throw 'No First Torch classes packaged.' }
    $reader = [IO.StreamReader]::new($archive.GetEntry('META-INF/neoforge.mods.toml').Open())
    try { $metadata = $reader.ReadToEnd() } finally { $reader.Dispose() }
    $sections = @([regex]::Matches($metadata, '(?ms)^\[\[dependencies\.firsttorch\]\]\s*(.*?)(?=^\[|\z)'))
    if ([regex]::Matches($metadata, '(?m)^\[\[dependencies\.').Count -ne 2) { throw 'Unexpected dependency sections.' }
    $ids = @($sections | ForEach-Object {
        $match = [regex]::Match($_.Groups[1].Value, '(?m)^modId\s*=\s*"([^"]+)"\s*$')
        if (-not $match.Success) { throw 'Unrecognised dependency metadata.' }
        $match.Groups[1].Value
    })
    if ($ids.Count -ne 2 -or $ids -cnotcontains 'minecraft' -or $ids -cnotcontains 'neoforge') {
        throw "Native dependency allowlist violated: $($ids -join ', ')"
    }
    Write-Output "Native JAR boundary passed: $($names.Count) entries; Minecraft and NeoForge only."
} finally { $archive.Dispose() }
