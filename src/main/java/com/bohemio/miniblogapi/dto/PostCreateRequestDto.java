package com.bohemio.miniblogapi.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequestDto {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 255, message = "제목은 255자 이내로 작성해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;

    public PostCreateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }


}
