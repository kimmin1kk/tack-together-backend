package com.dnlab.tacktogetherbackend.matched.controller;

import com.dnlab.tacktogetherbackend.matched.dto.SettlementInfoDTO;
import com.dnlab.tacktogetherbackend.matched.service.MatchedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/settlement")
@RequiredArgsConstructor
public class SettlementController {
    private final MatchedService matchedService;

    @GetMapping("/info")
    public ResponseEntity<SettlementInfoDTO> getSettlementInfo(Principal principal) {
        return ResponseEntity.ok(matchedService.getSettlementInfo(principal.getName()));
    }
}
