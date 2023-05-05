package com.dnlab.tacktogetherbackend.match.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRate {
    private double waypointRate;
    private double destinationRate;
}
