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

import com.bluepal.Sales.Entity.SalesPayment;
import com.bluepal.Sales.Entity.SalesPaymentComment;
import com.bluepal.Sales.Repository.SalesPaymentRepo;
import com.bluepal.Sales.Service.SalesPaymentService;

import jakarta.servlet.ServletOutputStream;
@RestController
@RequestMapping("/api/sales/payment")
public class SalesPaymentController {

				    @Autowired
				    private SalesPaymentService paymentService;
				    
				    @Autowired
				    private SalesPaymentRepo salesPaymentRepo;

				    // Get all line items
				    @GetMapping
				    public List<SalesPayment> getAll(@RequestHeader("Authorization") String jwt) {
				        return paymentService.getAll();
				    }

				    // Get a line item by ID
				    @GetMapping("/{id}")
				    public ResponseEntity<SalesPayment> getById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
				        Optional<SalesPayment> payment = paymentService.getPaymentById(id);
				        return payment.map(ResponseEntity::ok)
				                       .orElse(ResponseEntity.notFound().build());
				    }

				    // Create a new line item
				    @PostMapping
				    public ResponseEntity<SalesPayment> createPayment(@RequestHeader("Authorization") String jwt,@RequestBody SalesPayment payment) {
				        SalesPayment payments = paymentService.createPayment(payment);
				        return ResponseEntity.ok(payments);
				    }

				    // Update a line item by ID
				    @PutMapping("/{id}")
				    public ResponseEntity<SalesPayment> updatePayment(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody SalesPayment payment) {
				        Optional<SalesPayment> updatedPayment = paymentService.updatePayment(id, payment);
				        return updatedPayment.map(ResponseEntity::ok)
				                              .orElse(ResponseEntity.notFound().build());
				    }

				    // Delete a line item by ID
				    @DeleteMapping("/{id}")
				    public ResponseEntity<Void> deletedPayment(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
				        if (paymentService.deletePayment(id)) {
				            return ResponseEntity.noContent().build();
				        }
				        return ResponseEntity.notFound().build();
				    }
				  //comment
				    @PostMapping("/{paymentId}/comments")
				    public ResponseEntity<SalesPaymentComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long paymentId, @RequestBody String commentText) {
				        SalesPaymentComment comment = paymentService.addComment(paymentId, commentText);
				        return ResponseEntity.ok(comment);
				    }

				    @GetMapping("/{paymentId}/comments")
				    public Optional<SalesPaymentComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long paymentId) {
				        return paymentService.getCommentsByPaymentId(paymentId);
				    }
				    
				    @GetMapping("/export")
				    public ResponseEntity<byte[]> exportPaymentToExcel(@RequestHeader("Authorization") String jwt) {
				        try {
				            byte[] excelFile = paymentService.exportToExcel();
				            return ResponseEntity.ok()
				                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
				                    .header("Content-Disposition", "attachment; filename=sales_payment.xlsx")
				                    .body(excelFile);
				        } catch (Exception e) {
				            return ResponseEntity.internalServerError().build();
				        }
				    }
				    
				    @GetMapping("/pdf")
				    public void exportToPdf(@RequestHeader("Authorization") String jwt,ServletOutputStream servletOutputStream) throws Exception{
				    	List<SalesPayment> salesPayments = salesPaymentRepo.findAll();
				    	paymentService.generatePDF(salesPayments, servletOutputStream );
				    }
}
