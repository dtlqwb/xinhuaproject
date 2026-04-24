@echo off
chcp 65001 >nul
echo ========================================
echo   智销助手 - Maven自动安装脚本
echo ========================================
echo.

set MAVEN_VERSION=3.9.6
set MAVEN_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set INSTALL_DIR=C:\Program Files\Apache
set MAVEN_HOME=%INSTALL_DIR%\maven

echo 步骤1: 创建安装目录...
if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"
echo [成功] 目录已创建
echo.

echo 步骤2: 请手动下载Maven
echo.
echo 下载地址: %MAVEN_URL%
echo.
echo 或者访问: https://maven.apache.org/download.cgi
echo.
echo 下载后解压到: %MAVEN_HOME%
echo.
pause

echo.
echo 步骤3: 解压完成后，按任意键继续配置环境变量...
pause

echo.
echo 步骤4: 配置环境变量（需要管理员权限）
echo.
echo 请以管理员身份运行PowerShell，然后执行以下命令：
echo.
echo [Environment]::SetEnvironmentVariable("MAVEN_HOME", "%MAVEN_HOME%", "Machine")
echo $oldPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
echo $newPath = "$oldPath;%%MAVEN_HOME%%\bin"
echo [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
echo.
pause

echo.
echo 步骤5: 配置完成后，重新打开此脚本验证安装
echo.
pause
