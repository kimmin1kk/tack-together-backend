package com.dnlab.tacktogetherbackend.kakao.service;

import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebFluxKakaoMapServiceImpl implements KakaoMapService {
    private final WebClient webClient;

    @Override
    public ResponseDirections getRoute(RequestDirections requestDirections) {
        ResponseDirections responseDirections;

        if (requestDirections.getWaypoints() == null) {
            responseDirections = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/directions")
                            .queryParam("origin", requestDirections.getOrigin())
                            .queryParam("destination", requestDirections.getDestination())
                            .build())
                    .retrieve()
                    .bodyToMono(ResponseDirections.class)
                    .block();
        } else {
            responseDirections = webClient.get()
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

        return responseDirections;
    }
}
