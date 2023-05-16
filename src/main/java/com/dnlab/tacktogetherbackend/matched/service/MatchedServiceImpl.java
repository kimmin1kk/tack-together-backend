package com.dnlab.tacktogetherbackend.matched.service;

import com.dnlab.tacktogetherbackend.match.repository.MatchInfoMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;
import com.dnlab.tacktogetherbackend.matched.domain.redis.MatchSessionInfo;
import com.dnlab.tacktogetherbackend.matched.domain.redis.SessionMemberInfo;
import com.dnlab.tacktogetherbackend.matched.dto.LocationInfoResponseDTO;
import com.dnlab.tacktogetherbackend.matched.dto.LocationUpdateRequestDTO;
import com.dnlab.tacktogetherbackend.matched.repository.MatchSessionInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        return LocationInfoResponseDTO.builder()
                .username(username)
                .departureAgreed(locationUpdateRequestDTO.isDepartureAgreed())
                .location(locationUpdateRequestDTO.getLocation())
                .build();
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

    private SessionMemberInfo getSessionMemberInfoByUsername(MatchSessionInfo matchSessionInfo, String username) {
        return matchSessionInfo.getMemberInfos()
                .stream()
                .filter(sessionMemberInfo -> sessionMemberInfo.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
    }
}
