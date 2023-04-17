package com.dnlab.tacktogetherbackend.match.controller;

import com.dnlab.tacktogetherbackend.match.common.MatchDecisionStatus;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.dto.UserResponse;
import com.dnlab.tacktogetherbackend.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Objects;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequiredArgsConstructor
public class MatchController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MatchService matchService;

    private static final String MATCH_REQUEST_ID = "matchRequestId";
    private static final String DESTINATION_URL = "/queue/match";

    // 매칭 요청 처리
    @MessageMapping("/match/request")
    public void handleMatchRequest(MatchRequestDTO matchRequestDTO,
                                   SimpMessageHeaderAccessor headerAccessor,
                                   Principal principal) {
        log.debug("매칭 요청 수신");
        // DTO 로부터 MatchRequest 객체를 생성하고 맵에 추가
        matchRequestDTO.setUsername(principal.getName());
        final MatchRequest matchRequest = matchService.addMatchRequest(matchRequestDTO);
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put(MATCH_REQUEST_ID, matchRequest.getId());

        // 매칭 조건에 맞는 매칭 요청 찾기
        MatchRequest matchedMatchRequest = matchService.findMatchingMatchRequests(matchRequest);

        // 매칭 조건이 맞으면 각 사용자들에게 매칭 정보 전송
        if (matchedMatchRequest != null) {
            log.info("Match Succeed!");
            log.info("matched match request info : " + matchedMatchRequest);
            matchRequest.setMatchedMatchRequestId(matchedMatchRequest.getId());
            matchedMatchRequest.setMatchedMatchRequestId(matchRequest.getId());

            messagingTemplate.convertAndSendToUser(matchedMatchRequest.getUsername(), DESTINATION_URL, matchRequest);
            messagingTemplate.convertAndSendToUser(matchRequest.getUsername(), DESTINATION_URL, matchedMatchRequest);
            matchService.handlePendingMatched(matchRequest, matchedMatchRequest);
        }
    }

    // 매칭 수락 처리
    @MessageMapping("/match/accept")
    public void handleAccept(@Payload String matchedRequestId, SimpMessageHeaderAccessor headerAccessor) {
        String matchRequestId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(MATCH_REQUEST_ID);
        log.info(matchRequestId);
        MatchRequest matchRequest = matchService.getMatchRequestById(matchRequestId).orElseThrow();
        MatchRequest matchedRequest = matchService.getMatchRequestById(matchRequest.getMatchedMatchRequestId()).orElseThrow();

        MatchDecisionStatus status = matchService.acceptMatch(matchRequest);

        if (status.equals(MatchDecisionStatus.ACCEPTED)) {
            sendAcceptedMessage(matchRequest, matchedRequest);
        } else {
            messagingTemplate.convertAndSendToUser(matchRequest.getUsername(), DESTINATION_URL, new UserResponse(status.toString()));
        }
    }

    // 매칭 거절 처리
    @MessageMapping("/match/reject")
    public void handleReject(@Payload String matchedRequestId, SimpMessageHeaderAccessor headerAccessor) {
        String matchRequestId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(MATCH_REQUEST_ID);
        MatchRequest matchRequest = matchService.getMatchRequestById(matchRequestId).orElseThrow();

        matchService.rejectMatch(matchRequest);
    }

    // WebSocket 연결 해제 처리
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String matchRequestId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(MATCH_REQUEST_ID);

        if (!matchRequestId.isBlank()) {
            matchService.removeRideRequest(matchRequestId);
        }
    }

    private void sendAcceptedMessage(MatchRequest m1, MatchRequest m2) {
        messagingTemplate.convertAndSendToUser(m1.getUsername(), DESTINATION_URL, new UserResponse(MatchDecisionStatus.ACCEPTED.toString()));
        messagingTemplate.convertAndSendToUser(m2.getUsername(), DESTINATION_URL, new UserResponse(MatchDecisionStatus.ACCEPTED.toString()));
    }
}
