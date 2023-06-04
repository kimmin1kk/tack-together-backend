package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SettlementInfoDTO {
    private String origin;
    private String waypoint;
    private String destination;
    private int totalDistance;
    private double paymentRate;
    private double opponentPaymentRate;

    @Builder
    public SettlementInfoDTO(String origin, String waypoint, String destination, int totalDistance, double paymentRate, double opponentPaymentRate) {
        this.origin = origin;
        this.waypoint = waypoint;
        this.destination = destination;
        this.totalDistance = totalDistance;
        this.paymentRate = paymentRate;
        this.opponentPaymentRate = opponentPaymentRate;
    }
}