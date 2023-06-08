package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.common.PostMatchTemporaryInfo;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResultInfoDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResponseDTO;

import java.util.Map;
import java.util.Optional;

public interface MatchService {
    String addMatchRequest(MatchRequestDTO matchRequestDTO, String username);
    Optional<MatchRequest> getMatchRequestById(String matchRequestId);
    void removeRideRequest(String matchRequestId);
    String findMatchingMatchRequests(String matchRequestId);
    PostMatchTemporaryInfo handlePendingMatched(String matchRequestId, String opponentMatchRequestId);
    Map<String, MatchResultInfoDTO> getMatchResultInfoMap(PostMatchTemporaryInfo postMatchTemporaryInfo);
    MatchResponseDTO acceptMatch(String matchRequestId);
    void rejectMatch(String matchRequestId);
    void cancelSearchingByUsername(String username);
}
