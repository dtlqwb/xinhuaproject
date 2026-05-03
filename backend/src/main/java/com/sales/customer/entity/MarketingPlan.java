package com.sales.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 营销方案实体类
 */
@Data
@TableName("marketing_plan")
public class MarketingPlan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 方案名称
     */
    private String planName;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户姓名
     */
    private String customerName;
    
    /**
     * 销售ID
     */
    private Long salesId;
    
    /**
     * 销售姓名
     */
    private String salesName;
    
    /**
     * 方案类型: product-产品方案, service-服务方案, promotion-促销方案
     */
    private String planType;
    
    /**
     * 方案内容
     */
    private String planContent;
    
    /**
     * AI生成内容
     */
    private String aiGeneratedContent;
    
    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private LocalDate executeDate;
    
    /**
     * 状态: draft-草稿, pending-待执行, executing-执行中, completed-已完成, cancelled-已取消
     */
    private String status;
    
    /**
     * 执行结果
     */
    private String executeResult;
    
    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime completeTime;
    
    /**
     * 备注
     */
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
