package com.dnlab.tacktogetherbackend.match.dto;

import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.RouteDTO;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MatchResultInfoDTO {
    private String username;
    private String opponentUsername;
    private List<RouteDTO> routes;
    private int estimatedTotalTaxiFare;
    private int estimatedTaxiFare;
    private double paymentRate;
    private double opponentPaymentRate;

    @Builder
    public MatchResultInfoDTO(String username, String opponentUsername, List<RouteDTO> routes, int estimatedTotalTaxiFare, int estimatedTaxiFare, double paymentRate, double opponentPaymentRate) {
        this.username = username;
        this.opponentUsername = opponentUsername;
        this.routes = routes;
        this.estimatedTotalTaxiFare = estimatedTotalTaxiFare;
        this.estimatedTaxiFare = estimatedTaxiFare;
        this.paymentRate = paymentRate;
        this.opponentPaymentRate = opponentPaymentRate;
    }
}