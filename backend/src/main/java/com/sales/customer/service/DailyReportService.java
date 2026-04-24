package com.sales.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sales.customer.entity.DailyReport;
import java.util.List;

public interface DailyReportService extends IService<DailyReport> {
    
    /**
     * 保存或更新日报
     */
    void saveOrUpdateReport(DailyReport report);
    
    /**
     * 查询销售人员的日报列表
     */
    List<DailyReport> getReportsBySalesId(Long salesId);
}
