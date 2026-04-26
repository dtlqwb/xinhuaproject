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
    
    /**
     * 批量生成营销方案
     * @param customerIds 客户ID列表
     * @return 生成的营销方案列表
     */
    List<MarketingPlan> batchGeneratePlans(List<Long> customerIds);
}
