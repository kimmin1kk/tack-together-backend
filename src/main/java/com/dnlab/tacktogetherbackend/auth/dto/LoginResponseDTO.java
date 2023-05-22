package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDTO {
    private String username;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginResponseDTO(String username, String accessToken, String refreshToken) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
