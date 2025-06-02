package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.dto.JwtResponseDto;
import com.bohemio.miniblogapi.dto.LoginRequestDto;
import com.bohemio.miniblogapi.dto.SignUpRequestDto;

public interface AuthService {

    void signup(SignUpRequestDto singUpRequestDto);
    JwtResponseDto login(LoginRequestDto loginRequestDto);

}
