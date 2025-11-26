package com.moviego.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private final Key key;
    private final long expirationTime;

    /**
     * application.yml에서 JWT 설정 값을 주입받아 초기화합니다.
     */
    public JwtUtil(@Value("${spring.security.jwt.secret-key}") String secretKey,
                   @Value("${spring.security.jwt.expiration}") long expirationTime) {
        // Base64 인코딩된 비밀 키를 디코딩하여 Key 객체로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
    }

    /**
     * 1. JWT 토큰을 생성합니다. (로그인 성공 시 호출)
     * @param userId 토큰 Payload에 저장할 사용자 고유 ID
     * @return 생성된 JWT 문자열
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userId.toString()) // 토큰의 주체 (Subject)는 사용자 ID
                .setIssuedAt(now)              // 토큰 발급 시간
                .setExpiration(expiryDate)     // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명 (Key와 알고리즘)
                .compact();
    }

    /**
     * 2. JWT 토큰에서 사용자 ID(Subject)를 추출합니다.
     * @param token JWT 문자열
     * @return 사용자 고유 ID (Long 타입)
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        if (claims != null && claims.getSubject() != null) {
            return Long.parseLong(claims.getSubject());
        }
        return null;
    }

    /**
     * 3. 토큰의 유효성을 검사합니다. (JWT 필터에서 호출)
     * @param token JWT 문자열
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 내부적으로 토큰을 파싱하여 클레임(Payload)을 가져오는 메서드
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("JWT 파싱 오류: {}", e.getMessage());
            return null;
        }
    }
}
