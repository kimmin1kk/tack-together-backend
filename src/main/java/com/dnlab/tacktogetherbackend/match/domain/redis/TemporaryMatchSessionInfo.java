package com.dnlab.tacktogetherbackend.match.domain.redis;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@NoArgsConstructor
@RedisHash(value = "temporaryMatchSessionInfo")
public class TemporaryMatchSessionInfo implements Serializable {
    @Id
    private String sessionId;

    // 경로 검색 조건
    private String origin;

    private String destination;

    private String waypoints;

    // 거리
    private int destinationDistance;

    private int waypointDistance;

    // 매칭 요청 아이디
    private String destinationMatchRequestId;

    private String waypointMatchRequestId;

    // 만료시간
    @TimeToLive
    private int expiredTime;

    @Builder
    @SuppressWarnings("squid:S107")
    public TemporaryMatchSessionInfo(String sessionId, String origin, String destination, String waypoints, int destinationDistance, int waypointDistance, String destinationMatchRequestId, String waypointMatchRequestId, int expiredTime) {
        this.sessionId = sessionId;
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.destinationDistance = destinationDistance;
        this.waypointDistance = waypointDistance;
        this.destinationMatchRequestId = destinationMatchRequestId;
        this.waypointMatchRequestId = waypointMatchRequestId;
        this.expiredTime = expiredTime;
    }
}
