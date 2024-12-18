package com.bluepal.Sales.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.SalesPOComment;

public interface SalesPOCommentRepo extends JpaRepository<SalesPOComment, Long>{

	List<SalesPOComment> findBySalesPOId(Long poId);

}
