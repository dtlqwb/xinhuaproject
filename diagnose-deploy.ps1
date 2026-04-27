# 诊断部署问题脚本
# 服务器: 82.156.165.194

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  部署问题诊断" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$serverIP = "82.156.165.194"

Write-Host "请在服务器上SSH执行以下命令:" -ForegroundColor Yellow
Write-Host ""

$commands = @(
    "# 1. 检查容器状态",
    "cd xinhuaproject && docker-compose ps",
    "",
    "# 2. 检查端口监听",
    "sudo netstat -tlnp | grep -E '80|81|8080'",
    "",
    "# 3. 检查前端容器日志",
    "docker-compose logs sales-frontend --tail=100",
    "",
    "# 4. 检查后端容器日志",
    "docker-compose logs backend --tail=50",
    "",
    "# 5. 测试本地访问",
    "curl -I http://localhost:80",
    "curl -I http://localhost:81",
    "curl http://localhost:8080/api/customer/list?salesId=1",
    "",
    "# 6. 检查防火墙",
    "sudo ufw status",
    "",
    "# 7. 检查Nginx配置(如果使用了Nginx)",
    "docker-compose exec sales-frontend cat /etc/nginx/conf.d/default.conf 2>/dev/null || echo 'No nginx'",
    "",
    "# 8. 重启服务(如果需要)",
    "docker-compose restart"
)

foreach ($cmd in $commands) {
    if ($cmd.StartsWith("#")) {
        Write-Host $cmd -ForegroundColor Green
    } elseif ($cmd -eq "") {
        Write-Host ""
    } else {
        Write-Host $cmd -ForegroundColor White
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  本地测试命令" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "# 测试HTTP连接(不使用SSL)" -ForegroundColor Green
Write-Host "curl -I http://${serverIP}:80" -ForegroundColor White
Write-Host ""

Write-Host "# 测试后端API" -ForegroundColor Green
Write-Host "curl http://${serverIP}:8080/api/customer/list?salesId=1" -ForegroundColor White
Write-Host ""

Write-Host "# 使用PowerShell测试" -ForegroundColor Green
Write-Host "Invoke-WebRequest -Uri 'http://${serverIP}:80' -UseBasicParsing" -ForegroundColor White
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  常见问题排查" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "问题1: ERR_SSL_PROTOCOL_ERROR" -ForegroundColor Yellow
Write-Host "原因: 浏览器尝试HTTPS但服务器只有HTTP" -ForegroundColor Gray
Write-Host "解决: 确保访问 http:// 而不是 https://" -ForegroundColor Gray
Write-Host "     或在浏览器地址栏手动输入 http://82.156.165.194" -ForegroundColor Gray
Write-Host ""

Write-Host "问题2: 连接超时" -ForegroundColor Yellow
Write-Host "原因: 防火墙或安全组未开放端口" -ForegroundColor Gray
Write-Host "解决: 在腾讯云控制台开放80,81,8080端口" -ForegroundColor Gray
Write-Host "     或在服务器执行: sudo ufw allow 80/tcp" -ForegroundColor Gray
Write-Host ""

Write-Host "问题3: 容器未运行" -ForegroundColor Yellow
Write-Host "原因: Docker容器启动失败" -ForegroundColor Gray
Write-Host "解决: 查看日志 docker-compose logs" -ForegroundColor Gray
Write-Host "     重启服务 docker-compose restart" -ForegroundColor Gray
Write-Host ""
