package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.history.dto.HistoriesDTO;

public interface HistoryService {
    HistoriesDTO findHistoriesByUserName(String username);

}
