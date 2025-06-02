package com.bohemio.miniblogapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtResponseDto {

    private String token;
    private String tokenType = "Bearer";

    public JwtResponseDto(String token) {
        this.token = token;
    }
}
