package com.sales.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sales.customer.entity.MarketingPlan;

import java.util.List;

public interface MarketingPlanService extends IService<MarketingPlan> {
    
    /**
     * 创建营销方案
     */
    MarketingPlan createPlan(MarketingPlan plan);
    
    /**
     * 查询客户的营销方案列表
     */
    List<MarketingPlan> getPlansByCustomerId(Long customerId);
    
    /**
     * 查询销售的营销方案列表
     */
    List<MarketingPlan> getPlansBySalesId(Long salesId);
    
    /**
     * 更新方案状态
     */
    void updatePlanStatus(Long planId, String status);
    
    /**
     * 完成方案
     */
    void completePlan(Long planId, String result);
}
