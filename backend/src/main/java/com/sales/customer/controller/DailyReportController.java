package com.sales.customer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sales.customer.common.Result;
import com.sales.customer.entity.Customer;
import com.sales.customer.entity.DailyReport;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@CrossOrigin
public class DailyReportController {
    
    private final DailyReportService dailyReportService;
    private final CustomerService customerService;
    private final com.sales.customer.service.AiService aiService;
    
    /**
     * 获取所有日报列表
     */
    @GetMapping
    public Result<List<DailyReport>> getAllReports() {
        try {
            List<DailyReport> reports = dailyReportService.list();
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * AI自动生成日报
     */
    @PostMapping("/generate")
    public Result<DailyReport> generateDailyReport(@RequestParam String date) {
        try {
            log.info("开始生成日报, 日期: {}", date);
            
            // 解析日期
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            java.time.LocalDateTime startOfDay = localDate.atStartOfDay();
            java.time.LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            
            // 查询当天所有客户信息(使用日期范围查询,避免时区问题)
            QueryWrapper<Customer> customerWrapper = new QueryWrapper<Customer>()
                .ge("create_time", startOfDay)
                .le("create_time", endOfDay)
                .eq("deleted", 0)
                .orderByDesc("create_time");
            List<Customer> customers = customerService.list(customerWrapper);
            
            log.info("查询到 {} 条客户记录, 日期范围: {} 至 {}", 
                customers.size(), startOfDay, endOfDay);
            
            if (customers.isEmpty()) {
                return Result.error(404, "当天没有录入客户信息");
            }
            
            // 统计基本信息
            int totalCount = customers.size();
            long pendingCount = customers.stream().filter(c -> "pending".equals(c.getStatus())).count();
            long followedCount = customers.stream().filter(c -> "followed".equals(c.getStatus())).count();
            long convertedCount = customers.stream().filter(c -> "converted".equals(c.getStatus())).count();
            
            // 构建AI提示词
            StringBuilder prompt = new StringBuilder();
            prompt.append("你是一个专业的销售数据分析助手。请根据以下客户信息生成一份销售日报。\n\n");
            prompt.append("今日数据：\n");
            prompt.append("- 总录入客户数：").append(totalCount).append("\n");
            prompt.append("- 待跟进：").append(pendingCount).append("\n");
            prompt.append("- 已跟进：").append(followedCount).append("\n");
            prompt.append("- 已转化：").append(convertedCount).append("\n\n");
            prompt.append("客户详细信息：\n");
            
            for (Customer customer : customers) {
                prompt.append("1. ").append(customer.getName());
                if (customer.getCompany() != null) prompt.append(" - ").append(customer.getCompany());
                if (customer.getPosition() != null) prompt.append(" (").append(customer.getPosition()).append(")");
                if (customer.getRequirement() != null) prompt.append(": ").append(customer.getRequirement());
                prompt.append("\n");
            }
            
            prompt.append("\n请生成以下内容的日报：\n");
            prompt.append("1. 【今日概览】- 简要总结今日数据\n");
            prompt.append("2. 【工作内容】- 总结今日跟进的客户和需求\n");
            prompt.append("3. 【存在问题】- 分析可能存在的问题和挑战\n");
            prompt.append("4. 【明日计划】- 给出明日工作建议\n");
            prompt.append("\n请用简洁专业的语言,分段输出。");
                    
            // 调用AI生成日报内容
            String aiResponse = aiService.generateDailyReport(customers, 1L);
            log.info("===== AI响应原始内容开始 =====");
            log.info(aiResponse);
            log.info("===== AI响应原始内容结束 =====");
            
            // 解析AI响应,提取各个部分
            String workContent = extractSection(aiResponse, "工作内容");
            String problems = extractSection(aiResponse, "存在问题");
            String tomorrowPlan = extractSection(aiResponse, "明日计划");
            
            // 创建日报
            DailyReport report = new DailyReport();
            report.setSalesId(1L);
            report.setSalesName("系统生成");
            report.setReportDate(java.time.LocalDate.parse(date));
            report.setTodayCustomers(totalCount);
            report.setFollowUpCustomers((int)followedCount);
            report.setSignedCustomers((int)convertedCount);
            report.setWorkContent(workContent);
            report.setProblems(problems);
            report.setTomorrowPlan(tomorrowPlan);
            report.setStatus("draft");
            
            dailyReportService.save(report);
            
            log.info("日报生成成功, ID: {}", report.getId());
            return Result.success(report);
            
        } catch (Exception e) {
            log.error("生成日报失败", e);
            return Result.error(500, "生成日报失败: " + e.getMessage());
        }
    }
    
    /**
     * 从AI响应中提取指定部分
     */
    private String extractSection(String response, String sectionName) {
        // 尝试多种格式: 【工作内容】、工作内容、1. 工作内容
        String[] patterns = {
            "【" + sectionName + "】",
            "【" + sectionName + "】:",
            sectionName + ":",
            sectionName + "：",
            "1. " + sectionName,
            "2. " + sectionName,
            "3. " + sectionName,
            "4. " + sectionName
        };
        
        int startIndex = -1;
        for (String pattern : patterns) {
            startIndex = response.indexOf(pattern);
            if (startIndex != -1) {
                startIndex += pattern.length();
                break;
            }
        }
        
        if (startIndex == -1) {
            log.warn("未找到章节: {}", sectionName);
            return "";
        }
        
        // 查找下一个章节的开始位置
        int endIndex = response.length();
        String[] nextMarkers = {"【今日概览】", "【工作内容】", "【存在问题】", "【明日计划】", "\n\n", "\n##", "\n#"};
        
        for (String marker : nextMarkers) {
            int pos = response.indexOf(marker, startIndex);
            if (pos != -1 && pos > startIndex && pos < endIndex) {
                // 检查是否真的是下一个章节(跳过当前章节内部的可能匹配)
                if (!response.substring(startIndex, pos).contains(marker)) {
                    endIndex = pos;
                }
            }
        }
        
        String content = response.substring(startIndex, endIndex).trim();
        // 清理开头的标点符号
        if (content.startsWith(":") || content.startsWith("：")) {
            content = content.substring(1).trim();
        }
        
        return content;
    }
    
    /**
     * 根据销售ID查询日报列表
     */
    @GetMapping("/sales/{salesId}")
    public Result<List<DailyReport>> getReportsBySalesId(@PathVariable Long salesId) {
        try {
            List<DailyReport> reports = dailyReportService.getReportsBySalesId(salesId);
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取指定日期的日报
     */
    @GetMapping("/date")
    public Result<DailyReport> getReportByDate(@RequestParam Long salesId, 
                                                @RequestParam String reportDate) {
        try {
            DailyReport report = dailyReportService.getReportByDate(salesId, reportDate);
            return Result.success(report);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建或更新日报
     */
    @PostMapping
    public Result<DailyReport> saveOrUpdateReport(@RequestBody DailyReport report) {
        try {
            // 自动统计今日客户数据
            if (report.getTodayCustomers() == null) {
                LocalDate date = report.getReportDate();
                QueryWrapper<com.sales.customer.entity.Customer> query = new QueryWrapper<>();
                query.eq("sales_id", report.getSalesId())
                     .between("create_time", date.atStartOfDay(), date.plusDays(1).atStartOfDay());
                long count = customerService.count(query);
                report.setTodayCustomers((int) count);
            }
            
            DailyReport saved = dailyReportService.saveOrUpdateReport(report);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 提交日报
     */
    @PostMapping("/{id}/submit")
    public Result<Void> submitReport(@PathVariable Long id) {
        try {
            dailyReportService.submitReport(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核日报
     */
    @PostMapping("/{id}/review")
    public Result<Void> reviewReport(@PathVariable Long id,
                                     @RequestParam Long reviewerId,
                                     @RequestParam String reviewerName,
                                     @RequestParam(required = false) String comment) {
        try {
            dailyReportService.reviewReport(id, reviewerId, reviewerName, comment);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除日报
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReport(@PathVariable Long id) {
        try {
            dailyReportService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
