@echo off
chcp 65001 >nul
echo ========================================
echo   智销助手 - 一键部署到服务器
echo ========================================
echo.

echo [步骤1] 检查本地代码...
git status
if %errorlevel% neq 0 (
    echo [错误] 当前目录不是Git仓库
    pause
    exit /b 1
)

echo.
echo [步骤2] 提交并推送代码到GitHub...
git add .
git commit -m "Auto deploy %date% %time%"
git push

if %errorlevel% neq 0 (
    echo [警告] 代码推送可能失败，请检查网络连接
)

echo.
echo [步骤3] 连接服务器并部署...
echo.
echo 正在连接到服务器 82.156.165.194...
echo.
echo 请在弹出的窗口中输入密码: H6~4]+9wFt8pSd
echo.
echo 然后执行以下命令：
echo   cd /opt/xinhuaproject
echo   sudo git pull
echo   sudo docker compose up -d --build
echo.
echo 或者如果已设置自动部署脚本：
echo   sudo deploy-xinhua
echo.

pause

echo.
echo 正在启动SSH连接...
ssh ubuntu@82.156.165.194

echo.
echo ========================================
echo   部署说明
echo ========================================
echo.
echo 如果SSH连接成功，请在服务器上执行：
echo.
echo   cd /opt/xinhuaproject
echo   sudo git pull
echo   sudo docker compose up -d --build
echo.
echo 等待3-5分钟，部署完成后访问：
echo   销售端: http://82.156.165.194:80
echo   管理端: http://82.156.165.194:81
echo.

pause
