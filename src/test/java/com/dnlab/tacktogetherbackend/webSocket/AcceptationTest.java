package com.dnlab.tacktogetherbackend.webSocket;

import com.dnlab.tacktogetherbackend.SpringBootTestConfiguration;
import com.dnlab.tacktogetherbackend.auth.dto.RequestLogin;
import com.dnlab.tacktogetherbackend.auth.dto.RequestRegistration;
import com.dnlab.tacktogetherbackend.auth.dto.ResponseLogin;
import com.dnlab.tacktogetherbackend.auth.dto.ResponseRegistration;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.domain.MatchResult;
import com.dnlab.tacktogetherbackend.match.domain.MatchResultMember;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.repository.MatchResultMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchResultRepository;
import com.dnlab.tacktogetherbackend.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = SpringBootTestConfiguration.class)
class AcceptationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private MatchResultMemberRepository matchResultMemberRepository;

    @Autowired
    private TestRestTemplate restTemplate;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private BlockingQueue<MatchRequest> matchRequestBlockingQueue;
    private MatchResult matchResult;

    private static final String STUDENT_PLAZA = "129.0091051,35.1455966";
    private static final String NAENGJEONG_STA = "129.012175,35.151238";
    private static final String HYUNMU_APT_105 = "129.00501,35.1449747"; // 스플과 320m거리
    static final String USERNAME = "testUsername";
    static final String PASSWORD = "testPassword";

    @BeforeEach
    void setUpWebSocket() throws ExecutionException, InterruptedException, TimeoutException {
        RequestRegistration requestRegistration = new RequestRegistration();
        requestRegistration.setName("testName");
        requestRegistration.setUsername(USERNAME);
        requestRegistration.setPassword(PASSWORD);
        requestRegistration.setNickname("testNickname");
        restTemplate.postForEntity("/api/auth/signUp", requestRegistration, ResponseRegistration.class);
        String accessToken = Objects.requireNonNull(restTemplate.postForEntity("/api/auth/signIn", new RequestLogin(USERNAME, PASSWORD), ResponseLogin.class)
                .getBody()).getAccessToken();

        log.info("accessToken : " + accessToken);

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();

        SockJsClient sockJsClient = new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())));
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setTaskScheduler(taskScheduler);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setDefaultHeartbeat(new long[]{10_000, 10_000}); // 클라이언트 측 하트비트 설정

        URI baseUrl = URI.create("http://localhost:" + port);
        String webSocketUrl = baseUrl + "/match";
        StompSessionHandler sessionHandler = new TestSessionHandler(accessToken);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ListenableFuture<StompSession> future = stompClient.connect(webSocketUrl, headers, sessionHandler);

        stompSession = future.get(30, TimeUnit.SECONDS);
        matchRequestBlockingQueue = new LinkedBlockingDeque<>();
    }

    @BeforeEach
    void setMatchRequests() {
        MatchRequest req1 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("user1")
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());
    }

    @AfterEach
    public void tearDown() {
        stompSession.disconnect();
        stompClient.stop();
    }

    @Test
    void testAcceptation() throws InterruptedException, ExecutionException, TimeoutException {
        // 매칭 성공 테스트
        MatchRequestDTO req = MatchRequestDTO.builder()
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build();

        log.info("sending : " + req);
        log.info("sessionId : " + stompSession.getSessionId());
        String subscribeUrl = "/user/" + USERNAME + "/queue/match";
        stompSession.subscribe(subscribeUrl, new TestStompFrameHandler());

        log.info("session subscribed, url : " + subscribeUrl);
        stompSession.send("/app/match/request", req);

        MatchRequest receivedReq = matchRequestBlockingQueue.poll(20, TimeUnit.SECONDS);
        log.info("received : " + receivedReq);

        matchService.acceptMatch(matchService.getMatchRequestById(Objects.requireNonNull(receivedReq).getId()).orElseThrow()); //상대방 수락

        stompSession.send("/app/match/accept", receivedReq.getMatchedMatchRequestId()); //수락

        List<MatchResult> recentMatchResults = matchResultRepository.findTop2ByOrderByCreateTimeDesc();
        MatchResult matchResult = recentMatchResults.get(0);

        MatchResultMember matchResultMember = matchResult.getMatchResultMembers().stream()
                .filter(m -> m.getMember().getUsername().equals(USERNAME))
                .findFirst()
                .orElseThrow();
        MatchResultMember opponentMatchResultMember = matchResult.getMatchResultMembers().stream()
                .filter(m -> !m.getMember().getUsername().equals(USERNAME))
                .findFirst()
                .orElseThrow();

        assertThat(opponentMatchResultMember.getMember().getUsername()).isEqualTo("user1");


        // Then
        log.info("MatchResultMember : " + matchResultMember);
    }

    @Test
    @Transactional
    void testReject() throws InterruptedException, ExecutionException, TimeoutException { //미구현
        //매칭 실패 테스트
        MatchRequestDTO req = MatchRequestDTO.builder()
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build();

        log.info("sending : " + req);
        log.info("sessionId : " + stompSession.getSessionId());
        String subscribeUrl = "/user/" + USERNAME + "/queue/match";
        stompSession.subscribe(subscribeUrl, new TestStompFrameHandler());

        log.info("session subscribed, url : " + subscribeUrl);
        stompSession.send("/app/match/request", req);

        MatchRequest receivedReq = matchRequestBlockingQueue.poll(20, TimeUnit.SECONDS);
        log.info("received : " + receivedReq);

        matchService.rejectMatch(matchService.getMatchRequestById(Objects.requireNonNull(receivedReq).getId()).orElseThrow()); //상대방 거절

        stompSession.send("/app/match/accept", receivedReq.getMatchedMatchRequestId()); //수락

        List<MatchResult> recentMatchResults = matchResultRepository.findTop2ByOrderByCreateTimeDesc();
        MatchResult matchResult = recentMatchResults.get(0);

        MatchResultMember matchResultMember = matchResult.getMatchResultMembers().stream()
                .filter(m -> m.getMember().getUsername().equals(USERNAME))
                .findFirst()
                .orElseThrow();
        MatchResultMember opponentMatchResultMember = matchResult.getMatchResultMembers().stream()
                .filter(m -> !m.getMember().getUsername().equals(USERNAME))
                .findFirst()
                .orElseThrow();

//        assertThat(opponentMatchResultMember.getMember().getUsername()).isNotEqualTo("user1");

        // Then
        log.info("MatchResultMember : " + matchResultMember);
    }

    @RequiredArgsConstructor
    private class TestSessionHandler extends StompSessionHandlerAdapter {
        private final String accessToken;

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            log.info("afterConnected is called");
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/user/" + USERNAME + "/queue/match");
            headers.add("Authorization", "Bearer " + accessToken);
            session.setAutoReceipt(true);
            session.subscribe(headers, new TestStompFrameHandler());
        }
    }

    private class TestStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return MatchRequest.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            log.info("Received STOMP frame : " + o);
            matchRequestBlockingQueue.offer((MatchRequest) o);
        }
    }
}
