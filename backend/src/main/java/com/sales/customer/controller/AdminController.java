package com.sales.customer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sales.customer.common.Result;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.entity.Customer;
import com.sales.customer.entity.DailyReport;
import com.sales.customer.entity.MarketingPlan;
import com.sales.customer.service.AdminService;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.DailyReportService;
import com.sales.customer.service.MarketingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {
    
    private final AdminService adminService;
    private final CustomerService customerService;
    private final DailyReportService dailyReportService;
    private final MarketingPlanService marketingPlanService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = adminService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有客户列表（管理员查看）
     */
    @GetMapping("/customers")
    public Result<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.list();
            return Result.success(customers);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取Dashboard统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总客户数
            long totalCustomers = customerService.count();
            stats.put("totalCustomers", totalCustomers);
            
            // 今日新增客户数
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            QueryWrapper<Customer> todayQuery = new QueryWrapper<>();
            todayQuery.between("create_time", todayStart, todayEnd);
            long todayCustomers = customerService.count(todayQuery);
            stats.put("todayCustomers", todayCustomers);
            
            // 日报总数
            long totalReports = dailyReportService.count();
            stats.put("totalReports", totalReports);
            
            // 待执行方案数
            QueryWrapper<MarketingPlan> pendingQuery = new QueryWrapper<>();
            pendingQuery.in("status", "pending", "executing");
            long pendingPlans = marketingPlanService.count(pendingQuery);
            stats.put("pendingPlans", pendingPlans);
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
