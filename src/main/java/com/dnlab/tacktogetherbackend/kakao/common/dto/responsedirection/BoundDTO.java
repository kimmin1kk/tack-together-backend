package com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BoundDTO {
    @JsonProperty("min_x")
    private double minX;
    @JsonProperty("min_y")
    private double minY;
    @JsonProperty("max_x")
    private double maxX;
    @JsonProperty("max_y")
    private double maxY;
}
