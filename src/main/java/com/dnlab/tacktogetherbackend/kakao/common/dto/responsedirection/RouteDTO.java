package com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RouteDTO {
    @JsonProperty("result_code")
    private int resultCode;
    @JsonProperty("result_msg")
    private String resultMsg;
    private SummaryDTO summary;
    private List<SectionDTO> sections;
}
