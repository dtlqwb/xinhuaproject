package com.sales.customer.controller;

import com.sales.customer.common.Result;
import com.sales.customer.entity.Customer;
import com.sales.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin
public class CustomerController {
    
    private final CustomerService customerService;
    
    /**
     * 添加客户（支持语音/文字输入和附件上传）
     */
    @PostMapping("/add")
    public Result<Customer> addCustomer(
            @RequestParam String content,
            @RequestParam Long salesId,
            @RequestParam(required = false) List<MultipartFile> attachments) {
        try {
            Customer customer = customerService.addCustomer(content, salesId, attachments);
            return Result.success(customer);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询销售人员的客户列表
     */
    @GetMapping("/list")
    public Result<List<Customer>> getCustomers(@RequestParam Long salesId) {
        try {
            List<Customer> customers = customerService.getCustomersBySalesId(salesId);
            return Result.success(customers);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询客户详情
     */
    @GetMapping("/{id}")
    public Result<Customer> getCustomerDetail(@PathVariable Long id) {
        try {
            Customer customer = customerService.getById(id);
            return Result.success(customer);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新客户信息
     */
    @PutMapping("/update")
    public Result<Boolean> updateCustomer(@RequestBody Customer customer) {
        try {
            boolean success = customerService.updateById(customer);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCustomer(@PathVariable Long id) {
        try {
            boolean success = customerService.removeById(id);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
