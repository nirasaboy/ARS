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

import com.bluepal.Sales.Entity.SalesQuote;
import com.bluepal.Sales.Entity.SalesQuoteComment;
import com.bluepal.Sales.Repository.SalesQuoteRepo;
import com.bluepal.Sales.Service.SalesQuoteService;

import jakarta.servlet.ServletOutputStream;
@RestController
@RequestMapping("/api/sales/quote")
public class SalesQuoteController {

		    @Autowired
		    private SalesQuoteService quoteService;
		    
		    @Autowired
		    private SalesQuoteRepo salesQuoteRepo;

		    // Get all line items
		    @GetMapping
		    public List<SalesQuote> getAll(@RequestHeader("Authorization") String jwt) {
		        return quoteService.getAll();
		    }

		    // Get a line item by ID
		    @GetMapping("/{id}")
		    public ResponseEntity<SalesQuote> getQuoteById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
		        Optional<SalesQuote> quote = quoteService.getQuoteById(id);
		        return quote.map(ResponseEntity::ok)
		                       .orElse(ResponseEntity.notFound().build());
		    }

		    // Create a new line item
		    @PostMapping
		    public ResponseEntity<SalesQuote> creatingQuote(@RequestHeader("Authorization") String jwt,@RequestBody SalesQuote quote) {
		        SalesQuote quotes = quoteService.createQuote(quote);
		        return ResponseEntity.ok(quotes);
		    }

		    // Update a line item by ID
		    @PutMapping("/{id}")
		    public ResponseEntity<SalesQuote> updatedQuote(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody SalesQuote quote) {
		        Optional<SalesQuote> updatedQuote = quoteService.updateLineItem(id, quote);
		        return updatedQuote.map(ResponseEntity::ok)
		                              .orElse(ResponseEntity.notFound().build());
		    }

		    // Delete a line item by ID
		    @DeleteMapping("/{id}")
		    public ResponseEntity<Void> deletedQuote(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
		        if (quoteService.deleteQuote(id)) {
		            return ResponseEntity.noContent().build();
		        }
		        return ResponseEntity.notFound().build();
		    }
		    //comment
		    @PostMapping("/{quoteId}/comments")
		    public ResponseEntity<SalesQuoteComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long quoteId, @RequestBody String commentText) {
		        SalesQuoteComment comment = quoteService.addComment(quoteId, commentText);
		        return ResponseEntity.ok(comment);
		    }

		    @GetMapping("/{quoteId}/comments")
		    public Optional<SalesQuoteComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long quoteId) {
		        return quoteService.getCommentsByPOId(quoteId);
		    }
		    
		    @GetMapping("/export")
		    public ResponseEntity<byte[]> exportQuotesToExcel(@RequestHeader("Authorization") String jwt) {
		        try {
		            byte[] excelFile = quoteService.exportToExcel();
		            return ResponseEntity.ok()
		                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
		                    .header("Content-Disposition", "attachment; filename=sales_quote.xlsx")
		                    .body(excelFile);
		        } catch (Exception e) {
		            return ResponseEntity.internalServerError().build();
		        }
		    }
		    
		    @GetMapping("/pdf")
		    public void exportToPdf(@RequestHeader("Authorization") String jwt,ServletOutputStream servletOutputStream) throws Exception{
		    	List<SalesQuote> salesQuotes = salesQuoteRepo.findAll();
		    	quoteService.generatePDF(salesQuotes, servletOutputStream );
		    }
}
