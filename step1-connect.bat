@echo off
chcp 65001 >nul
echo ========================================
echo   步骤1: 连接到服务器
echo ========================================
echo.
echo 请输入服务器密码: H6~4]+9wFt8pSd
echo.
echo 连接成功后，在服务器上执行以下命令：
echo.
echo   ssh-keygen -t rsa -b 4096 -C "github-actions" -f ~/.ssh/github_actions -N ""
echo   cat ~/.ssh/github_actions.pub
echo.
echo 复制显示的公钥内容
echo.
echo ========================================
pause

ssh ubuntu@82.156.165.194

echo.
pause
