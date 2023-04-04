package com.dnlab.tacktogetherbackend.kakao.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDirections {
    private String origin;
    private String destination;
    private String waypoints;
}
