package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseLogin {
    private String username;
    private String accessToken;
    private String refreshToken;

    @Builder
    public ResponseLogin(String username, String accessToken, String refreshToken) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
