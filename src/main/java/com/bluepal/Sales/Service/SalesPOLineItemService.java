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

import com.bluepal.Sales.Entity.SalesPOComment;
import com.bluepal.Sales.Entity.SalesPOLineItem;
import com.bluepal.Sales.Repository.SalesPOCommentRepo;
import com.bluepal.Sales.Repository.SalesPOLineItemRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;

@Service
public class SalesPOLineItemService {

    private static final Logger logger = LoggerFactory.getLogger(SalesPOLineItemService.class);  // Logger instance

    @Autowired
    private SalesPOLineItemRepo lineItemRepo;

    @Autowired
    private SalesPOCommentRepo commentRepo;

    // Get all line items
    @Cacheable(value = "po_line_items")
    public List<SalesPOLineItem> getAllLineItems() {
        logger.info("Fetching all sales PO line items");
        return lineItemRepo.findAll();
    }

    // Get line item by ID
    @Cacheable(value = "po_line_items", key = "#id")
    public Optional<SalesPOLineItem> getLineItemById(Long id) {
        logger.info("Fetching sales PO line item with ID: {}", id);
        return lineItemRepo.findById(id);
    }

    // Create a new line item
    @CachePut(value = "po_line_items", key = "#result.id")
    public SalesPOLineItem createLineItem(SalesPOLineItem lineItem) {
        logger.info("Creating a new sales PO line item: {}", lineItem.getPo());
        return lineItemRepo.save(lineItem);
    }

    // Update line item by ID
    @CachePut(value = "po_line_items", key = "#id")
    public Optional<SalesPOLineItem> updateLineItem(Long id, SalesPOLineItem updatedLineItem) {
        logger.info("Updating sales PO line item with ID: {}", id);
        return lineItemRepo.findById(id).map(lineItem -> {
            lineItem.setName(updatedLineItem.getName());
            lineItem.setQuantity(updatedLineItem.getQuantity());
            lineItem.setUnitOfMeasure(updatedLineItem.getUnitOfMeasure());
            lineItem.setRate(updatedLineItem.getRate());
            lineItem.setAmount(updatedLineItem.getAmount());
            lineItem.setPromiseDate(updatedLineItem.getPromiseDate());
            lineItem.setStatus(updatedLineItem.getStatus());
            logger.info("Sales PO line item with ID: {} updated successfully", id);
            return lineItemRepo.save(lineItem);
        });
    }

    // Delete line item by ID
    @CacheEvict(value = "po_line_items", key = "#id")
    public boolean deleteLineItem(Long id) {
        if (lineItemRepo.existsById(id)) {
            logger.info("Deleting sales PO line item with ID: {}", id);
            lineItemRepo.deleteById(id);
            return true;
        } else {
            logger.warn("Sales PO line item with ID: {} not found for deletion", id);
            return false;
        }
    }

 // Comment service part
    public SalesPOComment addComment(Long poId, String commentText) {
        logger.info("Adding comment to RFQ line item with ID: {}", poId);
        Optional<SalesPOLineItem> salesOpt = lineItemRepo.findById(poId);
        if (salesOpt.isPresent()) {
            SalesPOLineItem salesPO = salesOpt.get();
            SalesPOComment comment = new SalesPOComment();
            comment.setSalesPOLineItem(salesPO);
            comment.setCommentedAt(LocalDateTime.now());
            comment.setCommentText(commentText);
            return commentRepo.save(comment);
        } else {
            logger.error("Sales RFQ not found with ID: {}", poId);
            throw new RuntimeException("Sales RFQ not found with id: " + poId);
        }
    }

    public List<SalesPOComment> getCommentsByPOId(Long poId) {
        logger.info("Fetching comments for RFQ line item with ID: {}", poId);
        return commentRepo.findBySalesPOId(poId);
    }

    // Export service
    public byte[] exportPOLineItemsToExcel() throws Exception {
        logger.info("Exporting sales PO line items to Excel");
        List<SalesPOLineItem> pos = lineItemRepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales PO Line Items");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("CName");
        headerRow.createCell(2).setCellValue("Quantity");
        headerRow.createCell(3).setCellValue("Amount");
        headerRow.createCell(4).setCellValue("Unit Of Measure");
        headerRow.createCell(5).setCellValue("Rate");
        headerRow.createCell(6).setCellValue("Status");
        headerRow.createCell(7).setCellValue("Promise-Date");

        int rowNum = 1;
        for (SalesPOLineItem po : pos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(po.getId());
            row.createCell(1).setCellValue(po.getName());
            row.createCell(2).setCellValue(po.getQuantity());
            row.createCell(3).setCellValue(po.getAmount());
            row.createCell(4).setCellValue(po.getUnitOfMeasure());
            row.createCell(5).setCellValue(po.getRate());
            row.createCell(6).setCellValue(po.getStatus());
            row.createCell(7).setCellValue(po.getPromiseDate());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        logger.info("Sales PO line items exported to Excel successfully");
        return outputStream.toByteArray();
    }

    public void generatePDF(List<SalesPOLineItem> salespos, ServletOutputStream servletOutputStream) throws Exception {
        logger.info("Generating PDF for sales PO line items");
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, servletOutputStream);
            document.open();

            for (SalesPOLineItem salespo : salespos) {
                document.add(new Paragraph("Sales PO ID: " + salespo.getId()));
                document.add(new Paragraph("Name: " + salespo.getName()));
                document.add(new Paragraph("Quantity: " + salespo.getQuantity()));
                document.add(new Paragraph("Unit Of Measure: " + salespo.getUnitOfMeasure()));
                document.add(new Paragraph("Rate: " + salespo.getRate()));
                document.add(new Paragraph("Amount: " + salespo.getAmount()));
                document.add(new Paragraph("Promise Date: " + salespo.getPromiseDate()));
                document.add(new Paragraph("Status: " + salespo.getStatus()));
            }

            document.close();
            logger.info("PDF generated successfully for sales PO line items");
        } catch (Exception e) {
            logger.error("Error exporting to PDF", e);
            throw new IOException("Error exporting to PDF", e);
        }
    }

	public Optional<SalesPOComment> getCommentsByPoId(Long poId) {
		// TODO Auto-generated method stub
		return null;
	}
}
