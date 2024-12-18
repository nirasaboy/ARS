package com.bluepal.Sales.Service;

import com.bluepal.Sales.Entity.SalesRFQComment;
import com.bluepal.Sales.Entity.SalesRFQLineItem;
import com.bluepal.Sales.Repository.SalesRFQCommentRepo;
import com.bluepal.Sales.Repository.SalesRFQLineItemRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletOutputStream;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesRFQLineItemService {

    private static final Logger logger = LoggerFactory.getLogger(SalesRFQLineItemService.class);
    
    @Autowired
    private SalesRFQLineItemRepo lineItemRepo;
    
    @Autowired
    private SalesRFQCommentRepo commentRepo;

    // Get all line items
    @Cacheable(value = "rfqs_line_items")
    public List<SalesRFQLineItem> getAllLineItems() {
        logger.info("Fetching all RFQ line items.");
        return lineItemRepo.findAll();
    }

    // Get line item by ID
    @Cacheable(value = "rfqs_line_items", key = "#id")
    public Optional<SalesRFQLineItem> getLineItemById(Long id) {
        logger.info("Fetching RFQ line item with ID: {}", id);
        return lineItemRepo.findById(id);
    }

    // Create a new line item
    @CachePut(value = "rfqs_line_items", key = "#result.id")
    public SalesRFQLineItem createLineItem(SalesRFQLineItem lineItem) {
        logger.info("Creating new RFQ line item: {}", lineItem);
        return lineItemRepo.save(lineItem);
    }

    // Update line item by ID
    @CachePut(value = "rfqs_line_items", key = "#id")
    public Optional<SalesRFQLineItem> updateLineItem(Long id, SalesRFQLineItem updatedLineItem) {
        logger.info("Updating RFQ line item with ID: {}", id);
        return lineItemRepo.findById(id).map(lineItem -> {
            lineItem.setName(updatedLineItem.getName());
            lineItem.setQuantity(updatedLineItem.getQuantity());
            lineItem.setUnitOfMeasure(updatedLineItem.getUnitOfMeasure());
            lineItem.setDeliveryBy(updatedLineItem.getDeliveryBy());
//            lineItem.setSalesRFQ(updatedLineItem.getSalesRFQ());
            if (updatedLineItem.getSalesRFQ() != null) {
                lineItem.setSalesRFQ(updatedLineItem.getSalesRFQ());
            }
            return lineItemRepo.save(lineItem);
        });
    }

    // Delete line item by ID
    @CacheEvict(value = "rfqs_line_items", key = "#id")
    public boolean deleteLineItem(Long id) {
        logger.info("Deleting RFQ line item with ID: {}", id);
        if (lineItemRepo.existsById(id)) {
            lineItemRepo.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Comment service part
    public SalesRFQComment addComment(Long rfqId, String commentText) {
        logger.info("Adding comment to RFQ line item with ID: {}", rfqId);
        Optional<SalesRFQLineItem> salesOpt = lineItemRepo.findById(rfqId);
        if (salesOpt.isPresent()) {
            SalesRFQLineItem salesRFQ = salesOpt.get();
            SalesRFQComment comment = new SalesRFQComment();
            comment.setSalesRFQLineItem(salesRFQ);
            comment.setCommentedAt(LocalDateTime.now());
            comment.setCommentText(commentText);
            return commentRepo.save(comment);
        } else {
            logger.error("Sales RFQ not found with ID: {}", rfqId);
            throw new RuntimeException("Sales RFQ not found with id: " + rfqId);
        }
    }

    public List<SalesRFQComment> getCommentsByRFQId(Long rfqId) {
        logger.info("Fetching comments for RFQ line item with ID: {}", rfqId);
        return commentRepo.findBySalesRFQId(rfqId);
    }
    
    // Export service
    public byte[] exportRFQsToExcel() throws Exception {
        logger.info("Exporting RFQs to Excel.");
        List<SalesRFQLineItem> rfqs = lineItemRepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales RFQ Line Items");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Quantity");
        headerRow.createCell(3).setCellValue("Delivery By");
        headerRow.createCell(4).setCellValue("Unit Of Measure");

        int rowNum = 1;
        for (SalesRFQLineItem rfq : rfqs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rfq.getId());
            row.createCell(1).setCellValue(rfq.getName());
            row.createCell(2).setCellValue(rfq.getQuantity());
            row.createCell(3).setCellValue(rfq.getDeliveryBy().toString());
            row.createCell(4).setCellValue(rfq.getUnitOfMeasure());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public void generatePDF(List<SalesRFQLineItem> salesRFQs, ServletOutputStream servletOutputStream) throws Exception {
        logger.info("Generating PDF for RFQs.");
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, servletOutputStream);
            document.open();
            
            for (SalesRFQLineItem salesRFQ : salesRFQs) {
                document.add(new Paragraph("Sales RFQ Line Item ID : " + salesRFQ.getId()));
                document.add(new Paragraph("Name : " + salesRFQ.getName()));
                document.add(new Paragraph("Quantity : " + salesRFQ.getQuantity()));
                document.add(new Paragraph("Unit Of Measure : " + salesRFQ.getUnitOfMeasure()));
                document.add(new Paragraph("Delivery By : " + salesRFQ.getDeliveryBy()));
            }
            document.close();
        } catch (Exception e) {
            logger.error("Error exporting to PDF", e);
            throw new IOException("Error exporting to PDF", e);
        }
    }
}
