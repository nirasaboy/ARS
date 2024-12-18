package com.bluepal.Sales.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesQuoteComment;

public interface SalesQuoteCommentRepo extends JpaRepository<SalesQuoteComment, Long>{

	List<SalesQuoteComment> findBySalesQuoteId(Long quoteId);

}
