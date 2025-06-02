package com.bohemio.miniblogapi.service;

import com.bohemio.miniblogapi.config.JwtTokenProvider;
import com.bohemio.miniblogapi.dto.JwtResponseDto;
import com.bohemio.miniblogapi.dto.LoginRequestDto;
import com.bohemio.miniblogapi.dto.SignUpRequestDto;
import com.bohemio.miniblogapi.entity.RefreshToken;
import com.bohemio.miniblogapi.entity.Role;
import com.bohemio.miniblogapi.entity.User;
import com.bohemio.miniblogapi.repository.RefreshTokenRepository;
import com.bohemio.miniblogapi.repository.RoleRepository;
import com.bohemio.miniblogapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final RefreshTokenRepository refreshTokenRepository; // <--- 새로 추가!
    private final long refreshTokenDurationMs; // <--- 리프레시 토큰 유효기간 (application.properties에서 읽어올 것)

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository, @Value("${jwt.refresh-token-expiration-in-ms}") long refreshTokenDurationMs) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository; // <--- 초기화!
        this.refreshTokenDurationMs = refreshTokenDurationMs; // <--- 초기화!
    }

    @Override
    @Transactional
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

        // 사용자 정보 가져오기 (DB에서 최신 정보로, 또는 Authentication 객체에서)
        // User 엔티티가 필요하므로, username으로 다시 조회하는 것이 안전할 수 있다.
        String username = authentication.getName();
        com.bohemio.miniblogapi.entity.User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("인증 후 사용자 정보를 찾을 수 없습니다: " + username));


        String accessToken = jwtTokenProvider.createAccessToken(authentication); // <--- 지난번에 이름 바꾼 메소드 사용!
        String refreshTokenString = jwtTokenProvider.createRefreshToken(authentication); // <--- 리프레시 토큰 생성!

        // 기존 리프레시 토큰이 있다면 삭제 (한 사용자당 하나의 리프레시 토큰만 유지한다고 가정)
        refreshTokenRepository.findByUser(currentUser).ifPresent(refreshTokenRepository::delete);
        // 또는 refreshTokenRepository.deleteByUser(currentUser); 를 사용할 수도 있지만,
        // findByUser 후 delete 하는 것이 좀 더 명확할 수 있고, 반환 타입(int)을 신경 쓰지 않아도 된다.

        // 새로운 리프레시 토큰 생성 및 저장
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(currentUser)
                .token(refreshTokenString)
                // JwtTokenProvider에서 만료 시간을 JWT에 포함시켰으므로, 거기서 읽어오거나
                // 여기서 application.properties에 설정된 값으로 직접 계산할 수 있다.
                // 여기서는 직접 계산하는 방식을 사용 (Instant 사용)
                .expiryDate( Instant.now().plusMillis(this.refreshTokenDurationMs))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        logger.info("{} 사용자가 로그인했습니다.", loginRequestDto.getUsername());
        return new JwtResponseDto(accessToken, refreshTokenString);

    }

    }
