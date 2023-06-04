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
    private double waypointRate;
    private double destinationRate;

    @Builder
    public SettlementReceivedRequestDTO(String sessionId, String username, int requestedFare, int totalFare, String accountInfo, double waypointRate, double destinationRate) {
        this.sessionId = sessionId;
        this.username = username;
        this.requestedFare = requestedFare;
        this.totalFare = totalFare;
        this.accountInfo = accountInfo;
        this.waypointRate = waypointRate;
        this.destinationRate = destinationRate;
    }
}
