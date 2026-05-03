package com.sales.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.entity.AdminUser;
import com.sales.customer.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;
    
    /**
     * 管理员登录
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 查询 admin_user 表
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getUsername, request.getUsername());
        AdminUser admin = adminUserService.getOne(wrapper);
        
        // 2. 验证用户是否存在
        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 3. 验证密码（暂时明文对比，后续可加BCrypt）
        if (!admin.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 4. 检查状态
        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 5. 生成 token（包含 role 信息）
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
        
        return LoginResponse.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getRealName() != null ? admin.getRealName() : admin.getUsername())
                .phone(admin.getPhone())
                .role(admin.getRole())
                .token(token)
                .build();
    }
}
