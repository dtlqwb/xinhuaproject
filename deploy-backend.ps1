# 编译并部署后端到服务器
# 使用方法: .\deploy-backend.ps1

$ErrorActionPreference = "Stop"

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  后端编译和部署脚本" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# 1. 检查是否在backend目录
if (-not (Test-Path "pom.xml")) {
    Write-Host "❌ 错误: 请在backend目录下执行此脚本" -ForegroundColor Red
    exit 1
}

# 2. 编译后端
Write-Host "`n[1/4] 编译后端代码..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 编译失败,请检查是否有JDK" -ForegroundColor Red
    exit 1
}

Write-Host "✅ 编译成功" -ForegroundColor Green

# 3. 检查jar文件
$jarFile = Get-ChildItem "target\*.jar" | Select-Object -First 1
if (-not $jarFile) {
    Write-Host "❌ 未找到jar文件" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Jar文件: $($jarFile.Name) ($([math]::Round($jarFile.Length/1MB, 2)) MB)" -ForegroundColor Green

# 4. 上传到服务器
Write-Host "`n[2/4] 上传jar包到服务器..." -ForegroundColor Yellow
$serverUser = "ubuntu"
$serverHost = "82.156.165.194"
$serverPath = "/home/ubuntu/xinhuaproject/backend/target/"

# 使用SCP上传
scp -o StrictHostKeyChecking=no $jarFile.FullName "${serverUser}@${serverHost}:${serverPath}"

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 上传失败" -ForegroundColor Red
    exit 1
}

Write-Host "✅ 上传成功" -ForegroundColor Green

# 5. 在服务器上重新构建Docker
Write-Host "`n[3/4] 在服务器上重新构建Docker..." -ForegroundColor Yellow
$sshCommand = @"
cd /home/ubuntu/xinhuaproject
docker-compose build --no-cache backend
docker-compose up -d backend
"@

ssh -o StrictHostKeyChecking=no ${serverUser}@${serverHost} $sshCommand

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker构建失败" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Docker构建成功" -ForegroundColor Green

# 6. 等待服务启动并检查
Write-Host "`n[4/4] 等待服务启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

$checkCommand = @"
docker logs sales-backend --tail 10 | grep "Started"
docker ps | grep backend
"@

ssh -o StrictHostKeyChecking=no ${serverUser}@${serverHost} $checkCommand

Write-Host "`n=========================================" -ForegroundColor Cyan
Write-Host "✅ 部署完成!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "请刷新浏览器测试功能" -ForegroundColor Yellow
