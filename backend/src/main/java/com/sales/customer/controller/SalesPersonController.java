package com.sales.customer.controller;

import com.sales.customer.common.Result;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.entity.Customer;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.SalesPersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin
public class SalesPersonController {
    
    private final SalesPersonService salesPersonService;
    private final CustomerService customerService;
    
    /**
     * 销售人员登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = salesPersonService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取今日客户数量
     */
    @GetMapping("/today-count")
    public Result<Map<String, Object>> getTodayCount(@RequestParam Long salesId) {
        try {
            Integer count = customerService.getTodayCustomerCount(salesId);
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
