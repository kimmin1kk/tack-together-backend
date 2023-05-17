package com.dnlab.tacktogetherbackend.matched.service;

import com.dnlab.tacktogetherbackend.global.util.TimestampUtil;
import com.dnlab.tacktogetherbackend.match.common.RidingStatus;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfoMember;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;
import com.dnlab.tacktogetherbackend.matched.domain.redis.MatchSessionInfo;
import com.dnlab.tacktogetherbackend.matched.domain.redis.SessionMemberInfo;
import com.dnlab.tacktogetherbackend.matched.dto.DropOffNotificationDTO;
import com.dnlab.tacktogetherbackend.matched.dto.DropOffRequestDTO;
import com.dnlab.tacktogetherbackend.matched.dto.LocationInfoResponseDTO;
import com.dnlab.tacktogetherbackend.matched.dto.LocationUpdateRequestDTO;
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
    private final MatchInfoMemberRepository matchInfoMemberRepository;
    private final MatchSessionInfoRepository matchSessionInfoRepository;

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

        MatchInfoMember waypointMember = matchInfoMembers.stream()
                .min(Comparator.comparing(MatchInfoMember::getDistance))
                .orElseThrow();
        MatchInfoMember destinationMember = matchInfoMembers.stream()
                .max(Comparator.comparing(MatchInfoMember::getDestination))
                .orElseThrow();

        if (waypointMember.getMember().getUsername().equals(username)) {
            matchInfo.setWaypoints(dropOffRequestDTO.getEndLocation());
            waypointMember.setDestination(dropOffRequestDTO.getEndLocation());
        } else if (destinationMember.getMember().getUsername().equals(username)) {
            matchInfo.setDestination(dropOffRequestDTO.getEndLocation());
            destinationMember.setDestination(dropOffRequestDTO.getEndLocation());
        }

        return DropOffNotificationDTO.builder()
                .dropOffLocation(dropOffRequestDTO.getEndLocation())
                .sessionId(dropOffRequestDTO.getSessionId())
                .username(username)
                .build();
    }

    private SessionMemberInfo getSessionMemberInfoByUsername(MatchSessionInfo matchSessionInfo, String username) {
        return matchSessionInfo.getMemberInfos()
                .stream()
                .filter(sessionMemberInfo -> sessionMemberInfo.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
    }
}
