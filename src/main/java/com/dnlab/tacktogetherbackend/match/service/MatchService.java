package com.dnlab.tacktogetherbackend.match.service;

import com.dnlab.tacktogetherbackend.match.common.MatchDecisionStatus;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;

import java.util.Optional;

public interface MatchService {
    MatchRequest addMatchRequest(MatchRequestDTO matchRequestDTO);
    Optional<MatchRequest> getMatchRequestById(String matchRequestId);
    void removeRideRequest(String matchRequestId);
    MatchRequest findMatchingMatchRequests(MatchRequest matchRequest);
    void handlePendingMatched(MatchRequest matchRequest, MatchRequest matchedMatchRequests);
    MatchDecisionStatus acceptMatch(MatchRequest matchRequest);
    void rejectMatch(MatchRequest matchRequest);
    void resetMatchRequests();

}
