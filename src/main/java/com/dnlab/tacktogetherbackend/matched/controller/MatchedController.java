package com.dnlab.tacktogetherbackend.matched.controller;

import com.dnlab.tacktogetherbackend.matched.dto.LocationInfoResponseDTO;
import com.dnlab.tacktogetherbackend.matched.dto.LocationUpdateRequestDTO;
import com.dnlab.tacktogetherbackend.matched.service.MatchedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequiredArgsConstructor
public class MatchedController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MatchedService matchedService;

    private static final String DESTINATION_URL = "/queue/matched";

    @MessageMapping("/matched/shareLocation")
    public void handleSharingLocation(Principal principal,
                                      @Payload LocationUpdateRequestDTO locationUpdateRequestDTO) {
        String opponentUsername = matchedService.getOpponentUsernameBySessionId(locationUpdateRequestDTO.getSessionId(), principal.getName());
        LocationInfoResponseDTO locationInfoResponseDTO = matchedService.handleLocationUpdate(locationUpdateRequestDTO, principal.getName());
        messagingTemplate.convertAndSendToUser(opponentUsername, DESTINATION_URL, locationInfoResponseDTO);

        // 동승 시작 시 사용자에게도 메시지 전송
        if (locationInfoResponseDTO.isRidingStarted()) {
            messagingTemplate.convertAndSendToUser(principal.getName(), DESTINATION_URL, LocationInfoResponseDTO.builder()
                    .sessionId(locationUpdateRequestDTO.getSessionId())
                    .ridingStarted(true)
                    .build());
        }
    }

    @MessageMapping("/matched/startRiding")
    public void handleStartRiding(Principal principal) {
    }
}
