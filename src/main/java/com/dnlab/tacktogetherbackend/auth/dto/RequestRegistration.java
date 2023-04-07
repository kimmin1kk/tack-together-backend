package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class RequestRegistration {
    @NotNull
    @Size(min = 5, max = 20)
    private String username;

    @NotNull
    @Size(min = 3)
    private String password;

    @NotNull
    @Size(min = 2, max = 45)
    private String name;

    @NotNull
    @Size(min = 2, max = 16)
    private String nickname;
}
