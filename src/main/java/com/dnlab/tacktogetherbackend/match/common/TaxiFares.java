package com.dnlab.tacktogetherbackend.match.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxiFares {
   private int totalFare;
   private int waypointFare;
   private int destinationFare;
}
