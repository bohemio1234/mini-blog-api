package com.bohemio.miniblogapi.controller;

import com.bohemio.miniblogapi.dto.CommentCreateRequestDto;
import com.bohemio.miniblogapi.dto.CommentResponseDto;
import com.bohemio.miniblogapi.dto.CommentUpdateRequestDto;
import com.bohemio.miniblogapi.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId, @Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto, @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        CommentResponseDto createdComment = commentService.createComment( postId, commentCreateRequestDto, username );
        return ResponseEntity.status( HttpStatus.CREATED ).body( createdComment );

    }

    @GetMapping
    public ResponseEntity<Page<CommentResponseDto>> getCommentsForPost(@PathVariable Long postId, @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CommentResponseDto> commentPage = commentService.getCommentsByPostId( postId, pageable );
        return ResponseEntity.ok( commentPage );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequestDto commentUpdateRequestDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        CommentResponseDto updatedComment = commentService.updateComment( postId, commentId, commentUpdateRequestDto, username );
        return ResponseEntity.ok( updatedComment );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        commentService.deleteComment( postId, commentId, username );
        return ResponseEntity.noContent().build();
    }

}
