package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesInvoiceComment;

public interface SalesInvoiceCommentRepo extends JpaRepository<SalesInvoiceComment, Long>{

}
