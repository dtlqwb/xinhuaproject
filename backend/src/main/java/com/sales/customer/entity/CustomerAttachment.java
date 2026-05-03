package com.sales.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
