package com.sales.customer.service.impl;

import com.sales.customer.entity.Customer;
import com.sales.customer.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AiServiceImpl implements AiService {
    
    @Override
    public Customer parseCustomerInfo(String content) {
        log.info("开始解析客户信息: {}", content);
        
        Customer customer = new Customer();
        customer.setContent(content);
        
        // 简单的正则表达式解析（实际应调用AI接口）
        // 示例格式：张三，某某科技公司，销售经理，电话13800138000，需要采购办公软件
        
        // 提取姓名（假设第一个逗号前是姓名）
        String[] parts = content.split("[，,]");
        if (parts.length > 0) {
            customer.setName(parts[0].trim());
        }
        
        // 提取公司（假设第二个部分包含公司）
        if (parts.length > 1) {
            customer.setCompany(parts[1].trim());
        }
        
        // 提取职位
        if (parts.length > 2) {
            customer.setPosition(parts[2].trim());
        }
        
        // 提取手机号
        Pattern phonePattern = Pattern.compile("1[3-9]\\d{9}");
        Matcher phoneMatcher = phonePattern.matcher(content);
        if (phoneMatcher.find()) {
            customer.setPhone(phoneMatcher.group());
        }
        
        // 提取需求（剩余内容）
        if (parts.length > 3) {
            StringBuilder requirement = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                requirement.append(parts[i].trim());
                if (i < parts.length - 1) {
                    requirement.append("，");
                }
            }
            customer.setRequirement(requirement.toString());
        }
        
        log.info("解析结果: {}", customer);
        return customer;
    }
    
    @Override
    public String generateDailyReport(List<Customer> customers, Long salesId) {
        log.info("生成日报，客户数量: {}", customers.size());
        
        // 模拟AI生成的日报内容
        StringBuilder report = new StringBuilder();
        report.append("今日工作总结：\n\n");
        report.append("共录入").append(customers.size()).append("个客户信息。\n\n");
        report.append("重点客户：\n");
        
        customers.stream()
            .limit(3)
            .forEach(c -> {
                report.append("- ").append(c.getName())
                      .append("（").append(c.getCompany()).append("）")
                      .append("\n");
            });
        
        report.append("\n明日计划：\n");
        report.append("1. 跟进重点客户\n");
        report.append("2. 继续拓展新客户\n");
        
        return report.toString();
    }
    
    @Override
    public String generateMarketingPlan(Customer customer) {
        log.info("为客户生成营销方案: {}", customer.getName());
        
        // 模拟AI生成的营销方案
        StringBuilder plan = new StringBuilder();
        plan.append("营销方案 - ").append(customer.getName()).append("\n\n");
        plan.append("客户背景：\n");
        plan.append(customer.getName()).append("来自").append(customer.getCompany())
            .append("，担任").append(customer.getPosition()).append("。\n\n");
        
        if (customer.getRequirement() != null) {
            plan.append("客户需求：\n");
            plan.append(customer.getRequirement()).append("\n\n");
        }
        
        plan.append("营销策略：\n");
        plan.append("1. 深入了解客户具体需求\n");
        plan.append("2. 提供定制化解决方案\n");
        plan.append("3. 安排产品演示或试用\n");
        plan.append("4. 制定优惠方案促进成交\n\n");
        
        plan.append("跟进计划：\n");
        plan.append("- 第1天：电话回访，确认需求细节\n");
        plan.append("- 第3天：发送详细方案\n");
        plan.append("- 第7天：邀请参观或演示\n");
        plan.append("- 第14天：商务谈判\n");
        
        return plan.toString();
    }
}
