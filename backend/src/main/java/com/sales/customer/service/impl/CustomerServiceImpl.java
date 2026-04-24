package com.sales.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sales.customer.entity.Customer;
import com.sales.customer.entity.CustomerAttachment;
import com.sales.customer.mapper.CustomerMapper;
import com.sales.customer.service.AiService;
import com.sales.customer.service.CustomerService;
import com.sales.customer.service.CustomerAttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    
    private final AiService aiService;
    private final CustomerAttachmentService attachmentService;
    
    @Override
    @Transactional
    public Customer addCustomer(String content, Long salesId, List<MultipartFile> attachments) {
        log.info("添加客户，销售人员ID: {}, 内容: {}", salesId, content);
        
        // 1. 调用AI解析客户信息
        Customer customer = aiService.parseCustomerInfo(content);
        customer.setSalesId(salesId);
        customer.setSourceType("voice"); // 默认语音输入
        customer.setStatus("pending");
        customer.setAttachmentCount(attachments != null ? attachments.size() : 0);
        
        // 2. 保存客户信息
        this.save(customer);
        
        // 3. 处理附件
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                try {
                    String filePath = saveFile(file);
                    
                    CustomerAttachment attachment = new CustomerAttachment();
                    attachment.setCustomerId(customer.getId());
                    attachment.setFileName(file.getOriginalFilename());
                    attachment.setFilePath(filePath);
                    attachment.setFileType(file.getContentType());
                    attachment.setFileSize(file.getSize());
                    
                    attachmentService.save(attachment);
                } catch (IOException e) {
                    log.error("文件上传失败", e);
                }
            }
        }
        
        log.info("客户添加成功，ID: {}", customer.getId());
        return customer;
    }
    
    @Override
    public List<Customer> getCustomersBySalesId(Long salesId) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getSalesId, salesId)
               .orderByDesc(Customer::getCreateTime);
        return this.list(wrapper);
    }
    
    @Override
    public Integer getTodayCustomerCount(Long salesId) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getSalesId, salesId)
               .ge(Customer::getCreateTime, LocalDate.now().atStartOfDay());
        return Math.toIntExact(this.count(wrapper));
    }
    
    /**
     * 保存文件到本地
     */
    private String saveFile(MultipartFile file) throws IOException {
        String uploadPath = "./uploads/";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = uploadPath + fileName;
        
        file.transferTo(new File(filePath));
        
        return "/uploads/" + fileName;
    }
}
