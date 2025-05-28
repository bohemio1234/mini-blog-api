package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.dto.CommentCreateRequestDto;
import com.bohemio.miniblogapi.dto.CommentResponseDto;
import com.bohemio.miniblogapi.dto.PostResponseDto;
import com.bohemio.miniblogapi.entity.Comment;
import com.bohemio.miniblogapi.entity.Post;
import com.bohemio.miniblogapi.entity.User;
import com.bohemio.miniblogapi.repository.CommentRepository;
import com.bohemio.miniblogapi.repository.PostRepository;
import com.bohemio.miniblogapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public CommentResponseDto createComment(Long postId, CommentCreateRequestDto requestDto, String username) {
        User commentAuthor = userRepository.findByUsername( username )
                .orElseThrow( () -> new UsernameNotFoundException( "사용자를 찾을 수 없어요" + username ) );

        Post target = postRepository.findById( postId ).orElseThrow(
                () -> new EntityNotFoundException( "해당 ID의 게시글을 찾을 수 없습니다: " + postId )
        );

        Comment newComment = Comment.builder()
                .content( requestDto.getContent() )
                .post( target )
                .user( commentAuthor )
                .build();

        Comment savedComment = commentRepository.save( newComment );

        return new CommentResponseDto( savedComment );


    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable){

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("댓글을 조회하려는 게시글을 찾을 수 없습니다: " + postId);
        }

        Page<Comment> commentPage = commentRepository.findByPost_Id(postId, pageable);
        Page<CommentResponseDto> responseDtoPage = commentPage.map( comment -> new CommentResponseDto( comment ) );
        return responseDtoPage;
    }

}
