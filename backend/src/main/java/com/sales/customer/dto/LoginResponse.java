package com.sales.customer.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long id;
    private String name;
    private String phone;
    private String department;
    private String token;
}
