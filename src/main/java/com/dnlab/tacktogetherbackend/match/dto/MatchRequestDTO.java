package com.dnlab.tacktogetherbackend.match.dto;

import lombok.Data;

@Data
public class MatchRequestDTO {
    private Long memberId;
    private String origin;
    private String destination;
    private short originRange;
    private short destinationRange;
}
