package com.sales.customer.service;

import com.sales.customer.BaseTest;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.entity.Admin;
import com.sales.customer.mapper.AdminMapper;
import com.sales.customer.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * AdminService 单元测试
 */
@DisplayName("AdminService 单元测试")
class AdminServiceTest extends BaseTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        // 设置默认管理员账号密码
        ReflectionTestUtils.setField(adminService, "adminUsername", "admin");
        ReflectionTestUtils.setField(adminService, "adminPassword", "admin123");
    }

    @Test
    @DisplayName("管理员登录 - 成功")
    void testLogin_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("mock-token");

        // When
        LoginResponse response = adminService.login(request);

        // Then
        assertNotNull(response);
        assertEquals(0L, response.getId());
        assertEquals("admin", response.getUsername());
        assertEquals("系统管理员", response.getName());
        assertEquals("mock-token", response.getToken());
    }

    @Test
    @DisplayName("管理员登录 - 用户名错误")
    void testLogin_WrongUsername() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("wrong-admin");
        request.setPassword("admin123");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.login(request);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("管理员登录 - 密码错误")
    void testLogin_WrongPassword() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong-password");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.login(request);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("管理员登录 - 空用户名")
    void testLogin_EmptyUsername() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("admin123");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.login(request);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("管理员登录 - 空密码")
    void testLogin_EmptyPassword() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.login(request);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }
}
