param(
    [string]$Source = (Join-Path $PSScriptRoot '../run/screenshots/2026-09-09_14.23.55.png'),
    [string]$Destination = (Join-Path $PSScriptRoot '../src/main/resources/assets/firsttorch/textures/questpics/pumpkin_view_capture.png')
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$original = [Drawing.Bitmap]::new((Resolve-Path -LiteralPath $Source).Path)
try {
    if ($original.Width -ne 2277 -or $original.Height -ne 1353) {
        throw 'HUD coordinates are specific to the approved 2277x1353 capture.'
    }
    $edited = [Drawing.Bitmap]::new($original)
    try {
        $graphics = [Drawing.Graphics]::FromImage($edited)
        try {
            # Both overlays lie on black areas: the pumpkin mask and the Enderman torso.
            # Neutral black rectangles remove only the HUD; no game scene is generated.
            $graphics.FillRectangle([Drawing.Brushes]::Black, 1127, 665, 23, 23)
            $graphics.FillRectangle([Drawing.Brushes]::Black, 945, 1305, 390, 48)
        } finally { $graphics.Dispose() }
        $edited.Save([IO.Path]::GetFullPath($Destination), [Drawing.Imaging.ImageFormat]::Png)
    } finally { $edited.Dispose() }
} finally { $original.Dispose() }
Write-Output "Prepared original pumpkin-view capture: $Destination"
