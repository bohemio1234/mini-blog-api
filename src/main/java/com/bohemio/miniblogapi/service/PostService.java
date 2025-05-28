package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.dto.PostCreateRequestDto;
import com.bohemio.miniblogapi.dto.PostResponseDto;
import com.bohemio.miniblogapi.dto.PostUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostResponseDto createPost(PostCreateRequestDto requestDto, String username);

    Page<PostResponseDto> getAllPosts(Pageable pageable);

    PostResponseDto getPostById(Long postId);

    PostResponseDto updatePost(Long postId, PostUpdateRequestDto requestDto, String username);

    void deletePost(Long postId, String username);

}
