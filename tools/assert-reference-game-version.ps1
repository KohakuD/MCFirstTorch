param([Parameter(Mandatory = $true)][string]$MinecraftJar)

$ErrorActionPreference = 'Stop'
# This is the reviewed reference-data baseline, not an automatically upgraded build property.
$expectedVersion = '26.1.2'
$archive = [IO.Compression.ZipFile]::OpenRead((Resolve-Path -LiteralPath $MinecraftJar).Path)
try {
    $entry = $archive.GetEntry('version.json')
    if ($null -eq $entry) { throw 'Reference verification requires a Minecraft archive with version.json.' }
    $reader = [IO.StreamReader]::new($entry.Open())
    try { $version = $reader.ReadToEnd() | ConvertFrom-Json } finally { $reader.Dispose() }
    if ($version.id -cne $expectedVersion) {
        throw "Reference data was reviewed for Minecraft $expectedVersion, not '$($version.id)'. Review the reference content before updating this baseline."
    }
} finally { $archive.Dispose() }
