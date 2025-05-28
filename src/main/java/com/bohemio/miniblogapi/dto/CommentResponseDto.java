package com.bohemio.miniblogapi.dto;

import com.bohemio.miniblogapi.entity.Comment;
import com.bohemio.miniblogapi.entity.Post;
import com.bohemio.miniblogapi.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private String authorNickname;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();

        User author = comment.getUser();
        this.authorNickname = (author != null) ? author.getNickname() : "알 수 없는 작성자";

        Post post = comment.getPost();
        this.postId = (post != null) ? post.getId() : null;

        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();


    }
}
