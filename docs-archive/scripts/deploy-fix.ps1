# 服务器部署修复脚本
Write-Host "请在服务器上执行以下命令:" -ForegroundColor Green
Write-Host ""
Write-Host "cd /home/ubuntu/xinhuaproject" -ForegroundColor Yellow
Write-Host ""
Write-Host "# 1. 停止服务" -ForegroundColor Cyan
Write-Host "docker-compose down" -ForegroundColor Yellow
Write-Host ""
Write-Host "# 2. 同步代码" -ForegroundColor Cyan
Write-Host "git fetch origin" -ForegroundColor Yellow
Write-Host "git reset --hard origin/master" -ForegroundColor Yellow
Write-Host ""
Write-Host "# 3. 检查接口" -ForegroundColor Cyan
Write-Host "grep -n 'generateDailyReport' backend/src/main/java/com/sales/customer/controller/DailyReportController.java" -ForegroundColor Yellow
Write-Host ""
Write-Host "# 4. 清理旧jar并重新编译" -ForegroundColor Cyan
Write-Host "rm -f backend/target/*.jar" -ForegroundColor Yellow
Write-Host "cd backend" -ForegroundColor Yellow
Write-Host "docker run --rm -v '$(pwd)':/app -w /app maven:3.8-openjdk-8 mvn clean package -DskipTests" -ForegroundColor Yellow
Write-Host "cd .." -ForegroundColor Yellow
Write-Host ""
Write-Host "# 5. 检查新jar" -ForegroundColor Cyan
Write-Host "ls -lh backend/target/*.jar" -ForegroundColor Yellow
Write-Host ""
Write-Host "# 6. 构建并启动" -ForegroundColor Cyan
Write-Host "docker-compose build --no-cache backend" -ForegroundColor Yellow
Write-Host "docker-compose up -d" -ForegroundColor Yellow
Write-Host ""
Write-Host "# 7. 等待并测试" -ForegroundColor Cyan
Write-Host "sleep 30" -ForegroundColor Yellow
Write-Host "docker logs sales-backend --tail 30" -ForegroundColor Yellow
Write-Host "curl -X POST 'http://localhost:8080/api/admin/reports/generate?date=2026-04-28' -s | python3 -m json.tool" -ForegroundColor Yellow
