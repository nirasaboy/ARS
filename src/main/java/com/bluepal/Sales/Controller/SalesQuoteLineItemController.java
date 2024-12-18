package com.bluepal.Sales.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bluepal.Sales.Entity.SalesQuoteComment;
import com.bluepal.Sales.Entity.SalesQuoteLineItem;
import com.bluepal.Sales.Repository.SalesQuoteLineItemRepo;
import com.bluepal.Sales.Service.SalesQuoteLineItemService;

import jakarta.servlet.ServletOutputStream;

@RestController
@RequestMapping("/api/sales/quote-line-items")
public class SalesQuoteLineItemController {

	    @Autowired
	    private SalesQuoteLineItemService lineItemService;
	    
	    @Autowired
	    private SalesQuoteLineItemRepo salesRFQLineItemRepo;

	    // Get all line items
	    @GetMapping
	    public List<SalesQuoteLineItem> getAllLineItems(@RequestHeader("Authorization") String jwt) {
	        return lineItemService.getAllLineItems();
	    }

	    // Get a line item by ID
	    @GetMapping("/{id}")
	    public ResponseEntity<SalesQuoteLineItem> getLineItemById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
	        Optional<SalesQuoteLineItem> lineItem = lineItemService.getLineItemById(id);
	        return lineItem.map(ResponseEntity::ok)
	                       .orElse(ResponseEntity.notFound().build());
	    }

	    // Create a new line item
	    @PostMapping
	    public ResponseEntity<SalesQuoteLineItem> createLineItem(@RequestHeader("Authorization") String jwt,@RequestBody SalesQuoteLineItem lineItem) {
	        SalesQuoteLineItem newLineItem = lineItemService.createLineItem(lineItem);
	        return ResponseEntity.ok(newLineItem);
	    }

	    // Update a line item by ID
	    @PutMapping("/{id}")
	    public ResponseEntity<SalesQuoteLineItem> updateLineItem(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody SalesQuoteLineItem lineItem) {
	        Optional<SalesQuoteLineItem> updatedLineItem = lineItemService.updateLineItem(id, lineItem);
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
	    
	    //comment
	    @PostMapping("/{quoteId}/comments")
	    public ResponseEntity<SalesQuoteComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long quoteId, @RequestBody String commentText) {
	    	SalesQuoteComment comment = lineItemService.addComment(quoteId, commentText);
	        return ResponseEntity.ok(comment);
	    }

	    @GetMapping("/{quoteId}/comments")
	    public List<SalesQuoteComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long quoteId) {
	        return lineItemService.getCommentsByQuoteId(quoteId);
	    }
	    
	    @GetMapping("/export")
	    public ResponseEntity<byte[]> exportQuotesToExcel(@RequestHeader("Authorization") String jwt) {
	        try {
	            byte[] excelFile = lineItemService.exportQuotesToExcel();
	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                    .header("Content-Disposition", "attachment; filename=sales_quote_line_items.xlsx")
	                    .body(excelFile);
	        } catch (Exception e) {
	            return ResponseEntity.internalServerError().build();
	        }
	    }
	    
	    @GetMapping("/pdf")
	    public void exportToPdf(@RequestHeader("Authorization") String jwt,ServletOutputStream servletOutputStream) throws Exception{
	    	List<SalesQuoteLineItem> salesQuoteLineItems = salesRFQLineItemRepo.findAll();
	    	lineItemService.generatePDF(salesQuoteLineItems, servletOutputStream );
	    }
}
