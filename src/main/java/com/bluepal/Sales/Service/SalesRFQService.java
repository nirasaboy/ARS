package com.bluepal.Sales.Service;

import com.bluepal.Sales.Entity.SalesRFQ;
import com.bluepal.Sales.Entity.SalesRFQComment;
import com.bluepal.Sales.Repository.SalesRFQCommentRepo;
import com.bluepal.Sales.Repository.SalesRFQRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesRFQService {

    private static final Logger logger = LoggerFactory.getLogger(SalesRFQService.class);
    
    @Autowired
    private SalesRFQRepo salesRFQRepo;
    
    @Autowired
    private SalesRFQCommentRepo commentRepo;

    @Cacheable(value = "rfqs")
    public List<SalesRFQ> getAllSalesRFQs() {
        logger.info("Fetching all Sales RFQs.");
        return salesRFQRepo.findAll();
    }

    @Cacheable(value = "rfqs", key = "#id")
    public Optional<SalesRFQ> getSalesRFQById(Long id) {
        logger.info("Fetching Sales RFQ with ID: {}", id);
        return salesRFQRepo.findById(id);
    }

    @CachePut(value = "rfqs", key = "#result.id")
    public SalesRFQ createSalesRFQ(SalesRFQ salesRFQ) {
        logger.info("Creating new Sales RFQ: {}", salesRFQ);
        return salesRFQRepo.save(salesRFQ);
    }

    @CachePut(value = "rfqs", key = "#id")
    public Optional<SalesRFQ> updateSalesRFQ(Long id, SalesRFQ updatedRFQ) {
        logger.info("Updating Sales RFQ with ID: {}", id);
        return salesRFQRepo.findById(id).map(rfq -> {
            rfq.setClientName(updatedRFQ.getClientName());
            rfq.setDate(updatedRFQ.getDate());
            rfq.setDeliveryBy(updatedRFQ.getDeliveryBy());
            rfq.setStatus(updatedRFQ.getStatus());
            rfq.getLineItems().clear();
            rfq.getLineItems().addAll(updatedRFQ.getLineItems());
            return salesRFQRepo.save(rfq);
        });
    }

    @CacheEvict(value = "rfqs", key = "#id")
    public boolean deleteSalesRFQ(Long id) {
        logger.info("Deleting Sales RFQ with ID: {}", id);
        if (salesRFQRepo.existsById(id)) {
            salesRFQRepo.deleteById(id);
            return true;
        }
        logger.warn("Sales RFQ with ID: {} not found for deletion.", id);
        return false;
    }
    
    // Comment service part
    public SalesRFQComment addComment(Long rfqId, String commentText) {
        logger.info("Adding comment to Sales RFQ with ID: {}", rfqId);
        Optional<SalesRFQ> salesOpt = salesRFQRepo.findById(rfqId);
        if (salesOpt.isPresent()) {
            SalesRFQ salesRFQ = salesOpt.get();
            SalesRFQComment comment = new SalesRFQComment();
            comment.setSalesRFQ(salesRFQ);
            comment.setCommentedAt(LocalDateTime.now());
            comment.setCommentText(commentText);
            return commentRepo.save(comment);
        } else {
            logger.error("Sales RFQ not found with ID: {}", rfqId);
            throw new RuntimeException("Sales RFQ not found with id: " + rfqId);
        }
    }

    public List<SalesRFQComment> getCommentsByRFQId(Long rfqId) {
        logger.info("Fetching comments for Sales RFQ with ID: {}", rfqId);
        return commentRepo.findBySalesRFQId(rfqId);
    }
}
