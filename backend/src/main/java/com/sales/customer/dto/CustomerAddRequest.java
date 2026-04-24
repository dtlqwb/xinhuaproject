package com.sales.customer.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class CustomerAddRequest {
    private String content;  // 语音或文字输入的原始内容
    private Long salesId;    // 销售人员ID
    private List<MultipartFile> attachments;  // 附件列表
}
