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

import com.bluepal.Sales.Entity.SalesQuoteComment;
import com.bluepal.Sales.Entity.SalesQuoteLineItem;
import com.bluepal.Sales.Repository.SalesQuoteCommentRepo;
import com.bluepal.Sales.Repository.SalesQuoteLineItemRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;

@Service
public class SalesQuoteLineItemService {

    private static final Logger logger = LoggerFactory.getLogger(SalesQuoteLineItemService.class);

    @Autowired
    private SalesQuoteLineItemRepo lineItemRepo;
  
    @Autowired
    private SalesQuoteCommentRepo commentRepo;

    // Get all line items
    @Cacheable(value = "quote_line_items")
    public List<SalesQuoteLineItem> getAllLineItems() {
        logger.info("Fetching all Sales Quote Line Items from the database.");
        return lineItemRepo.findAll();
    }

    // Get line item by ID
    @Cacheable(value = "quote_line_items", key = "#id")
    public Optional<SalesQuoteLineItem> getLineItemById(Long id) {
        logger.info("Fetching Sales Quote Line Item with ID: {}", id);
        return lineItemRepo.findById(id);
    }

    // Create a new line item
    @CachePut(value = "quote_line_items", key = "#result.id")
    public SalesQuoteLineItem createLineItem(SalesQuoteLineItem lineItem) {
        logger.info("Creating a new Sales Quote Line Item: {}", lineItem);
        return lineItemRepo.save(lineItem);
    }

    // Update line item by ID
    @CachePut(value = "quote_line_items", key = "#id")
    public Optional<SalesQuoteLineItem> updateLineItem(Long id, SalesQuoteLineItem updatedLineItem) {
        logger.info("Updating Sales Quote Line Item with ID: {}", id);
        return lineItemRepo.findById(id).map(lineItem -> {
            lineItem.setName(updatedLineItem.getName());
            lineItem.setQuantity(updatedLineItem.getQuantity());
            lineItem.setUnitOfMeasure(updatedLineItem.getUnitOfMeasure());
            lineItem.setRate(updatedLineItem.getRate());
            lineItem.setAmount(updatedLineItem.getAmount());
            lineItem.setPromiseDate(updatedLineItem.getPromiseDate());
            lineItem.setStatus(updatedLineItem.getStatus());
            SalesQuoteLineItem savedItem = lineItemRepo.save(lineItem);
            logger.info("Successfully updated Sales Quote Line Item: {}", savedItem);
            return savedItem;
        });
    }
    
    // Add comment to a quote
    public SalesQuoteComment addComment(Long quoteId, String commentText) {
        logger.info("Adding comment to RFQ line item with ID: {}", quoteId);
        Optional<SalesQuoteLineItem> salesOpt = lineItemRepo.findById(quoteId);
        if (salesOpt.isPresent()) {
            SalesQuoteLineItem salesRFQ = salesOpt.get();
            SalesQuoteComment comment = new SalesQuoteComment();
            comment.setSalesQuoteLineItem(salesRFQ);
            comment.setCommentedAt(LocalDateTime.now());
            comment.setCommentText(commentText);
            return commentRepo.save(comment);
        } else {
            logger.error("Sales RFQ not found with ID: {}", quoteId);
            throw new RuntimeException("Sales RFQ not found with id: " + quoteId);
        }
    }

    public List<SalesQuoteComment> getCommentsByQuoteId(Long quoteId) {
        logger.info("Fetching comments for RFQ line item with ID: {}", quoteId);
        return commentRepo.findBySalesQuoteId(quoteId);
    }

    // Delete line item by ID
    @CacheEvict(value = "quote_line_items", key = "#id")
    public boolean deleteLineItem(Long id) {
        logger.info("Deleting Sales Quote Line Item with ID: {}", id);
        if (lineItemRepo.existsById(id)) {
            lineItemRepo.deleteById(id);
            logger.info("Successfully deleted Sales Quote Line Item with ID: {}", id);
            return true;
        } else {
            logger.warn("Failed to delete Sales Quote Line Item with ID: {}. Item not found.", id);
            return false;
        }
    }

    // Export service to Excel
    public byte[] exportQuotesToExcel() throws Exception {
        logger.info("Exporting Sales Quote Line Items to Excel.");
        List<SalesQuoteLineItem> rfqs = lineItemRepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Quote Line Items");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Quantity");
        headerRow.createCell(3).setCellValue("Amount");
        headerRow.createCell(4).setCellValue("Unit Of Measure");
        headerRow.createCell(5).setCellValue("Rate");
        headerRow.createCell(6).setCellValue("Status");
        headerRow.createCell(7).setCellValue("Promise-Date");

        int rowNum = 1;
        for (SalesQuoteLineItem rfq : rfqs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rfq.getId());
            row.createCell(1).setCellValue(rfq.getName());
            row.createCell(2).setCellValue(rfq.getQuantity());
            row.createCell(3).setCellValue(rfq.getAmount());
            row.createCell(4).setCellValue(rfq.getUnitOfMeasure());
            row.createCell(5).setCellValue(rfq.getRate());
            row.createCell(6).setCellValue(rfq.getStatus());
            row.createCell(7).setCellValue(rfq.getPromiseDate());
        }
      
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        logger.info("Excel file generated successfully.");
        return outputStream.toByteArray();
    }

    // Export service to PDF
    public void generatePDF(List<SalesQuoteLineItem> salesQuotes, ServletOutputStream servletOutputStream) throws Exception {
        logger.info("Exporting Sales Quote Line Items to PDF.");
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, servletOutputStream);
            document.open();

            for (SalesQuoteLineItem salesQuote : salesQuotes) {
                document.add(new Paragraph("SalesQuote ID: " + salesQuote.getId()));
                document.add(new Paragraph("Name : " + salesQuote.getName()));
                document.add(new Paragraph("Quantity : " + salesQuote.getQuantity()));
                document.add(new Paragraph("Unit Of Measure : " + salesQuote.getUnitOfMeasure()));
                document.add(new Paragraph("Rate : " + salesQuote.getRate()));
                document.add(new Paragraph("Amount : " + salesQuote.getAmount()));
                document.add(new Paragraph("Promise Date : " + salesQuote.getPromiseDate()));
                document.add(new Paragraph("Status : " + salesQuote.getStatus()));
            }
            document.close();
            logger.info("PDF generated successfully.");
        } catch (Exception e) {
            logger.error("Error exporting to PDF", e);
            throw new IOException("Error exporting to PDF", e);
        }
    }

}
