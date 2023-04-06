package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.kakao.service.KakaoMapService;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.config.MatchRangeProperties;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.repository.MatchResultMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final Map<String, MatchRequest> activeMatchRequests = new ConcurrentHashMap<>();
    private final Map<String, List<MatchRequest>> pendingMatchedMatchRequests = new ConcurrentHashMap<>();
    private final MatchResultRepository matchResultRepository;
    private final MatchResultMemberRepository matchResultMemberRepository;
    private final KakaoMapService kakaoMapService;
    private final MatchRangeProperties matchRangeProperties;

    @Override
    public MatchRequest addMatchRequest(MatchRequestDTO matchRequestDTO) {
        MatchRequest matchRequest = new MatchRequest(matchRequestDTO);
        activeMatchRequests.put(matchRequest.getId(), matchRequest);
        return matchRequest;
    }

    @Override
    public MatchRequest getMatchRequestById(String matchRequestId) {
        return activeMatchRequests.get(matchRequestId);
    }

    @Override
    public void removeRideRequest(String matchRequestId) {
        activeMatchRequests.remove(matchRequestId);
    }

    @Override
    public MatchRequest findMatchingMatchRequests(MatchRequest matchRequest) {
        // 매칭 로직 구현
        return null;
    }

    // 매칭이 성사된 후 대기 상태를 처리
    @Override
    public void handlePendingMatched(MatchRequest matchRequest, MatchRequest matchedMatchRequests) {

    }

    @Override
    public void acceptMatch(MatchRequest matchRequest, MatchRequest matchedMatchRequest) {
        // 매칭 수락 로직
    }

    @Override
    public void rejectMatch(MatchRequest matchRequest, MatchRequest matchedMatchRequest) {
        // 매칭 거절 로직
    }
}
