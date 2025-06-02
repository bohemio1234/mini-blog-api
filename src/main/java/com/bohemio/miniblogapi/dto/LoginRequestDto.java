package com.bohemio.miniblogapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class LoginRequestDto {

    @NotBlank(message = "사용자 아이디는 필수 입력 항목입니다.")
    private String username;
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
