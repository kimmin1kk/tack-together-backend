package com.dnlab.tacktogetherbackend.match.common;

import com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection.ResponseDirections;
import com.dnlab.tacktogetherbackend.match.config.MinimumFareProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaxiFareCalculator {
    private final MinimumFareProperties minimumFareProperties;

    public int getFareByResponseDirections(ResponseDirections responseDirections) {
        return responseDirections.getRoutes()
                .stream().findFirst().orElseThrow()
                .getSummary()
                .getFare()
                .getTaxi();
    }

    public TaxiFares calculateFare(int totalFare,
                                   int waypointDistance,
                                   int destinationDistance) {
        int destinationFare = (minimumFareProperties.getFare() / 2) +
                ((totalFare - minimumFareProperties.getFare()) *
                        (destinationDistance / (destinationDistance + waypointDistance)));
        int waypointFare = (minimumFareProperties.getFare() / 2) +
                ((totalFare - minimumFareProperties.getFare()) *
                        (waypointDistance / (destinationDistance + waypointDistance)));

        // 계산 후 잔금은 목적지로 가는 사람에게 부담
        destinationFare += totalFare - destinationFare - waypointFare;

        return TaxiFares.builder()
                .totalFare(totalFare)
                .destinationFare(destinationFare)
                .waypointFare(waypointFare)
                .build();
    }

    public PaymentRate calculatePaymentRate(int totalFare,
                                            int waypointDistance,
                                            int destinationDistance) {
        TaxiFares taxiFares = calculateFare(totalFare, waypointDistance, destinationDistance);

        return PaymentRate.builder()
                .destinationRate((double) taxiFares.getDestinationFare() / totalFare * 100)
                .waypointRate((double) taxiFares.getWaypointFare() / totalFare * 100)
                .build();
    }

    public PaymentRate calculatePaymentRate(TaxiFares taxiFares) {
        return PaymentRate.builder()
                .destinationRate((double) taxiFares.getDestinationFare() / taxiFares.getTotalFare() * 100)
                .waypointRate((double) taxiFares.getWaypointFare() / taxiFares.getTotalFare() * 100)
                .build();
    }
}
