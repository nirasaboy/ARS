package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesQuote;

public interface SalesQuoteRepo extends JpaRepository<SalesQuote, Long> {

}
