package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class LoginRequestDTO {
    @NotNull
    private String username;
    @NotNull
    private String password;

    @Builder
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
