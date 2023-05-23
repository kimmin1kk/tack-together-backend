package com.dnlab.tacktogetherbackend.kakao.service;

import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;

public interface KakaoMapService {
    ResponseDirections getRoute(RequestDirections requestDirections);

    default ResponseDirections getRoute(MatchRequest matchRequest1, MatchRequest matchRequest2) {
        return getRoute(RequestDirections.builder()
                .origin(matchRequest1.getOrigin())
                .destination(matchRequest1.getDestination())
                .waypoints(matchRequest2.getDestination())
                .build());
    }

    default int getDistance(RequestDirections requestDirections) {
        return getDistance(getRoute(requestDirections));
    }

    default int getDistance(MatchRequest matchRequest) {
        return getDistance(RequestDirections.ofMatchRequest(matchRequest));
    }

    default int getDistance(ResponseDirections responseDirections) {
        return responseDirections.getRoutes()
                .stream()
                .findFirst().orElseThrow()
                .getSummary()
                .getDistance();
    }
}
