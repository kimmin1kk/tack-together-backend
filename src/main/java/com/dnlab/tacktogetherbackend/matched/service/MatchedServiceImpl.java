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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Set;


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
                .max(Comparator.comparing(MatchInfoMember::getDestination))
                .orElseThrow();

        if (waypointMember.getMember().getUsername().equals(username)) {
            matchInfo.setWaypoints(dropOffRequestDTO.getEndLocation());
            waypointMember.setDestination(dropOffRequestDTO.getEndLocation());
            waypointMember.setDistance(kakaoMapService.getDistance(RequestDirections.builder()
                    .origin(matchInfo.getOrigin())
                    .destination(waypointMember.getDestination())
                    .build()));

        } else if (destinationMember.getMember().getUsername().equals(username)) {
            int distance = kakaoMapService.getDistance(RequestDirections.builder()
                    .origin(matchInfo.getOrigin())
                    .waypoints(waypointMember.getDestination())
                    .destination(destinationMember.getDestination())
                    .build());
            matchInfo.setDestination(dropOffRequestDTO.getEndLocation());
            matchInfo.setTotalDistance(distance);
            destinationMember.setDestination(dropOffRequestDTO.getEndLocation());
            destinationMember.setDistance(distance);
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

        Set<MatchInfoMember> matchInfoMembers = matchInfo.getMatchInfoMembers();
        MatchInfoMember waypointMatchInfoMember = matchInfoMembers.stream().min(Comparator.comparing(MatchInfoMember::getDistance)).orElseThrow();
        MatchInfoMember destinationMatchInfoMember = matchInfoMembers.stream().min(Comparator.comparing(MatchInfoMember::getDistance)).orElseThrow();

        TaxiFares taxiFares = taxiFareCalculator.calculateFare(settlementRequestDTO.getTotalFare(),
                waypointMatchInfoMember.getDistance(),
                destinationMatchInfoMember.getDistance());

        waypointMatchInfoMember.setPaymentAmount(taxiFares.getWaypointFare());
        destinationMatchInfoMember.setPaymentAmount(taxiFares.getDestinationFare());
        matchSessionInfoRepository.delete(matchSessionInfo);

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
                .destinationRate(taxiFares.getDestinationRate())
                .waypointRate(taxiFares.getWaypointRate())
                .build();
    }
}
