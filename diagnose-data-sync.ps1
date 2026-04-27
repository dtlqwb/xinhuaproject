# 数据同步问题诊断脚本
# 用于检查销售端录入的数据是否能被管理端看到

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "数据同步问题诊断" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$server = "ubuntu@82.156.165.194"

Write-Host "正在连接到服务器..." -ForegroundColor Yellow
ssh $server @"
echo '========================================='
echo '步骤1: 检查Docker容器状态'
echo '========================================='
docker-compose ps

echo ''
echo '========================================='
echo '步骤2: 检查数据库中的客户数据'
echo '========================================='
docker exec -it sales-mysql mysql -uroot -proot123 sales_customer_db -e "
SELECT 
    id,
    name,
    phone,
    company,
    sales_id,
    content,
    status,
    create_time
FROM customer 
ORDER BY create_time DESC 
LIMIT 10;
"

echo ''
echo '========================================='
echo '步骤3: 统计客户总数'
echo '========================================='
docker exec -it sales-mysql mysql -uroot -proot123 sales_customer_db -e "
SELECT COUNT(*) as total_customers FROM customer;
"

echo ''
echo '========================================='
echo '步骤4: 检查后端日志(最近20条)'
echo '========================================='
docker logs sales-backend --tail 20

echo ''
echo '========================================='
echo '步骤5: 测试后端API接口'
echo '========================================='
echo '测试管理员获取客户列表API...'
curl -s http://localhost:8080/api/admin/customers | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/api/admin/customers

echo ''
echo '========================================='
echo '诊断完成!'
echo '========================================='
echo ''
echo '请检查以上输出:'
echo '1. 数据库中是否有客户数据?'
echo '2. 后端API是否返回了客户列表?'
echo '3. 后端日志是否有错误信息?'
echo ''
echo '如果数据库有数据但管理端看不到,可能是:'
echo '- 管理前端API地址配置错误'
echo '- 浏览器缓存问题(请强制刷新 Ctrl+F5)'
echo '- JavaScript错误(请检查浏览器控制台F12)'
echo '========================================='
"@

Write-Host ""
Write-Host "诊断脚本执行完成!" -ForegroundColor Green
