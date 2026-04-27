package com.sales.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日报实体类
 */
@Data
@TableName("daily_report")
public class DailyReport {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 销售ID
     */
    private Long salesId;
    
    /**
     * 销售姓名
     */
    private String salesName;
    
    /**
     * 日报日期
     */
    private LocalDate reportDate;
    
    /**
     * 今日新增客户数
     */
    private Integer todayCustomers;
    
    /**
     * 今日跟进客户数
     */
    private Integer followUpCustomers;
    
    /**
     * 今日签约客户数
     */
    private Integer signedCustomers;
    
    /**
     * 工作内容
     */
    private String workContent;
    
    /**
     * 存在问题
     */
    private String problems;
    
    /**
     * 明日计划
     */
    private String tomorrowPlan;
    
    /**
     * 状态: draft-草稿, submitted-已提交, reviewed-已审核
     */
    private String status;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
    
    /**
     * 审核人姓名
     */
    private String reviewerName;
    
    /**
     * 审核意见
     */
    private String reviewComment;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
