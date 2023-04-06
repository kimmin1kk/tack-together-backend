package com.dnlab.tacktogetherbackend.kakao.common.dto;

import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDirections {
    private String origin;
    private String destination;
    private String waypoints;

    public static RequestDirections ofMatchRequest(MatchRequest matchRequest) {
        return RequestDirections.builder()
                .origin(matchRequest.getOrigin())
                .destination(matchRequest.getDestination())
                .build();
    }
}
