package com.sales.customer.service;

import com.sales.customer.entity.Customer;
import java.util.List;

/**
 * AI智能体服务接口（预留）
 */
public interface AiService {
    
    /**
     * 解析客户信息，从原始文本中提取结构化数据
     * @param content 原始文本内容
     * @return 解析后的客户对象
     */
    Customer parseCustomerInfo(String content);
    
    /**
     * 分析客户数据生成日报
     * @param customers 客户列表
     * @param salesId 销售人员ID
     * @return 日报总结内容
     */
    String generateDailyReport(List<Customer> customers, Long salesId);
    
    /**
     * 为未成单客户生成营销方案
     * @param customer 客户信息
     * @return 营销方案内容
     */
    String generateMarketingPlan(Customer customer);
}
