package com.sales.customer.controller;

import com.sales.customer.common.Result;
import com.sales.customer.entity.DailyReport;
import com.sales.customer.entity.Customer;
import com.sales.customer.service.DailyReportService;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@CrossOrigin
public class DailyReportController {
    
    private final DailyReportService dailyReportService;
    private final CustomerService customerService;
    private final AiService aiService;
    
    /**
     * 生成日报
     */
    @PostMapping("/generate")
    public Result<DailyReport> generateReport(@RequestParam Long salesId) {
        try {
            // 查询今日客户
            List<Customer> todayCustomers = customerService.getCustomersBySalesId(salesId);
            
            // 过滤今日的客户
            LocalDate today = LocalDate.now();
            List<Customer> filteredCustomers = todayCustomers.stream()
                .filter(c -> c.getCreateTime().toLocalDate().equals(today))
                .toList();
            
            // 调用AI生成日报内容
            String summary = aiService.generateDailyReport(filteredCustomers, salesId);
            
            // 保存日报
            DailyReport report = new DailyReport();
            report.setSalesId(salesId);
            report.setReportDate(today);
            report.setSummary(summary);
            report.setCustomerCount(filteredCustomers.size());
            report.setAiAnalysis(summary);
            
            dailyReportService.saveOrUpdateReport(report);
            
            return Result.success(report);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询日报列表
     */
    @GetMapping("/list")
    public Result<List<DailyReport>> getReports(@RequestParam Long salesId) {
        try {
            List<DailyReport> reports = dailyReportService.getReportsBySalesId(salesId);
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询日报详情
     */
    @GetMapping("/{id}")
    public Result<DailyReport> getReportDetail(@PathVariable Long id) {
        try {
            DailyReport report = dailyReportService.getById(id);
            return Result.success(report);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
