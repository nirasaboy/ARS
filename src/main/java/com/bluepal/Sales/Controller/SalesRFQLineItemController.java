package com.bluepal.Sales.Controller;

import com.bluepal.Sales.Entity.SalesRFQComment;
import com.bluepal.Sales.Entity.SalesRFQLineItem;
import com.bluepal.Sales.Repository.SalesRFQLineItemRepo;
import com.bluepal.Sales.Service.SalesRFQLineItemService;

import jakarta.servlet.ServletOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sales/rfq-line-items")
public class SalesRFQLineItemController {

    @Autowired
    private SalesRFQLineItemService lineItemService;
    
    @Autowired
    private SalesRFQLineItemRepo salesRFQLineItemRepo;

    // Get all line items
    @GetMapping
    public List<SalesRFQLineItem> getAllLineItems(@RequestHeader("Authorization") String jwt) {
        return lineItemService.getAllLineItems();
    }

    // Get a line item by ID
    @GetMapping("/{id}")
    public ResponseEntity<SalesRFQLineItem> getLineItemById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
        Optional<SalesRFQLineItem> lineItem = lineItemService.getLineItemById(id);
        return lineItem.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    // Create a new line item
    @PostMapping
    public ResponseEntity<SalesRFQLineItem> createLineItem(@RequestHeader("Authorization") String jwt,@RequestBody SalesRFQLineItem lineItem) {
        SalesRFQLineItem newLineItem = lineItemService.createLineItem(lineItem);
        return ResponseEntity.ok(newLineItem);
    }

    // Update a line item by ID
    @PutMapping("/{id}")
    public ResponseEntity<SalesRFQLineItem> updateLineItem(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody SalesRFQLineItem lineItem) {
        Optional<SalesRFQLineItem> updatedLineItem = lineItemService.updateLineItem(id, lineItem);
        return updatedLineItem.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
    }

    // Delete a line item by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLineItem(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
        if (lineItemService.deleteLineItem(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{rfqId}/comments")
    public ResponseEntity<SalesRFQComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long rfqId, @RequestBody String commentText) {
        SalesRFQComment comment = lineItemService.addComment(rfqId, commentText);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{rfqId}/comments")
    public List<SalesRFQComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long rfqId) {
        return lineItemService.getCommentsByRFQId(rfqId);
    }
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportRFQLineItemsToExcel(@RequestHeader("Authorization") String jwt) {
        try {
            byte[] excelFile = lineItemService.exportRFQsToExcel();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=sales_rfq_line_items.xlsx")
                    .body(excelFile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/pdf")
    public void exportToPdf(@RequestHeader("Authorization") String jwt,ServletOutputStream servletOutputStream) throws Exception{
    	List<SalesRFQLineItem> salesRFQLineItems = salesRFQLineItemRepo.findAll();
    	lineItemService.generatePDF(salesRFQLineItems, servletOutputStream );
    }
    
}
