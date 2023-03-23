package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberResponseDTO {
    private String accessToken;

    @Builder
    public MemberResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
