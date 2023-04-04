package com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GuideDTO {
    private String name;
    private double x;
    private double y;
    private int distance;
    private int duration;
    private int type;
    private String guidance;
    @JsonProperty("road_index")
    private int roadIndex;
}
