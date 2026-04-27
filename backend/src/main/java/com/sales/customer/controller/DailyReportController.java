package com.sales.customer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sales.customer.common.Result;
import com.sales.customer.entity.DailyReport;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@CrossOrigin
public class DailyReportController {
    
    private final DailyReportService dailyReportService;
    private final CustomerService customerService;
    
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
