package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.dto.CommentCreateRequestDto;
import com.bohemio.miniblogapi.dto.CommentResponseDto;
import com.bohemio.miniblogapi.dto.CommentUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponseDto createComment(Long postId, CommentCreateRequestDto commentCreateRequestDto, String username);
    Page<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable);
//    CommentResponseDto updateComment(Long postId, Long commentId, CommentUpdateRequestDto commentUpdateRequestDto, String username);
//    void deleteComment(Long postId, Long commentId, String username);

}
