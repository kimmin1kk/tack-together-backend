package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.history.dto.HistorySummaryDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistoryDetailDTO;

public interface HistoryService {
    HistorySummaryDTO getHistorySummaryByUsername(String username);
    HistoryDetailDTO getHistoryDetailByUsername(String username);

}
