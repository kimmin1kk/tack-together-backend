package com.dnlab.tacktogetherbackend.history.controller;

import com.dnlab.tacktogetherbackend.history.dto.HistorySummaryDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistoryDetailDTO;
import com.dnlab.tacktogetherbackend.history.service.HistoryService;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    //이용기록 조회시
    //누르면 최근 한달간 볼 수 있고 전체보기를 누르면 다 볼 수 있게
    private final HistoryService historyService;

    @GetMapping("/simple")
    //이용기록 간단하게
    public ResponseEntity<List<MatchInfo>> handleSimpleHistoryRequest(Principal principal) {
        return ResponseEntity.ok(historyService.getHistorySummaryByUsername(principal.getName()));
    }
//    //이용기록 자세하게
//    @GetMapping("detail")
//    public ResponseEntity<HistoryDetailDTO> handleDetailHistoryRequest(Principal principal) {
//        return ResponseEntity.ok(historyService.getHistoryDetailByUsername(principal.getName()));
//    }

}
