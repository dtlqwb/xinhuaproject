package com.sales.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("marketing_plan")
public class MarketingPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    
    private String planContent;
    
    private String priority;
    
    private String status;
    
    private LocalDateTime generateTime;
    
    private LocalDateTime executeTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
