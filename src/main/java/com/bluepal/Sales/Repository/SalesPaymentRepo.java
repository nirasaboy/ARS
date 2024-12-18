package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesPayment;

public interface SalesPaymentRepo extends JpaRepository<SalesPayment, Long>{

}
