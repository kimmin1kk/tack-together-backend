package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SettlementRequestDTO {
    private String sessionId;
    private int totalFare;
    private String accountInfo; // 계좌 정보
}
