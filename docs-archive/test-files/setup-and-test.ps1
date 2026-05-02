# ========================================
# 配置JDK环境并运行测试
# ========================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  智销助手 - 测试环境配置脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查是否已安装JDK
Write-Host "[1/5] 检查Java环境..." -ForegroundColor Yellow
$javaHome = $env:JAVA_HOME
if ($javaHome) {
    Write-Host "  ✓ JAVA_HOME已设置: $javaHome" -ForegroundColor Green
} else {
    Write-Host "  ✗ JAVA_HOME未设置" -ForegroundColor Red
    Write-Host ""
    Write-Host "请按照以下步骤安装JDK:" -ForegroundColor Yellow
    Write-Host "  1. 下载JDK 8: https://adoptium.net/" -ForegroundColor White
    Write-Host "  2. 安装到默认路径" -ForegroundColor White
    Write-Host "  3. 重新运行此脚本" -ForegroundColor White
    Write-Host ""
    exit 1
}

# 检查javac
Write-Host "[2/5] 检查JDK编译器..." -ForegroundColor Yellow
try {
    $javacVersion = javac -version 2>&1
    Write-Host "  ✓ Javac可用: $javacVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Javac不可用,可能只安装了JRE" -ForegroundColor Red
    Write-Host ""
    Write-Host "解决方案:" -ForegroundColor Yellow
    Write-Host "  1. 确保安装的是JDK而不是JRE" -ForegroundColor White
    Write-Host "  2. JDK通常安装在: C:\Program Files\Java\jdk1.8.0_xxx" -ForegroundColor White
    Write-Host "  3. 设置JAVA_HOME指向JDK目录" -ForegroundColor White
    exit 1
}

# 检查Maven
Write-Host "[3/5] 检查Maven环境..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "  ✓ Maven可用: $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Maven未安装或未配置PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "请下载Maven: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# 进入backend目录
Write-Host "[4/5] 进入后端项目目录..." -ForegroundColor Yellow
Set-Location -Path "$PSScriptRoot\backend"
Write-Host "  ✓ 当前目录: $(Get-Location)" -ForegroundColor Green

# 运行测试
Write-Host "[5/5] 运行单元测试..." -ForegroundColor Yellow
Write-Host ""
mvn clean test

# 显示测试结果
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试执行完成!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "查看测试报告:" -ForegroundColor Yellow
Write-Host "  HTML报告: backend\target\site\jacoco\index.html" -ForegroundColor White
Write-Host ""
Write-Host "下一步:" -ForegroundColor Yellow
Write-Host "  1. 查看上面的测试结果统计" -ForegroundColor White
Write-Host "  2. 如有失败的测试,检查错误信息" -ForegroundColor White
Write-Host "  3. 导入Postman集合进行API测试" -ForegroundColor White
Write-Host ""

# 返回原目录
Set-Location -Path $PSScriptRoot
