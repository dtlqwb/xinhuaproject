@echo off
chcp 65001 >nul
echo ========================================
echo   智销助手 - 全自动首次部署
echo ========================================
echo.
echo 即将连接到服务器并执行部署...
echo 请在提示时输入密码: H6~4]+9wFt8pSd
echo.
echo 部署内容包括：
echo   1. 克隆项目代码
echo   2. 配置环境变量
echo   3. 启动Docker服务
echo.
echo ========================================
pause

echo.
echo 正在连接服务器...
echo.

ssh ubuntu@82.156.165.194 "bash -s" < deploy-commands.sh

echo.
echo ========================================
echo   部署命令已发送！
echo ========================================
echo.
echo 如果看到密码提示，请输入: H6~4]+9wFt8pSd
echo.
echo 部署需要5-10分钟，请耐心等待...
echo.
pause
