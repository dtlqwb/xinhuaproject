package com.sales.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sales.customer.entity.MarketingPlan;
import java.util.List;

public interface MarketingPlanService extends IService<MarketingPlan> {
    
    /**
     * 查询所有营销方案（关联客户信息）
     */
    List<MarketingPlan> getAllPlans();
    
    /**
     * 查询客户的营销方案
     */
    MarketingPlan getPlanByCustomerId(Long customerId);
    
    /**
     * 标记为已执行
     */
    boolean markAsExecuted(Long id);
}
