package com.sales.customer.controller;

import com.sales.customer.common.Result;
import com.sales.customer.entity.MarketingPlan;
import com.sales.customer.service.MarketingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/plans")
@RequiredArgsConstructor
@CrossOrigin
public class MarketingPlanController {
    
    private final MarketingPlanService marketingPlanService;
    
    /**
     * 获取所有营销方案列表
     */
    @GetMapping
    public Result<List<MarketingPlan>> getAllPlans() {
        try {
            List<MarketingPlan> plans = marketingPlanService.list();
            return Result.success(plans);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据客户ID查询营销方案列表
     */
    @GetMapping("/customer/{customerId}")
    public Result<List<MarketingPlan>> getPlansByCustomerId(@PathVariable Long customerId) {
        try {
            List<MarketingPlan> plans = marketingPlanService.getPlansByCustomerId(customerId);
            return Result.success(plans);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据销售ID查询营销方案列表
     */
    @GetMapping("/sales/{salesId}")
    public Result<List<MarketingPlan>> getPlansBySalesId(@PathVariable Long salesId) {
        try {
            List<MarketingPlan> plans = marketingPlanService.getPlansBySalesId(salesId);
            return Result.success(plans);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建营销方案
     */
    @PostMapping
    public Result<MarketingPlan> createPlan(@RequestBody MarketingPlan plan) {
        try {
            MarketingPlan created = marketingPlanService.createPlan(plan);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新营销方案
     */
    @PutMapping("/{id}")
    public Result<MarketingPlan> updatePlan(@PathVariable Long id, 
                                            @RequestBody MarketingPlan plan) {
        try {
            plan.setId(id);
            marketingPlanService.updateById(plan);
            return Result.success(plan);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新方案状态
     */
    @PostMapping("/{id}/status")
    public Result<Void> updatePlanStatus(@PathVariable Long id,
                                         @RequestParam String status) {
        try {
            marketingPlanService.updatePlanStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 完成方案
     */
    @PostMapping("/{id}/complete")
    public Result<Void> completePlan(@PathVariable Long id,
                                     @RequestParam(required = false) String result) {
        try {
            marketingPlanService.completePlan(id, result);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除营销方案
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePlan(@PathVariable Long id) {
        try {
            marketingPlanService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
