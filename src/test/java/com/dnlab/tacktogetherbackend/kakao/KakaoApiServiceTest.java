package com.dnlab.tacktogetherbackend.kakao;

import com.dnlab.tacktogetherbackend.SpringBootTestConfiguration;
import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.service.KakaoMapService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = SpringBootTestConfiguration.class)
class KakaoApiServiceTest {

    @Autowired
    private KakaoMapService kakaoMapService;

    private final String origin = "127.11015314141542,37.39472714688412"; // 판교역 4번출구
    private final String destination = "127.10824367964793,37.401937080111644"; // 판교 에이치스퀘어 N동
    private final String waypoints = "127.11445570946518,37.39613946659914";

    @Test
    void getRouteTestWithoutWaypoints() {
        RequestDirections requestDirections = RequestDirections.builder()
                .origin(origin)
                .destination(destination)
                .build();

        log.info(kakaoMapService.getRoute(requestDirections).toString());
    }

    @Test
    void getRouteTestWithWaypoints() {
        RequestDirections requestDirections = RequestDirections.builder()
                .origin(origin)
                .destination(destination)
                .waypoints(waypoints)
                .build();

        log.info(kakaoMapService.getRoute(requestDirections).toString());
    }
}
