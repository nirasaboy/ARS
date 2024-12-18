package com.bluepal.Sales.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bluepal.Sales.Entity.SalesPO;
import com.bluepal.Sales.Entity.SalesPOComment;
import com.bluepal.Sales.Repository.SalesPOCommentRepo;
import com.bluepal.Sales.Repository.SalesPORepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;

@Service
public class SalesPOService {

    private static final Logger logger = LoggerFactory.getLogger(SalesPOService.class);

    @Autowired
    private SalesPORepo salesPORepo;
    
    @Autowired
    private SalesPOCommentRepo commentRepo;

    // Get all line items
    @Cacheable(value = "pos")
    public List<SalesPO> getAll() {
        logger.info("Fetching all sales purchase orders (POs).");
        List<SalesPO> pos = salesPORepo.findAll();
        logger.info("Fetched {} POs.", pos.size());
        return pos;
    }

    // Get line item by ID
    @Cacheable(value = "pos", key = "#id")
    public Optional<SalesPO> getPOById(Long id) {
        logger.info("Fetching sales PO with ID: {}", id);
        Optional<SalesPO> po = salesPORepo.findById(id);
        if (po.isPresent()) {
            logger.info("Found sales PO with ID: {}", id);
        } else {
            logger.warn("Sales PO with ID {} not found.", id);
        }
        return po;
    }

    // Create a new line item
    @CachePut(value = "pos", key = "#result.id")
    public SalesPO createPO(SalesPO salesPO) {
        logger.info("Creating a new sales PO.");
        SalesPO savedPO = salesPORepo.save(salesPO);
        logger.info("Sales PO created with ID: {}", savedPO.getId());
        return savedPO;
    }

    // Update line item by ID
    @CachePut(value = "pos", key = "#id")
    public Optional<SalesPO> updatePO(Long id, SalesPO updatedPO) {
        logger.info("Updating sales PO with ID: {}", id);
        return salesPORepo.findById(id).map(po -> {
            po.setDate(updatedPO.getDate());
            po.setNumber(updatedPO.getNumber());
            po.setAmount(updatedPO.getAmount());
            po.setApprovedBy(updatedPO.getApprovedBy());
            po.setStatus(updatedPO.getStatus());
            po.setDeliveryBy(updatedPO.getDeliveryBy());
            
            SalesPO savedPO = salesPORepo.save(po);
            logger.info("Sales PO with ID {} updated successfully.", id);
            return savedPO;
        });
    }

    // Delete line item by ID
    @CacheEvict(value = "pos", key = "#id")
    public boolean deletePO(Long id) {
        logger.info("Deleting sales PO with ID: {}", id);
        if (salesPORepo.existsById(id)) {
            salesPORepo.deleteById(id);
            logger.info("Sales PO with ID {} deleted successfully.", id);
            return true;
        } else {
            logger.warn("Sales PO with ID {} not found.", id);
            return false;
        }
    }
    
    // Comment service part
    public SalesPOComment addComment(Long poId, String commentText) {
        logger.info("Adding comment to sales PO with ID: {}", poId);
        Optional<SalesPO> salesOpt = salesPORepo.findById(poId);
        if (salesOpt.isPresent()) {
            SalesPO salesPo = salesOpt.get();
            SalesPOComment comment = new SalesPOComment();
            comment.setSalesPO(salesPo);
            comment.setCommentedAt(LocalDateTime.now());
            comment.setCommentText(commentText);
            
            return commentRepo.save(comment);
        } else {
            logger.error("Sales PO not found with ID: {}", poId);
            throw new RuntimeException("Sales PO not found with ID: " + poId);
        }
    }

    public Optional<SalesPOComment> getCommentsByPOId(Long poId) {
        logger.info("Fetching comments for sales PO with ID: {}", poId);
        return commentRepo.findById(poId);
    }
    
    // Export to Excel
    public byte[] exportToExcel() throws Exception {
        logger.info("Exporting sales POs to Excel.");
        List<SalesPO> pos = salesPORepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales PO");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Number");
        headerRow.createCell(2).setCellValue("Date");
        headerRow.createCell(3).setCellValue("Delivery By");
        headerRow.createCell(4).setCellValue("Status");
        headerRow.createCell(5).setCellValue("Amount");
        headerRow.createCell(6).setCellValue("Approved By");

        int rowNum = 1;
        for (SalesPO po : pos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(po.getId());
            row.createCell(1).setCellValue(po.getNumber());
            row.createCell(2).setCellValue(po.getDate());
            row.createCell(3).setCellValue(po.getDeliveryBy());
            row.createCell(4).setCellValue(po.getStatus());
            row.createCell(5).setCellValue(po.getAmount());
            row.createCell(6).setCellValue(po.getApprovedBy());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        logger.info("Excel export completed successfully.");
        return outputStream.toByteArray();
    }

    // Export to PDF
    public void generatePDF(List<SalesPO> salesPOs, ServletOutputStream servletOutputStream) throws Exception {
        logger.info("Generating PDF for sales POs.");
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, servletOutputStream);
            document.open();

            for (SalesPO salesPO : salesPOs) {
                document.add(new Paragraph("Sales PO ID: " + salesPO.getId()));
                document.add(new Paragraph("Amount: " + salesPO.getAmount()));
                document.add(new Paragraph("Date: " + salesPO.getDate()));
                document.add(new Paragraph("Status: " + salesPO.getStatus()));
                document.add(new Paragraph("Approved By: " + salesPO.getApprovedBy()));
                document.add(new Paragraph("Number: " + salesPO.getNumber()));
                document.add(new Paragraph("Delivery By: " + salesPO.getDeliveryBy()));
            }

            document.close();
            logger.info("PDF generation completed successfully.");
        } catch (Exception e) {
            logger.error("Error generating PDF", e);
            throw new IOException("Error generating PDF", e);
        }
    }
}
