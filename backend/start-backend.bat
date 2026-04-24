@echo off
chcp 65001 >nul
echo ========================================
echo   智销助手 - 后端启动脚本
echo ========================================
echo.

cd /d "%~dp0"

echo 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java，请先安装JDK 8或更高版本
    pause
    exit /b 1
)
echo [成功] Java环境正常
echo.

echo 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [警告] 未检测到Maven命令
    echo.
    echo 请选择以下方案之一：
    echo 1. 使用 IntelliJ IDEA 打开此项目并运行
    echo 2. 安装Maven后重新运行此脚本
    echo 3. 下载Maven Wrapper (mvnw.cmd)
    echo.
    pause
    exit /b 1
)
echo [成功] Maven环境正常
echo.

echo ========================================
echo 正在启动后端服务...
echo ========================================
echo.
echo 提示：首次启动需要下载依赖，请耐心等待...
echo.

mvn spring-boot:run

pause
