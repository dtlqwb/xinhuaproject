# 管理端API地址修复脚本
# 使用方法: 在服务器上执行此脚本

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "管理端API地址修复" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# SSH连接到服务器
$server = "ubuntu@82.156.165.194"

Write-Host "步骤1: 连接到服务器..." -ForegroundColor Yellow
ssh $server @"
cd ~/xinhuaproject

echo '步骤2: 拉取最新代码...'
git pull origin master

echo ''
echo '步骤3: 重新构建管理前端容器...'
docker-compose up -d --build admin-frontend

echo ''
echo '步骤4: 重启后端服务...'
docker-compose restart backend

echo ''
echo '步骤5: 等待服务启动...'
sleep 15

echo ''
echo '步骤6: 检查容器状态...'
docker-compose ps

echo ''
echo '========================================'
echo '部署完成!'
echo '========================================'
echo ''
echo '请访问以下地址测试:'
echo '销售端: http://82.156.165.194'
echo '管理端: http://82.156.165.194:81'
echo ''
echo '测试清单:'
echo '1. 管理端登录 (admin/123456)'
echo '2. 进入客户管理页面'
echo '3. 确认能看到销售录入的客户数据'
echo '========================================'
"@

Write-Host ""
Write-Host "脚本执行完成!" -ForegroundColor Green
