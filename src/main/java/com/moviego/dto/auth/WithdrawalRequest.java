package com.moviego.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 탈퇴 요청 DTO")
public class WithdrawalRequest {
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Schema(description = "탈퇴 확인을 위한 현재 비밀번호", example = "Password123!")
    private String password;
}

