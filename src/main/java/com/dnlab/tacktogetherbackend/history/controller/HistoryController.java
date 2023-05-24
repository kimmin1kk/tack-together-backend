package com.dnlab.tacktogetherbackend.history.controller;

import com.dnlab.tacktogetherbackend.history.dto.HistoryDetailDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistorySummaryListDTO;
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

    @GetMapping("/simple")
    //이용기록 간단하게
    public ResponseEntity<HistorySummaryListDTO> handleHistorySummaryRequest(Principal principal) {
        return ResponseEntity.ok(historyService.getHistorySummaryListByUsername(principal.getName()));
    }

    @GetMapping("/detail")
    //이용기록 자세하게
    public ResponseEntity<HistoryDetailDTO> handleHistoryDetailRequest(Principal principal,
                                                                       @RequestParam long historyId) {
        return ResponseEntity.ok(historyService.getHistoryDetailByHistoryIdAndUsername(historyId, principal.getName()));
    }


}
