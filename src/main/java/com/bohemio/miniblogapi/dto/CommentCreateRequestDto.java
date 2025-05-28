package com.bohemio.miniblogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateRequestDto {

    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    @Size(max = 200, message = "댓글 내용은 200자 이내로 입력해주세요.")
    private String content;

    public CommentCreateRequestDto(String content) {
        this.content = content;
    }
}
