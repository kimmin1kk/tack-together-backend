package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;

public interface MatchService {
    MatchRequest addMatchRequest(MatchRequestDTO matchRequestDTO);
    MatchRequest getMatchRequestById(String matchRequestId);
    void removeRideRequest(String matchRequestId);
    MatchRequest findMatchingMatchRequests(MatchRequest matchRequest);
    void handlePendingMatched(MatchRequest matchRequest, MatchRequest matchedMatchRequests);
    void acceptMatch(MatchRequest matchRequest, MatchRequest matchedMatchRequest);
    void rejectMatch(MatchRequest matchRequest, MatchRequest matchedMatchRequest);
    void resetActiveMatchRequests();

}
