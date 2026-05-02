package com.sales.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.entity.SalesPerson;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.SalesPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SalesPersonController MockMvc 集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SalesPersonController MockMvc 集成测试")
class SalesPersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SalesPersonService salesPersonService;

    @MockBean
    private CustomerService customerService;

    @Test
    @DisplayName("销售人员登录 - 成功")
    void testSalesLogin_Success() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("123456");

        LoginResponse response = LoginResponse.builder()
                .id(1L)
                .username("13800138000")
                .name("张三")
                .phone("13800138000")
                .department("销售部")
                .token("mock-jwt-token")
                .build();

        when(salesPersonService.login(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/sales/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("张三"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    @DisplayName("销售人员登录 - 用户不存在")
    void testSalesLogin_UserNotFound() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPhone("99999999999");
        request.setPassword("123456");

        when(salesPersonService.login(any())).thenThrow(new RuntimeException("手机号或密码错误"));

        // When & Then
        mockMvc.perform(post("/api/sales/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("手机号或密码错误"));
    }

    @Test
    @DisplayName("获取今日客户数量 - 成功")
    void testGetTodayCount_Success() throws Exception {
        // Given
        Long salesId = 1L;
        when(customerService.getTodayCustomerCount(salesId)).thenReturn(5);

        // When & Then
        mockMvc.perform(get("/api/sales/today-count")
                        .param("salesId", salesId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.count").value(5));
    }

    @Test
    @DisplayName("获取今日客户数量 - 客户数量为0")
    void testGetTodayCount_ZeroCount() throws Exception {
        // Given
        Long salesId = 1L;
        when(customerService.getTodayCustomerCount(salesId)).thenReturn(0);

        // When & Then
        mockMvc.perform(get("/api/sales/today-count")
                        .param("salesId", salesId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.count").value(0));
    }
}
