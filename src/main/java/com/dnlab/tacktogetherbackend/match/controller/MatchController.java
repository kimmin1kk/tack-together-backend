package com.dnlab.tacktogetherbackend.match.controller;

import com.dnlab.tacktogetherbackend.match.common.MatchDecisionStatus;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.common.PostMatchTemporaryInfo;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResultInfoDTO;
import com.dnlab.tacktogetherbackend.match.dto.MatchResponseDTO;
import com.dnlab.tacktogetherbackend.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
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

    @Value("${stomp.header-event-type}")
    private String headerEventType;

    // 매칭 요청 처리
    @MessageMapping("/match/request")
    public void handleMatchRequest(MatchRequestDTO matchRequestDTO,
                                   SimpMessageHeaderAccessor headerAccessor,
                                   Principal principal) {
        log.debug("매칭 요청 수신");
        // DTO 로부터 MatchRequest 객체를 생성하고 맵에 추가
        matchRequestDTO.setUsername(principal.getName());
        String matchRequestId = matchService.addMatchRequest(matchRequestDTO);
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put(MATCH_REQUEST_ID, matchRequestId);

        // 매칭 조건에 맞는 매칭 요청 찾기
        String opponentMatchRequestId = matchService.findMatchingMatchRequests(matchRequestId);

        // 매칭 조건이 맞으면 각 사용자들에게 매칭 정보 전송
        if (opponentMatchRequestId != null && !opponentMatchRequestId.isBlank()) {
            log.info("Match Succeed!");

            // 매칭 결과 생성
            PostMatchTemporaryInfo postMatchTemporaryInfo = matchService.handlePendingMatched(matchRequestId, opponentMatchRequestId);
            Map<String, MatchResultInfoDTO> resultInfoDTOMap = matchService.getMatchResultInfoMap(postMatchTemporaryInfo);
            log.debug("resultInfoDTOMap : " + resultInfoDTOMap);
            log.debug("opponentMatchRequestId : " + resultInfoDTOMap.get(opponentMatchRequestId));
            log.debug("matchRequestId : " + resultInfoDTOMap.get(matchRequestId));

            // 매칭 결과를 각각 전송
            Map<String, Object> headers = Collections.singletonMap(headerEventType, "request");
            messagingTemplate.convertAndSendToUser(resultInfoDTOMap.get(opponentMatchRequestId).getUsername(), DESTINATION_URL, new GenericMessage<>(resultInfoDTOMap.get(opponentMatchRequestId), headers));
            messagingTemplate.convertAndSendToUser(principal.getName(), DESTINATION_URL, new GenericMessage<>(resultInfoDTOMap.get(matchRequestId), headers));
        }
    }

    @MessageMapping("/match/cancel-search")
    public void handleCancelingSearch(Principal principal) {
        matchService.cancelSearchingByUsername(principal.getName());
    }

    // 매칭 수락 처리
    // 결합도가 높음
    @MessageMapping("/match/accept")
    public void handleAccept(SimpMessageHeaderAccessor headerAccessor) {
        String matchRequestId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(MATCH_REQUEST_ID);
        log.debug("matchRequestId (/match/accept): " + matchRequestId);
        MatchRequest matchRequest = matchService.getMatchRequestById(matchRequestId).orElseThrow();
        MatchRequest opponentMatchRequest = matchService.getMatchRequestById(matchRequest.getOpponentMatchRequestId()).orElseThrow();

        MatchResponseDTO matchResponseDTO = matchService.acceptMatch(matchRequestId);

        Map<String, Object> headers = Collections.singletonMap(headerEventType, "accept");
        if (opponentMatchRequest.getMatchDecisionStatus().equals(MatchDecisionStatus.ACCEPTED)) {
            messagingTemplate.convertAndSendToUser(matchRequest.getUsername(), DESTINATION_URL, new GenericMessage<>(matchResponseDTO, headers));
            messagingTemplate.convertAndSendToUser(opponentMatchRequest.getUsername(), DESTINATION_URL, new GenericMessage<>(matchResponseDTO, headers));
        } else {
            messagingTemplate.convertAndSendToUser(matchRequest.getUsername(), DESTINATION_URL, new GenericMessage<>(matchResponseDTO, headers));
        }
    }

    // 매칭 거절 처리
    @MessageMapping("/match/reject")
    public void handleReject(SimpMessageHeaderAccessor headerAccessor) {
        String matchRequestId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(MATCH_REQUEST_ID);
        MatchRequest matchRequest = matchService.getMatchRequestById(matchRequestId).orElseThrow();
        MatchRequest opponentMatchRequest = matchService.getMatchRequestById(matchRequest.getOpponentMatchRequestId()).orElseThrow();
        matchService.rejectMatch(matchRequestId);

        MatchResponseDTO payload = new MatchResponseDTO(MatchDecisionStatus.REJECTED);
        Map<String, Object> headers = Collections.singletonMap(headerEventType, "reject");
        messagingTemplate.convertAndSendToUser(matchRequest.getUsername(), DESTINATION_URL, new GenericMessage<>(payload, headers));
        messagingTemplate.convertAndSendToUser(opponentMatchRequest.getUsername(), DESTINATION_URL, new GenericMessage<>(payload, headers));
    }

    // WebSocket 연결 해제 처리
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String matchRequestId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(MATCH_REQUEST_ID);

        if (matchRequestId != null) {
            matchService.removeRideRequest(matchRequestId);
        }
    }

}
