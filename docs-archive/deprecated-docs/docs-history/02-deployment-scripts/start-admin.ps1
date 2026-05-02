# 一键启动管理后台(后端 + 前端)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动管理后台服务" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$backendPath = "d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend"
$frontendPath = "d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\admin-frontend"

# 检查后端目录
if (Test-Path $backendPath) {
    Write-Host "[1/3] 启动后端服务..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendPath'; Write-Host '正在启动后端服务...' -ForegroundColor Green; mvn spring-boot:run"
    Write-Host "   ✓ 后端启动窗口已打开" -ForegroundColor Green
} else {
    Write-Host "   ✗ 后端目录不存在: $backendPath" -ForegroundColor Red
}

Write-Host ""
Write-Host "等待后端启动(5秒)..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# 检查前端目录
if (Test-Path $frontendPath) {
    Write-Host "[2/3] 启动管理前端..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendPath'; Write-Host '正在启动前端服务...' -ForegroundColor Green; npm run dev"
    Write-Host "   ✓ 前端启动窗口已打开" -ForegroundColor Green
} else {
    Write-Host "   ✗ 前端目录不存在: $frontendPath" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "[3/3] 启动完成!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "访问地址:" -ForegroundColor Yellow
Write-Host "  管理后台: http://localhost:3002" -ForegroundColor Cyan
Write-Host "  后端API:  http://localhost:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "提示:" -ForegroundColor Yellow
Write-Host "  - 请等待两个窗口都显示启动成功后再访问" -ForegroundColor White
Write-Host "  - 后端首次启动可能需要1-2分钟" -ForegroundColor White
Write-Host "  - 查看控制台输出确认启动状态" -ForegroundColor White
Write-Host ""
