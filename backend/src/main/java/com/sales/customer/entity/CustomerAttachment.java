package com.sales.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customer_attachment")
public class CustomerAttachment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    
    private String fileName;
    
    private String filePath;
    
    private String fileType;
    
    private Long fileSize;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
