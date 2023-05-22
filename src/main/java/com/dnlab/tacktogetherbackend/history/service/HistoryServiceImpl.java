package com.dnlab.tacktogetherbackend.history.service;
import com.dnlab.tacktogetherbackend.history.dto.SimpleHistoryDTO;
import com.dnlab.tacktogetherbackend.history.dto.DetailHistoryDTO;

import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;
import com.dnlab.tacktogetherbackend.match.repository.TemporaryMatchSessionInfoRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final MemberRepository memberRepository;
    private final MatchInfoMemberRepository matchInfoMemberRepository;
    private final MatchInfoRepository matchInfoRepository;
    private final TemporaryMatchSessionInfoRepository temporaryMatchSessionInfoRepository;

    @Override //History 간단하게
    public SimpleHistoryDTO getSimpleHistoryByUsername(String username) {
        return new SimpleHistoryDTO().builder()
                .date()
                .origin()
                .destination()
                .paymentAmount()
                .build();
    }

    @Override //History 자세하게
    @Builder
    public DetailHistoryDTO getDetailHistoryByUsername(String username) {
        return new DetailHistoryDTO().builder()
                .date()
                .origin()
                .waypoints()
                .destination()
                .startTime()
                .rideDuration()
                .endTime()
                .opponentMember()
                .savedCost()
                .build();
    }
}
