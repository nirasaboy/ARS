package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.Client;

public interface ClientRepo extends JpaRepository<Client, Long> {

	
}
