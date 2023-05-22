package com.dnlab.tacktogetherbackend.history.controller;

import com.dnlab.tacktogetherbackend.history.dto.SimpleHistoryDTO;
import com.dnlab.tacktogetherbackend.history.dto.DetailHistoryDTO;
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
    public ResponseEntity<SimpleHistoryDTO> handleSimpleHistoryRequest(Principal principal) {
        return ResponseEntity.ok(historyService.getSimpleHistoryByUsername(principal.getName()));
    }
    //이용기록 자세하게
    @GetMapping("detail")
    public ResponseEntity<DetailHistoryDTO> handleDetailHistoryRequest(Principal principal) {
        return ResponseEntity.ok(historyService.getDetailHistoryByUsername(principal.getName()));
    }

}
