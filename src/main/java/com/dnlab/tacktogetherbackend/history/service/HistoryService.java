package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.history.dto.SimpleHistoryDTO;
import com.dnlab.tacktogetherbackend.history.dto.DetailHistoryDTO;

public interface HistoryService {
    SimpleHistoryDTO getSimpleHistoryByUsername(String username);
    DetailHistoryDTO getDetailHistoryByUsername(String username);

}
