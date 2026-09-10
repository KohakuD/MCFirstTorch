param([string]$OutputDirectory = 'build/release-screenshots')

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null
$captures = @(
    @{ Source = 'C:/Users/daves/AppData/Local/Temp/codex-clipboard-d84c6407-6645-46aa-a943-ae548b204f63.png'; Name = 'first-torch-welcome.png'; X = 702; Y = 487; Right = 1344; Bottom = 785 },
    @{ Source = 'C:/Users/daves/AppData/Local/Temp/codex-clipboard-9b25e37e-73a6-404e-9dd8-e3e0edea0819.png'; Name = 'first-torch-questbook.png'; X = 88; Y = 112; Right = 1960; Bottom = 1161 },
    @{ Source = 'C:/Users/daves/AppData/Local/Temp/codex-clipboard-16b6d50b-d133-4ae7-a357-5369f16267d7.png'; Name = 'first-torch-first-steps.png'; X = 88; Y = 112; Right = 1960; Bottom = 1161 }
)
foreach ($capture in $captures) {
    $source = [System.Drawing.Bitmap]::new($capture.Source)
    try {
        # Coordinates refer to the 2048 x 1245 review preview; preserve source pixels.
        $left = [int][Math]::Floor($capture.X * $source.Width / 2048)
        $top = [int][Math]::Floor($capture.Y * $source.Height / 1245)
        $right = [int][Math]::Ceiling($capture.Right * $source.Width / 2048)
        $bottom = [int][Math]::Ceiling($capture.Bottom * $source.Height / 1245)
        $rect = [System.Drawing.Rectangle]::new($left, $top, $right - $left, $bottom - $top)
        $cropped = $source.Clone($rect, $source.PixelFormat)
        try {
            $destination = Join-Path $OutputDirectory $capture.Name
            $cropped.Save([IO.Path]::GetFullPath($destination), [System.Drawing.Imaging.ImageFormat]::Png)
            Write-Output "$destination ($($cropped.Width) x $($cropped.Height))"
        } finally { $cropped.Dispose() }
    } finally { $source.Dispose() }
}
