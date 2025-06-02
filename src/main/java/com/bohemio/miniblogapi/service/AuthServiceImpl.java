package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.config.JwtTokenProvider;
import com.bohemio.miniblogapi.dto.JwtResponseDto;
import com.bohemio.miniblogapi.dto.LoginRequestDto;
import com.bohemio.miniblogapi.dto.SignUpRequestDto;
import com.bohemio.miniblogapi.entity.Role;
import com.bohemio.miniblogapi.entity.User;
import com.bohemio.miniblogapi.repository.RoleRepository;
import com.bohemio.miniblogapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signup(SignUpRequestDto signUpRequestDto){
        if (userRepository.existsByUsername(signUpRequestDto.getUsername())){
            throw new IllegalArgumentException("이미 사용중인 사용자 아이디 입니다: " + signUpRequestDto.getUsername());
        }
        if (userRepository.existsByEmail(signUpRequestDto.getEmail())){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + signUpRequestDto.getEmail());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("오류: ROLE_USER 역할을 찾을 수 없습니다. DB를 확인해주세요."));

        User user = User.builder()
                .username(signUpRequestDto.getUsername())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .email(signUpRequestDto.getEmail())
                .nickname(signUpRequestDto.getNickname())
                .name(signUpRequestDto.getName())
                .birthdate(signUpRequestDto.getBirthdate())
                .roles( Set.of(userRole))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        userRepository.save(user);
        logger.info("새로운 사용자가 등록되었습니다. {}", user.getUsername());

    }

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequestDto){
     Authentication authentication = authenticationManager.authenticate(
             new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
     );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.createToken(authentication);

        logger.info("{} 사용자가 로그인했습니다.", loginRequestDto.getUsername());
        return new JwtResponseDto(jwt);

    }

    }
