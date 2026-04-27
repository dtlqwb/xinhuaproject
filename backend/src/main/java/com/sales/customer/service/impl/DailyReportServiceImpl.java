package com.sales.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sales.customer.entity.DailyReport;
import com.sales.customer.mapper.DailyReportMapper;
import com.sales.customer.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl extends ServiceImpl<DailyReportMapper, DailyReport> implements DailyReportService {
    
    @Override
    public DailyReport saveOrUpdateReport(DailyReport report) {
        log.info("保存或更新日报，销售ID: {}, 日期: {}", report.getSalesId(), report.getReportDate());
        
        // 查询是否已存在该日期的日报
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyReport::getSalesId, report.getSalesId())
               .eq(DailyReport::getReportDate, report.getReportDate());
        
        DailyReport existingReport = getOne(wrapper);
        
        if (existingReport != null) {
            // 更新现有日报
            report.setId(existingReport.getId());
            updateById(report);
            log.info("更新日报成功，ID: {}", report.getId());
        } else {
            // 创建新日报
            report.setStatus("draft");
            save(report);
            log.info("创建日报成功，ID: {}", report.getId());
        }
        
        return report;
    }
    
    @Override
    public List<DailyReport> getReportsBySalesId(Long salesId) {
        log.info("查询销售的日报列表，销售ID: {}", salesId);
        
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyReport::getSalesId, salesId)
               .orderByDesc(DailyReport::getReportDate);
        
        return list(wrapper);
    }
    
    @Override
    public DailyReport getReportByDate(Long salesId, String reportDate) {
        log.info("查询指定日期的日报，销售ID: {}, 日期: {}", salesId, reportDate);
        
        LambdaQueryWrapper<DailyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyReport::getSalesId, salesId)
               .eq(DailyReport::getReportDate, reportDate);
        
        return getOne(wrapper);
    }
    
    @Override
    public void submitReport(Long reportId) {
        log.info("提交日报，ID: {}", reportId);
        
        DailyReport report = getById(reportId);
        if (report == null) {
            throw new RuntimeException("日报不存在");
        }
        
        report.setStatus("submitted");
        updateById(report);
        
        log.info("日报提交成功");
    }
    
    @Override
    public void reviewReport(Long reportId, Long reviewerId, String reviewerName, String comment) {
        log.info("审核日报，ID: {}, 审核人: {}", reportId, reviewerName);
        
        DailyReport report = getById(reportId);
        if (report == null) {
            throw new RuntimeException("日报不存在");
        }
        
        report.setStatus("reviewed");
        report.setReviewerId(reviewerId);
        report.setReviewerName(reviewerName);
        report.setReviewComment(comment);
        report.setReviewTime(LocalDateTime.now());
        updateById(report);
        
        log.info("日报审核成功");
    }
}
