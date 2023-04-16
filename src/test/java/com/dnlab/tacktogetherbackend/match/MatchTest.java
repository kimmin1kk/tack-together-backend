package com.dnlab.tacktogetherbackend.match;

import com.dnlab.tacktogetherbackend.SpringBootTestConfiguration;
import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
@ContextConfiguration(classes = SpringBootTestConfiguration.class)
@Transactional
class MatchTest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MemberRepository memberRepository;

    private static final String STUDENT_PLAZA = "129.0091051,35.1455966";
    private static final String NAENGJEONG_STA = "129.012175,35.151238";
    private static final String HYUNMU_APT_105 = "129.00501,35.1449747"; // 스플과 320m거리
    private static final String KOR_SHOES_MUSEUM = "129.0258261,35.1573104";
    private static final String DONGEUI_UNIV_KAYA = "129.0340908,35.1429679";
    private static final String BUSAN_STA = "129.03933,35.114495"; //Origin
    private static final String MARYKNOLL_HOSPITAL = "129.0323992,35.1073029"; //Destination
    private static final String HAIDILAO = "129.0383567,35.1132896"; //부산역이랑 161m 거리
    private static final String MAGA_DUMPLING = "129.0385153,35.1145318"; //부산역이랑 73m 거리
    private static final String GORILLA_SUSHI = "129.0349343,35.1081818"; //메리놀이랑 250m 거리
    private static final String ABC_BAWLING_CENTER = "129.0247969,35.0969482"; //메리놀이랑 1.35km 거리

    @AfterEach
    void resetMap() {
        matchService.resetMatchRequests();
    }


    @Test
    @Transactional
    void sameDestinationTest() { //같은 출발지, 같은 목적지
        MatchRequest req1 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username1")
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());

        MatchRequest req2 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username2")
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());

        MatchRequest req3 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username3")
                .origin(HYUNMU_APT_105)
                .destination(NAENGJEONG_STA)
                .originRange((short) 1)
                .destinationRange((short) 2)
                .build());

        MatchRequest matchedReq = matchService.findMatchingMatchRequests(req1);

        log.info("req1 : " + req1.toString());
        log.info("req2 : " + req2.toString());
        log.info("req3 : " + req3.toString());
        log.info("matchedReq : " + matchedReq.toString());
        assertEquals(matchedReq, req2);
    }

    @Test
    @Transactional
    void waypointMatchTest() { //같은 출발지 다른 목적지
        MatchRequest req1 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username1")
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 2)
                .build());

        MatchRequest req2 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username2")
                .origin(STUDENT_PLAZA)
                .destination(KOR_SHOES_MUSEUM)
                .originRange((short) 0)
                .destinationRange((short) 2)
                .build());

        MatchRequest req3 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username3")
                .origin(HYUNMU_APT_105)
                .destination(DONGEUI_UNIV_KAYA)
                .originRange((short) 0)
                .destinationRange((short) 2)
                .build());

        MatchRequest matchedReq = matchService.findMatchingMatchRequests(req1);

        log.info("req1 : " + req1.toString());
        log.info("req2 : " + req2.toString());
        log.info("req3 : " + req3.toString());
        log.info("matchedReq : " + matchedReq.toString());
        assertEquals(matchedReq, req2);
    }

    @Test
    @Transactional
    void originRangeTest() { //출발지 거리 범위 확인
        MatchRequest req1 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username1")
                .origin(BUSAN_STA)
                .destination(MARYKNOLL_HOSPITAL)
                .originRange((short) 1)
                .destinationRange((short) 0)
                .build());

        MatchRequest req2 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username2")
                .origin(MAGA_DUMPLING)
                .destination(MARYKNOLL_HOSPITAL)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());

        MatchRequest req3 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username3")
                .origin(DONGEUI_UNIV_KAYA)
                .destination(MARYKNOLL_HOSPITAL)
                .originRange((short) 1)
                .destinationRange((short) 0)
                .build());

        MatchRequest matchedReq = matchService.findMatchingMatchRequests(req1);

        log.info("req1 : " + req1.toString());
        log.info("req2 : " + req2.toString());
        log.info("req3 : " + req3.toString());
        log.info("matchedReq : " + matchedReq.toString());
        assertEquals(matchedReq, req2);
    }

    @Transactional
    @Test
    void destinationRangeTest() { //목적지 거리 범위 확인
        MatchRequest req1 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username1")
                .origin(BUSAN_STA)
                .destination(MARYKNOLL_HOSPITAL)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());

        MatchRequest req2 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username2")
                .origin(BUSAN_STA)
                .destination(GORILLA_SUSHI)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());

        MatchRequest req3 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .username("username3")
                .origin(BUSAN_STA)
                .destination(ABC_BAWLING_CENTER)
                .originRange((short) 0)
                .destinationRange((short) 1)
                .build());

        MatchRequest matchedReq = matchService.findMatchingMatchRequests(req2);

        log.info("req1 : " + req1.toString());
        log.info("req2 : " + req2.toString());
        log.info("req3 : " + req3.toString());
        log.info("matchedReq : " + matchedReq.toString());
        assertEquals(matchedReq, req1);
    }
}
