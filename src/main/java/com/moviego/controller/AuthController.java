package com.moviego.controller;

import com.moviego.dto.auth.*;
import com.moviego.service.AuthService;
import com.moviego.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 및 사용자 관리", description = "로그인, 회원가입, JWT 관련 API")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.registerUser(request); // 서비스 계층 호출
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponse(null, null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JoinResponse> LoginUser(
            @Valid @RequestBody JoinRequest joinRequest,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ){
        try {
            // 토큰이 있고 유효하면 바로 예외 발생
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (authService.validateToken(token)) {  // jwtUtil.validateToken() 래핑된 메서드가 AuthService에 있다고 가정
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new JoinResponse(null, null, null, "이미 로그인된 상태입니다."));
                }
            }

            JoinResponse response = authService.join(joinRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JoinResponse(null,null,null, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        // 1. 헤더 유효성 검사 (Bearer 토큰 형식 확인)
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, "인증 토큰(Bearer)이 필요합니다."));
        }

        try {
            String accessToken = header.substring(7);

            // 2. 서비스 계층에 로그아웃 로직 위임 (블랙리스트 등록)
            authService.logout(accessToken);

            // 3. 성공 응답
            return ResponseEntity.ok(new AuthResponse(null, "성공적으로 로그아웃되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "로그아웃 처리 중 오류 발생: " + e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody WithdrawalRequest request
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        authService.deleteUser(userId, request.getPassword(), token);

        return ResponseEntity.noContent().build();
    }
}
