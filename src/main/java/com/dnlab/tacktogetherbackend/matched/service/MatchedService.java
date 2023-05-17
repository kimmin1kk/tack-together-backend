package com.dnlab.tacktogetherbackend.matched.service;

import com.dnlab.tacktogetherbackend.matched.dto.DropOffNotificationDTO;
import com.dnlab.tacktogetherbackend.matched.dto.DropOffRequestDTO;
import com.dnlab.tacktogetherbackend.matched.dto.LocationUpdateRequestDTO;
import com.dnlab.tacktogetherbackend.matched.dto.LocationInfoResponseDTO;

public interface MatchedService {
    LocationInfoResponseDTO handleLocationUpdate(LocationUpdateRequestDTO locationUpdateRequestDTO, String username);
    void handleStartRiding(String sessionId, String currentLocation);
    String getOpponentUsernameBySessionId(String sessionId, String username);
    DropOffNotificationDTO processDropOffRequest(DropOffRequestDTO dropOffRequestDTO, String username);
}
