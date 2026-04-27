package com.sales.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sales.customer.entity.DailyReport;

import java.util.List;

public interface DailyReportService extends IService<DailyReport> {
    
    /**
     * 创建或更新日报
     */
    DailyReport saveOrUpdateReport(DailyReport report);
    
    /**
     * 查询销售的日报列表
     */
    List<DailyReport> getReportsBySalesId(Long salesId);
    
    /**
     * 查询指定日期的日报
     */
    DailyReport getReportByDate(Long salesId, String reportDate);
    
    /**
     * 提交日报
     */
    void submitReport(Long reportId);
    
    /**
     * 审核日报
     */
    void reviewReport(Long reportId, Long reviewerId, String reviewerName, String comment);
}
