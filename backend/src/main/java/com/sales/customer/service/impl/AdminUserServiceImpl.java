package com.sales.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sales.customer.entity.AdminUser;
import com.sales.customer.mapper.AdminUserMapper;
import com.sales.customer.service.AdminUserService;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {
}
