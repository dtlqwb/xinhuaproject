Write-Host "========================================" -ForegroundColor Cyan
Write-Host "管理后台网络连接诊断工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 检查后端服务
Write-Host "1. 检查后端服务 (端口 8080)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080" -TimeoutSec 3 -ErrorAction Stop
    Write-Host "   [OK] 后端服务正在运行" -ForegroundColor Green
} catch {
    Write-Host "   [FAIL] 后端服务未启动或无法访问" -ForegroundColor Red
    Write-Host "   请启动后端服务:" -ForegroundColor Yellow
    Write-Host "   - Docker方式: docker compose up -d backend" -ForegroundColor White
    Write-Host "   - 本地方式: cd backend; mvn spring-boot:run" -ForegroundColor White
}
Write-Host ""

# 2. 检查前端服务
Write-Host "2. 检查管理前端服务 (端口 3002)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3002" -TimeoutSec 3 -ErrorAction Stop
    Write-Host "   [OK] 管理前端正在运行" -ForegroundColor Green
} catch {
    Write-Host "   [FAIL] 管理前端未启动" -ForegroundColor Red
    Write-Host "   请启动前端: cd admin-frontend; npm run dev" -ForegroundColor Yellow
}
Write-Host ""

# 3. 检查Docker容器
Write-Host "3. 检查Docker容器状态..." -ForegroundColor Yellow
docker ps --filter "name=sales" --format "table {{.Names}}\t{{.Status}}" 2>&1
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "诊断完成! 请查看上述结果" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
