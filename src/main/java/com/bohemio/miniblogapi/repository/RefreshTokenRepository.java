package com.bohemio.miniblogapi.repository;

import com.bohemio.miniblogapi.entity.RefreshToken;
import com.bohemio.miniblogapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    @Modifying
        // SELECT 쿼리가 아님을 명시
    int deleteByUser(User user); // 특정 사용자의 리프레시 토큰을 삭제할 때 사용 (예: 로그아웃 또는 토큰 재발급 시 기존 토큰 삭제)
    void deleteByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
