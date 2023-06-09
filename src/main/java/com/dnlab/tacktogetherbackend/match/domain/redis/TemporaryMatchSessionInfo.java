package com.dnlab.tacktogetherbackend.match.domain.redis;

import com.dnlab.tacktogetherbackend.match.common.PostMatchTemporaryInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@RedisHash(value = "temporaryMatchSessionInfo")
public class TemporaryMatchSessionInfo implements Serializable {
    @Id
    @Indexed
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
    public TemporaryMatchSessionInfo(String origin, String destination, String waypoints, int destinationDistance, int waypointDistance, String destinationMatchRequestId, String waypointMatchRequestId, int expiredTime) {
        this.sessionId = UUID.randomUUID().toString();
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.destinationDistance = destinationDistance;
        this.waypointDistance = waypointDistance;
        this.destinationMatchRequestId = destinationMatchRequestId;
        this.waypointMatchRequestId = waypointMatchRequestId;
        this.expiredTime = expiredTime;
    }

    public static TemporaryMatchSessionInfo of(PostMatchTemporaryInfo postMatchTemporaryInfo,
                                               int expiredTime) {
        return TemporaryMatchSessionInfo.builder()
                .origin(postMatchTemporaryInfo.getFartherRequest().getOrigin())
                .destination(postMatchTemporaryInfo.getFartherRequest().getDestination())
                .waypoints(postMatchTemporaryInfo.getNearerRequest().getDestination())
                .destinationDistance(postMatchTemporaryInfo.getDestinationDistance())
                .waypointDistance(postMatchTemporaryInfo.getWaypointDistance())
                .destinationMatchRequestId(postMatchTemporaryInfo.getFartherRequest().getId())
                .waypointMatchRequestId(postMatchTemporaryInfo.getNearerRequest().getId())
                .expiredTime(expiredTime)
                .build();
    }
}
