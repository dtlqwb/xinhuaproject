# Maven自动安装脚本 - PowerShell
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  智销助手 - Maven自动安装" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$MAVEN_VERSION = "3.9.6"
$DOWNLOAD_URL = "https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
$BACKUP_URL = "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
$INSTALL_DIR = "C:\Program Files\Apache"
$MAVEN_HOME = "$INSTALL_DIR\apache-maven-$MAVEN_VERSION"
$ZIP_FILE = "$env:TEMP\apache-maven-$MAVEN_VERSION-bin.zip"

# 步骤1: 检查是否已安装
Write-Host "[步骤1] 检查Maven是否已安装..." -ForegroundColor Yellow
if (Test-Path "$MAVEN_HOME\bin\mvn.cmd") {
    Write-Host "[成功] Maven已安装在: $MAVEN_HOME" -ForegroundColor Green
    Write-Host ""
    Write-Host "正在验证版本..." -ForegroundColor Yellow
    & "$MAVEN_HOME\bin\mvn.cmd" -version
    Write-Host ""
    Write-Host "Maven已就绪！可以直接使用。" -ForegroundColor Green
    pause
    exit 0
}

# 步骤2: 创建安装目录
Write-Host ""
Write-Host "[步骤2] 创建安装目录..." -ForegroundColor Yellow
if (-not (Test-Path $INSTALL_DIR)) {
    New-Item -ItemType Directory -Path $INSTALL_DIR -Force | Out-Null
    Write-Host "[成功] 目录已创建: $INSTALL_DIR" -ForegroundColor Green
} else {
    Write-Host "[信息] 目录已存在: $INSTALL_DIR" -ForegroundColor Cyan
}

# 步骤3: 下载Maven
Write-Host ""
Write-Host "[步骤3] 开始下载Maven..." -ForegroundColor Yellow
Write-Host "下载地址: $DOWNLOAD_URL" -ForegroundColor Cyan
Write-Host ""

try {
    # 尝试主下载地址
    Write-Host "正在从主地址下载..." -ForegroundColor Yellow
    Invoke-WebRequest -Uri $DOWNLOAD_URL -OutFile $ZIP_FILE -UseBasicParsing
    Write-Host "[成功] 下载完成" -ForegroundColor Green
} catch {
    Write-Host "[警告] 主地址下载失败，尝试备用地址..." -ForegroundColor Yellow
    try {
        Invoke-WebRequest -Uri $BACKUP_URL -OutFile $ZIP_FILE -UseBasicParsing
        Write-Host "[成功] 从备用地址下载完成" -ForegroundColor Green
    } catch {
        Write-Host "[错误] 下载失败！" -ForegroundColor Red
        Write-Host ""
        Write-Host "请手动下载Maven：" -ForegroundColor Yellow
        Write-Host "1. 访问: https://maven.apache.org/download.cgi" -ForegroundColor Cyan
        Write-Host "2. 下载 apache-maven-$MAVEN_VERSION-bin.zip" -ForegroundColor Cyan
        Write-Host "3. 解压到: $MAVEN_HOME" -ForegroundColor Cyan
        Write-Host ""
        pause
        exit 1
    }
}

# 步骤4: 解压文件
Write-Host ""
Write-Host "[步骤4] 解压Maven..." -ForegroundColor Yellow

try {
    # 使用Expand-Archive解压
    Expand-Archive -Path $ZIP_FILE -DestinationPath $INSTALL_DIR -Force
    Write-Host "[成功] 解压完成" -ForegroundColor Green
    
    # 删除zip文件
    Remove-Item $ZIP_FILE -Force
    Write-Host "[信息] 临时文件已清理" -ForegroundColor Cyan
} catch {
    Write-Host "[错误] 解压失败: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "请手动解压zip文件到: $INSTALL_DIR" -ForegroundColor Yellow
    pause
    exit 1
}

# 步骤5: 验证解压
Write-Host ""
Write-Host "[步骤5] 验证安装..." -ForegroundColor Yellow
if (Test-Path "$MAVEN_HOME\bin\mvn.cmd") {
    Write-Host "[成功] Maven文件已找到" -ForegroundColor Green
} else {
    Write-Host "[错误] Maven文件未找到，请检查解压路径" -ForegroundColor Red
    Write-Host "期望路径: $MAVEN_HOME\bin\mvn.cmd" -ForegroundColor Yellow
    pause
    exit 1
}

# 步骤6: 配置环境变量
Write-Host ""
Write-Host "[步骤6] 配置环境变量..." -ForegroundColor Yellow
Write-Host ""

try {
    # 检查是否有管理员权限
    $isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
    
    if (-not $isAdmin) {
        Write-Host "[警告] 需要管理员权限来配置环境变量" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "请以【管理员身份】重新运行此脚本，或者手动配置环境变量：" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "手动配置步骤：" -ForegroundColor Cyan
        Write-Host "1. 右键'此电脑' -> 属性 -> 高级系统设置 -> 环境变量" -ForegroundColor White
        Write-Host "2. 新建系统变量: MAVEN_HOME = $MAVEN_HOME" -ForegroundColor White
        Write-Host "3. 编辑Path变量，添加: %MAVEN_HOME%\bin" -ForegroundColor White
        Write-Host ""
        
        # 尝试以提升的权限运行
        $answer = Read-Host "是否以管理员身份重新运行此脚本？(Y/N)"
        if ($answer -eq 'Y' -or $answer -eq 'y') {
            Start-Process powershell -Verb RunAs -ArgumentList "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`""
            exit 0
        } else {
            Write-Host ""
            Write-Host "稍后请手动配置环境变量，然后运行: mvn -version 验证" -ForegroundColor Yellow
            pause
            exit 0
        }
    }
    
    # 有管理员权限，直接配置
    Write-Host "正在配置MAVEN_HOME..." -ForegroundColor Yellow
    [Environment]::SetEnvironmentVariable("MAVEN_HOME", $MAVEN_HOME, "Machine")
    Write-Host "[成功] MAVEN_HOME已设置" -ForegroundColor Green
    
    Write-Host "正在更新PATH..." -ForegroundColor Yellow
    $oldPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
    if ($oldPath -notlike "*$MAVEN_HOME\bin*") {
        $newPath = "$oldPath;$MAVEN_HOME\bin"
        [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
        Write-Host "[成功] PATH已更新" -ForegroundColor Green
    } else {
        Write-Host "[信息] PATH中已包含Maven" -ForegroundColor Cyan
    }
    
    Write-Host ""
    Write-Host "[成功] 环境变量配置完成！" -ForegroundColor Green
    
} catch {
    Write-Host "[错误] 配置环境变量失败: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "请手动配置环境变量（见上方说明）" -ForegroundColor Yellow
    pause
    exit 1
}

# 步骤7: 验证安装
Write-Host ""
Write-Host "[步骤7] 验证Maven安装..." -ForegroundColor Yellow
Write-Host ""

# 刷新当前会话的环境变量
$env:MAVEN_HOME = $MAVEN_HOME
$env:Path = "$env:Path;$MAVEN_HOME\bin"

try {
    & "$MAVEN_HOME\bin\mvn.cmd" -version
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  Maven安装成功！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "现在可以启动后端了：" -ForegroundColor Cyan
    Write-Host "cd backend" -ForegroundColor White
    Write-Host "mvn spring-boot:run" -ForegroundColor White
    Write-Host ""
} catch {
    Write-Host "[警告] 验证失败，可能需要重启终端" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "请关闭所有终端窗口，重新打开后运行: mvn -version" -ForegroundColor Cyan
}

pause
