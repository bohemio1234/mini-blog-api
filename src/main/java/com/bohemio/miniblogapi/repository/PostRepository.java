package com.bohemio.miniblogapi.repository;

import com.bohemio.miniblogapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
