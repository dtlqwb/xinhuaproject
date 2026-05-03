package com.sales.customer.config;

import com.sales.customer.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        
        // 只拦截需要权限控制的接口
        if (!uri.startsWith("/api/admin/users")) {
            return true;
        }
        
        // 从 header 获取 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }
        
        // 去除 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 解析 token 获取 role
        String role = jwtUtil.getRoleFromToken(token);
        
        // 验证权限：只有 super_admin 可以操作用户管理
        if (!"super_admin".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"code\":403,\"message\":\"权限不足，仅超级管理员可操作\"}");
            return false;
        }
        
        return true;
    }
}
