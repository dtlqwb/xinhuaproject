package com.sales.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.customer.BaseIntegrationTest;
import com.sales.customer.dto.LoginRequest;
import com.sales.customer.dto.LoginResponse;
import com.sales.customer.service.AdminService;
import com.sales.customer.service.SalesPersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AdminController MockMvc 集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AdminController MockMvc 集成测试")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Test
    @DisplayName("管理员登录 - 成功")
    void testAdminLogin_Success() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        LoginResponse response = LoginResponse.builder()
                .id(0L)
                .username("admin")
                .name("系统管理员")
                .token("mock-jwt-token")
                .build();

        when(adminService.login(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(0))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.name").value("系统管理员"))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    @DisplayName("管理员登录 - 用户名错误")
    void testAdminLogin_WrongUsername() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("wrong");
        request.setPassword("admin123");

        when(adminService.login(any())).thenThrow(new RuntimeException("用户名或密码错误"));

        // When & Then
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    @DisplayName("管理员登录 - 空请求体")
    void testAdminLogin_EmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }
}
