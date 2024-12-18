package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesPaymentComment;

public interface SalesPaymentCommentRepo extends JpaRepository<SalesPaymentComment, Long>{

}
