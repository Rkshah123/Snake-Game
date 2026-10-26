#Requires -Version 5.1
<#
.SYNOPSIS
    Builds Snake3D for Windows: tests, portable app image, portable zip and installer.

.DESCRIPTION
    Uses the project-local toolchain in .tools when present (JDK, Maven, WiX),
    otherwise falls back to JAVA_HOME / PATH. The installer step requires the
    WiX Toolset 3.x (candle.exe and light.exe) on PATH.

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File scripts\build-desktop.ps1
#>
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

$AppName = "Snake3D"
$AppVersion = "1.0.0"
$PortableDir = Join-Path $Root "dist\portable"
$InstallerDir = Join-Path $Root "dist\installer"
$InputDir = Join-Path $Root "target\desktop-input"
$Icon = Join-Path $Root "src\main\resources\icon.ico"

function Find-Directory([string]$Base, [string]$Pattern) {
    if (-not (Test-Path $Base)) { return $null }
    return Get-ChildItem -Path $Base -Directory -Filter $Pattern |
        Sort-Object Name -Descending | Select-Object -First 1
}

# ---------- toolchain ----------
$JdkDir = Find-Directory (Join-Path $Root ".tools") "jdk-17*"
if ($JdkDir) {
    $env:JAVA_HOME = $JdkDir.FullName
    $JdkBin = Join-Path $JdkDir.FullName "bin"
} elseif ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\jpackage.exe"))) {
    $JdkBin = Join-Path $env:JAVA_HOME "bin"
} else {
    throw "JDK 17+ not found. Install a JDK and set JAVA_HOME, or place it under .tools\jdk-17*."
}
$env:PATH = "$JdkBin;$env:PATH"

$MavenDir = Find-Directory (Join-Path $Root ".tools") "apache-maven-*"
$Mvn = if ($MavenDir) { Join-Path $MavenDir.FullName "bin\mvn.cmd" } else { "mvn" }
if ($MavenDir) { $env:PATH = "$(Join-Path $MavenDir.FullName 'bin');$env:PATH" }

$WixDir = Join-Path $Root ".tools\wix"
$HasWix = (Test-Path (Join-Path $WixDir "candle.exe")) -and (Test-Path (Join-Path $WixDir "light.exe"))
if ($HasWix) { $env:PATH = "$WixDir;$env:PATH" }
if (-not $HasWix) {
    $candle = Get-Command candle.exe -ErrorAction SilentlyContinue
    $HasWix = [bool]$candle
}

Write-Host "JDK:    $env:JAVA_HOME"
Write-Host "Maven:  $Mvn"
Write-Host "WiX:    $(if ($HasWix) { 'available' } else { 'MISSING (installer step will be skipped)' })"

$running = Get-Process -Name $AppName -ErrorAction SilentlyContinue
if ($running) {
    $pids = ($running | ForEach-Object { $_.Id }) -join ", "
    throw "$AppName is running (PID $pids). Close it before building so the output files can be replaced."
}

# ---------- tests + jar ----------
Write-Host "`n[1/4] Running tests and building the jar..."
& $Mvn -B clean package
if ($LASTEXITCODE -ne 0) { throw "Maven build failed with exit code $LASTEXITCODE" }

Copy-Item (Join-Path $Root "target\snake3d.jar") $InputDir -Force

# ---------- portable app image ----------
Write-Host "`n[2/4] Building the portable app image..."
if (Test-Path $PortableDir) { Remove-Item $PortableDir -Recurse -Force }
New-Item -ItemType Directory -Path $PortableDir -Force | Out-Null

& (Join-Path $JdkBin "jpackage.exe") `
    --type app-image `
    --name $AppName `
    --app-version $AppVersion `
    --vendor "Snake3D" `
    --description "3D arcade snake game with AI opponents" `
    --input $InputDir `
    --main-jar "snake3d.jar" `
    --main-class Launcher `
    --icon $Icon `
    --dest $PortableDir
if ($LASTEXITCODE -ne 0) { throw "jpackage app-image failed with exit code $LASTEXITCODE" }

$PortableZip = Join-Path $PortableDir "$AppName-$AppVersion-windows-portable.zip"
Write-Host "`n[3/4] Creating $PortableZip ..."
Compress-Archive -Path (Join-Path $PortableDir "$AppName\*") -DestinationPath $PortableZip -Force

# ---------- installer ----------
if ($HasWix) {
    Write-Host "`n[4/4] Building the Windows installer..."
    if (Test-Path $InstallerDir) { Remove-Item $InstallerDir -Recurse -Force }
    New-Item -ItemType Directory -Path $InstallerDir -Force | Out-Null

    & (Join-Path $JdkBin "jpackage.exe") `
        --type exe `
        --name $AppName `
        --app-version $AppVersion `
        --vendor "Snake3D" `
        --description "3D arcade snake game with AI opponents" `
        --input $InputDir `
        --main-jar "snake3d.jar" `
        --main-class Launcher `
        --icon $Icon `
        --dest $InstallerDir `
        --win-menu `
        --win-menu-group "Snake3D" `
        --win-shortcut `
        --win-dir-chooser `
        --win-per-user-install `
        --win-upgrade-uuid "5a9b7c8d-2f3e-4a6b-8c1d-9e0f2a4b6c8d"
    if ($LASTEXITCODE -ne 0) { throw "jpackage installer failed with exit code $LASTEXITCODE" }
    $Installer = Join-Path $InstallerDir "$AppName-$AppVersion.exe"
} else {
    Write-Warning "WiX not found, so only the portable build was created."
    $Installer = $null
}

# ---------- result ----------
Write-Host "`nBuild complete."
Write-Host "Portable app:  $(Join-Path $PortableDir "$AppName\$AppName.exe")"
Write-Host "Portable zip:  $PortableZip"
if ($Installer) { Write-Host "Installer:     $Installer" }
