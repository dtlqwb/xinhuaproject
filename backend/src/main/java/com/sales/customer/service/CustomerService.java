package com.sales.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sales.customer.entity.Customer;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CustomerService extends IService<Customer> {
    
    /**
     * 添加客户（支持AI解析）
     */
    Customer addCustomer(String content, Long salesId, List<MultipartFile> attachments);
    
    /**
     * 查询销售人员的客户列表
     */
    List<Customer> getCustomersBySalesId(Long salesId);
    
    /**
     * 查询今日客户数量
     */
    Integer getTodayCustomerCount(Long salesId);
}
