package com.sales.customer.service;

import com.sales.customer.BaseTest;
import com.sales.customer.entity.Customer;
import com.sales.customer.entity.MarketingPlan;
import com.sales.customer.service.impl.MarketingPlanServiceImpl;
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
import static org.mockito.Mockito.when;

/**
 * MarketingPlanService 批量生成测试
 */
@DisplayName("MarketingPlanService 批量生成测试")
class MarketingPlanServiceBatchTest extends BaseTest {

    @Mock
    private AiService aiService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private MarketingPlanServiceImpl marketingPlanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("批量生成 - 所有客户都存在")
    void testBatchGenerate_AllCustomersExist() {
        // Given
        List<Long> customerIds = Arrays.asList(1L, 2L, 3L);

        // Mock 客户数据
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("张三");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("李四");

        Customer customer3 = new Customer();
        customer3.setId(3L);
        customer3.setName("王五");

        when(customerService.getById(1L)).thenReturn(customer1);
        when(customerService.getById(2L)).thenReturn(customer2);
        when(customerService.getById(3L)).thenReturn(customer3);

        // Mock AI服务返回
        when(aiService.generateMarketingPlan(any(Customer.class)))
                .thenReturn("营销方案内容");

        // When
        List<MarketingPlan> plans = marketingPlanService.batchGeneratePlans(customerIds);

        // Then
        assertNotNull(plans);
        assertEquals(3, plans.size(), "应该生成3个方案");

        // 验证每个方案都关联到正确的客户ID
        assertTrue(plans.stream().anyMatch(p -> p.getCustomerId().equals(1L)), "应包含客户1的方案");
        assertTrue(plans.stream().anyMatch(p -> p.getCustomerId().equals(2L)), "应包含客户2的方案");
        assertTrue(plans.stream().anyMatch(p -> p.getCustomerId().equals(3L)), "应包含客户3的方案");

        // 验证没有方案关联到错误的客户ID
        assertFalse(plans.stream().anyMatch(p -> p.getCustomerId().equals(999L)), "不应包含不存在客户的方案");
    }

    @Test
    @DisplayName("批量生成 - 部分客户不存在")
    void testBatchGenerate_SomeCustomersNotExist() {
        // Given
        List<Long> customerIds = Arrays.asList(1L, 999L, 3L); // 999不存在

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("张三");

        Customer customer3 = new Customer();
        customer3.setId(3L);
        customer3.setName("王五");

        when(customerService.getById(1L)).thenReturn(customer1);
        when(customerService.getById(999L)).thenReturn(null); // 不存在的客户
        when(customerService.getById(3L)).thenReturn(customer3);

        when(aiService.generateMarketingPlan(any(Customer.class)))
                .thenReturn("营销方案内容");

        // When
        List<MarketingPlan> plans = marketingPlanService.batchGeneratePlans(customerIds);

        // Then
        assertNotNull(plans);
        assertEquals(2, plans.size(), "应该生成2个方案(跳过不存在的客户)");

        // 验证只包含存在的客户
        assertTrue(plans.stream().anyMatch(p -> p.getCustomerId().equals(1L)));
        assertTrue(plans.stream().anyMatch(p -> p.getCustomerId().equals(3L)));
        assertFalse(plans.stream().anyMatch(p -> p.getCustomerId().equals(999L)));
    }

    @Test
    @DisplayName("批量生成 - 空列表")
    void testBatchGenerate_EmptyList() {
        // Given
        List<Long> customerIds = Arrays.asList();

        // When
        List<MarketingPlan> plans = marketingPlanService.batchGeneratePlans(customerIds);

        // Then
        assertNotNull(plans);
        assertEquals(0, plans.size(), "空列表应返回空结果");
    }

    @Test
    @DisplayName("批量生成 - 验证每个方案内容不同")
    void testBatchGenerate_DifferentContent() {
        // Given
        List<Long> customerIds = Arrays.asList(1L, 2L);

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("张三");
        customer1.setCompany("公司A");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("李四");
        customer2.setCompany("公司B");

        when(customerService.getById(1L)).thenReturn(customer1);
        when(customerService.getById(2L)).thenReturn(customer2);

        // Mock AI为不同客户返回不同内容
        when(aiService.generateMarketingPlan(customer1)).thenReturn("针对公司A的方案");
        when(aiService.generateMarketingPlan(customer2)).thenReturn("针对公司B的方案");

        // When
        List<MarketingPlan> plans = marketingPlanService.batchGeneratePlans(customerIds);

        // Then
        assertEquals(2, plans.size());

        // 找到每个客户的方案并验证内容
        MarketingPlan plan1 = plans.stream()
                .filter(p -> p.getCustomerId().equals(1L))
                .findFirst()
                .orElse(null);
        MarketingPlan plan2 = plans.stream()
                .filter(p -> p.getCustomerId().equals(2L))
                .findFirst()
                .orElse(null);

        assertNotNull(plan1);
        assertNotNull(plan2);
        assertEquals("针对公司A的方案", plan1.getPlanContent());
        assertEquals("针对公司B的方案", plan2.getPlanContent());
        assertNotEquals(plan1.getPlanContent(), plan2.getPlanContent(), "不同客户的方案内容应不同");
    }

    @Test
    @DisplayName("批量生成 - 验证方案状态和优先级")
    void testBatchGenerate_PlanStatusAndPriority() {
        // Given
        List<Long> customerIds = Arrays.asList(1L);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("测试客户");

        when(customerService.getById(1L)).thenReturn(customer);
        when(aiService.generateMarketingPlan(any())).thenReturn("方案内容");

        // When
        List<MarketingPlan> plans = marketingPlanService.batchGeneratePlans(customerIds);

        // Then
        assertEquals(1, plans.size());
        MarketingPlan plan = plans.get(0);

        assertEquals("pending", plan.getStatus(), "初始状态应为pending");
        assertEquals("medium", plan.getPriority(), "优先级应为medium");
        assertNotNull(plan.getCreateTime(), "创建时间不应为空");
        assertEquals(1L, plan.getCustomerId(), "客户ID应正确关联");
    }

    @Test
    @DisplayName("批量生成 - AI服务异常时继续处理其他客户")
    void testBatchGenerate_AiServiceException() {
        // Given
        List<Long> customerIds = Arrays.asList(1L, 2L, 3L);

        Customer customer1 = new Customer();
        customer1.setId(1L);

        Customer customer2 = new Customer();
        customer2.setId(2L);

        Customer customer3 = new Customer();
        customer3.setId(3L);

        when(customerService.getById(1L)).thenReturn(customer1);
        when(customerService.getById(2L)).thenReturn(customer2);
        when(customerService.getById(3L)).thenReturn(customer3);

        // 第一个客户AI正常，第二个异常，第三个正常
        when(aiService.generateMarketingPlan(customer1)).thenReturn("方案1");
        when(aiService.generateMarketingPlan(customer2)).thenThrow(new RuntimeException("AI服务异常"));
        when(aiService.generateMarketingPlan(customer3)).thenReturn("方案3");

        // When
        List<MarketingPlan> plans = marketingPlanService.batchGeneratePlans(customerIds);

        // Then
        assertNotNull(plans);
        assertEquals(2, plans.size(), "应该生成2个方案(跳过异常的客户)");

        // 验证只包含成功的客户
        assertTrue(plans.stream().anyMatch(p -> p.getCustomerId().equals(1L)));
        assertTrue(plans.stream().anyMatch(p -> p.getCustomerId().equals(3L)));
        assertFalse(plans.stream().anyMatch(p -> p.getCustomerId().equals(2L)));
    }
}
