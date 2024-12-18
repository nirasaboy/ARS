package com.bluepal.Sales.Service;

import com.bluepal.Sales.Entity.SalesRFQ;
import com.bluepal.Sales.Repository.SalesRFQRepo;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class SalesRFQExportService {

    @Autowired
    private SalesRFQRepo salesRFQRepo;

    public byte[] exportRFQsToExcel() throws Exception {
        List<SalesRFQ> rfqs = salesRFQRepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales RFQs");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Client Name");
        headerRow.createCell(2).setCellValue("Date");
        headerRow.createCell(3).setCellValue("Delivery By");
        headerRow.createCell(4).setCellValue("Status");

        int rowNum = 1;
        for (SalesRFQ rfq : rfqs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rfq.getId());
            row.createCell(1).setCellValue(rfq.getClientName());
            row.createCell(2).setCellValue(rfq.getDate());
            row.createCell(3).setCellValue(rfq.getDeliveryBy().toString());
            row.createCell(4).setCellValue(rfq.getStatus());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}
