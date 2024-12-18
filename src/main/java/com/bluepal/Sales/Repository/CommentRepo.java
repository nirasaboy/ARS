package com.bluepal.Sales.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluepal.Sales.Entity.Comment;

public interface CommentRepo extends JpaRepository<Comment, Long>{


}
