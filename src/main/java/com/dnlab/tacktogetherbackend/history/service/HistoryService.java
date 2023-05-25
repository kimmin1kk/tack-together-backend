package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.history.dto.HistoryDetailDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistorySummaryListDTO;

public interface HistoryService {

    HistorySummaryListDTO getHistorySummaryListByUsername(String username);
    HistoryDetailDTO getHistoryDetailByHistoryIdAndUsername(Long id, String username);

}
