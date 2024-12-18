package com.bluepal.Sales.Controller;

import com.bluepal.Sales.Entity.SalesRFQ;
import com.bluepal.Sales.Entity.SalesRFQComment;
import com.bluepal.Sales.Repository.SalesRFQRepo;
import com.bluepal.Sales.Service.SalesRFQExportService;
import com.bluepal.Sales.Service.SalesRFQPdfService;
import com.bluepal.Sales.Service.SalesRFQService;

import jakarta.servlet.ServletOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sales/rfqs")
public class SalesRFQController {

    @Autowired
    private SalesRFQService salesRFQService;
    
    @Autowired
    private SalesRFQExportService salesRFQExportService;
    
    @Autowired
    private SalesRFQPdfService salesRFQPdfService;
    

    @Autowired
    private SalesRFQRepo salesRFQRepo;

    @GetMapping
    public List<SalesRFQ> getAllSalesRFQs(@RequestHeader("Authorization") String jwt) {
        return salesRFQService.getAllSalesRFQs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesRFQ> getSalesRFQById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
        Optional<SalesRFQ> rfq = salesRFQService.getSalesRFQById(id);
        return rfq.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SalesRFQ> createSalesRFQ(@RequestHeader("Authorization") String jwt,@RequestBody SalesRFQ salesRFQ) {
        SalesRFQ newRFQ = salesRFQService.createSalesRFQ(salesRFQ);
        return ResponseEntity.ok(newRFQ);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesRFQ> updateSalesRFQ(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody SalesRFQ salesRFQ) {
        Optional<SalesRFQ> updatedRFQ = salesRFQService.updateSalesRFQ(id, salesRFQ);
        return updatedRFQ.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesRFQ(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
        if (salesRFQService.deleteSalesRFQ(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    //comment section
    
    @PostMapping("/{rfqId}/comments")
    public ResponseEntity<SalesRFQComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long rfqId, @RequestBody String commentText) {
        SalesRFQComment comment = salesRFQService.addComment(rfqId, commentText);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{rfqId}/comments")
    public List<SalesRFQComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long rfqId) {
        return salesRFQService.getCommentsByRFQId(rfqId);
    }
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportRFQsToExcel(@RequestHeader("Authorization") String jwt) {
        try {
            byte[] excelFile = salesRFQExportService.exportRFQsToExcel();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=sales_rfqs.xlsx")
                    .body(excelFile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/pdf")
    public void exportToPdf(@RequestHeader("Authorization") String jwt,ServletOutputStream servletOutputStream) throws Exception{
    	List<SalesRFQ> salesRFQs = salesRFQRepo.findAll();
    	salesRFQPdfService.generatePDF(salesRFQs, servletOutputStream );
    }
    
}
