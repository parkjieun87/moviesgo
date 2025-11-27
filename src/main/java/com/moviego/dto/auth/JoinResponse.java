package com.moviego.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinResponse {
    private String accessToken;
    private Long  id;
    private String email;
    private String message;
}
