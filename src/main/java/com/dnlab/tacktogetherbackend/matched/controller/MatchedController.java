package com.dnlab.tacktogetherbackend.matched.controller;

import com.dnlab.tacktogetherbackend.matched.dto.*;
import com.dnlab.tacktogetherbackend.matched.service.MatchedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequiredArgsConstructor
public class MatchedController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MatchedService matchedService;

    private static final String DESTINATION_URL = "/queue/matched";

    @Value("${stomp.header-event-type}")
    private String headerEventType;

    @MessageMapping("/matched/shareLocation")
    public void handleSharingLocation(Principal principal,
                                      @Payload LocationUpdateRequestDTO locationUpdateRequestDTO) {
        String opponentUsername = matchedService.getOpponentUsernameBySessionId(locationUpdateRequestDTO.getSessionId(), principal.getName());
        LocationInfoResponseDTO locationInfoResponseDTO = matchedService.handleLocationUpdate(locationUpdateRequestDTO, principal.getName());


        Map<String, Object> headers = Collections.singletonMap(headerEventType, "request");
        messagingTemplate.convertAndSendToUser(opponentUsername, DESTINATION_URL, new GenericMessage<>(locationInfoResponseDTO, headers));

        // 동승 시작 시 사용자에게도 메시지 전송
        if (locationInfoResponseDTO.isRidingStarted()) {
            matchedService.handleStartRiding(locationInfoResponseDTO.getSessionId(), locationInfoResponseDTO.getLocation());
            messagingTemplate.convertAndSendToUser(principal.getName(), DESTINATION_URL, new GenericMessage<>(LocationInfoResponseDTO.builder()
                    .sessionId(locationUpdateRequestDTO.getSessionId())
                    .ridingStarted(true)
                    .build(), headers));
        }
    }

    @MessageMapping("/matched/dropOff")
    public void handleDropOffRequest(Principal principal,
                                     @Payload DropOffRequestDTO dropOffRequestDTO) {
        String opponentUsername = matchedService.getOpponentUsernameBySessionId(dropOffRequestDTO.getSessionId(), principal.getName());
        DropOffNotificationDTO dropOffNotificationDTO = matchedService.processDropOffRequest(dropOffRequestDTO, principal.getName());

        Map<String, Object> headers = Collections.singletonMap(headerEventType, "dropOff");
        messagingTemplate.convertAndSendToUser(opponentUsername, DESTINATION_URL, new GenericMessage<>(dropOffNotificationDTO, headers));
    }


    @MessageMapping("/matched/settlement")
    public void handleSettlementRequest(Principal principal,
                                        @Payload SettlementRequestDTO settlementRequestDTO) {
        SettlementReceivedRequestDTO settlementReceivedRequestDTO = matchedService.processSettlementRequest(settlementRequestDTO, principal.getName());

        Map<String, Object> headers = Collections.singletonMap(headerEventType, "settlement");
        messagingTemplate.convertAndSendToUser(settlementReceivedRequestDTO.getUsername(), DESTINATION_URL, new GenericMessage<>(settlementReceivedRequestDTO, headers));
    }

}
