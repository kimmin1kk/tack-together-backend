package com.dnlab.tacktogetherbackend.user.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRegistrationDTO {
    private String username;
    private String password;
}
