package com.sales.customer.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;     // 销售人员手机号
    private String password;  // 密码
}
