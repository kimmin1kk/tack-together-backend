package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.history.dto.HistoriesDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    @Override
    public HistoriesDTO findHistoriesByUserName(String username) {

        return null;
    }

    @Override
    public HistoryDTO findHistoryByUsername(String username) {
        return null;
    }
}
