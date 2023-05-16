package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.history.dto.HistoriesDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistoryDTO;

public interface HistoryService {
    HistoriesDTO findHistoriesByUserName(String username);
    HistoryDTO findHistoryByUsername(String username);

}
