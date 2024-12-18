package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesPOLineItem;

public interface SalesPOLineItemRepo extends JpaRepository<SalesPOLineItem, Long>{

}
