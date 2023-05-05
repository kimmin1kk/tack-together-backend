package com.dnlab.tacktogetherbackend.kakao.service;

import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;

public interface KakaoMapService {
    ResponseDirections getRoute(RequestDirections requestDirections);

    default int getDistance(RequestDirections requestDirections) {
        return getDistance(getRoute(requestDirections));
    }

    default int getDistance(ResponseDirections responseDirections) {
        return responseDirections.getRoutes()
                .stream()
                .findFirst().orElseThrow()
                .getSummary()
                .getDistance();
    }
}
