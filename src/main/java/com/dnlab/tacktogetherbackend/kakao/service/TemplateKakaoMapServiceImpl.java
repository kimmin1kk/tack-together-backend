package com.dnlab.tacktogetherbackend.kakao.service;

import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class TemplateKakaoMapServiceImpl implements KakaoMapService {
    private final RestTemplate restTemplate;

    @Value("${api.kakao.mobility-base-url}")
    private String mobilityBaseUrl;

    @Value("${api.kakao.key}")
    private String kakaoApiKey;

    @Override
    public ResponseDirections getRoute(RequestDirections requestDirections) {
        String url = UriComponentsBuilder.fromHttpUrl(mobilityBaseUrl)
                .path("/directions")
                .queryParam("origin", requestDirections.getOrigin())
                .queryParam("destination", requestDirections.getDestination())
                .queryParam("waypoints", requestDirections.getWaypoints())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ResponseDirections> response = restTemplate.exchange(url, HttpMethod.GET, entity, ResponseDirections.class);

        return response.getBody();
    }
}
