package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RouteInfoDTO {
    private String origin;
    private String waypoint;
    private String destination;

    @Builder
    public RouteInfoDTO(String origin, String waypoint, String destination) {
        this.origin = origin;
        this.waypoint = waypoint;
        this.destination = destination;
    }
}
