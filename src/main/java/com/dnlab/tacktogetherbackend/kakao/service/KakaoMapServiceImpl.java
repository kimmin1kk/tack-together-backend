package com.dnlab.tacktogetherbackend.kakao.service;

import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class KakaoMapServiceImpl implements KakaoMapService {
    private final WebClient webClient;

    public KakaoMapServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ResponseDirections getRoute(RequestDirections requestDirections) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/directions")
                        .queryParam("origin", requestDirections.getOrigin())
                        .queryParam("destination", requestDirections.getDestination())
                        .queryParam("waypoints", requestDirections.getWaypoints())
                        .build())
                .retrieve()
                .bodyToMono(ResponseDirections.class)
                .block();
    }
}
