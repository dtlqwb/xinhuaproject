package com.sales.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sales_person")
public class SalesPerson {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String phone;
    
    private String password;
    
    private String department;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
