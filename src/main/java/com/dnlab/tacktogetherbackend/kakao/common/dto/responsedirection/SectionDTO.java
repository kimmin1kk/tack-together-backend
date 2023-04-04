package com.dnlab.tacktogetherbackend.kakao.common.dto.responsedirection;

import lombok.Data;

import java.util.List;

@Data
public class SectionDTO {
    private int distance;
    private int duration;
    private BoundDTO bound;
    private List<RoadDTO> roads;
    private List<GuideDTO> guides;
}
