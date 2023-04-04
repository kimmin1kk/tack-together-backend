package com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseDirections {
    @JsonProperty("trans_id")
    private String transId;
    private List<RouteDTO> routes;
}
