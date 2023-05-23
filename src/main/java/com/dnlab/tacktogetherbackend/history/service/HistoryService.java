package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;

import java.util.List;

public interface HistoryService {
    List<MatchInfo> getHistorySummaryByUsername(String username);
//    MatchInfo getHistoryDetailByUsername(String username);

}
