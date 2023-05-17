package com.dnlab.tacktogetherbackend.matched.domain.redis;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Data
@RedisHash("matchSessionInfo")
@NoArgsConstructor
public class MatchSessionInfo implements Serializable {
    @Id
    private String sessionId;
    private Long matchInfoId;
    private Set<SessionMemberInfo> memberInfos = new LinkedHashSet<>();

    public MatchSessionInfo(Set<SessionMemberInfo> memberInfos, Long matchInfoId) {
        this.sessionId = UUID.randomUUID().toString();
        this.memberInfos = memberInfos;
    }
}
