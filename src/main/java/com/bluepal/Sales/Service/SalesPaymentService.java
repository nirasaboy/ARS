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

import com.bluepal.Sales.Entity.SalesPayment;
import com.bluepal.Sales.Entity.SalesPaymentComment;
import com.bluepal.Sales.Repository.SalesPaymentCommentRepo;
import com.bluepal.Sales.Repository.SalesPaymentRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;

@Service
public class SalesPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(SalesPaymentService.class);

    @Autowired
    private SalesPaymentRepo salesPaymentRepo;
    
    @Autowired
    private SalesPaymentCommentRepo commentRepo;

    // Get all line items
    @Cacheable(value = "payments")
    public List<SalesPayment> getAll() {
        logger.info("Fetching all sales payments.");
        List<SalesPayment> payments = salesPaymentRepo.findAll();
        logger.info("Fetched {} sales payments.", payments.size());
        return payments;
    }

    // Get line item by ID
    @Cacheable(value = "payments", key = "#id")
    public Optional<SalesPayment> getPaymentById(Long id) {
        logger.info("Fetching sales payment with id: {}", id);
        Optional<SalesPayment> payment = salesPaymentRepo.findById(id);
        if (payment.isPresent()) {
            logger.info("Sales payment found with id: {}", id);
        } else {
            logger.warn("Sales payment with id {} not found.", id);
        }
        return payment;
    }

    // Create a new line item
    @CachePut(value = "payments", key = "#result.id")
    public SalesPayment createPayment(SalesPayment salesPayment) {
        logger.info("Creating new sales payment.");
        SalesPayment savedPayment = salesPaymentRepo.save(salesPayment);
        logger.info("Sales payment created with id: {}", savedPayment.getId());
        return savedPayment;
    }

    // Update line item by ID
    @CachePut(value = "payments", key = "#id")
    public Optional<SalesPayment> updatePayment(Long id, SalesPayment updatedPayment) {
        logger.info("Updating sales payment with id: {}", id);
        return salesPaymentRepo.findById(id).map(payment -> {
            payment.setDate(updatedPayment.getDate());
            payment.setAmount(updatedPayment.getAmount());
            payment.setModeOfPayment(updatedPayment.getModeOfPayment());
            
            SalesPayment savedPayment = salesPaymentRepo.save(payment);
            logger.info("Sales payment with id {} updated successfully.", id);
            return savedPayment;
        });
    }

    // Delete line item by ID
    @CacheEvict(value = "payments", key = "#id")
    public boolean deletePayment(Long id) {
        logger.info("Attempting to delete sales payment with id: {}", id);
        if (salesPaymentRepo.existsById(id)) {
            salesPaymentRepo.deleteById(id);
            logger.info("Sales payment with id {} deleted successfully.", id);
            return true;
        } else {
            logger.warn("Sales payment with id {} not found.", id);
            return false;
        }
    }
    // Comment service part
    public SalesPaymentComment addComment(Long paymnetId, String commentText) {
        logger.info("Adding comment to sales PO with ID: {}", paymnetId);
        Optional<SalesPayment> salesOpt = salesPaymentRepo.findById(paymnetId);
        if (salesOpt.isPresent()) {
            SalesPayment salesPayment = salesOpt.get();
            SalesPaymentComment comment = new SalesPaymentComment();
            comment.setSalesPayment(salesPayment);
            comment.setCommentedAt(LocalDateTime.now());
            comment.setCommentText(commentText);
            
            return commentRepo.save(comment);
        } else {
            logger.error("Sales PO not found with ID: {}", paymnetId);
            throw new RuntimeException("Sales PO not found with ID: " + paymnetId);
        }
    }

    public Optional<SalesPaymentComment> getCommentsByPaymentId(Long paymnetId) {
        logger.info("Fetching comments for sales PO with ID: {}", paymnetId);
        return commentRepo.findById(paymnetId);
    }

    // Export service
    public byte[] exportToExcel() throws Exception {
        logger.info("Exporting sales payments to Excel.");
        List<SalesPayment> payments = salesPaymentRepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Payment");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Date");
        headerRow.createCell(2).setCellValue("Amount");
        headerRow.createCell(3).setCellValue("Mode Of Payment");

        int rowNum = 1;
        for (SalesPayment payment : payments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(payment.getId());
            row.createCell(1).setCellValue(payment.getDate());
            row.createCell(2).setCellValue(payment.getAmount());
            row.createCell(3).setCellValue(payment.getModeOfPayment());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        logger.info("Excel file generated successfully.");
        return outputStream.toByteArray();
    }

    public void generatePDF(List<SalesPayment> salesPayments, ServletOutputStream servletOutputStream) throws Exception {
        logger.info("Generating PDF for sales payments.");
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, servletOutputStream);
            document.open();

            for (SalesPayment salesPayment : salesPayments) {
                document.add(new Paragraph("Sales Payment ID : " + salesPayment.getId()));
                document.add(new Paragraph("Amount : " + salesPayment.getAmount()));
                document.add(new Paragraph("Date : " + salesPayment.getDate()));
                document.add(new Paragraph("Mode Of Payment : " + salesPayment.getModeOfPayment()));
            }

            document.close();
            logger.info("PDF generated successfully.");
        } catch (Exception e) {
            logger.error("Error exporting to PDF", e);
            throw new IOException("Error exporting to PDF", e);
        }
    }
}
