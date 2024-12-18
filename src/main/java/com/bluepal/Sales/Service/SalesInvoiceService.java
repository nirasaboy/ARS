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

import com.bluepal.Sales.Entity.SalesInvoice;
import com.bluepal.Sales.Entity.SalesInvoiceComment;
import com.bluepal.Sales.Repository.SalesInvoiceCommentRepo;
import com.bluepal.Sales.Repository.SalesInvoiceRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;

@Service
public class SalesInvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(SalesInvoiceService.class);

    @Autowired
    private SalesInvoiceRepo salesInvoiceRepo;

    @Autowired
    private SalesInvoiceCommentRepo commentRepo;

    // Get all line items
    @Cacheable(value = "invoices")
    public List<SalesInvoice> getAll() {
        logger.info("Fetching all sales invoices.");
        List<SalesInvoice> invoices = salesInvoiceRepo.findAll();
        logger.info("Fetched {} sales invoices.", invoices.size());
        return invoices;
    }

    // Get line item by ID
    @Cacheable(value = "invoices", key = "#id")
    public Optional<SalesInvoice> getInvoiceById(Long id) {
        logger.info("Fetching sales invoice with id: {}", id);
        Optional<SalesInvoice> invoice = salesInvoiceRepo.findById(id);
        if (invoice.isPresent()) {
            logger.info("Sales invoice found with id: {}", id);
        } else {
            logger.warn("Sales invoice with id {} not found.", id);
        }
        return invoice;
    }

    // Create a new line item
    @CachePut(value = "invoices", key = "#result.id")
    public SalesInvoice createInvoice(SalesInvoice salesInvoice) {
        logger.info("Creating new sales invoice with number: {}", salesInvoice.getNumber());
        SalesInvoice savedInvoice = salesInvoiceRepo.save(salesInvoice);
        logger.info("Sales invoice created with id: {}", savedInvoice.getId());
        return savedInvoice;
    }

    // Update line item by ID
    @CachePut(value = "invoices", key = "#id")
    public Optional<SalesInvoice> updateInvoice(Long id, SalesInvoice updatedInvoice) {
        logger.info("Updating sales invoice with id: {}", id);
        return salesInvoiceRepo.findById(id).map(invoice -> {
            invoice.setDate(updatedInvoice.getDate());
            invoice.setNumber(updatedInvoice.getNumber());
            invoice.setAmount(updatedInvoice.getAmount());
            invoice.setParticulars(updatedInvoice.getParticulars());
            invoice.setDueBy(updatedInvoice.getDueBy());
            
            SalesInvoice savedInvoice = salesInvoiceRepo.save(invoice);
            logger.info("Sales invoice with id {} updated successfully.", id);
            return savedInvoice;
        });
    }

    // Delete line item by ID
    @CacheEvict(value = "invoices", key = "#id")
    public boolean deleteInvoice(Long id) {
        logger.info("Attempting to delete sales invoice with id: {}", id);
        if (salesInvoiceRepo.existsById(id)) {
            salesInvoiceRepo.deleteById(id);
            logger.info("Sales invoice with id {} deleted successfully.", id);
            return true;
        } else {
            logger.warn("Sales invoice with id {} not found.", id);
            return false;
        }
    }

    // Comment service part
    public SalesInvoiceComment addComment(Long invoiceId, String commentText) {
        logger.info("Adding comment to sales PO with ID: {}", invoiceId);
        Optional<SalesInvoice> salesOpt = salesInvoiceRepo.findById(invoiceId);
        if (salesOpt.isPresent()) {
            SalesInvoice salesInvoice = salesOpt.get();
            SalesInvoiceComment comment = new SalesInvoiceComment();
            comment.setSalesInvoice(salesInvoice);
            comment.setCommentedAt(LocalDateTime.now());
            comment.setCommentText(commentText);
            
            return commentRepo.save(comment);
        } else {
            logger.error("Sales PO not found with ID: {}", invoiceId);
            throw new RuntimeException("Sales PO not found with ID: " + invoiceId);
        }
    }

    public Optional<SalesInvoiceComment> getCommentsByInvoiceId(Long invoiceId) {
        logger.info("Fetching comments for sales PO with ID: {}", invoiceId);
        return commentRepo.findById(invoiceId);
    }

    // Export service
    public byte[] exportToExcel() throws Exception {
        logger.info("Exporting sales invoices to Excel.");
        List<SalesInvoice> invoices = salesInvoiceRepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Invoice");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Number");
        headerRow.createCell(2).setCellValue("Date");
        headerRow.createCell(3).setCellValue("Particular");
        headerRow.createCell(4).setCellValue("Due By");
        headerRow.createCell(5).setCellValue("Amount");

        int rowNum = 1;
        for (SalesInvoice invoice : invoices) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(invoice.getId());
            row.createCell(1).setCellValue(invoice.getNumber());
            row.createCell(2).setCellValue(invoice.getDate());
            row.createCell(3).setCellValue(invoice.getParticulars());
            row.createCell(4).setCellValue(invoice.getDueBy());
            row.createCell(5).setCellValue(invoice.getAmount());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        logger.info("Excel file generated successfully.");
        return outputStream.toByteArray();
    }

    public void generatePDF(List<SalesInvoice> salesInvoices, ServletOutputStream servletOutputStream) throws Exception {
        logger.info("Generating PDF for sales invoices.");
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, servletOutputStream);
            document.open();

            for (SalesInvoice salesInvoice : salesInvoices) {
                document.add(new Paragraph("Sales Invoice ID : " + salesInvoice.getId()));
                document.add(new Paragraph("Amount : " + salesInvoice.getAmount()));
                document.add(new Paragraph("Date : " + salesInvoice.getDate()));
                document.add(new Paragraph("Particulars : " + salesInvoice.getParticulars()));
                document.add(new Paragraph("Number : " + salesInvoice.getNumber()));
                document.add(new Paragraph("Due By : " + salesInvoice.getDueBy()));
            }

            document.close();
            logger.info("PDF generated successfully.");
        } catch (Exception e) {
            logger.error("Error exporting to PDF", e);
            throw new IOException("Error exporting to PDF", e);
        }
    }
}
