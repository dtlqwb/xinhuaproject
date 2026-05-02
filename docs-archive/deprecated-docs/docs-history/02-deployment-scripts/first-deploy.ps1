# 智销助手 - 首次部署脚本（PowerShell）
# 使用Plink或手动SSH连接

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  智销助手 - 首次部署到服务器" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$server = "82.156.165.194"
$user = "ubuntu"
$password = "H6~4]+9wFt8pSd"

Write-Host "由于Windows SSH需要交互式密码输入，请按以下步骤操作：" -ForegroundColor Yellow
Write-Host ""

Write-Host "步骤1: 连接到服务器" -ForegroundColor Green
Write-Host "  ssh ${user}@${server}" -ForegroundColor White
Write-Host "  密码: ${password}" -ForegroundColor White
Write-Host ""

Write-Host "步骤2: 在服务器上执行以下命令（可以一次性复制粘贴）：" -ForegroundColor Green
Write-Host ""

$commands = @"
cd /opt && sudo git clone https://github.com/dtlqwb/xinhuaproject.git
cd xinhuaproject
sudo cp .env.example .env
"@

Write-Host $commands -ForegroundColor Cyan
Write-Host ""

Write-Host "步骤3: 编辑.env文件，修改密码：" -ForegroundColor Green
Write-Host "  sudo vi .env" -ForegroundColor White
Write-Host ""
Write-Host "  修改以下内容：" -ForegroundColor Yellow
Write-Host "  DB_PASSWORD=your_secure_password" -ForegroundColor White
Write-Host "  JWT_SECRET=your_jwt_secret_min_32_chars" -ForegroundColor White
Write-Host ""
Write-Host "  按Esc，输入:wq保存" -ForegroundColor Yellow
Write-Host ""

Write-Host "步骤4: 启动服务：" -ForegroundColor Green
Write-Host "  sudo docker compose up -d" -ForegroundColor White
Write-Host ""

Write-Host "等待5-10分钟完成部署..." -ForegroundColor Cyan
Write-Host ""

Write-Host "步骤5: 验证部署：" -ForegroundColor Green
Write-Host "  sudo docker compose ps" -ForegroundColor White
Write-Host ""

Write-Host "访问网站：" -ForegroundColor Cyan
Write-Host "  销售端: http://${server}:80" -ForegroundColor White
Write-Host "  管理端: http://${server}:81" -ForegroundColor White
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  现在按任意键打开SSH连接..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

Write-Host ""
Write-Host "正在连接到服务器..." -ForegroundColor Green
ssh "${user}@${server}"
