package com.dnlab.tacktogetherbackend.match;

import com.dnlab.tacktogetherbackend.SpringBootTestConfiguration;
import com.dnlab.tacktogetherbackend.match.common.MatchRequest;
import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import com.dnlab.tacktogetherbackend.match.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
@ContextConfiguration(classes = SpringBootTestConfiguration.class)
class MatchTest {

    @Autowired
    private MatchService matchService;

    private static final String STUDENT_PLAZA = "129.0091051,35.1455966";
    private static final String NAENGJEONG_STA = "129.012175,35.151238";
    private static final String HYUNMU_APT_105 = "129.00501,35.1449747"; // 스플과 320m거리
    private static final String KOR_SHOES_MUSEUM = "129.0258261,35.1573104";
    private static final String DONGEUI_UNIV_KAYA = "129.0340908,35.1429679";

    @AfterEach
    void resetMap() {
        matchService.resetActiveMatchRequests();
    }

    @Test
    void sameDestinationTest() {
        MatchRequest req1 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .memberId(1L)
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());

        MatchRequest req2 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .memberId(2L)
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());

        MatchRequest req3 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .memberId(3L)
                .origin(HYUNMU_APT_105)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 0)
                .build());

        MatchRequest matchedReq = matchService.findMatchingMatchRequests(req1);

        log.info("req2 : " + req2.toString());
        log.info("matchedReq : " + matchedReq.toString());
        assertEquals(matchedReq, req2);
    }

    @Test
    void waypointMatchTest() {
        MatchRequest req1 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .memberId(1L)
                .origin(STUDENT_PLAZA)
                .destination(NAENGJEONG_STA)
                .originRange((short) 0)
                .destinationRange((short) 2)
                .build());

        MatchRequest req2 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .memberId(2L)
                .origin(STUDENT_PLAZA)
                .destination(KOR_SHOES_MUSEUM)
                .originRange((short) 0)
                .destinationRange((short) 2)
                .build());

        MatchRequest req3 = matchService.addMatchRequest(MatchRequestDTO.builder()
                .memberId(3L)
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
}
