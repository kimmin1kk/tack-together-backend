package com.dnlab.tacktogetherbackend.history.controller;

import com.dnlab.tacktogetherbackend.history.dto.HistoriesDTO;
import com.dnlab.tacktogetherbackend.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    //이용기록 조회시
    //누르면 최근 한달간 볼 수 있고 전체보기를 누르면 다 볼 수 있게
    private final HistoryService historyService;

    public ResponseEntity<HistoriesDTO> handleHistoryRequest(Principal principal) {
        return ResponseEntity.ok(historyService.findHistoriesByUserName(principal.getName()));
    }

}
