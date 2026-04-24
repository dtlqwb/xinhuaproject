package com.sales.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sales.customer.entity.CustomerAttachment;
import com.sales.customer.mapper.CustomerAttachmentMapper;
import com.sales.customer.service.CustomerAttachmentService;
import org.springframework.stereotype.Service;

@Service
public class CustomerAttachmentServiceImpl extends ServiceImpl<CustomerAttachmentMapper, CustomerAttachment> implements CustomerAttachmentService {
}
