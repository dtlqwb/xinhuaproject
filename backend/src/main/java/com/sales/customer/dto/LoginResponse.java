package com.sales.customer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String department;
    private String role; // super_admin, admin, sales
    private String token;
}
