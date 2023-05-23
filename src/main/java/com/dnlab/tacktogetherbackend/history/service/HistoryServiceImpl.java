package com.dnlab.tacktogetherbackend.history.service;
import com.dnlab.tacktogetherbackend.history.dto.HistoryDetailDTO;

import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;
import com.dnlab.tacktogetherbackend.match.repository.TemporaryMatchSessionInfoRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final MemberRepository memberRepository;
    private final MatchInfoMemberRepository matchInfoMemberRepository;
    private final MatchInfoRepository matchInfoRepository;
    private final TemporaryMatchSessionInfoRepository temporaryMatchSessionInfoRepository;

    // 히스토리 서비스에서는 get만 할 예정이므로 별 다른 서비스 로직이 필요하진 않을 것 같음
    @Override //History 간단하게
    @Transactional(readOnly = true)
    public List<MatchInfo> getHistorySummaryByUsername(String username) {
        log.info("MatchInfos : " + matchInfoRepository.findMatchInfosByMemberUsername(username));
        return null;
//                HistorySummaryDTO().builder()
//                .date()
//                .origin()
//                .destination()
//                .paymentAmount()
//                .build();
    }

//    @Override //History 자세하게
//    @Builder
//    public HistoryDetailDTO getHistoryDetailByUsername(String username) {
//        return null;
////                new HistoryDetailDTO().builder()
////                .date()
////                .origin()
////                .waypoints()
////                .destination()
////                .startTime()
////                .rideDuration()
////                .endTime()
////                .opponentMember()
////                .savedCost()
////                .build();
//    }
}
