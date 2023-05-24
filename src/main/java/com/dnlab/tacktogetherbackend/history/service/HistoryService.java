package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.history.dto.HistoryDetailDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistorySummaryDTO;

public interface HistoryService {
    HistorySummaryDTO getHistorySummaryByUsername(String username);
    HistoryDetailDTO getHistoryDetailByUsername(String username);
}
