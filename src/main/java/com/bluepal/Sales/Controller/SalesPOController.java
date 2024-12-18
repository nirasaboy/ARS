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

import com.bluepal.Sales.Entity.SalesPO;
import com.bluepal.Sales.Entity.SalesPOComment;
import com.bluepal.Sales.Repository.SalesPORepo;
import com.bluepal.Sales.Service.SalesPOService;

import jakarta.servlet.ServletOutputStream;
@RestController
@RequestMapping("/api/sales/po")
public class SalesPOController {

			    @Autowired
			    private SalesPOService poService;
			    
			    @Autowired
			    private SalesPORepo salesPORepo;

			    // Get all line items
			    @GetMapping
			    public List<SalesPO> getAll(@RequestHeader("Authorization") String jwt) {
			        return poService.getAll();
			    }

			    // Get a line item by ID
			    @GetMapping("/{id}")
			    public ResponseEntity<SalesPO> getById(@RequestHeader("Authorization") String jwt,@PathVariable Long id) {
			        Optional<SalesPO> po = poService.getPOById(id);
			        return po.map(ResponseEntity::ok)
			                       .orElse(ResponseEntity.notFound().build());
			    }

			    // Create a new line item
			    @PostMapping
			    public ResponseEntity<SalesPO> createPO(@RequestHeader("Authorization") String jwt,@RequestBody SalesPO po) {
			        SalesPO pos = poService.createPO(po);
			        return ResponseEntity.ok(pos);
			    }

			    // Update a line item by ID
			    @PutMapping("/{id}")
			    public ResponseEntity<SalesPO> updatePO(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @RequestBody SalesPO po) {
			        Optional<SalesPO> updatedPo = poService.updatePO(id, po);
			        return updatedPo.map(ResponseEntity::ok)
			                              .orElse(ResponseEntity.notFound().build());
			    }

			    // Delete a line item by ID
			    @DeleteMapping("/{id}")
			    public ResponseEntity<Void> deletePO(@RequestHeader("Authorization") String jwt, @PathVariable Long id) {
			        if (poService.deletePO(id)) {
			            return ResponseEntity.noContent().build();
			        }
			        return ResponseEntity.notFound().build();
			    }
			  //comment
			    @PostMapping("/{poId}/comments")
			    public ResponseEntity<SalesPOComment> addComment(@RequestHeader("Authorization") String jwt,@PathVariable Long poId, @RequestBody String commentText) {
			    	SalesPOComment comment = poService.addComment(poId, commentText);
			        return ResponseEntity.ok(comment);
			    }

			    @GetMapping("/{poId}/comments")
			    public Optional<SalesPOComment> getComments(@RequestHeader("Authorization") String jwt,@PathVariable Long poId) {
			        return poService.getCommentsByPOId(poId);
			    }
			    
			    @GetMapping("/export")
			    public ResponseEntity<byte[]> exportPOsToExcel(@RequestHeader("Authorization") String jwt) {
			        try {
			            byte[] excelFile = poService.exportToExcel();
			            return ResponseEntity.ok()
			                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
			                    .header("Content-Disposition", "attachment; filename=sales_po.xlsx")
			                    .body(excelFile);
			        } catch (Exception e) {
			            return ResponseEntity.internalServerError().build();
			        }
			    }
			    
			    @GetMapping("/pdf")
			    public void exportToPdf(@RequestHeader("Authorization") String jwt,ServletOutputStream servletOutputStream) throws Exception{
			    	List<SalesPO> salesPOs = salesPORepo.findAll();
			    	poService.generatePDF(salesPOs, servletOutputStream );
			    }
}
