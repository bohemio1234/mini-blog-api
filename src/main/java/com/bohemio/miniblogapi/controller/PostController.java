package com.bohemio.miniblogapi.controller;

import com.bohemio.miniblogapi.dto.PostCreateRequestDto;
import com.bohemio.miniblogapi.dto.PostResponseDto;
import com.bohemio.miniblogapi.dto.PostUpdateRequestDto;
import com.bohemio.miniblogapi.entity.Post;
import com.bohemio.miniblogapi.service.PostService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostCreateRequestDto postCreateRequestDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        PostResponseDto createdPost = postService.createPost(postCreateRequestDto, username);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPost.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdPost);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<PostResponseDto> postsPage = postService.getAllPosts(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(postsPage);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId){
        PostResponseDto post = postService.getPostById(postId);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequestDto postUpdateRequestDto, @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        PostResponseDto updatedPost = postService.updatePost(postId, postUpdateRequestDto, username);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        postService.deletePost(postId, username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
