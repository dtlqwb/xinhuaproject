# 智销助手 - 快速测试脚本
# 服务器: 82.156.165.194
# 使用方法: 在PowerShell中执行 .\test-deployment.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  智销助手部署测试" -ForegroundColor Cyan
Write-Host "  服务器: 82.156.165.194" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$serverIP = "82.156.165.194"
$backendPort = "8080"
$baseUrl = "http://${serverIP}:${backendPort}"

# 测试计数器
$passCount = 0
$failCount = 0
$totalTests = 0

function Test-Api {
    param(
        [string]$Name,
        [string]$Url,
        [string]$Method = "GET",
        [string]$ContentType = $null,
        [string]$Body = $null,
        [hashtable]$Headers = @{}
    )
    
    $totalTests++
    Write-Host "[$totalTests] 测试: $Name" -ForegroundColor Yellow
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $Headers
            TimeoutSec = 10
            UseBasicParsing = $true
        }
        
        if ($ContentType) {
            $params["ContentType"] = $ContentType
        }
        
        if ($Body) {
            $params["Body"] = $Body
        }
        
        $response = Invoke-WebRequest @params
        $statusCode = $response.StatusCode
        $content = $response.Content
        
        if ($statusCode -eq 200) {
            Write-Host "  ✅ 通过 (HTTP $statusCode)" -ForegroundColor Green
            $passCount++
            
            # 显示响应摘要
            if ($content.Length -lt 200) {
                Write-Host "  响应: $content" -ForegroundColor Gray
            } else {
                Write-Host "  响应: $($content.Substring(0, 200))..." -ForegroundColor Gray
            }
        } else {
            Write-Host "  ⚠️  警告 (HTTP $statusCode)" -ForegroundColor Yellow
            Write-Host "  响应: $content" -ForegroundColor Gray
        }
    } catch {
        Write-Host "  ❌ 失败: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
    
    Write-Host ""
}

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "  开始测试..." -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host ""

# 测试1: 后端健康检查
Test-Api `
    -Name "后端服务健康检查" `
    -Url "$baseUrl/auth/login" `
    -Method "POST" `
    -ContentType "application/json" `
    -Body '{"username":"test","password":"test123"}'

# 测试2: 客户列表(不需要认证)
Test-Api `
    -Name "获取客户列表" `
    -Url "$baseUrl/customer/list?salesId=1"

# 测试3: 测试FormData上传(使用PowerShell的multipart)
$totalTests++
Write-Host "[$totalTests] 测试: 添加客户(FormData上传)" -ForegroundColor Yellow

try {
    # 使用Invoke-RestMethod测试FormData
    $boundary = [System.Guid]::NewGuid().ToString()
    $LF = "`r`n"
    
    $bodyLines = (
        "--$boundary",
        "Content-Disposition: form-data; name=`"content`"$LF",
        "赵六,测试公司,CEO,13600136000,需要SaaS服务",
        "--$boundary",
        "Content-Disposition: form-data; name=`"salesId`"$LF",
        "1",
        "--$boundary--$LF"
    ) -join $LF
    
    $response = Invoke-RestMethod `
        -Uri "$baseUrl/customer/add" `
        -Method Post `
        -ContentType "multipart/form-data; boundary=`"$boundary`"" `
        -Body $bodyLines `
        -TimeoutSec 10 `
        -UseBasicParsing
    
    Write-Host "  ✅ 通过 - FormData上传成功" -ForegroundColor Green
    $passCount++
    Write-Host "  响应: $($response | ConvertTo-Json -Depth 1)" -ForegroundColor Gray
} catch {
    Write-Host "  ❌ 失败: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Write-Host ""

# 测试4: AI日报生成
Test-Api `
    -Name "AI日报生成" `
    -Url "$baseUrl/daily-report/generate" `
    -Method "POST" `
    -ContentType "application/json" `
    -Body '{"salesId":1,"date":"2026-04-26"}'

# 测试5: 营销方案生成
Test-Api `
    -Name "AI营销方案生成" `
    -Url "$baseUrl/plan/generate?customerId=1" `
    -Method "POST"

# 测试6: 批量生成营销方案
$totalTests++
Write-Host "[$totalTests] 测试: 批量生成营销方案" -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod `
        -Uri "$baseUrl/plan/batch-generate" `
        -Method Post `
        -ContentType "application/json" `
        -Body '[1,2,3]' `
        -TimeoutSec 30 `
        -UseBasicParsing
    
    Write-Host "  ✅ 通过 - 批量生成成功" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "  ❌ 失败: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}

Write-Host ""

# 测试结果汇总
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "  测试结果汇总" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host ""
Write-Host "总测试数: $totalTests" -ForegroundColor White
Write-Host "✅ 通过: $passCount" -ForegroundColor Green
Write-Host "❌ 失败: $failCount" -ForegroundColor $(if ($failCount -gt 0) { "Red" } else { "Green" })
Write-Host ""

if ($failCount -eq 0) {
    Write-Host "🎉 所有测试通过!" -ForegroundColor Green
} else {
    Write-Host "⚠️  部分测试失败,请检查日志" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 提供下一步建议
Write-Host "📝 下一步:" -ForegroundColor Cyan
Write-Host "1. 浏览器访问销售前端: http://${serverIP}" -ForegroundColor White
Write-Host "2. 浏览器访问管理后台: http://${serverIP}:81" -ForegroundColor White
Write-Host "3. 查看后端日志: ssh ubuntu@${serverIP} 'cd xinhuaproject && docker-compose logs -f backend'" -ForegroundColor White
Write-Host ""
