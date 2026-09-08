param(
    [string]$ScreenshotDirectory = (Join-Path $PSScriptRoot '../run/screenshots')
)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$destination = Join-Path $PSScriptRoot '../overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics'

# Preserve the original capture pixel-for-pixel except for a neutral instructional overlay.
$source = Join-Path $ScreenshotDirectory '2026-09-08_17.21.05.png'
$bitmap = [System.Drawing.Bitmap]::new($source)
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$shadow = [System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(255, 24, 19, 10), 14)
$gold = [System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(255, 255, 170, 24), 8)
$brush = [System.Drawing.SolidBrush]::new($gold.Color)
try {
    if ($bitmap.Width -ne 2277 -or $bitmap.Height -ne 1353) { throw 'Unexpected screenshot dimensions; recheck button coordinates.' }
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    # Button on the wall immediately right of the Door, not the Door handle.
    $box = [System.Drawing.Rectangle]::new(1333, 552, 105, 76)
    $graphics.DrawRectangle($shadow, $box)
    $graphics.DrawRectangle($gold, $box)
    $graphics.DrawLine($shadow, 1595, 778, 1444, 642)
    $graphics.DrawLine($gold, 1595, 778, 1444, 642)
    $arrow = [System.Drawing.Point[]]@(
        [System.Drawing.Point]::new(1425, 625),
        [System.Drawing.Point]::new(1471, 641),
        [System.Drawing.Point]::new(1446, 668)
    )
    $graphics.FillPolygon($brush, $arrow)
    $bitmap.Save((Join-Path $destination 'stronghold_iron_door_capture.png'), [System.Drawing.Imaging.ImageFormat]::Png)
} finally {
    $brush.Dispose(); $gold.Dispose(); $shadow.Dispose(); $graphics.Dispose(); $bitmap.Dispose()
}

# These are authentic, unmodified scene captures, not regenerated diagrams.
Copy-Item -LiteralPath (Join-Path $ScreenshotDirectory '2026-09-08_17.22.37.png') -Destination (Join-Path $destination 'end_portal_room_capture.png')
Copy-Item -LiteralPath (Join-Path $ScreenshotDirectory '2026-09-08_17.23.39.png') -Destination (Join-Path $destination 'end_portal_active_capture.png')
