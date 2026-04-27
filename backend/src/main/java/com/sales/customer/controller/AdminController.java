package com.sales.customer.controller;

import com.sales.customer.common.Result;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.entity.Customer;
import com.sales.customer.service.AdminService;
import com.sales.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {
    
    private final AdminService adminService;
    private final CustomerService customerService;
    
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
}
