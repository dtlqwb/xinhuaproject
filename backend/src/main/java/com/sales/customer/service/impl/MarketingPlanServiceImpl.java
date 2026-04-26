package com.sales.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sales.customer.entity.Customer;
import com.sales.customer.entity.MarketingPlan;
import com.sales.customer.mapper.MarketingPlanMapper;
import com.sales.customer.service.AiService;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.MarketingPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingPlanServiceImpl extends ServiceImpl<MarketingPlanMapper, MarketingPlan> implements MarketingPlanService {
    
    private final AiService aiService;
    private final CustomerService customerService;
    
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
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MarketingPlan> batchGeneratePlans(List<Long> customerIds) {
        log.info("开始批量生成营销方案, 客户数量: {}", customerIds.size());
        
        List<MarketingPlan> generatedPlans = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        
        // 遍历每个客户ID，为每个客户生成独立的营销方案
        for (Long customerId : customerIds) {
            try {
                log.info("正在为客户 ID={} 生成营销方案", customerId);
                
                // 1. 查询客户信息
                Customer customer = customerService.getById(customerId);
                if (customer == null) {
                    log.warn("客户 ID={} 不存在，跳过", customerId);
                    failCount++;
                    continue;
                }
                
                // 2. 调用AI生成该客户的专属营销方案
                String planContent = aiService.generateMarketingPlan(customer);
                
                // 3. 创建营销方案并关联到当前客户
                MarketingPlan plan = new MarketingPlan();
                plan.setCustomerId(customerId); // 关键：确保关联到正确的客户ID
                plan.setPlanContent(planContent);
                plan.setPriority("medium");
                plan.setStatus("pending");
                plan.setCreateTime(LocalDateTime.now());
                
                // 4. 保存方案
                boolean saved = this.save(plan);
                if (saved) {
                    generatedPlans.add(plan);
                    successCount++;
                    log.info("客户 ID={} 的营销方案生成成功", customerId);
                } else {
                    log.error("客户 ID={} 的营销方案保存失败", customerId);
                    failCount++;
                }
                
            } catch (Exception e) {
                log.error("为客户 ID={} 生成营销方案时发生错误: {}", customerId, e.getMessage(), e);
                failCount++;
                // 继续处理下一个客户，不中断整个批量流程
            }
        }
        
        log.info("批量生成完成: 总数={}, 成功={}, 失败={}", 
                customerIds.size(), successCount, failCount);
        
        return generatedPlans;
    }
}
