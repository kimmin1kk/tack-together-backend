package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.auth.domain.Member;
import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.global.common.RedisEntityProperties;
import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.service.KakaoMapService;
import com.dnlab.tacktogetherbackend.match.common.*;
import com.dnlab.tacktogetherbackend.match.config.MatchRangeProperties;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfoMember;
import com.dnlab.tacktogetherbackend.match.common.RidingStatus;
import com.dnlab.tacktogetherbackend.match.domain.redis.TemporaryMatchSessionInfo;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResponseDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResultInfoDTO;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;
import com.dnlab.tacktogetherbackend.match.repository.TemporaryMatchSessionInfoRepository;
import com.dnlab.tacktogetherbackend.matched.domain.redis.MatchSessionInfo;
import com.dnlab.tacktogetherbackend.matched.domain.redis.SessionMemberInfo;
import com.dnlab.tacktogetherbackend.matched.repository.MatchSessionInfoRepository;
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
    private final MatchSessionInfoRepository matchSessionInfoRepository;
    private final KakaoMapService kakaoMapService;
    private final MatchRangeProperties matchRangeProperties;
    private final RedisEntityProperties redisProperties;
    private final TaxiFareCalculator taxiFareCalculator;

    @Override
    public String addMatchRequest(MatchRequestDTO matchRequestDTO, String username) {
        if (isMatchRequestExistInActiveMatchRequests(username)) {
            cancelSearchingByUsername(username);
        }

        Member member = memberRepository.findMemberByUsername(username).orElseThrow();
        MatchRequest matchRequest = new MatchRequest(matchRequestDTO, username, member.getNickname());
        activeMatchRequests.put(matchRequest.getId(), matchRequest);

        log.info("매칭 대기열이 추가됨 : {" + matchRequest + "}");
        log.info("매칭 대기열 : {" + activeMatchRequests + "}");
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
        log.info("적합한 요청 찾는 중 ...");
        List<MatchRequest> suitableRequests = activeMatchRequests.keySet().stream()
                .filter(key -> !key.equals(matchRequestId))
                .map(activeMatchRequests::get)
                .filter(req -> (!req.isMatched() && !req.getUsername().equals(matchRequest.getUsername()) && isSuitableRequests(matchRequest, req)))
                .collect(Collectors.toList());

        MatchRequest opponentMatchRequest = suitableRequests.stream()
                .findFirst()
                .orElse(null);

        if (opponentMatchRequest == null) {
            return null;
        }

        log.info("매칭 된 match request info : {" + opponentMatchRequest + "}");
        return opponentMatchRequest.getId();
    }

    @Override
    public PostMatchTemporaryInfo handlePendingMatched(String matchRequestId, String opponentMatchRequestId) {
        MatchRequest matchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        MatchRequest opponentMatchRequest = getMatchRequestById(opponentMatchRequestId).orElseThrow(NoSuchMatchRequestException::new);

        matchRequest.updateStatusToMatched(opponentMatchRequest);
        opponentMatchRequest.updateStatusToMatched(matchRequest);

        PostMatchTemporaryInfo postMatchTemporaryInfo = PostMatchTemporaryInfo.of(matchRequest, opponentMatchRequest, kakaoMapService);

        TemporaryMatchSessionInfo temporaryMatchSessionInfo = temporaryMatchSessionInfoRepository
                .save(TemporaryMatchSessionInfo.of(postMatchTemporaryInfo, redisProperties.getTtl()));
        log.debug("TemporaryMatchSessionInfo 가 저장됨 : {" + temporaryMatchSessionInfo + "}");

        postMatchTemporaryInfo.setTemporarySessionId(temporaryMatchSessionInfo.getSessionId());

        return postMatchTemporaryInfo;
    }

    @Override
    public Map<String, MatchResultInfoDTO> getMatchResultInfoMap(PostMatchTemporaryInfo postMatchTemporaryInfo) {
        TemporaryMatchSessionInfo temporaryMatchSessionInfo = temporaryMatchSessionInfoRepository.findById(postMatchTemporaryInfo.getFartherRequest().getTempSessionId())
                .orElseThrow();

        Map<String, MatchResultInfoDTO> infoDTOMap = new HashMap<>();

        int totalFare = taxiFareCalculator.getFareByResponseDirections(postMatchTemporaryInfo.getFixedDirections());
        TaxiFares taxiFares = taxiFareCalculator.calculateFare(totalFare,
                temporaryMatchSessionInfo.getWaypointDistance(),
                temporaryMatchSessionInfo.getDestinationDistance());

        infoDTOMap.put(postMatchTemporaryInfo.getNearerRequest().getId(), MatchResultInfoDTO.of(postMatchTemporaryInfo, taxiFares, false));
        infoDTOMap.put(postMatchTemporaryInfo.getFartherRequest().getId(), MatchResultInfoDTO.of(postMatchTemporaryInfo, taxiFares, true));
        log.debug("getMatchResultInfo 에서 반환되는 infoDTOMap : {" + infoDTOMap + "}");

        return infoDTOMap;
    }

    // 매칭 수락 로직
    @Override
    @Transactional
    public MatchResponseDTO acceptMatch(String matchRequestId) {
        MatchRequest matchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        matchRequest.setMatchDecisionStatus(MatchDecisionStatus.ACCEPTED);
        MatchRequest opponentMatchRequest = activeMatchRequests.get(matchRequest.getOpponentMatchRequestId());

        if (opponentMatchRequest.getMatchDecisionStatus().equals(MatchDecisionStatus.ACCEPTED)) {
            return handleAcceptedMatchedRequests(opponentMatchRequest.getTempSessionId());
        }

        return new MatchResponseDTO(MatchDecisionStatus.WAITING, "");
    }

    @Override
    public void rejectMatch(String matchRequestId) {
        MatchRequest matchRequest = getMatchRequestById(matchRequestId).orElseThrow(NoSuchMatchRequestException::new);
        matchRequest.setMatchDecisionStatus(MatchDecisionStatus.REJECTED);
        MatchRequest opponentMatchedRequest = activeMatchRequests.get(matchRequest.getOpponentMatchRequestId());

        matchRequest.resetStatus();
        opponentMatchedRequest.resetStatus();

        TemporaryMatchSessionInfo temporaryMatchSessionInfo = temporaryMatchSessionInfoRepository.findById(matchRequest.getTempSessionId()).orElseThrow();
        temporaryMatchSessionInfoRepository.delete(temporaryMatchSessionInfo);
    }

    private boolean isSuitableRequests(MatchRequest originReq, MatchRequest targetReq) {
        return !targetReq.isMatched()
                && isSuitableOriginRanges(originReq, targetReq)
                && isSuitableDestinations(originReq, targetReq);
    }

    @Override
    public void cancelSearchingByUsername(String username) {
        activeMatchRequests.keySet()
                .stream()
                .filter(key -> key.startsWith(username))
                .forEach(activeMatchRequests::remove);

        log.info("삭제 후 매칭 대기열 : " + activeMatchRequests);
    }

    private boolean isSuitableOriginRanges(MatchRequest req1, MatchRequest req2) {
        short minRange = (req1.getOriginRange() > req2.getOriginRange()) ? req2.getOriginRange() : req1.getOriginRange();
        return matchRangeProperties.convertRangeLevelToRange(minRange, RangeKind.ORIGIN) > calculateDistance(req1.getOrigin(), req2.getOrigin());
    }

    private boolean isSuitableDestinations(MatchRequest request1, MatchRequest request2) {
        int distance1 = kakaoMapService.getDistance(request1, request2);
        int distance2 = kakaoMapService.getDistance(request2, request1);

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

    private MatchResponseDTO handleAcceptedMatchedRequests(String sessionId) {
        log.debug("handleAcceptedMatchedRequests 메소드가 호출됨");

        TemporaryMatchSessionInfo tempMatchInfo = temporaryMatchSessionInfoRepository.findById(sessionId).orElseThrow();
        log.debug("handleAcceptedMatchedRequests 에서 조회 된 TemporaryMatchInfo : {" + tempMatchInfo + "}");

        MatchRequest fartherReq = activeMatchRequests.get(tempMatchInfo.getDestinationMatchRequestId());
        MatchRequest nearerReq = activeMatchRequests.get(tempMatchInfo.getWaypointMatchRequestId());

        MatchInfo matchInfo = saveMatchInfo(tempMatchInfo, fartherReq, nearerReq);

        MatchSessionInfo matchSessionInfo = saveMatchSessionInfo(fartherReq, nearerReq, matchInfo.getId());

        activeMatchRequests.remove(fartherReq.getId());
        activeMatchRequests.remove(nearerReq.getId());

        temporaryMatchSessionInfoRepository.delete(tempMatchInfo);

        return new MatchResponseDTO(MatchDecisionStatus.ACCEPTED, matchSessionInfo.getSessionId());
    }

    private MatchSessionInfo saveMatchSessionInfo(MatchRequest fartherRequest,
                                                  MatchRequest nearerRequest,
                                                  long matchInfoId) {
        Set<SessionMemberInfo> sessionMemberInfos = new HashSet<>();
        sessionMemberInfos.add(new SessionMemberInfo(fartherRequest.getUsername(), fartherRequest.getNickname(), false));
        sessionMemberInfos.add(new SessionMemberInfo(nearerRequest.getUsername(), nearerRequest.getNickname(), false));
        return matchSessionInfoRepository.save(new MatchSessionInfo(sessionMemberInfos, matchInfoId));
    }

    private MatchInfo saveMatchInfo(TemporaryMatchSessionInfo temporaryMatchSessionInfo,
                                    MatchRequest fartherRequest,
                                    MatchRequest nearerRequest) {

        MatchInfo matchInfo = MatchInfo.builder()
                .origin(temporaryMatchSessionInfo.getOrigin())
                .destination(temporaryMatchSessionInfo.getDestination())
                .waypoints(temporaryMatchSessionInfo.getWaypoints())
                .totalDistance(temporaryMatchSessionInfo.getDestinationDistance())
                .status(RidingStatus.WAITING)
                .build();

        matchInfoRepository.save(matchInfo);
        matchInfoMemberRepository.save(MatchInfoMember.builder()
                .destination(fartherRequest.getDestination())
                .distance(temporaryMatchSessionInfo.getDestinationDistance())
                .matchInfo(matchInfo)
                .member(memberRepository.findMemberByUsername(fartherRequest.getUsername()).orElseThrow())
                .build());

        matchInfoMemberRepository.save(MatchInfoMember.builder()
                .destination(nearerRequest.getDestination())
                .distance(temporaryMatchSessionInfo.getWaypointDistance())
                .matchInfo(matchInfo)
                .member(memberRepository.findMemberByUsername(nearerRequest.getUsername()).orElseThrow())
                .build());

        return matchInfo;
    }

    private boolean isMatchRequestExistInActiveMatchRequests(String username) {
        return activeMatchRequests.keySet()
                .stream()
                .map(activeMatchRequests::get)
                .anyMatch(matchRequest -> matchRequest.getUsername().equals(username));
    }

}
