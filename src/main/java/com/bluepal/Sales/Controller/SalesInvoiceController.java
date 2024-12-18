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

import com.bluepal.Sales.Entity.SalesInvoice;
import com.bluepal.Sales.Entity.SalesInvoiceComment;
import com.bluepal.Sales.Repository.SalesInvoiceRepo;
import com.bluepal.Sales.Service.SalesInvoiceService;

import jakarta.servlet.ServletOutputStream;

@RestController
@RequestMapping("/api/sales/invoice")
public class SalesInvoiceController {

				    @Autowired
				    private SalesInvoiceService invoiceService;
				    
				    @Autowired
				    private SalesInvoiceRepo salesInvoiceRepo;

				    // Get all line items
				    @GetMapping
				    public List<SalesInvoice> getAll(@RequestHeader("Authorization") String jwt) {
				        return invoiceService.getAll();
				    }

				    // Get a line item by ID
				    @GetMapping("/{id}")
				    public ResponseEntity<SalesInvoice> getById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
				        Optional<SalesInvoice> invoice = invoiceService.getInvoiceById(id);
				        return invoice.map(ResponseEntity::ok)
				                       .orElse(ResponseEntity.notFound().build());
				    }

				    // Create a new line item
				    @PostMapping
				    public ResponseEntity<SalesInvoice> createInvoice(@RequestHeader("Authorization") String jwt, @RequestBody SalesInvoice invoice) {
				        SalesInvoice invoices = invoiceService.createInvoice(invoice);
				        return ResponseEntity.ok(invoices);
				    }

				    // Update a line item by ID
				    @PutMapping("/{id}")
				    public ResponseEntity<SalesInvoice> updateInvoice(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody SalesInvoice invoice) {
				        Optional<SalesInvoice> updatedInvoice = invoiceService.updateInvoice(id, invoice);
				        return updatedInvoice.map(ResponseEntity::ok)
				                              .orElse(ResponseEntity.notFound().build());
				    }

				    // Delete a line item by ID
				    @DeleteMapping("/{id}")
				    public ResponseEntity<Void> deleteInvoice(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
				        if (invoiceService.deleteInvoice(id)) {
				            return ResponseEntity.noContent().build();
				        }
				        return ResponseEntity.notFound().build();
				    }
				    //comment section
				    
				    @PostMapping("/{invoiceId}/comments")
				    public ResponseEntity<SalesInvoiceComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long invoiceId, @RequestBody String commentText) {
				        SalesInvoiceComment comment = invoiceService.addComment(invoiceId, commentText);
				        return ResponseEntity.ok(comment);
				    }

				    @GetMapping("/{invoiceId}/comments")
				    public Optional<SalesInvoiceComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long invoiceId) {
				        return invoiceService.getCommentsByInvoiceId(invoiceId);
				    }
				    
				    @GetMapping("/export")
				    public ResponseEntity<byte[]> exportInvoiceToExcel(@RequestHeader("Authorization") String jwt) {
				        try {
				            byte[] excelFile = invoiceService.exportToExcel();
				            return ResponseEntity.ok()
				                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
				                    .header("Content-Disposition", "attachment; filename=sales_invoice.xlsx")
				                    .body(excelFile);
				        } catch (Exception e) {
				            return ResponseEntity.internalServerError().build();
				        }
				    }
				    
				    @GetMapping("/pdf")
				    public void exportToPdf( @RequestHeader("Authorization") String jwt, ServletOutputStream servletOutputStream) throws Exception{
				    	List<SalesInvoice> salesInvoices = salesInvoiceRepo.findAll();
				    	invoiceService.generatePDF(salesInvoices, servletOutputStream );
				    }
}
