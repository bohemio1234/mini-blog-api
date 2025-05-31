package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.dto.CommentCreateRequestDto;
import com.bohemio.miniblogapi.dto.CommentResponseDto;
import com.bohemio.miniblogapi.dto.CommentUpdateRequestDto;
import com.bohemio.miniblogapi.entity.Comment;
import com.bohemio.miniblogapi.entity.Post;
import com.bohemio.miniblogapi.entity.User;
import com.bohemio.miniblogapi.repository.CommentRepository;
import com.bohemio.miniblogapi.repository.PostRepository;
import com.bohemio.miniblogapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Page<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {

        if (!postRepository.existsById( postId )) {
            throw new EntityNotFoundException( "댓글을 조회하려는 게시글을 찾을 수 없습니다: " + postId );
        }

        Page<Comment> commentPage = commentRepository.findByPost_Id( postId, pageable );
        Page<CommentResponseDto> responseDtoPage = commentPage.map( comment -> new CommentResponseDto( comment ) );
        return responseDtoPage;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @commentRepository.findById(#commentId).orElse(null)?.user.username == authentication.name")
    public CommentResponseDto updateComment(Long postId, Long commentId, CommentUpdateRequestDto commentUpdateRequestDto, String username) {

        Comment commentToUpdate = commentRepository.findById( commentId ).orElseThrow(
                () -> new EntityNotFoundException( "수정하려는 댓글을 찾을 수 없습니다: " + commentId ) );


        if (!commentToUpdate.getPost().getId().equals( postId )) {
            throw new IllegalStateException( "해당 게시글에 존재하지 않는 댓글입니다. (댓글 ID: " + commentId + ", 게시글 ID: " + postId + ")" );
        }



        commentToUpdate.updateContent( commentUpdateRequestDto.getContent() );


        return new CommentResponseDto( commentToUpdate );


    }

    @Override
    public void deleteComment(Long postId, Long commentId, String username) {

        Comment commentToDelete = commentRepository.findById( commentId ).orElseThrow(
                () -> new EntityNotFoundException("수정하려는 댓글을 찾을 수 없습니다: " + commentId)
        );

        if (!commentToDelete.getPost().getId().equals( postId )) {
            throw new IllegalStateException( "해당 게시글에 존재하지 않는 댓글입니다. (댓글 ID: " + commentId + ", 게시글 ID: " + postId + ")" );
        }

        if (!commentToDelete.getUser().getUsername().equals( username )) {
            throw new AccessDeniedException( "댓글을 수정할 권한이 없습니다." );
        }

        commentRepository.delete( commentToDelete );

    }


}
