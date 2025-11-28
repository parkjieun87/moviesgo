package com.moviego.service;

import com.moviego.dto.auth.JoinRequest;
import com.moviego.dto.auth.JoinResponse;
import com.moviego.dto.auth.RegisterRequest;
import com.moviego.dto.auth.RegisterResponse;
import com.moviego.entity.Users;
import com.moviego.repository.UserRepository;
import com.moviego.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    //회원가입 메서드
    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {
        // 1. 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Users 엔티티 생성 및 저장
        Users newUser = Users.builder()
                .email(request.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .role(Users.UserRole.USER) // 기본 역할 설정
                .status(Users.Status.ACTIVE)
                .build();

        Users savedUser = userRepository.save(newUser);

        return new RegisterResponse(
                savedUser.getUserId(),
                savedUser.getEmail(),
                "회원가입이 성공적으로 완료되었습니다."
        );
    }

    //로그인 메서드
    @Transactional
    public JoinResponse join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("회원가입이 안된 이메일 입니다."));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateToken(user.getUserId(),user.getEmail());

        return new JoinResponse(
          accessToken,
          user.getUserId(),
          user.getEmail(),
          "로그인이 성공적으로 완료되었습니다."
        );
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    //로그아웃 메서드
    @Transactional
    public void logout(String accessToken) {
        // 1. JwtUtil을 사용하여 토큰의 만료 시간을 알아냅니다.
        long expirationTimeMs = jwtUtil.getExpirationTime(accessToken); // 이 메서드가 JwtUtil에 있어야 함
        long remainingTimeMs = expirationTimeMs - System.currentTimeMillis();

        if (remainingTimeMs > 0) {
            // 2. Redis에 토큰을 저장하고, 남은 유효기간 동안만 TTL을 설정합니다.
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken,
                    "revoked",
                    Duration.ofMillis(remainingTimeMs)
            );
        }
    }

}
