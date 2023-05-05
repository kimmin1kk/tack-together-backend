package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.global.common.RedisEntityProperties;
import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;
import com.dnlab.tacktogetherbackend.kakao.service.KakaoMapService;
import com.dnlab.tacktogetherbackend.match.common.*;
import com.dnlab.tacktogetherbackend.match.config.MatchRangeProperties;
import com.dnlab.tacktogetherbackend.match.domain.MatchResult;
import com.dnlab.tacktogetherbackend.match.domain.MatchResultMember;
import com.dnlab.tacktogetherbackend.match.domain.TemporaryMatchInfo;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResultInfoDTO;
import com.dnlab.tacktogetherbackend.match.repository.MatchResultMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchResultRepository;
import com.dnlab.tacktogetherbackend.match.repository.TemporaryMatchInfoRepository;
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
    private final TemporaryMatchInfoRepository temporaryMatchInfoRepository;
    private final Map<String, MatchRequest> activeMatchRequests = new ConcurrentHashMap<>();

    private final MatchResultRepository matchResultRepository;
    private final MatchResultMemberRepository matchResultMemberRepository;
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
                .filter(req -> (req.getMatchDecisionStatus() == null && isSuitableRequests(matchRequest, req)))
                .collect(Collectors.toList());

        MatchRequest opponentMatchRequest = suitableRequests.stream()
                .findFirst()
                .orElse(null);

        if (opponentMatchRequest == null) {
            return null;
        }

        matchRequest.setOpponentMatchRequestId(opponentMatchRequest.getId());
        opponentMatchRequest.setOpponentMatchRequestId(matchRequest.getId());
        log.info("matched match request info : " + opponentMatchRequest);
        return opponentMatchRequest.getId();
    }

    @Override
    public Map<String, MatchResultInfoDTO> getMatchResultInfos(String matchRequestId, String matchedMatchRequestId) {
        MatchRequest matchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        MatchRequest matchedMatchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        log.debug("handlePendingMatched 메소드가 호출되었습니다.");
        // 매칭이 성사된 후 대기 상태를 처리
        matchRequest.setMatchDecisionStatus(MatchDecisionStatus.WAITING);
        matchedMatchRequest.setMatchDecisionStatus(MatchDecisionStatus.WAITING);

        // 임시 매칭 정보를 Redis 에 저장
        String sessionId = UUID.randomUUID().toString();
        matchRequest.setTempSessionId(sessionId);
        matchedMatchRequest.setTempSessionId(sessionId);

        ResponseDirections directionCase1 = kakaoMapService.getRoute(RequestDirections.builder()
                .origin(matchRequest.getOrigin())
                .destination(matchRequest.getDestination())
                .waypoints(matchedMatchRequest.getDestination())
                .build());

        ResponseDirections directionCase2 = kakaoMapService.getRoute(RequestDirections.builder()
                .origin(matchedMatchRequest.getOrigin())
                .destination(matchedMatchRequest.getDestination())
                .waypoints(matchRequest.getDestination())
                .build());

        int distance1 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(matchRequest.getOrigin())
                .destination(matchRequest.getDestination())
                .waypoints(matchedMatchRequest.getDestination())
                .build());

        int distance2 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(matchedMatchRequest.getOrigin())
                .destination(matchedMatchRequest.getDestination())
                .waypoints(matchRequest.getDestination())
                .build());

        MatchRequest fartherRequest = null;
        MatchRequest nearerRequest = null;
        ResponseDirections fixedDirections = null;

        if (distance1 > distance2) {
            fartherRequest = matchedMatchRequest;
            nearerRequest = matchRequest;
            fixedDirections = directionCase2;
        } else {
            fartherRequest = matchRequest;
            nearerRequest = matchedMatchRequest;
            fixedDirections = directionCase1;
        }

        TemporaryMatchInfo temporaryMatchInfo = TemporaryMatchInfo.builder()
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

        log.debug("Before Saving TemporaryMatchInfo : " + temporaryMatchInfo);
        TemporaryMatchInfo savedTemporaryMatchInfo = temporaryMatchInfoRepository.save(temporaryMatchInfo);
        log.debug("Saved TemporaryMatchInfo : " + savedTemporaryMatchInfo);

        Map<String, MatchResultInfoDTO> infoDTOMap = new HashMap<>();
        int totalFare = fixedDirections.getRoutes()
                .stream().findFirst().orElseThrow()
                .getSummary()
                .getFare()
                .getTaxi();
        TaxiFares taxiFares = taxiFareCalculator.calculateFare(totalFare,
                temporaryMatchInfo.getWaypointDistance(),
                temporaryMatchInfo.getDestinationDistance());
        PaymentRate paymentRate = taxiFareCalculator.calculatePaymentRate(taxiFares);
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

        if (matchedRequest.getMatchDecisionStatus().equals(MatchDecisionStatus.REJECTED)) { // 동시 요청 시 무시
            return;
        }

        matchRequest.setMatchDecisionStatus(null);
        matchedRequest.setMatchDecisionStatus(null);
        temporaryMatchInfoRepository.deleteBySessionId(matchRequest.getTempSessionId());
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

        TemporaryMatchInfo matchInfo = temporaryMatchInfoRepository.findById(sessionId).orElseThrow();
        log.debug("Found TemporaryMatchInfo : " + matchInfo);

        MatchRequest fartherReq = activeMatchRequests.get(matchInfo.getDestinationMatchRequestId());
        MatchRequest nearerReq = activeMatchRequests.get(matchInfo.getWaypointMatchRequestId());

        MatchResult matchResult = MatchResult.builder()
                .origin(matchInfo.getOrigin())
                .destination(matchInfo.getDestination())
                .waypoints(matchInfo.getWaypoints())
                .build();

        matchResultRepository.save(matchResult);
        matchResultMemberRepository.save(MatchResultMember.builder()
                .destination(fartherReq.getDestination())
                .distance(matchInfo.getDestinationDistance())
                .matchResult(matchResult)
                .member(memberRepository.findMemberByUsername(fartherReq.getUsername()).orElseThrow())
                .build());

        matchResultMemberRepository.save(MatchResultMember.builder()
                .destination(nearerReq.getDestination())
                .distance(matchInfo.getWaypointDistance())
                .matchResult(matchResult)
                .member(memberRepository.findMemberByUsername(nearerReq.getUsername()).orElseThrow())
                .build());

        activeMatchRequests.remove(fartherReq.getId());
        activeMatchRequests.remove(nearerReq.getId());
        temporaryMatchInfoRepository.delete(matchInfo);
    }


}
