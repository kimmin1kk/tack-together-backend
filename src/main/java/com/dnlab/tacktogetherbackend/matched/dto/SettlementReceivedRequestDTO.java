package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SettlementReceivedRequestDTO {
    private String sessionId;
    private String username;
    private int requestedFare;
    private int totalFare;
    private String accountInfo;

    @Builder
    public SettlementReceivedRequestDTO(String sessionId, String username, int requestedFare, int totalFare, String accountInfo) {
        this.sessionId = sessionId;
        this.username = username;
        this.requestedFare = requestedFare;
        this.totalFare = totalFare;
        this.accountInfo = accountInfo;
    }
}
