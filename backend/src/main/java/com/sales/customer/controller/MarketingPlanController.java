package com.sales.customer.controller;

import com.sales.customer.common.Result;
import com.sales.customer.entity.MarketingPlan;
import com.sales.customer.entity.Customer;
import com.sales.customer.service.MarketingPlanService;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
@CrossOrigin
public class MarketingPlanController {
    
    private final MarketingPlanService marketingPlanService;
    private final CustomerService customerService;
    private final AiService aiService;
    
    /**
     * 生成营销方案
     */
    @PostMapping("/generate")
    public Result<MarketingPlan> generatePlan(@RequestParam Long customerId) {
        try {
            // 查询客户信息
            Customer customer = customerService.getById(customerId);
            if (customer == null) {
                return Result.error("客户不存在");
            }
            
            // 调用AI生成营销方案
            String planContent = aiService.generateMarketingPlan(customer);
            
            // 保存营销方案
            MarketingPlan plan = new MarketingPlan();
            plan.setCustomerId(customerId);
            plan.setPlanContent(planContent);
            plan.setPriority("medium");
            plan.setStatus("pending");
            
            marketingPlanService.save(plan);
            
            return Result.success(plan);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询营销方案列表
     */
    @GetMapping("/list")
    public Result<List<MarketingPlan>> getPlans() {
        try {
            List<MarketingPlan> plans = marketingPlanService.getAllPlans();
            return Result.success(plans);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询客户的营销方案
     */
    @GetMapping("/customer/{customerId}")
    public Result<MarketingPlan> getPlanByCustomer(@PathVariable Long customerId) {
        try {
            MarketingPlan plan = marketingPlanService.getPlanByCustomerId(customerId);
            return Result.success(plan);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新执行状态
     */
    @PutMapping("/{id}/execute")
    public Result<Boolean> updateExecuteStatus(@PathVariable Long id) {
        try {
            boolean success = marketingPlanService.markAsExecuted(id);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
