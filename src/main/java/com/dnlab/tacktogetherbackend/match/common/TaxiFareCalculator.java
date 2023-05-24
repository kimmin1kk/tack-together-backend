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
                .destinationRate((double) destinationFare / totalFare * 100)
                .waypointRate((double) waypointFare / totalFare * 100)
                .build();
    }
}
