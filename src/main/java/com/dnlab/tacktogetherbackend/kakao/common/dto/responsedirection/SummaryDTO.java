package com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection;

import lombok.Data;

import java.util.List;

@Data
public class SummaryDTO {
    private LocationDTO origin;
    private LocationDTO destination;
    private List<LocationDTO> waypoints;
    private String priority;
    private BoundDTO bound;
    private FareDTO fare;
    private int distance;
    private int duration;
}
