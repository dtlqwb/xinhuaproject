package com.sales.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sales.customer.entity.MarketingPlan;
import com.sales.customer.mapper.MarketingPlanMapper;
import com.sales.customer.service.MarketingPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingPlanServiceImpl extends ServiceImpl<MarketingPlanMapper, MarketingPlan> implements MarketingPlanService {
    
    @Override
    public MarketingPlan createPlan(MarketingPlan plan) {
        log.info("创建营销方案，客户ID: {}, 销售ID: {}", plan.getCustomerId(), plan.getSalesId());
        
        plan.setStatus("draft");
        save(plan);
        
        log.info("营销方案创建成功，ID: {}", plan.getId());
        return plan;
    }
    
    @Override
    public List<MarketingPlan> getPlansByCustomerId(Long customerId) {
        log.info("查询客户的营销方案列表，客户ID: {}", customerId);
        
        LambdaQueryWrapper<MarketingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketingPlan::getCustomerId, customerId)
               .orderByDesc(MarketingPlan::getCreateTime);
        
        return list(wrapper);
    }
    
    @Override
    public List<MarketingPlan> getPlansBySalesId(Long salesId) {
        log.info("查询销售的营销方案列表，销售ID: {}", salesId);
        
        LambdaQueryWrapper<MarketingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketingPlan::getSalesId, salesId)
               .orderByDesc(MarketingPlan::getCreateTime);
        
        return list(wrapper);
    }
    
    @Override
    public void updatePlanStatus(Long planId, String status) {
        log.info("更新方案状态，ID: {}, 状态: {}", planId, status);
        
        MarketingPlan plan = getById(planId);
        if (plan == null) {
            throw new RuntimeException("营销方案不存在");
        }
        
        plan.setStatus(status);
        updateById(plan);
        
        log.info("方案状态更新成功");
    }
    
    @Override
    public void completePlan(Long planId, String result) {
        log.info("完成方案，ID: {}", planId);
        
        MarketingPlan plan = getById(planId);
        if (plan == null) {
            throw new RuntimeException("营销方案不存在");
        }
        
        plan.setStatus("completed");
        plan.setExecuteResult(result);
        plan.setCompleteTime(LocalDateTime.now());
        updateById(plan);
        
        log.info("方案完成成功");
    }
}
