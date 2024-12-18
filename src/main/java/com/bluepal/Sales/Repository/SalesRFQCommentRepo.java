package com.bluepal.Sales.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesRFQComment;

public interface SalesRFQCommentRepo extends JpaRepository<SalesRFQComment, Long>{

	List<SalesRFQComment> findBySalesRFQId(Long rfqId);
}
