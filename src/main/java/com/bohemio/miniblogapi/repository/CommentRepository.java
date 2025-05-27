package com.bohemio.miniblogapi.repository;

import com.bohemio.miniblogapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
