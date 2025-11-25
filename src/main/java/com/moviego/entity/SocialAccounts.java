package com.moviego.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="social_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialAccounts extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_id")
    private Long socialId;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Provider provider;

    @Column(nullable = false, name = "provider_user_id", length = 100)
    private String providerUserId;

    @Column(name = "access_token", length = 100)
    private String accessToken;

    @Column(name = "refresh_token", length = 100)
    private String RefreshToken;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 비즈니스 메서드
    public enum Provider {
        KAKAO,
        GOOGLE,
        NAVER
    }
}
