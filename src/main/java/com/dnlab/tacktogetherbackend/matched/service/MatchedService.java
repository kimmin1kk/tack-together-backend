package com.dnlab.tacktogetherbackend.matched.service;

import com.dnlab.tacktogetherbackend.matched.dto.*;

public interface MatchedService {
    LocationInfoResponseDTO handleLocationUpdate(LocationUpdateRequestDTO locationUpdateRequestDTO, String username);
    void handleStartRiding(String sessionId, String currentLocation);
    String getOpponentUsernameBySessionId(String sessionId, String username);
    DropOffNotificationDTO processDropOffRequest(DropOffRequestDTO dropOffRequestDTO, String username);
    SettlementReceivedRequestDTO processSettlementRequest(SettlementRequestDTO settlementRequestDTO, String username);
    SettlementInfoDTO getSettlementInfo(String username);
}
