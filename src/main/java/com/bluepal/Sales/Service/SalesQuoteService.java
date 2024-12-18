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

import com.bluepal.Sales.Entity.SalesQuote;
import com.bluepal.Sales.Entity.SalesQuoteComment;
import com.bluepal.Sales.Repository.SalesQuoteCommentRepo;
import com.bluepal.Sales.Repository.SalesQuoteRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;

@Service
public class SalesQuoteService {

    private static final Logger logger = LoggerFactory.getLogger(SalesQuoteService.class); // Add Logger

    @Autowired
    private SalesQuoteRepo salesQuoteRepo;
    
    @Autowired
    private SalesQuoteCommentRepo commentRepo;

    // Get all line items
    @Cacheable(value = "quotes")
    public List<SalesQuote> getAll() {
        logger.info("Fetching all sales quotes");
        return salesQuoteRepo.findAll();
    }

    // Get line item by ID
    @Cacheable(value = "quotes", key = "#id")
    public Optional<SalesQuote> getQuoteById(Long id) {
        logger.info("Fetching sales quote with ID: {}", id);
        return salesQuoteRepo.findById(id);
    }

    // Create a new line item
    @CachePut(value = "quotes", key = "#result.id")
    public SalesQuote createQuote(SalesQuote salesQuote) {
        logger.info("Creating new sales quote");
        return salesQuoteRepo.save(salesQuote);
    }

    // Update line item by ID
    @CachePut(value = "quotes", key = "#id")
    public Optional<SalesQuote> updateLineItem(Long id, SalesQuote updatedQuote) {
        logger.info("Updating sales quote with ID: {}", id);
        return salesQuoteRepo.findById(id).map(quote -> {
            quote.setDate(updatedQuote.getDate());
            quote.setNumber(updatedQuote.getNumber());
            quote.setAmount(updatedQuote.getAmount());
            quote.setApprovedAt(updatedQuote.getApprovedAt());
            quote.setStatus(updatedQuote.getStatus());
            
            logger.info("Sales quote updated successfully");
            return salesQuoteRepo.save(quote);
        });
    }

    // Delete line item by ID
    @CacheEvict(value = "quotes", key = "#id")
    public boolean deleteQuote(Long id) {
        logger.info("Deleting sales quote with ID: {}", id);
        if (salesQuoteRepo.existsById(id)) {
            salesQuoteRepo.deleteById(id);
            logger.info("Sales quote deleted successfully");
            return true;
        }
        logger.warn("Sales quote with ID {} not found", id);
        return false;
    }
    
 // Comment service part
    public SalesQuoteComment addComment(Long quoteId, String commentText) {
        logger.info("Adding comment to sales PO with ID: {}", quoteId);
        Optional<SalesQuote> salesOpt = salesQuoteRepo.findById(quoteId);
        if (salesOpt.isPresent()) {
            SalesQuote salesQuote = salesOpt.get();
            SalesQuoteComment comment = new SalesQuoteComment();
            comment.setSalesQuote(salesQuote);
            comment.setCommentedAt(LocalDateTime.now());
            comment.setCommentText(commentText);
            
            return commentRepo.save(comment);
        } else {
            logger.error("Sales PO not found with ID: {}", quoteId);
            throw new RuntimeException("Sales PO not found with ID: " + quoteId);
        }
    }

    public Optional<SalesQuoteComment> getCommentsByPOId(Long quoteId) {
        logger.info("Fetching comments for sales PO with ID: {}", quoteId);
        return commentRepo.findById(quoteId);
    }
    
    // Export service
    public byte[] exportToExcel() throws Exception {
        logger.info("Exporting sales quotes to Excel");
        List<SalesQuote> rfqs = salesQuoteRepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Quotes");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Date");
        headerRow.createCell(2).setCellValue("Amount");
        headerRow.createCell(3).setCellValue("Approved By");
        headerRow.createCell(4).setCellValue("Status");
        headerRow.createCell(5).setCellValue("Number");

        int rowNum = 1;
        for (SalesQuote rfq : rfqs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rfq.getId());
            row.createCell(1).setCellValue(rfq.getDate());
            row.createCell(2).setCellValue(rfq.getAmount());
            row.createCell(3).setCellValue(rfq.getApprovedAt());
            row.createCell(4).setCellValue(rfq.getStatus());
            row.createCell(5).setCellValue(rfq.getNumber());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        logger.info("Excel file created successfully");
        return outputStream.toByteArray();
    }

    public void generatePDF(List<SalesQuote> salesQuotes, ServletOutputStream servletOutputStream) throws Exception {
        logger.info("Generating PDF for sales quotes");
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, servletOutputStream);
            document.open();
            
            for (SalesQuote salesQuote : salesQuotes) {
                document.add(new Paragraph("Sales Quote ID: " + salesQuote.getId()));
                document.add(new Paragraph("Amount: " + salesQuote.getAmount()));
                document.add(new Paragraph("Date: " + salesQuote.getDate()));
                document.add(new Paragraph("Status: " + salesQuote.getStatus()));
                document.add(new Paragraph("Approved By: " + salesQuote.getApprovedAt()));
                document.add(new Paragraph("Number :" + salesQuote.getNumber()));
            }
            document.close();
            logger.info("PDF generated successfully");
        } catch (Exception e) {
            logger.error("Error exporting to PDF", e);
            throw new IOException("Error exporting to PDF", e);
        }
    }
}
