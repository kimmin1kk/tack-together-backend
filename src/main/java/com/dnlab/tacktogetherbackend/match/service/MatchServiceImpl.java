package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.global.common.RedisEntityProperties;
import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;
import com.dnlab.tacktogetherbackend.kakao.service.KakaoMapService;
import com.dnlab.tacktogetherbackend.match.common.*;
import com.dnlab.tacktogetherbackend.match.config.MatchRangeProperties;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfoMember;
import com.dnlab.tacktogetherbackend.match.domain.RidingStatus;
import com.dnlab.tacktogetherbackend.match.domain.redis.TemporaryMatchSessionInfo;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResultInfoDTO;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;
import com.dnlab.tacktogetherbackend.match.repository.TemporaryMatchSessionInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalPosition;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final MemberRepository memberRepository;
    private final TemporaryMatchSessionInfoRepository temporaryMatchSessionInfoRepository;
    private final Map<String, MatchRequest> activeMatchRequests = new ConcurrentHashMap<>();

    private final MatchInfoRepository matchInfoRepository;
    private final MatchInfoMemberRepository matchInfoMemberRepository;
    private final KakaoMapService kakaoMapService;
    private final MatchRangeProperties matchRangeProperties;
    private final RedisEntityProperties redisProperties;
    private final TaxiFareCalculator taxiFareCalculator;

    @Override
    public String addMatchRequest(MatchRequestDTO matchRequestDTO) {
        MatchRequest matchRequest = new MatchRequest(matchRequestDTO);
        activeMatchRequests.put(matchRequest.getId(), matchRequest);

        log.info("MatchRequest is added, " + matchRequest);
        return matchRequest.getId();
    }

    @Override
    public Optional<MatchRequest> getMatchRequestById(String matchRequestId) {
        return Optional.of(activeMatchRequests.get(matchRequestId));
    }

    @Override
    public void removeRideRequest(String matchRequestId) {
        activeMatchRequests.remove(matchRequestId);
    }

    @Override
    public String findMatchingMatchRequests(String matchRequestId) {
        MatchRequest matchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        log.info("finding MatchRequest");
        // 매칭 로직 구현
        List<MatchRequest> suitableRequests = activeMatchRequests.keySet().stream()
                .filter(key -> !key.equals(matchRequestId))
                .map(activeMatchRequests::get)
                .filter(req -> (!req.isMatched() && isSuitableRequests(matchRequest, req)))
                .collect(Collectors.toList());

        MatchRequest opponentMatchRequest = suitableRequests.stream()
                .findFirst()
                .orElse(null);

        if (opponentMatchRequest == null) {
            return null;
        }

        log.info("matched match request info : " + opponentMatchRequest);
        return opponentMatchRequest.getId();
    }

    @Override
    public Map<String, MatchResultInfoDTO> handlePendingMatchedAndGetMatchResultInfos(String matchRequestId, String opponentMatchRequestId) {
        log.debug("handlePendingMatchedAndGetMatchResultInfos 메소드가 호출되었습니다.");
        MatchRequest matchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        MatchRequest opponentMatchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);

        // MatchRequest 들을 각각 매칭 된 상태로 변경
        matchRequest.setMatched(true);
        matchRequest.setOpponentMatchRequestId(opponentMatchRequest.getId());
        matchRequest.setMatchDecisionStatus(MatchDecisionStatus.WAITING);

        opponentMatchRequest.setMatched(true);
        opponentMatchRequest.setOpponentMatchRequestId(matchRequest.getId());
        opponentMatchRequest.setMatchDecisionStatus(MatchDecisionStatus.WAITING);

        // 임시 매칭 정보를 Redis 에 저장
        String sessionId = UUID.randomUUID().toString();
        matchRequest.setTempSessionId(sessionId);
        opponentMatchRequest.setTempSessionId(sessionId);

        // 두 경로 중 거리가 더 가까운 경로를 세션에 저장
        ResponseDirections directionCase1 = kakaoMapService.getRoute(RequestDirections.builder()
                .origin(matchRequest.getOrigin())
                .destination(matchRequest.getDestination())
                .waypoints(opponentMatchRequest.getDestination())
                .build());

        ResponseDirections directionCase2 = kakaoMapService.getRoute(RequestDirections.builder()
                .origin(opponentMatchRequest.getOrigin())
                .destination(opponentMatchRequest.getDestination())
                .waypoints(matchRequest.getDestination())
                .build());

        int distance1 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(matchRequest.getOrigin())
                .destination(matchRequest.getDestination())
                .waypoints(opponentMatchRequest.getDestination())
                .build());

        int distance2 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(opponentMatchRequest.getOrigin())
                .destination(opponentMatchRequest.getDestination())
                .waypoints(matchRequest.getDestination())
                .build());

        MatchRequest fartherRequest = null;
        MatchRequest nearerRequest = null;
        ResponseDirections fixedDirections = null;

        // 서로를 경유지로 설정했을 경우 어느 경로가 더 짧은가 비교
        if (distance1 > distance2) {
            fartherRequest = opponentMatchRequest;
            nearerRequest = matchRequest;
            fixedDirections = directionCase2;
        } else {
            fartherRequest = matchRequest;
            nearerRequest = opponentMatchRequest;
            fixedDirections = directionCase1;
        }

        TemporaryMatchSessionInfo temporaryMatchSessionInfo = TemporaryMatchSessionInfo.builder()
                .sessionId(sessionId)
                .origin(fartherRequest.getOrigin())
                .destination(fartherRequest.getDestination())
                .waypoints(nearerRequest.getDestination())
                .destinationDistance(Math.min(distance1, distance2))
                .waypointDistance(kakaoMapService.getDistance(RequestDirections.ofMatchRequest(nearerRequest)))
                .destinationMatchRequestId(fartherRequest.getOpponentMatchRequestId())
                .waypointMatchRequestId(nearerRequest.getOpponentMatchRequestId())
                .expiredTime(redisProperties.getTtl())
                .build();

        log.debug("Before Saving TemporaryMatchInfo : " + temporaryMatchSessionInfo);
        TemporaryMatchSessionInfo savedTemporaryMatchSessionInfo = temporaryMatchSessionInfoRepository.save(temporaryMatchSessionInfo);
        log.debug("Saved TemporaryMatchInfo : " + savedTemporaryMatchSessionInfo);

        Map<String, MatchResultInfoDTO> infoDTOMap = new HashMap<>();

        // 택시 비용 계산
        int totalFare = fixedDirections.getRoutes()
                .stream().findFirst().orElseThrow()
                .getSummary()
                .getFare()
                .getTaxi();
        TaxiFares taxiFares = taxiFareCalculator.calculateFare(totalFare,
                temporaryMatchSessionInfo.getWaypointDistance(),
                temporaryMatchSessionInfo.getDestinationDistance());
        PaymentRate paymentRate = taxiFareCalculator.calculatePaymentRate(taxiFares);

        // 각 사용자의 매칭 정보를 Map 에 삽입 후 반환
        infoDTOMap.put(nearerRequest.getId(),
                MatchResultInfoDTO.builder()
                        .username(nearerRequest.getUsername())
                        .opponentUsername(fartherRequest.getUsername())
                        .routes(fixedDirections.getRoutes())
                        .estimatedTotalTaxiFare(totalFare)
                        .estimatedTaxiFare(taxiFares.getWaypointFare())
                        .paymentRate(paymentRate.getWaypointRate())
                        .opponentPaymentRate(paymentRate.getDestinationRate())
                        .build());
        infoDTOMap.put(fartherRequest.getId(),
                MatchResultInfoDTO.builder()
                        .username(fartherRequest.getUsername())
                        .opponentUsername(nearerRequest.getUsername())
                        .routes(fixedDirections.getRoutes())
                        .estimatedTotalTaxiFare(totalFare)
                        .estimatedTaxiFare(taxiFares.getDestinationFare())
                        .paymentRate(paymentRate.getDestinationRate())
                        .opponentPaymentRate(paymentRate.getWaypointRate())
                        .build());

        return infoDTOMap;
    }

    // 매칭 수락 로직
    @Override
    @Transactional
    public MatchDecisionStatus acceptMatch(String matchRequestId) {
        MatchRequest matchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        matchRequest.setMatchDecisionStatus(MatchDecisionStatus.ACCEPTED);
        MatchRequest matchedRequest = activeMatchRequests.get(matchRequest.getOpponentMatchRequestId());

        if (matchedRequest.getMatchDecisionStatus().equals(MatchDecisionStatus.ACCEPTED)) {
            handleAcceptedMatchedRequests(matchedRequest.getTempSessionId());
            return MatchDecisionStatus.ACCEPTED;
        }

        return matchedRequest.getMatchDecisionStatus();
    }

    @Override
    public void rejectMatch(String matchRequestId) {
        // 매칭 거절 로직
        MatchRequest matchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        matchRequest.setMatchDecisionStatus(MatchDecisionStatus.REJECTED);
        MatchRequest matchedRequest = activeMatchRequests.get(matchRequest.getOpponentMatchRequestId());

        matchRequest.setMatched(false);
        matchRequest.setMatchDecisionStatus(null);
        matchedRequest.setMatched(false);
        matchedRequest.setMatchDecisionStatus(null);
        temporaryMatchSessionInfoRepository.deleteBySessionId(matchRequest.getTempSessionId());
    }

    @Override
    public void resetMatchRequests() {
        activeMatchRequests.clear();
    }

    private boolean isSuitableRequests(MatchRequest originReg, MatchRequest targetReq) {
        return !targetReq.isMatched()
                && isSuitableOriginRanges(originReg, targetReq)
                && isSuitableDestinations(originReg, targetReq);
    }

    private boolean isSuitableOriginRanges(MatchRequest req1, MatchRequest req2) {
        short minRange = (req1.getOriginRange() > req2.getOriginRange()) ? req2.getOriginRange() : req1.getOriginRange();
        return matchRangeProperties.convertRangeLevelToRange(minRange, RangeKind.ORIGIN) > calculateDistance(req1.getOrigin(), req2.getOrigin());
    }

    private boolean isSuitableDestinations(MatchRequest request1, MatchRequest request2) {
        int distance1 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(request1.getOrigin())
                .destination(request1.getDestination())
                .waypoints(request2.getDestination())
                .build());

        int distance2 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(request2.getOrigin())
                .destination(request2.getDestination())
                .waypoints(request1.getDestination())
                .build());

        MatchRequest finalDestinationReq = distance1 < distance2 ? request1 : request2;
        int originDistance = kakaoMapService.getDistance(RequestDirections.ofMatchRequest(finalDestinationReq));
        int shorterTotalDistance = Math.min(distance1, distance2);

        return (matchRangeProperties.convertRangeLevelToRange(
                request1.getDestinationRange() < request2.getDestinationRange() ? request1.getDestinationRange() : request2.getDestinationRange(),
                RangeKind.DESTINATION)) > (shorterTotalDistance - originDistance);
    }

    /**
     * @param coordinate1 좌표값 1, ${경도},${위도}로 구성되어 있음
     * @param coordinate2 좌표값 2, 좌표값 1과 구성 동일
     * @return 두 좌표 간 거리계산 후 미터단위로 반환
     */
    private int calculateDistance(String coordinate1, String coordinate2) {
        final double elevation = 0;
        String[] params1 = coordinate1.split(",");
        String[] params2 = coordinate2.split(",");
        GlobalPosition position1 = new GlobalPosition(Double.parseDouble(params1[1]), Double.parseDouble(params1[0]), elevation);
        GlobalPosition position2 = new GlobalPosition(Double.parseDouble(params2[1]), Double.parseDouble(params2[0]), elevation);

        GeodeticCalculator calculator = new GeodeticCalculator();
        GeodeticMeasurement measurement = calculator.calculateGeodeticMeasurement(Ellipsoid.WGS84, position1, position2);
        return (int) measurement.getPointToPointDistance();
    }

    private void handleAcceptedMatchedRequests(String sessionId) {
        log.debug("handleAcceptedMatchedRequests 메소드가 호출되었습니다.");

        TemporaryMatchSessionInfo tempMatchInfo = temporaryMatchSessionInfoRepository.findById(sessionId).orElseThrow();
        log.debug("Found TemporaryMatchInfo : " + tempMatchInfo);

        MatchRequest fartherReq = activeMatchRequests.get(tempMatchInfo.getDestinationMatchRequestId());
        MatchRequest nearerReq = activeMatchRequests.get(tempMatchInfo.getWaypointMatchRequestId());

        MatchInfo matchInfo = MatchInfo.builder()
                .origin(tempMatchInfo.getOrigin())
                .destination(tempMatchInfo.getDestination())
                .waypoints(tempMatchInfo.getWaypoints())
                .totalDistance(tempMatchInfo.getDestinationDistance())
                .status(RidingStatus.WAITING)
                .build();

        matchInfoRepository.save(matchInfo);
        matchInfoMemberRepository.save(MatchInfoMember.builder()
                .destination(fartherReq.getDestination())
                .distance(tempMatchInfo.getDestinationDistance())
                .matchInfo(matchInfo)
                .member(memberRepository.findMemberByUsername(fartherReq.getUsername()).orElseThrow())
                .build());

        matchInfoMemberRepository.save(MatchInfoMember.builder()
                .destination(nearerReq.getDestination())
                .distance(tempMatchInfo.getWaypointDistance())
                .matchInfo(matchInfo)
                .member(memberRepository.findMemberByUsername(nearerReq.getUsername()).orElseThrow())
                .build());

        activeMatchRequests.remove(fartherReq.getId());
        activeMatchRequests.remove(nearerReq.getId());

        temporaryMatchSessionInfoRepository.delete(tempMatchInfo);
    }


}
