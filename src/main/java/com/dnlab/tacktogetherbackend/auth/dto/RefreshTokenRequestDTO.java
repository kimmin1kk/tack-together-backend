package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenRequestDTO {
    private String refreshToken;

    @Builder
    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
