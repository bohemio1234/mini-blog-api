package com.bohemio.miniblogapi.dto;

import com.bohemio.miniblogapi.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {

        private Long id;
        private String title;
        private String content;
        private String authorNickname;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int viewCount;

        public PostResponseDto(Post post) {
                this.id = post.getId();
                this.title = post.getTitle();
                this.content = post.getContent();
                this.authorNickname = (post.getUser() != null) ? post.getUser().getNickname() : "알 수 없는 사용자"; // User가 null일 수도 있으니 안전하게!
                this.createdAt = post.getCreatedAt();
                this.updatedAt = post.getUpdatedAt();
                this.viewCount = post.getViewCount();
        }
}
