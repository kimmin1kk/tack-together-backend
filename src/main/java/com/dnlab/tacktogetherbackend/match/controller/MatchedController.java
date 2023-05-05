package com.dnlab.tacktogetherbackend.match.controller;

import com.dnlab.tacktogetherbackend.match.dto.LocationSharingDTO;
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

    private static final String DESTINATION_URL = "/queue/matched";

    @MessageMapping("/matched/shareLocation")
    public void handleSharingLocation(Principal principal,
                                      @Payload LocationSharingDTO locationSharingDTO) {
        messagingTemplate.convertAndSendToUser(locationSharingDTO.getOpponentUsername(),
                DESTINATION_URL,
                new LocationSharingDTO(principal.getName(), locationSharingDTO.getLocation()));
    }


}
