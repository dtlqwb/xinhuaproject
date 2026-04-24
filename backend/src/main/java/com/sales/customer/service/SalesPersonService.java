package com.sales.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.entity.SalesPerson;

public interface SalesPersonService extends IService<SalesPerson> {
    
    /**
     * 销售人员登录
     */
    LoginResponse login(LoginRequest request);
}
