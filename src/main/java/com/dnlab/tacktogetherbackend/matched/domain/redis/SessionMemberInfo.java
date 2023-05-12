package com.dnlab.tacktogetherbackend.matched.domain.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionMemberInfo implements Serializable {
    private String username;
    private boolean departureAgreed;
}
