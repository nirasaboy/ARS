package com.bluepal.Sales.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.ClientComment;

public interface ClientCommentRepo extends JpaRepository<ClientComment, Long> {

	List<ClientComment> findByClientId(Long clientId);
}
