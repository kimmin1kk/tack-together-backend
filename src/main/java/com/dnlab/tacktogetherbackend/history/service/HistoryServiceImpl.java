package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.history.dto.HistoryDetailDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistorySummaryDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistorySummaryListDTO;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfoMember;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoMemberRepository;
import com.dnlab.tacktogetherbackend.match.repository.MatchInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MatchInfo
 * date, origin destination,waypoints, createTime, dropOffTime
 * MatchInfoMember
 * paymentAmount,distance
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final MatchInfoMemberRepository matchInfoMemberRepository;
    private final MatchInfoRepository matchInfoRepository;

    @Override //요약된 이용기록 리스트 반환
    @Transactional(readOnly = true)
    public HistorySummaryListDTO getHistorySummaryListByUsername(String username) {
        List<MatchInfo> matchInfos = matchInfoRepository.findMatchInfosByMemberUsername(username);

        return new HistorySummaryListDTO(matchInfos.stream()
                .map(matchInfo -> convertMatchInfoToHistorySummaryDTO(matchInfo, username))
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryDetailDTO getHistoryDetailByHistoryIdAndUsername(Long id, String username) {
        return convertMatchInfoToHistoryDetailDTO(matchInfoRepository.findById(id).orElseThrow(), username);
    }

    private HistorySummaryDTO convertMatchInfoToHistorySummaryDTO(MatchInfo matchInfo, String username) {
        MatchInfoMember matchInfoMember = matchInfoMemberRepository.findMatchInfoMemberByMatchInfoAndMemberUsername(matchInfo, username);
        return HistorySummaryDTO.builder()
                .id(matchInfo.getId())
                .date(matchInfo.getCreateTime().getTime())
                .origin(matchInfo.getOrigin())
                .destination(matchInfo.getDestination())
                .paymentAmount(matchInfoMember.getPaymentAmount())
                .build();
    }

    private HistoryDetailDTO convertMatchInfoToHistoryDetailDTO(MatchInfo matchInfo, String username) {
        MatchInfoMember matchInfoMember = matchInfoMemberRepository.findMatchInfoMemberByMatchInfoAndMemberUsername(matchInfo, username);
        return HistoryDetailDTO.builder()
                .id(matchInfo.getId())
                .date(matchInfo.getCreateTime().getTime())
                .origin(matchInfo.getOrigin())
                .waypoints(matchInfo.getWaypoints())
                .destination(matchInfo.getDestination())
                .createTime(matchInfo.getCreateTime().getTime())
                .distance(matchInfo.getTotalDistance())
                .dropOffTime(matchInfoMember.getDropOffTime().getTime())
                .opponentMember(matchInfo.getMatchInfoMembers().toString())
                .paymentAmount(matchInfoMember.getPaymentAmount())
                .build();
    }

}
