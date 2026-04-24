@echo off
chcp 65001 >nul
echo ========================================
echo   智销助手 - 数据库初始化
echo ========================================
echo.

set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.4\bin
set SQL_FILE=src\main\resources\schema.sql

echo 请按照以下步骤操作：
echo.
echo 1. 打开 MySQL 客户端工具（如 Navicat、MySQL Workbench）
echo 2. 连接到本地 MySQL 服务器
echo    - 主机: localhost
echo    - 端口: 3306
echo    - 用户: root
echo    - 密码: 您的MySQL密码
echo.
echo 3. 执行SQL文件: %SQL_FILE%
echo.
echo 4. 或者在命令行中执行：
echo    "%MYSQL_PATH%\mysql.exe" -u root -p ^< %SQL_FILE%
echo.
pause
