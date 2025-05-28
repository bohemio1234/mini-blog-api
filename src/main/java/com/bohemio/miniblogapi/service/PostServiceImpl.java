package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.dto.PostCreateRequestDto;
import com.bohemio.miniblogapi.dto.PostResponseDto;
import com.bohemio.miniblogapi.dto.PostUpdateRequestDto;
import com.bohemio.miniblogapi.entity.Post;
import com.bohemio.miniblogapi.entity.User;
import com.bohemio.miniblogapi.repository.PostRepository;
import com.bohemio.miniblogapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PostResponseDto createPost(PostCreateRequestDto requestDto, String username) {
        User author = userRepository.findByUsername( username ).orElseThrow( () -> new UsernameNotFoundException( "사용자를 찾을 수 없어요" + username ) );
        Post newPost = Post.builder().title( requestDto.getTitle() ).content( requestDto.getContent() ).user( author ).build();
        Post savedPost = postRepository.save( newPost );
        return new PostResponseDto( savedPost );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAll( pageable );

        Page<PostResponseDto> responseDtoPage = postsPage.map( post -> new PostResponseDto( post ) );

        return responseDtoPage;
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findById( postId ).orElseThrow( () -> new EntityNotFoundException( "해당 ID의 게시글을 찾을 수 없어요" + postId ) );
        return new PostResponseDto( post );
    }

    @Override
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto requestDto, String username) {

        Post postToUpdate = postRepository.findById( postId ).orElseThrow( () -> new EntityNotFoundException( "해당 ID의 게시글을 찾을 수 없습니다: " + postId ) );

        User requestUser = userRepository.findByUsername( username ).orElseThrow( () -> new UsernameNotFoundException( "사용자를 찾을 수 없습니다: " + username ) );

        if (!postToUpdate.getUser().getUsername().equals( username )) {
            throw new IllegalStateException( "게시물 수정 권한 없음" );
        }

        if (requestDto.getTitle() != null) {
            postToUpdate.updateTitle( requestDto.getTitle() );
        }
        if (requestDto.getContent() != null) {
            postToUpdate.updateContent( requestDto.getContent() );
        }

        return new PostResponseDto( postToUpdate );
    }

    @Override
    public void deletePost(Long postId, String username) {
        // TODO: 게시글 삭제 로직 구현!
        Post postToDelete = postRepository.findById( postId ).orElseThrow( () -> new EntityNotFoundException( "해당 ID의 게시글을 찾을수 없어요" + postId ) );

        User requestUser = userRepository.findByUsername( username ).orElseThrow( () -> new UsernameNotFoundException( "사용자를 찾을 수 없습니다: " + username ) );

        if (!postToDelete.getUser().getUsername().equals( username )) {
            throw new IllegalStateException( "게시글을 삭제할 권한이 없습니다." );
        }

        postRepository.delete( postToDelete );
    }
}
