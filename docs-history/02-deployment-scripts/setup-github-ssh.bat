@echo off
chcp 65001 >nul
echo ========================================
echo   GitHub Actions SSH密钥自动配置
echo ========================================
echo.
echo 这个脚本将：
echo   1. 连接到服务器
echo   2. 在服务器上生成SSH密钥
echo   3. 自动配置authorized_keys
echo   4. 显示私钥供您复制到GitHub Secrets
echo.
echo 请在提示时输入服务器密码: H6~4]+9wFt8pSd
echo.
echo ========================================
pause

echo.
echo 正在连接到服务器并执行配置...
echo.

ssh ubuntu@82.156.165.194 "bash -s" < setup-github-actions-ssh.sh

echo.
echo ========================================
echo 配置完成！
echo ========================================
echo.
echo 下一步：
echo 1. 复制上面显示的私钥内容（从-----BEGIN到-----END）
echo 2. 访问：https://github.com/dtlqwb/xinhuaproject/settings/secrets/actions
echo 3. 编辑 SSH_PRIVATE_KEY
echo 4. 粘贴私钥内容并保存
echo.
pause
