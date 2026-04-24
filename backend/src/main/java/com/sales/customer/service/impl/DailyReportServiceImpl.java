package com.sales.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sales.customer.entity.DailyReport;
import com.sales.customer.mapper.DailyReportMapper;
import com.sales.customer.service.DailyReportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyReportServiceImpl extends ServiceImpl<DailyReportMapper, DailyReport> implements DailyReportService {
    
    @Override
    public void saveOrUpdateReport(DailyReport report) {
        // 查询是否已存在该销售人员当天的日报
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyReport::getSalesId, report.getSalesId())
               .eq(DailyReport::getReportDate, report.getReportDate());
        
        DailyReport existingReport = this.getOne(wrapper);
        
        if (existingReport != null) {
            // 更新
            report.setId(existingReport.getId());
            this.updateById(report);
        } else {
            // 新增
            this.save(report);
        }
    }
    
    @Override
    public List<DailyReport> getReportsBySalesId(Long salesId) {
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyReport::getSalesId, salesId)
               .orderByDesc(DailyReport::getReportDate);
        return this.list(wrapper);
    }
}
