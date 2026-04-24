# Fast Maven Install using Aliyun Mirror
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Fast Maven Install (Aliyun)" -ForegroundColor Cyan  
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$MAVEN_VERSION = "3.9.6"
# Using Aliyun mirror for faster download in China
$DOWNLOAD_URL = "https://mirrors.aliyun.com/apache/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
$INSTALL_DIR = "C:\Program Files\Apache"
$MAVEN_HOME = "$INSTALL_DIR\apache-maven-$MAVEN_VERSION"
$ZIP_FILE = "$env:TEMP\apache-maven-$MAVEN_VERSION-bin.zip"

# Check if already installed
Write-Host "[Step 1] Checking Maven..." -ForegroundColor Yellow
if (Test-Path "$MAVEN_HOME\bin\mvn.cmd") {
    Write-Host "[SUCCESS] Maven already installed" -ForegroundColor Green
    & "$MAVEN_HOME\bin\mvn.cmd" -version
    pause
    exit 0
}

# Create directory
Write-Host ""
Write-Host "[Step 2] Creating directory..." -ForegroundColor Yellow
if (-not (Test-Path $INSTALL_DIR)) {
    try {
        New-Item -ItemType Directory -Path $INSTALL_DIR -Force -ErrorAction Stop | Out-Null
        Write-Host "[SUCCESS] Directory created" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Cannot create directory. Please run as Administrator." -ForegroundColor Red
        Write-Host "Or manually create: $INSTALL_DIR" -ForegroundColor Yellow
        pause
        exit 1
    }
}

# Download from Aliyun (much faster in China)
Write-Host ""
Write-Host "[Step 3] Downloading from Aliyun mirror..." -ForegroundColor Yellow
Write-Host "URL: $DOWNLOAD_URL" -ForegroundColor Cyan

try {
    Invoke-WebRequest -Uri $DOWNLOAD_URL -OutFile $ZIP_FILE -UseBasicParsing
    Write-Host "[SUCCESS] Download completed" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Download failed: $_" -ForegroundColor Red
    Write-Host "Please download manually from Aliyun mirror" -ForegroundColor Yellow
    pause
    exit 1
}

# Extract
Write-Host ""
Write-Host "[Step 4] Extracting..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $ZIP_FILE -DestinationPath $INSTALL_DIR -Force
    Remove-Item $ZIP_FILE -Force
    Write-Host "[SUCCESS] Extraction completed" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Extraction failed" -ForegroundColor Red
    pause
    exit 1
}

# Verify
Write-Host ""
Write-Host "[Step 5] Verifying..." -ForegroundColor Yellow
if (Test-Path "$MAVEN_HOME\bin\mvn.cmd") {
    Write-Host "[SUCCESS] Maven installed at: $MAVEN_HOME" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Installation failed" -ForegroundColor Red
    pause
    exit 1
}

# Configure environment variables
Write-Host ""
Write-Host "[Step 6] Configuring environment..." -ForegroundColor Yellow

$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "[WARNING] Admin rights needed" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Manual configuration:" -ForegroundColor Cyan
    Write-Host "1. System Properties -> Environment Variables" -ForegroundColor White
    Write-Host "2. New: MAVEN_HOME = $MAVEN_HOME" -ForegroundColor White
    Write-Host "3. Edit Path, add: %MAVEN_HOME%\bin" -ForegroundColor White
    Write-Host ""
    
    $answer = Read-Host "Run as Administrator? (Y/N)"
    if ($answer -eq 'Y' -or $answer -eq 'y') {
        Start-Process powershell -Verb RunAs -ArgumentList "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`""
        exit 0
    }
    pause
    exit 0
}

[Environment]::SetEnvironmentVariable("MAVEN_HOME", $MAVEN_HOME, "Machine")
$oldPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
if ($oldPath -notlike "*$MAVEN_HOME\bin*") {
    $newPath = "$oldPath;$MAVEN_HOME\bin"
    [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
}

Write-Host "[SUCCESS] Environment configured!" -ForegroundColor Green

# Final verification
Write-Host ""
Write-Host "[Step 7] Final verification..." -ForegroundColor Yellow
$env:MAVEN_HOME = $MAVEN_HOME
$env:Path = "$env:Path;$MAVEN_HOME\bin"

& "$MAVEN_HOME\bin\mvn.cmd" -version

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  INSTALLATION COMPLETE!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Start backend with: mvn spring-boot:run" -ForegroundColor Cyan
Write-Host ""

pause
