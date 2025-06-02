package com.bohemio.miniblogapi.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(name="REFRESH_TOKEN")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 사용자와 리프레시 토큰은 1:1 관계
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 512) // 토큰 값은 충분히 길게
    private String token;

    @Column(nullable = false, name = "expiry_date")
    private Instant expiryDate; // 만료 시점 (UTC)

    @Builder
    public RefreshToken(User user, String token, Instant expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public void updateToken(String newToken, Instant newExpiryDate) {
        this.token = newToken;
        this.expiryDate = newExpiryDate;
    }

}
