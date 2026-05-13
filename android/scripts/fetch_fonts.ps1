# Downloads the three font families bundled by the Cream & Terracotta UI
# (Cormorant Garamond, Manrope, JetBrains Mono) into res/font/.
#
# Sources (all OFL-licensed):
#   - Cormorant Garamond: github.com/CatharsisFonts/Cormorant (upstream)
#   - Manrope:            github.com/davelab6/manrope (mirror, legacy ttf)
#   - JetBrains Mono:     github.com/JetBrains/JetBrainsMono (upstream)
#
# Run from anywhere:
#   powershell -NoProfile -ExecutionPolicy Bypass -File android\scripts\fetch_fonts.ps1
#
# Idempotent: skips files already present.

$ErrorActionPreference = 'Stop'
$ProgressPreference    = 'SilentlyContinue'

$repoRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$fontDir  = Join-Path $repoRoot 'android\app\src\main\res\font'
if (-not (Test-Path $fontDir)) {
    throw "Font dir not found: $fontDir"
}

$files = @(
    @{ name = 'cormorant_garamond_medium.ttf';
       url  = 'https://raw.githubusercontent.com/CatharsisFonts/Cormorant/master/fonts/ttf/CormorantGaramond-Medium.ttf' },
    @{ name = 'cormorant_garamond_medium_italic.ttf';
       url  = 'https://raw.githubusercontent.com/CatharsisFonts/Cormorant/master/fonts/ttf/CormorantGaramond-MediumItalic.ttf' },
    @{ name = 'cormorant_garamond_semibold_italic.ttf';
       url  = 'https://raw.githubusercontent.com/CatharsisFonts/Cormorant/master/fonts/ttf/CormorantGaramond-SemiBoldItalic.ttf' },

    @{ name = 'manrope_regular.ttf';
       url  = 'https://raw.githubusercontent.com/davelab6/manrope/master/ttf%20format%20(legacy)/manrope-regular.ttf' },
    @{ name = 'manrope_medium.ttf';
       url  = 'https://raw.githubusercontent.com/davelab6/manrope/master/ttf%20format%20(legacy)/manrope-medium.ttf' },
    @{ name = 'manrope_semibold.ttf';
       url  = 'https://raw.githubusercontent.com/davelab6/manrope/master/ttf%20format%20(legacy)/manrope-semibold.ttf' },
    @{ name = 'manrope_bold.ttf';
       url  = 'https://raw.githubusercontent.com/davelab6/manrope/master/ttf%20format%20(legacy)/manrope-bold.ttf' },

    @{ name = 'jetbrains_mono_regular.ttf';
       url  = 'https://raw.githubusercontent.com/JetBrains/JetBrainsMono/master/fonts/ttf/JetBrainsMono-Regular.ttf' },
    @{ name = 'jetbrains_mono_medium.ttf';
       url  = 'https://raw.githubusercontent.com/JetBrains/JetBrainsMono/master/fonts/ttf/JetBrainsMono-Medium.ttf' },
    @{ name = 'jetbrains_mono_semibold.ttf';
       url  = 'https://raw.githubusercontent.com/JetBrains/JetBrainsMono/master/fonts/ttf/JetBrainsMono-SemiBold.ttf' },
    @{ name = 'jetbrains_mono_bold.ttf';
       url  = 'https://raw.githubusercontent.com/JetBrains/JetBrainsMono/master/fonts/ttf/JetBrainsMono-Bold.ttf' }
)

$downloaded = 0
$skipped    = 0
$failed     = @()

foreach ($f in $files) {
    $dest = Join-Path $fontDir $f.name
    if (Test-Path $dest) {
        Write-Host "SKIP $($f.name) (already present)"
        $skipped++
        continue
    }
    try {
        Invoke-WebRequest -Uri $f.url -OutFile $dest -UseBasicParsing -MaximumRedirection 5
        $size = (Get-Item $dest).Length
        Write-Host ("GOT  {0} ({1:N0} bytes)" -f $f.name, $size)
        $downloaded++
    } catch {
        Write-Warning "FAIL $($f.name) <- $($f.url): $_"
        if (Test-Path $dest) { Remove-Item $dest -Force }
        $failed += $f
    }
}

Write-Host ""
Write-Host "Downloaded: $downloaded, Skipped: $skipped, Failed: $($failed.Count)"
if ($failed.Count -gt 0) { exit 1 }
