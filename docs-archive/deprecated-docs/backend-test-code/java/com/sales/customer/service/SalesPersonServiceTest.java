package com.sales.customer.service;

import com.sales.customer.BaseTest;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.entity.SalesPerson;
import com.sales.customer.mapper.SalesPersonMapper;
import com.sales.customer.service.impl.SalesPersonServiceImpl;
import com.sales.customer.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * SalesPersonService 单元测试
 */
@DisplayName("SalesPersonService 单元测试")
class SalesPersonServiceTest extends BaseTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SalesPersonMapper salesPersonMapper;

    @InjectMocks
    private SalesPersonServiceImpl salesPersonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("销售人员登录 - 成功")
    void testLogin_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("123456");

        SalesPerson salesPerson = new SalesPerson();
        salesPerson.setId(1L);
        salesPerson.setName("张三");
        salesPerson.setPhone("13800138000");
        salesPerson.setDepartment("销售部");

        when(salesPersonService.getOne(any())).thenReturn(salesPerson);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("mock-jwt-token");

        // When
        LoginResponse response = salesPersonService.login(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("张三", response.getName());
        assertEquals("13800138000", response.getPhone());
        assertEquals("销售部", response.getDepartment());
        assertEquals("mock-jwt-token", response.getToken());
    }

    @Test
    @DisplayName("销售人员登录 - 用户不存在")
    void testLogin_UserNotFound() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPhone("99999999999");
        request.setPassword("123456");

        when(salesPersonService.getOne(any())).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            salesPersonService.login(request);
        });
        assertEquals("手机号或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("销售人员登录 - 密码错误")
    void testLogin_WrongPassword() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("wrong-password");

        SalesPerson salesPerson = new SalesPerson();
        salesPerson.setId(1L);
        salesPerson.setName("张三");
        salesPerson.setPhone("13800138000");

        when(salesPersonService.getOne(any())).thenReturn(salesPerson);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            salesPersonService.login(request);
        });
        assertEquals("手机号或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("销售人员登录 - 空手机号")
    void testLogin_EmptyPhone() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPhone("");
        request.setPassword("123456");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            salesPersonService.login(request);
        });
        assertEquals("手机号或密码错误", exception.getMessage());
    }
}
