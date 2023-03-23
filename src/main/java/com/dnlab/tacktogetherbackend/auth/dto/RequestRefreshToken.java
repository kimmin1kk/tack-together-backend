package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestRefreshToken {
    private String refreshToken;

    @Builder
    public RequestRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
