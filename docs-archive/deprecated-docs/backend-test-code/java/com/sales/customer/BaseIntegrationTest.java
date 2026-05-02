package com.sales.customer;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 集成测试基础类
 * 启动完整的Spring上下文
 */
@SpringBootTest
@ActiveProfiles("test")
public class BaseIntegrationTest {
    
}
