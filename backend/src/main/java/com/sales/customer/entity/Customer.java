package com.sales.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customer")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String phone;
    
    private String company;
    
    private String position;
    
    private String industry;
    
    private String sourceType;
    
    private String content;
    
    private String imagePath;
    
    private String audioText;
    
    private String requirement;
    
    private String status;
    
    private Long salesId;
    
    private Integer attachmentCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
