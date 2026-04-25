package com.sales.customer.service;

import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    @Value("${admin.username:admin}")
    private String adminUsername;
    
    @Value("${admin.password:admin123}")
    private String adminPassword;
    
    private final JwtUtil jwtUtil;
    
    /**
     * 管理员登录
     */
    public LoginResponse login(LoginRequest request) {
        // 验证用户名和密码
        if (!adminUsername.equals(request.getUsername()) || !adminPassword.equals(request.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成JWT token
        String token = jwtUtil.generateToken(0L, request.getUsername());
        
        return LoginResponse.builder()
                .id(0L)
                .username(request.getUsername())
                .name("系统管理员")
                .token(token)
                .build();
    }
}
