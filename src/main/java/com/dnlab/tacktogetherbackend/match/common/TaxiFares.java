package com.dnlab.tacktogetherbackend.match.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxiFares {
   private final int totalFare;
   private final int waypointFare;
   private final int destinationFare;
   private final double waypointRate;
   private final double destinationRate;
}
