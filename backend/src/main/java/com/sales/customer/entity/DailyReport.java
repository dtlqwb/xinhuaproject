package com.sales.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_report")
public class DailyReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long salesId;
    
    private LocalDate reportDate;
    
    private String summary;
    
    private Integer customerCount;
    
    private String aiAnalysis;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
