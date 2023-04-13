package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.service.KakaoMapService;
import com.dnlab.tacktogetherbackend.match.common.MatchDecisionStatus;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.config.MatchRangeProperties;
import com.dnlab.tacktogetherbackend.match.domain.MatchResult;
import com.dnlab.tacktogetherbackend.match.domain.MatchResultMember;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.repository.MatchResultMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalPosition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final MemberRepository memberRepository;
    private final Map<String, MatchRequest> activeMatchRequests = new ConcurrentHashMap<>();
    private final Map<String, MatchRequest> waitingMatchedRequests = new ConcurrentHashMap<>();

    private final MatchResultRepository matchResultRepository;
    private final MatchResultMemberRepository matchResultMemberRepository;
    private final KakaoMapService kakaoMapService;
    private final MatchRangeProperties matchRangeProperties;

    @Override
    public MatchRequest addMatchRequest(MatchRequestDTO matchRequestDTO) {
        MatchRequest matchRequest = new MatchRequest(matchRequestDTO);
        activeMatchRequests.put(matchRequest.getId(), matchRequest);

        log.info("MatchRequest is added, " + matchRequest);
        return matchRequest;
    }

    @Override
    public Optional<MatchRequest> getMatchRequestById(String matchRequestId) {
        MatchRequest matchRequest = activeMatchRequests.get(matchRequestId);
        if (matchRequest == null) {
            matchRequest = waitingMatchedRequests.get(matchRequestId);
        }
        return Optional.of(matchRequest);
    }

    @Override
    public void removeRideRequest(String matchRequestId) {
        activeMatchRequests.remove(matchRequestId);
    }

    @Override
    public MatchRequest findMatchingMatchRequests(MatchRequest matchRequest) {
        log.info("finding MatchReq");
        // 매칭 로직 구현
        List<MatchRequest> suitableRequests = activeMatchRequests.keySet().stream()
                .filter(key -> !key.equals(matchRequest.getId()))
                .map(activeMatchRequests::get)
                .filter(req -> isSuitableRequests(matchRequest, req))
                .collect(Collectors.toList());

        return suitableRequests.stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void handlePendingMatched(MatchRequest matchRequest, MatchRequest matchedMatchRequest) {
        // 매칭이 성사된 후 대기 상태를 처리
        matchRequest.setMatchDecisionStatus(MatchDecisionStatus.WAITING);
        matchedMatchRequest.setMatchDecisionStatus(MatchDecisionStatus.WAITING);

        // 매칭 대상에 포함되지 않게 다른 map 으로 격리
        waitingMatchedRequests.put(matchRequest.getId(), activeMatchRequests.remove(matchRequest.getId()));
        waitingMatchedRequests.put(matchedMatchRequest.getId(), activeMatchRequests.remove(matchedMatchRequest.getId()));
    }

    // 매칭 수락 로직
    @Override
    @Transactional
    public MatchDecisionStatus acceptMatch(MatchRequest matchRequest) {
        matchRequest.setMatchDecisionStatus(MatchDecisionStatus.ACCEPTED);
        MatchRequest matchedRequest = waitingMatchedRequests.get(matchRequest.getMatchedMatchRequestId());

        switch (matchedRequest.getMatchDecisionStatus()) {
            case ACCEPTED:
                handleAcceptedMatchedRequests(matchRequest, matchedRequest);
                break;
            case REJECTED:
            case WAITING:
            default:
        }

        return null;
    }

    @Override
    public void rejectMatch(MatchRequest matchRequest) {
        // 매칭 거절 로직
    }

    @Override
    public void resetMatchRequests() {
        activeMatchRequests.clear();
        waitingMatchedRequests.clear();
    }

    private boolean isSuitableRequests(MatchRequest originReg, MatchRequest targetReq) {
        return isSuitableOriginRanges(originReg, targetReq) && isSuitableDestinations(originReg, targetReq);
    }

    private boolean isSuitableOriginRanges(MatchRequest req1, MatchRequest req2) {
        short minRange = (req1.getOriginRange() > req2.getOriginRange()) ? req2.getOriginRange() : req1.getOriginRange();
        return convertOriginRangeLevelToRange(minRange) > calculateDistance(req1.getOrigin(), req2.getOrigin());
    }

    private boolean isSuitableDestinations(MatchRequest req1, MatchRequest req2) {
        int distance1 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(req1.getOrigin())
                .destination(req1.getDestination())
                .waypoints(req2.getDestination())
                .build());

        int distance2 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(req2.getOrigin())
                .destination(req2.getDestination())
                .waypoints(req1.getDestination())
                .build());

        MatchRequest finalDestinationReq = distance1 < distance2 ? req1 : req2;
        int originDistance = kakaoMapService.getDistance(RequestDirections.ofMatchRequest(finalDestinationReq));
        int shorterTotalDistance = Math.min(distance1, distance2);

        return convertDestinationRangeLevelToRange(req1.getDestinationRange() < req2.getDestinationRange() ? req1.getDestinationRange() : req2.getDestinationRange()) > (shorterTotalDistance - Objects.requireNonNull(originDistance));
    }

    /**
     * @param rangeLevel 범위 레벨, narrow : 0, normal : 1, wide : 2
     * @return 범위 레벨에 따른 범위 반환
     */
    private int convertOriginRangeLevelToRange(short rangeLevel) {
        MatchRangeProperties.Range range = matchRangeProperties.getOrigin();
        return getRangeByRangeLevel(rangeLevel, range);
    }

    private int convertDestinationRangeLevelToRange(short rangeLevel) {
        MatchRangeProperties.Range range = matchRangeProperties.getDestination();
        return getRangeByRangeLevel(rangeLevel, range);
    }

    private int getRangeByRangeLevel(short rangeLevel, MatchRangeProperties.Range range) {
        switch (rangeLevel) {
            case 0:
                return range.getNarrow();
            case 1:
                return range.getNormal();
            case 2:
                return range.getWide();
            default:
                throw new IllegalArgumentException("올바른 레벨 단위를 입력해주세요.");
        }
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

    private void handleAcceptedMatchedRequests(MatchRequest match1, MatchRequest match2) {

        int distance1 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(match1.getOrigin())
                .destination(match1.getDestination())
                .waypoints(match2.getDestination())
                .build());

        int distance2 = kakaoMapService.getDistance(RequestDirections.builder()
                .origin(match2.getOrigin())
                .destination(match2.getDestination())
                .waypoints(match1.getDestination())
                .build());

        log.info("Distances are Called");
        log.info("distance1: " + distance1);
        log.info("distance2: " + distance2);

        MatchRequest fartherReq;
        MatchRequest nearerReq;

        if (distance1 > distance2) {
            fartherReq = match2;
            nearerReq = match1;
        } else {
            fartherReq = match1;
            nearerReq = match2;
        }

        MatchResult matchResult = MatchResult.builder()
                .origin(fartherReq.getOrigin())
                .destination(fartherReq.getDestination())
                .waypoints(nearerReq.getDestination())
                .build();

        matchResultRepository.save(matchResult);
        matchResultMemberRepository.save(MatchResultMember.builder()
                .destination(fartherReq.getDestination())
                .distance(Math.min(distance1, distance2))
                .matchResult(matchResult)
                .member(memberRepository.findMemberByUsername(fartherReq.getUsername()).orElseThrow())
                .build());
        matchResultMemberRepository.save(MatchResultMember.builder()
                .destination(nearerReq.getDestination())
                .distance(kakaoMapService.getDistance(RequestDirections.ofMatchRequest(nearerReq)))
                .matchResult(matchResult)
                .member(memberRepository.findMemberByUsername(nearerReq.getUsername()).orElseThrow())
                .build());
    }

    private void handleRejected(MatchRequest m1, MatchRequest m2) {

    }
}
