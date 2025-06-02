package com.bohemio.miniblogapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank(message = "사용자 아이디는 필수 입력 항목입니다.")
    @Size(min = 4, max = 20, message = "사용자 아이디는 4자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자 아이디는 알파벳, 숫자, 밑줄(_)만 사용할 수 있습니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이내로 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 50, message = "이름은 50자 이내로 입력해주세요.")
    private String name;

    private LocalDate birthdate;


    public SignUpRequestDto(String username, String password, String email, String nickname, String name) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.name = name;
        this.birthdate = birthdate;
    }
}
