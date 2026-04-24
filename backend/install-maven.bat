@echo off
chcp 65001 >nul
echo ========================================
echo   智销助手 - Maven自动安装配置
echo ========================================
echo.

set MAVEN_VERSION=3.9.6
set DOWNLOAD_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set INSTALL_DIR=C:\Program Files\Apache
set MAVEN_HOME=%INSTALL_DIR%\apache-maven-%MAVEN_VERSION%

echo [步骤1] 检查是否已安装...
if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [成功] Maven已安装在: %MAVEN_HOME%
    echo.
    echo 正在验证...
    call "%MAVEN_HOME%\bin\mvn.cmd" -version
    echo.
    echo 如果看到版本信息，说明安装成功！
    pause
    exit /b 0
)

echo [步骤2] 创建安装目录...
if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"
echo [成功] 目录已创建: %INSTALL_DIR%
echo.

echo [步骤3] 请手动下载Maven
echo.
echo 下载地址:
echo %DOWNLOAD_URL%
echo.
echo 备用地址（如果上面链接失败）:
echo https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
echo.
echo 或者访问官网: https://maven.apache.org/download.cgi
echo.
echo 下载完成后，将zip文件解压到:
echo %INSTALL_DIR%
echo.
echo 解压后应该有这样的路径:
echo %MAVEN_HOME%\bin\mvn.cmd
echo.
pause

echo.
echo [步骤4] 验证安装...
if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [成功] Maven文件已找到
    echo.
    
    echo [步骤5] 配置环境变量（需要管理员权限）
    echo.
    echo 请以【管理员身份】运行PowerShell，然后执行以下命令：
    echo.
    echo powershell -Command "[Environment]::SetEnvironmentVariable('MAVEN_HOME', '%MAVEN_HOME%', 'Machine')"
    echo powershell -Command "$oldPath = [Environment]::GetEnvironmentVariable('Path', 'Machine'); $newPath = \"$oldPath;%%MAVEN_HOME%%\bin\"; [Environment]::SetEnvironmentVariable('Path', $newPath, 'Machine')"
    echo.
    echo 或者手动配置：
    echo 1. 右键"此电脑" -> 属性 -> 高级系统设置 -> 环境变量
    echo 2. 新建系统变量：MAVEN_HOME = %MAVEN_HOME%
    echo 3. 编辑Path变量，添加：%%MAVEN_HOME%%\bin
    echo.
    pause
    
    echo.
    echo [步骤6] 配置完成后，重新打开此脚本验证
    echo 或者直接运行: mvn -version
    echo.
) else (
    echo [错误] 未找到Maven文件，请确认已正确解压
    echo 期望路径: %MAVEN_HOME%\bin\mvn.cmd
    echo.
)

pause
