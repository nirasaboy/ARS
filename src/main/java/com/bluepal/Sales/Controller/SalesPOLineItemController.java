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

import com.bluepal.Sales.Entity.SalesPOComment;
import com.bluepal.Sales.Entity.SalesPOLineItem;
import com.bluepal.Sales.Repository.SalesPOLineItemRepo;
import com.bluepal.Sales.Service.SalesPOLineItemService;

import jakarta.servlet.ServletOutputStream;

@RestController
@RequestMapping("/api/sales/po-line-items")
public class SalesPOLineItemController {

		    @Autowired
		    private SalesPOLineItemService lineItemService;
		    
		    @Autowired
		    private SalesPOLineItemRepo salesPOLineItemRepo;

		    // Get all line items
		    @GetMapping
		    public List<SalesPOLineItem> getAllLineItems(@RequestHeader("Authorization") String jwt) {
		        return lineItemService.getAllLineItems();
		    }

		    // Get a line item by ID
		    @GetMapping("/{id}")
		    public ResponseEntity<SalesPOLineItem> getLineItemById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
		        Optional<SalesPOLineItem> lineItem = lineItemService.getLineItemById(id);
		        return lineItem.map(ResponseEntity::ok)
		                       .orElse(ResponseEntity.notFound().build());
		    }

		    // Create a new line item
		    @PostMapping
		    public ResponseEntity<SalesPOLineItem> createLineItem(@RequestHeader("Authorization") String jwt,@RequestBody SalesPOLineItem lineItem) {
		        SalesPOLineItem newLineItem = lineItemService.createLineItem(lineItem);
		        return ResponseEntity.ok(newLineItem);
		    }

		    // Update a line item by ID
		    @PutMapping("/{id}")
		    public ResponseEntity<SalesPOLineItem> updateLineItem(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody SalesPOLineItem lineItem) {
		        Optional<SalesPOLineItem> updatedLineItem = lineItemService.updateLineItem(id, lineItem);
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
		    @PostMapping("/{poId}/comments")
		    public ResponseEntity<SalesPOComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long poId, @RequestBody String commentText) {
		        SalesPOComment comment = lineItemService.addComment(poId, commentText);
		        return ResponseEntity.ok(comment);
		    }

		    @GetMapping("/{poId}/comments")
		    public List<SalesPOComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long poId) {
		        return lineItemService.getCommentsByPOId(poId);
		    }
		    
		    @GetMapping("/export")
		    public ResponseEntity<byte[]> exportPOLineItemssToExcel(@RequestHeader("Authorization") String jwt) {
		        try {
		            byte[] excelFile = lineItemService.exportPOLineItemsToExcel();
		            return ResponseEntity.ok()
		                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
		                    .header("Content-Disposition", "attachment; filename=sales_po_line_items.xlsx")
		                    .body(excelFile);
		        } catch (Exception e) {
		            return ResponseEntity.internalServerError().build();
		        }
		    }
		    
		    @GetMapping("/pdf")
		    public void exportToPdf(@RequestHeader("Authorization") String jwt,ServletOutputStream servletOutputStream) throws Exception{
		    	List<SalesPOLineItem> salesPOLineItems = salesPOLineItemRepo.findAll();
		    	lineItemService.generatePDF(salesPOLineItems, servletOutputStream );
		    }

}
