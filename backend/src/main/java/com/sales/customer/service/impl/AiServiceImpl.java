package com.sales.customer.service.impl;

import com.sales.customer.client.AliyunBailianClient;
import com.sales.customer.entity.Customer;
import com.sales.customer.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    
    private final AliyunBailianClient aiClient;
    
    @Override
    public Customer parseCustomerInfo(String content) {
        log.info("开始解析客户信息: {}", content);
        
        Customer customer = new Customer();
        customer.setContent(content);
        
        try {
            // 构建提示词
            String prompt = String.format(
                "请从以下文本中提取客户信息，并以JSON格式返回：\n" +
                "{\n" +
                "  \"name\": \"客户姓名\",\n" +
                "  \"company\": \"公司名称\",\n" +
                "  \"position\": \"职位\",\n" +
                "  \"phone\": \"手机号\",\n" +
                "  \"requirement\": \"客户需求\"\n" +
                "}\n\n" +
                "文本内容：%s\n\n" +
                "如果某些字段不存在，请设置为null。只返回JSON，不要其他内容。",
                content
            );
            
            // 调用AI
            String aiResponse = aiClient.callAI(prompt);
            log.info("AI响应: {}", aiResponse);
            
            // 简单解析（实际应该用JSON解析库）
            // 这里为了简化，仍然使用正则提取
            extractWithRegex(customer, content);
            
        } catch (Exception e) {
            log.error("AI解析失败，使用正则降级: {}", e.getMessage());
            // 降级：使用正则表达式
            extractWithRegex(customer, content);
        }
        
        log.info("解析结果: {}", customer);
        return customer;
    }
    
    /**
     * 使用正则表达式提取（降级方案）
     */
    private void extractWithRegex(Customer customer, String content) {
        String[] parts = content.split("[，,]");
        if (parts.length > 0) {
            customer.setName(parts[0].trim());
        }
        if (parts.length > 1) {
            customer.setCompany(parts[1].trim());
        }
        if (parts.length > 2) {
            customer.setPosition(parts[2].trim());
        }
        
        Pattern phonePattern = Pattern.compile("1[3-9]\\d{9}");
        Matcher phoneMatcher = phonePattern.matcher(content);
        if (phoneMatcher.find()) {
            customer.setPhone(phoneMatcher.group());
        }
        
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
    }
    
    @Override
    public String generateDailyReport(List<Customer> customers, Long salesId) {
        log.info("生成日报，客户数量: {}", customers.size());
        
        try {
            // 构建客户信息摘要
            StringBuilder customerSummary = new StringBuilder();
            for (Customer c : customers) {
                customerSummary.append(String.format(
                    "- %s（%s，%s）：%s\n",
                    c.getName(),
                    c.getCompany(),
                    c.getPosition(),
                    c.getRequirement() != null ? c.getRequirement() : "无详细需求"
                ));
            }
            
            // 构建提示词
            String prompt = String.format(
                "请根据以下今日客户拜访记录，生成一份专业的销售日报：\n\n" +
                "客户拜访记录：\n%s\n\n" +
                "要求：\n" +
                "1. 包含今日工作总结\n" +
                "2. 列出重点客户\n" +
                "3. 分析客户意向\n" +
                "4. 制定明日计划\n" +
                "5. 用简洁专业的中文，分段落输出",
                customerSummary.toString()
            );
            
            return aiClient.callAI(prompt);
            
        } catch (Exception e) {
            log.error("AI生成日报失败，使用降级方案: {}", e.getMessage());
            return generateFallbackDailyReport(customers);
        }
    }
    
    /**
     * 降级方案：生成模拟日报
     */
    private String generateFallbackDailyReport(List<Customer> customers) {
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
        
        try {
            // 构建提示词
            String prompt = String.format(
                "请为以下客户生成一份专业的营销方案：\n\n" +
                "客户信息：\n" +
                "- 姓名：%s\n" +
                "- 公司：%s\n" +
                "- 职位：%s\n" +
                "- 电话：%s\n" +
                "- 需求：%s\n\n" +
                "要求：\n" +
                "1. 分析客户背景和需求\n" +
                "2. 制定营销策略（至少4条）\n" +
                "3. 制定跟进计划（分阶段，含时间节点）\n" +
                "4. 提供话术建议\n" +
                "5. 用专业、简洁的中文输出，分段落",
                customer.getName(),
                customer.getCompany() != null ? customer.getCompany() : "未知",
                customer.getPosition() != null ? customer.getPosition() : "未知",
                customer.getPhone() != null ? customer.getPhone() : "未知",
                customer.getRequirement() != null ? customer.getRequirement() : "暂无详细需求"
            );
            
            return aiClient.callAI(prompt);
            
        } catch (Exception e) {
            log.error("AI生成营销方案失败，使用降级方案: {}", e.getMessage());
            return generateFallbackMarketingPlan(customer);
        }
    }
    
    /**
     * 降级方案：生成模拟营销方案
     */
    private String generateFallbackMarketingPlan(Customer customer) {
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
