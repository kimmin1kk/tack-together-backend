package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.history.dto.HistoryDetailDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistorySummaryDTO;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfoMember;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 MatchInfo
 date, origin destination,waypoints, createTime, dropOffTime

 MatchInfoMember
 paymentAmount,distance
*/


@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final MemberRepository memberRepository;
    private final MatchInfoMemberRepository matchInfoMemberRepository;
    private final MatchInfoRepository matchInfoRepository;

    // 히스토리 서비스에서는 get만 할 예정이므로 별 다른 서비스 로직이 필요하진 않을 것 같음
    @Override //History 간단하게
    @Transactional(readOnly = true)
    public HistorySummaryDTO getHistorySummaryByUsername(String username) {
        log.info("MatchInfos : " + matchInfoRepository.findMatchInfosByMemberUsername(username));

        MatchInfo matchInfo = matchInfoRepository.findMatchInfoByMemberUsername(username);
        MatchInfoMember matchInfoMember = matchInfoMemberRepository.findMatchInfoMemberByUsername(username);

        return HistorySummaryDTO.builder()
                .date(matchInfo.getCreateTime().getTime())
                .origin(matchInfo.getOrigin())
                .destination(matchInfo.getDestination())
                .paymentAmount(matchInfoMember.getPaymentAmount())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryDetailDTO getHistoryDetailByUsername(String username) {
        log.info("MatchInfos : " + matchInfoRepository.findMatchInfosByMemberUsername(username));

        MatchInfo matchInfo = matchInfoRepository.findMatchInfoByMemberUsername(username);
        MatchInfoMember matchInfoMember = matchInfoMemberRepository.findMatchInfoMemberByUsername(username);

        return HistoryDetailDTO.builder()
                .date(matchInfo.getCreateTime().getTime())
                .origin(matchInfo.getOrigin())
                .waypoints(matchInfo.getWaypoints())
                .destination(matchInfo.getDestination())
                .createTime(matchInfo.getCreateTime().getTime())
                .distance(matchInfo.getTotalDistance())
                .dropOffTime(matchInfoMember.getDropOffTime().getTime())
                .opponentMember(matchInfo.getMatchInfoMembers().toString())
                .paymentAmount(String.valueOf(matchInfoMember.getPaymentAmount()))
                .build();
    }

}
