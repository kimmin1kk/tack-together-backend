package com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoadDTO {
    private String name;
    private int distance;
    private int duration;
    @JsonProperty("traffic_speed")
    private int trafficSpeed;
    @JsonProperty("traffic_state")
    private int trafficState;
    private List<Double> vertexes;
}
