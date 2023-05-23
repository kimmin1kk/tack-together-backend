package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResultInfoDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResponseDTO;

import java.util.Map;
import java.util.Optional;

public interface MatchService {
    String addMatchRequest(MatchRequestDTO matchRequestDTO);
    Optional<MatchRequest> getMatchRequestById(String matchRequestId);
    void removeRideRequest(String matchRequestId);
    String findMatchingMatchRequests(String matchRequestId);
    Map<String, MatchResultInfoDTO> handlePendingMatchedAndGetMatchResultInfos(String matchRequestId, String matchedMatchRequestsId);
    MatchResponseDTO acceptMatch(String matchRequestId);
    void rejectMatch(String matchRequestId);
    void resetMatchRequests();
    void cancelSearchingByUsername(String username);
}
