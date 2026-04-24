# Auto Install Maven Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Auto Install Maven" -ForegroundColor Cyan  
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$MAVEN_VERSION = "3.9.6"
$DOWNLOAD_URL = "https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
$BACKUP_URL = "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
$INSTALL_DIR = "C:\Program Files\Apache"
$MAVEN_HOME = "$INSTALL_DIR\apache-maven-$MAVEN_VERSION"
$ZIP_FILE = "$env:TEMP\apache-maven-$MAVEN_VERSION-bin.zip"

# Step 1: Check if already installed
Write-Host "[Step 1] Checking Maven..." -ForegroundColor Yellow
if (Test-Path "$MAVEN_HOME\bin\mvn.cmd") {
    Write-Host "[SUCCESS] Maven already installed at: $MAVEN_HOME" -ForegroundColor Green
    Write-Host ""
    & "$MAVEN_HOME\bin\mvn.cmd" -version
    pause
    exit 0
}

# Step 2: Create directory
Write-Host ""
Write-Host "[Step 2] Creating directory..." -ForegroundColor Yellow
if (-not (Test-Path $INSTALL_DIR)) {
    New-Item -ItemType Directory -Path $INSTALL_DIR -Force | Out-Null
    Write-Host "[SUCCESS] Directory created: $INSTALL_DIR" -ForegroundColor Green
}

# Step 3: Download Maven
Write-Host ""
Write-Host "[Step 3] Downloading Maven..." -ForegroundColor Yellow
Write-Host "URL: $DOWNLOAD_URL" -ForegroundColor Cyan

try {
    Write-Host "Downloading from primary URL..." -ForegroundColor Yellow
    Invoke-WebRequest -Uri $DOWNLOAD_URL -OutFile $ZIP_FILE -UseBasicParsing
    Write-Host "[SUCCESS] Download completed" -ForegroundColor Green
} catch {
    Write-Host "[WARNING] Primary URL failed, trying backup..." -ForegroundColor Yellow
    try {
        Invoke-WebRequest -Uri $BACKUP_URL -OutFile $ZIP_FILE -UseBasicParsing
        Write-Host "[SUCCESS] Downloaded from backup URL" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Download failed!" -ForegroundColor Red
        Write-Host "Please download manually from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
        pause
        exit 1
    }
}

# Step 4: Extract
Write-Host ""
Write-Host "[Step 4] Extracting Maven..." -ForegroundColor Yellow

try {
    Expand-Archive -Path $ZIP_FILE -DestinationPath $INSTALL_DIR -Force
    Write-Host "[SUCCESS] Extraction completed" -ForegroundColor Green
    Remove-Item $ZIP_FILE -Force
} catch {
    Write-Host "[ERROR] Extraction failed: $_" -ForegroundColor Red
    pause
    exit 1
}

# Step 5: Verify
Write-Host ""
Write-Host "[Step 5] Verifying installation..." -ForegroundColor Yellow
if (Test-Path "$MAVEN_HOME\bin\mvn.cmd") {
    Write-Host "[SUCCESS] Maven files found" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Maven files not found" -ForegroundColor Red
    pause
    exit 1
}

# Step 6: Configure environment variables
Write-Host ""
Write-Host "[Step 6] Configuring environment variables..." -ForegroundColor Yellow

try {
    $isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
    
    if (-not $isAdmin) {
        Write-Host "[WARNING] Admin rights required for environment variables" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Please run this script as Administrator, or configure manually:" -ForegroundColor Yellow
        Write-Host "1. Right-click 'This PC' -> Properties -> Advanced system settings -> Environment Variables" -ForegroundColor Cyan
        Write-Host "2. New System Variable: MAVEN_HOME = $MAVEN_HOME" -ForegroundColor Cyan
        Write-Host "3. Edit Path, add: %MAVEN_HOME%\bin" -ForegroundColor Cyan
        Write-Host ""
        
        $answer = Read-Host "Run as Administrator now? (Y/N)"
        if ($answer -eq 'Y' -or $answer -eq 'y') {
            Start-Process powershell -Verb RunAs -ArgumentList "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`""
            exit 0
        }
        pause
        exit 0
    }
    
    Write-Host "Setting MAVEN_HOME..." -ForegroundColor Yellow
    [Environment]::SetEnvironmentVariable("MAVEN_HOME", $MAVEN_HOME, "Machine")
    Write-Host "[SUCCESS] MAVEN_HOME set" -ForegroundColor Green
    
    Write-Host "Updating PATH..." -ForegroundColor Yellow
    $oldPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
    if ($oldPath -notlike "*$MAVEN_HOME\bin*") {
        $newPath = "$oldPath;$MAVEN_HOME\bin"
        [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
        Write-Host "[SUCCESS] PATH updated" -ForegroundColor Green
    }
    
    Write-Host ""
    Write-Host "[SUCCESS] Environment configured!" -ForegroundColor Green
    
} catch {
    Write-Host "[ERROR] Configuration failed: $_" -ForegroundColor Red
    pause
    exit 1
}

# Step 7: Verify installation
Write-Host ""
Write-Host "[Step 7] Verifying Maven..." -ForegroundColor Yellow
Write-Host ""

$env:MAVEN_HOME = $MAVEN_HOME
$env:Path = "$env:Path;$MAVEN_HOME\bin"

try {
    & "$MAVEN_HOME\bin\mvn.cmd" -version
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  Maven Installation SUCCESS!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can now start the backend:" -ForegroundColor Cyan
    Write-Host "mvn spring-boot:run" -ForegroundColor White
    Write-Host ""
} catch {
    Write-Host "[WARNING] Please restart terminal and run: mvn -version" -ForegroundColor Yellow
}

pause
