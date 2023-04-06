package com.dnlab.tacktogetherbackend.kakao.service;

import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;

public interface KakaoMapService {
    ResponseDirections getRoute(RequestDirections requestDirections);
    int getDistance(RequestDirections requestDirections);
}
