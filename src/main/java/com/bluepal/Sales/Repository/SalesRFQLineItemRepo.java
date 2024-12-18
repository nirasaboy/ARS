package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesRFQLineItem;

public interface SalesRFQLineItemRepo extends JpaRepository<SalesRFQLineItem, Long> {

}
