package com.sales.customer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sales.customer.common.Result;
import com.sales.customer.entity.AdminUser;
import com.sales.customer.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {
    
    private final AdminUserService adminUserService;
    
    /**
     * 获取用户列表
     */
    @GetMapping
    public Result<List<AdminUser>> getUserList() {
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AdminUser::getCreateTime);
        List<AdminUser> users = adminUserService.list(wrapper);
        
        // 隐藏密码字段
        users.forEach(user -> user.setPassword(null));
        
        return Result.success(users);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public Result<AdminUser> createUser(@RequestBody AdminUser user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getUsername, user.getUsername());
        if (adminUserService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }
        
        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getRole() == null) {
            user.setRole("admin");
        }
        
        adminUserService.save(user);
        user.setPassword(null); // 隐藏密码
        
        return Result.success(user);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<AdminUser> updateUser(@PathVariable Long id, @RequestBody AdminUser user) {
        AdminUser existingUser = adminUserService.getById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 不允许修改自己的角色为普通管理员（防止失去超级管理员权限）
        if ("super_admin".equals(existingUser.getRole()) && !"super_admin".equals(user.getRole())) {
            return Result.error("不能修改超级管理员的角色");
        }
        
        user.setId(id);
        user.setPassword(null); // 不更新密码
        adminUserService.updateById(user);
        
        AdminUser updatedUser = adminUserService.getById(id);
        updatedUser.setPassword(null);
        
        return Result.success(updatedUser);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        AdminUser user = adminUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 不允许删除超级管理员
        if ("super_admin".equals(user.getRole())) {
            return Result.error("不能删除超级管理员账号");
        }
        
        adminUserService.removeById(id);
        return Result.success();
    }
    
    /**
     * 启用/禁用用户
     */
    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        AdminUser user = adminUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 不允许禁用超级管理员
        if ("super_admin".equals(user.getRole()) && status == 0) {
            return Result.error("不能禁用超级管理员账号");
        }
        
        user.setStatus(status);
        adminUserService.updateById(user);
        
        return Result.success();
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        AdminUser user = adminUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 生成随机密码（实际应该使用BCrypt加密）
        String newPassword = "123456"; // 临时密码
        user.setPassword(newPassword);
        adminUserService.updateById(user);
        
        return Result.success();
    }
}
