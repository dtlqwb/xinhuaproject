package com.sales.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sales.customer.entity.MarketingPlan;
import com.sales.customer.mapper.MarketingPlanMapper;
import com.sales.customer.service.MarketingPlanService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarketingPlanServiceImpl extends ServiceImpl<MarketingPlanMapper, MarketingPlan> implements MarketingPlanService {
    
    @Override
    public List<MarketingPlan> getAllPlans() {
        LambdaQueryWrapper<MarketingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MarketingPlan::getCreateTime);
        return this.list(wrapper);
    }
    
    @Override
    public MarketingPlan getPlanByCustomerId(Long customerId) {
        LambdaQueryWrapper<MarketingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketingPlan::getCustomerId, customerId)
               .orderByDesc(MarketingPlan::getCreateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }
    
    @Override
    public boolean markAsExecuted(Long id) {
        MarketingPlan plan = this.getById(id);
        if (plan != null) {
            plan.setStatus("completed");
            plan.setExecuteTime(LocalDateTime.now());
            return this.updateById(plan);
        }
        return false;
    }
}
