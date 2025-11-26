package com.moviego.controller;

import com.moviego.dto.auth.RegisterRequest;
import com.moviego.entity.Users;
import com.moviego.repository.UserRepository;
import com.moviego.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 및 사용자 관리", description = "로그인, 회원가입, JWT 관련 API")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    @PostMapping("/register")
    public Users registerUser(RegisterRequest request) {
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

        return userRepository.save(newUser);
    }


}
