package com.dnlab.tacktogetherbackend.match.dto;

import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.RouteDTO;
import com.dnlab.tacktogetherbackend.match.common.PostMatchTemporaryInfo;
import com.dnlab.tacktogetherbackend.match.common.TaxiFares;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MatchResultInfoDTO {
    private String username;
    private String opponentNickname;
    private List<RouteDTO> routes;
    private int estimatedTotalTaxiFare;
    private int estimatedTaxiFare;
    private double paymentRate;
    private double opponentPaymentRate;

    @Builder
    public MatchResultInfoDTO(String username, String opponentNickname, List<RouteDTO> routes, int estimatedTotalTaxiFare, int estimatedTaxiFare, double paymentRate, double opponentPaymentRate) {
        this.username = username;
        this.opponentNickname = opponentNickname;
        this.routes = routes;
        this.estimatedTotalTaxiFare = estimatedTotalTaxiFare;
        this.estimatedTaxiFare = estimatedTaxiFare;
        this.paymentRate = paymentRate;
        this.opponentPaymentRate = opponentPaymentRate;
    }

    public static MatchResultInfoDTO of(PostMatchTemporaryInfo postMatchTemporaryInfo,
                                        TaxiFares taxiFares,
                                        boolean destination) {
        int taxiFare = (destination) ? taxiFares.getDestinationFare() : taxiFares.getWaypointFare();
        double paymentRate = (destination) ? taxiFares.getDestinationRate() : taxiFares.getWaypointRate();
        double opponentPaymentRate = (!destination) ? taxiFares.getDestinationRate() : taxiFares.getWaypointRate();

        MatchResultInfoDTO.MatchResultInfoDTOBuilder builder = MatchResultInfoDTO.builder()
                .routes(postMatchTemporaryInfo.getFixedDirections().getRoutes())
                .estimatedTotalTaxiFare(taxiFares.getTotalFare())
                .estimatedTaxiFare(taxiFare)
                .paymentRate(paymentRate)
                .opponentPaymentRate(opponentPaymentRate);
        if (destination) {
            return builder
                    .username(postMatchTemporaryInfo.getFartherRequest().getUsername())
                    .opponentNickname(postMatchTemporaryInfo.getNearerRequest().getNickname())
                    .build();
        } else {
            return builder
                    .username(postMatchTemporaryInfo.getNearerRequest().getUsername())
                    .opponentNickname(postMatchTemporaryInfo.getFartherRequest().getNickname())
                    .build();
        }
    }
}