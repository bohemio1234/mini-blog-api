package com.bohemio.miniblogapi.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequestDto {

    @Size(max = 255, message = "제목은 255자 이내로 입력해주세요.")
    private String title;

    private String content;

    @Builder
    public PostUpdateRequestDto(String title, String content){
        this.title = title;
        this.content = content;
    }

}