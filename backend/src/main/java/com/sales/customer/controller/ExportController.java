package com.sales.customer.controller;

import com.sales.customer.entity.Customer;
import com.sales.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/admin/export")
@RequiredArgsConstructor
@CrossOrigin
public class ExportController {
    
    private final CustomerService customerService;
    
    /**
     * 导出客户明细到Excel
     */
    @GetMapping("/customers")
    public void exportCustomers(HttpServletResponse response) throws IOException {
        // 查询所有客户
        List<Customer> customers = customerService.list();
        
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("客户明细");
        
        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "客户姓名", "联系电话", "公司名称", "职位", 
                           "行业", "来源类型", "客户需求", "跟进状态", "创建时间"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 填充数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (Customer customer : customers) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(customer.getId());
            row.createCell(1).setCellValue(customer.getName() != null ? customer.getName() : "");
            row.createCell(2).setCellValue(customer.getPhone() != null ? customer.getPhone() : "");
            row.createCell(3).setCellValue(customer.getCompany() != null ? customer.getCompany() : "");
            row.createCell(4).setCellValue(customer.getPosition() != null ? customer.getPosition() : "");
            row.createCell(5).setCellValue(customer.getIndustry() != null ? customer.getIndustry() : "");
            row.createCell(6).setCellValue(customer.getSourceType() != null ? customer.getSourceType() : "");
            row.createCell(7).setCellValue(customer.getRequirement() != null ? customer.getRequirement() : "");
            row.createCell(8).setCellValue(customer.getStatus() != null ? customer.getStatus() : "");
            row.createCell(9).setCellValue(customer.getCreateTime() != null ? 
                    customer.getCreateTime().format(formatter) : "");
        }
        
        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("客户明细_" + System.currentTimeMillis(), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        
        // 输出Excel文件
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
