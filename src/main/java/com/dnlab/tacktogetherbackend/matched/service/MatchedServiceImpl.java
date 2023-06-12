package com.dnlab.tacktogetherbackend.matched.service;

import com.dnlab.tacktogetherbackend.global.util.TimestampUtil;
import com.dnlab.tacktogetherbackend.kakao.common.dto.RequestDirections;
import com.dnlab.tacktogetherbackend.kakao.service.KakaoMapService;
import com.dnlab.tacktogetherbackend.match.common.RidingStatus;
import com.dnlab.tacktogetherbackend.match.common.TaxiFareCalculator;
import com.dnlab.tacktogetherbackend.match.common.TaxiFares;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfoMember;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;
import com.dnlab.tacktogetherbackend.matched.domain.redis.MatchSessionInfo;
import com.dnlab.tacktogetherbackend.matched.domain.redis.SessionMemberInfo;
import com.dnlab.tacktogetherbackend.matched.dto.*;
import com.dnlab.tacktogetherbackend.matched.repository.MatchSessionInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class MatchedServiceImpl implements MatchedService {
    private final MatchInfoRepository matchInfoRepository;
    private final MatchSessionInfoRepository matchSessionInfoRepository;
    private final KakaoMapService kakaoMapService;
    private final TaxiFareCalculator taxiFareCalculator;

    @Override
    public LocationInfoResponseDTO handleLocationUpdate(LocationUpdateRequestDTO locationUpdateRequestDTO, String username) {
        MatchSessionInfo matchSessionInfo = matchSessionInfoRepository.findById(locationUpdateRequestDTO.getSessionId()).orElseThrow();
        SessionMemberInfo sessionMemberInfo = getSessionMemberInfoByUsername(matchSessionInfo, username);
        if (locationUpdateRequestDTO.isDepartureAgreed() != sessionMemberInfo.isDepartureAgreed()) {
            sessionMemberInfo.setDepartureAgreed(locationUpdateRequestDTO.isDepartureAgreed());
            matchSessionInfoRepository.save(matchSessionInfo);
        }

        // 동승자들이 모두 동의하여 매칭이 시작되야할 경우
        boolean ridingStarted = matchSessionInfo.getMemberInfos()
                .stream()
                .allMatch(SessionMemberInfo::isDepartureAgreed);

        return LocationInfoResponseDTO.builder()
                .sessionId(locationUpdateRequestDTO.getSessionId())
                .username(username)
                .departureAgreed(locationUpdateRequestDTO.isDepartureAgreed())
                .location(locationUpdateRequestDTO.getLocation())
                .ridingStarted(ridingStarted)
                .build();
    }

    @Override
    @Transactional
    public void handleStartRiding(String sessionId, String currentLocation) {
        MatchSessionInfo matchSessionInfo = matchSessionInfoRepository.findById(sessionId).orElseThrow();
        MatchInfo matchInfo = matchInfoRepository.findById(matchSessionInfo.getMatchInfoId()).orElseThrow();

        matchInfo.setRidingStartTime(TimestampUtil.getCurrentTime());
        matchInfo.setStatus(RidingStatus.ONGOING);
        matchInfo.setOrigin(currentLocation);
    }

    @Override
    public String getOpponentUsernameBySessionId(String sessionId, String username) {
        return matchSessionInfoRepository.findById(sessionId)
                .orElseThrow()
                .getMemberInfos()
                .stream()
                .filter(sessionMemberInfo -> !sessionMemberInfo.getUsername().equals(username))
                .findFirst()
                .orElseThrow()
                .getUsername();
    }

    @Override
    @Transactional
    public DropOffNotificationDTO processDropOffRequest(DropOffRequestDTO dropOffRequestDTO, String username) {
        MatchSessionInfo matchSessionInfo = matchSessionInfoRepository.findById(dropOffRequestDTO.getSessionId()).orElseThrow();
        MatchInfo matchInfo = matchInfoRepository.findById(matchSessionInfo.getMatchInfoId()).orElseThrow();
        Set<MatchInfoMember> matchInfoMembers = matchInfo.getMatchInfoMembers();

        // 두 사용자의 이동 거리를 비교하여 waypointMember 와 destinationMember 결정
        MatchInfoMember waypointMember = matchInfoMembers.stream()
                .min(Comparator.comparing(MatchInfoMember::getDistance))
                .orElseThrow();
        MatchInfoMember destinationMember = matchInfoMembers.stream()
                .max(Comparator.comparing(MatchInfoMember::getDistance))
                .orElseThrow();

        if (waypointMember.getMember().getUsername().equals(username)) {
            matchInfo.setWaypoints(dropOffRequestDTO.getEndLocation());
            waypointMember.setDestination(dropOffRequestDTO.getEndLocation());
            waypointMember.setDistance(kakaoMapService.getDistance(RequestDirections.builder()
                    .origin(matchInfo.getOrigin())
                    .destination(waypointMember.getDestination())
                    .build()));
            waypointMember.setDropOffTime(TimestampUtil.getCurrentTime());

        } else if (destinationMember.getMember().getUsername().equals(username)) {
            int distance = kakaoMapService.getDistance(RequestDirections.builder()
                    .origin(matchInfo.getOrigin())
                    .waypoints(waypointMember.getDestination())
                    .destination(destinationMember.getDestination())
                    .build());
            matchInfo.setDestination(dropOffRequestDTO.getEndLocation());
            matchInfo.setTotalDistance(distance);
            matchInfo.setStatus(RidingStatus.DROP_OFFED);
            destinationMember.setDestination(dropOffRequestDTO.getEndLocation());
            destinationMember.setDistance(distance);
            destinationMember.setDropOffTime(TimestampUtil.getCurrentTime());
        }

        return DropOffNotificationDTO.of(dropOffRequestDTO, username);
    }

    private SessionMemberInfo getSessionMemberInfoByUsername(MatchSessionInfo matchSessionInfo, String username) {
        return matchSessionInfo.getMemberInfos()
                .stream()
                .filter(sessionMemberInfo -> sessionMemberInfo.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
    }

    @Override
    @Transactional
    public SettlementReceivedRequestDTO processSettlementRequest(SettlementRequestDTO settlementRequestDTO, String username) {
        MatchSessionInfo matchSessionInfo = matchSessionInfoRepository.findById(settlementRequestDTO.getSessionId()).orElseThrow();
        MatchInfo matchInfo = matchInfoRepository.findById(matchSessionInfo.getMatchInfoId()).orElseThrow();
        matchInfo.setTotalFare(settlementRequestDTO.getTotalFare());
        matchInfo.setStatus(RidingStatus.COMPLETE);
        matchInfo.setMatchEndTime(TimestampUtil.getCurrentTime());

        Set<MatchInfoMember> matchInfoMembers = matchInfo.getMatchInfoMembers();
        MatchInfoMember waypointMatchInfoMember = matchInfoMembers.stream().min(Comparator.comparing(MatchInfoMember::getDistance)).orElseThrow();
        MatchInfoMember destinationMatchInfoMember = matchInfoMembers.stream().max(Comparator.comparing(MatchInfoMember::getDistance)).orElseThrow();

        log.debug("matchSessionInfo in processSettlementRequest: {" + matchSessionInfo + "}");
        int waypointPaymentAmount = (int) ((double) settlementRequestDTO.getTotalFare() * (matchSessionInfo.getWaypointFareRate() / 100));
        int destinationPaymentAmount = settlementRequestDTO.getTotalFare() - waypointPaymentAmount;

        waypointMatchInfoMember.setPaymentAmount(waypointPaymentAmount);
        destinationMatchInfoMember.setPaymentAmount(destinationPaymentAmount);
        matchSessionInfoRepository.delete(matchSessionInfo);

        RouteInfoDTO routeInfoDTO = RouteInfoDTO.builder()
                .origin(matchInfo.getOrigin())
                .waypoint(waypointMatchInfoMember.getDestination())
                .destination(destinationMatchInfoMember.getDestination())
                .build();

        return SettlementReceivedRequestDTO.builder()
                .sessionId(settlementRequestDTO.getSessionId())
                .requestedFare(waypointMatchInfoMember.getPaymentAmount())
                .accountInfo(settlementRequestDTO.getAccountInfo())
                .totalFare(settlementRequestDTO.getTotalFare())
                .username(matchSessionInfo.getMemberInfos()
                        .stream()
                        .filter(sessionMemberInfo -> !sessionMemberInfo.getUsername().equals(username))
                        .findAny().orElseThrow()
                        .getUsername())
                .destinationRate(matchSessionInfo.getDestinationFareRate())
                .waypointRate(matchSessionInfo.getWaypointFareRate())
                .routeInfo(routeInfoDTO)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SettlementInfoDTO getSettlementInfo(String username, String sessionId) {
        List<MatchInfo> matchInfos = matchInfoRepository.findMatchInfosByUsernameAndStatus(username, RidingStatus.DROP_OFFED);
        MatchInfo matchInfo = matchInfos.stream().findFirst().orElseThrow();
        Set<MatchInfoMember> matchInfoMembers = matchInfo.getMatchInfoMembers();

        MatchInfoMember waypointMember = matchInfoMembers.stream()
                .min(Comparator.comparing(MatchInfoMember::getDistance))
                .orElseThrow();
        MatchInfoMember destinationMember = matchInfoMembers.stream()
                .max(Comparator.comparing(MatchInfoMember::getDistance))
                .orElseThrow();

        TaxiFares taxiFares = taxiFareCalculator.calculateFare(10000,
                waypointMember.getDistance(),
                destinationMember.getDistance());

        Optional<MatchSessionInfo> optionalMatchSessionInfo = matchSessionInfoRepository.findById(sessionId);
        if (optionalMatchSessionInfo.isPresent()) {
            MatchSessionInfo matchSessionInfo = optionalMatchSessionInfo.get();
            matchSessionInfo.setDestinationFareRate(taxiFares.getDestinationRate());
            matchSessionInfo.setWaypointFareRate(taxiFares.getWaypointRate());
            matchSessionInfoRepository.save(matchSessionInfo);
            log.debug("matchSessionInfo in getSettlementInfo: {" + matchSessionInfo + "}");
        }

        return SettlementInfoDTO.builder()
                .origin(matchInfo.getOrigin())
                .totalDistance(destinationMember.getDistance())
                .waypoint(waypointMember.getDestination())
                .destination(matchInfo.getDestination())
                .paymentRate(taxiFares.getDestinationRate())
                .opponentPaymentRate(taxiFares.getWaypointRate())
                .build();
    }
}
