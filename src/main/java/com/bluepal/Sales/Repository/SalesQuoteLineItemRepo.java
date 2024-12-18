package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesQuoteLineItem;

public interface SalesQuoteLineItemRepo extends JpaRepository<SalesQuoteLineItem, Long>{

}
