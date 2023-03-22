package com.dnlab.tacktogetherbackend.user.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponseDTO {
    private String username;
}
