package com.dnlab.tacktogetherbackend.match.common;

import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;
import com.dnlab.tacktogetherbackend.kakao.service.KakaoMapService;
import lombok.Getter;

@Getter
public class PostMatchTemporaryInfo {
    private final MatchRequest fartherRequest;
    private final MatchRequest nearerRequest;
    private final ResponseDirections fixedDirections;
    private final int waypointDistance;
    private final int destinationDistance;

    private PostMatchTemporaryInfo(MatchRequest fartherRequest,
                                   MatchRequest nearerRequest,
                                   ResponseDirections fixedDirections,
                                   int waypointDistance,
                                   int destinationDistance) {
        this.fartherRequest = fartherRequest;
        this.nearerRequest = nearerRequest;
        this.fixedDirections = fixedDirections;
        this.waypointDistance = waypointDistance;
        this.destinationDistance = destinationDistance;
    }

    public void setTemporarySessionId(String sessionId) {
        this.fartherRequest.setTempSessionId(sessionId);
        this.nearerRequest.setTempSessionId(sessionId);
    }

    public static PostMatchTemporaryInfo of(MatchRequest matchRequest1, MatchRequest matchRequest2,
                                            KakaoMapService kakaoMapService) {

        // 두 경로 중 거리가 더 가까운 경로를 세션에 저장
        ResponseDirections directionCase1 = kakaoMapService.getRoute(matchRequest1, matchRequest2);
        ResponseDirections directionCase2 = kakaoMapService.getRoute(matchRequest2, matchRequest1);

        int distance1 = kakaoMapService.getDistance(directionCase1);
        int distance2 = kakaoMapService.getDistance(directionCase2);

        MatchRequest destinationRequest;
        MatchRequest waypointRequest;
        ResponseDirections fixedDirections;

        // 서로를 경유지로 설정했을 경우 어느 경로가 더 짧은가 비교
        int destinationDistance;
        if (distance1 <= distance2) {
            destinationDistance = distance1;
            destinationRequest = matchRequest1;
            waypointRequest = matchRequest2;
            fixedDirections = directionCase1;
        } else {
            destinationDistance = distance2;
            destinationRequest = matchRequest2;
            waypointRequest = matchRequest1;
            fixedDirections = directionCase2;
        }

        int waypointDistance = kakaoMapService.getDistance(waypointRequest);

        return new PostMatchTemporaryInfo(
                destinationRequest,
                waypointRequest,
                fixedDirections,
                waypointDistance,
                destinationDistance);
    }
}
