package com.bohemio.miniblogapi.controller;

import com.bohemio.miniblogapi.dto.JwtResponseDto;
import com.bohemio.miniblogapi.dto.LoginRequestDto;
import com.bohemio.miniblogapi.dto.SignUpRequestDto;
import com.bohemio.miniblogapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signupUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto){
        authService.signup(signUpRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto){
        JwtResponseDto jwtToken = authService.login(loginRequestDto);

        return ResponseEntity.ok(jwtToken);

    }

}
