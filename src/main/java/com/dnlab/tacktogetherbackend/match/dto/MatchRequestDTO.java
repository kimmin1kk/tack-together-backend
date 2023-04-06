package com.dnlab.tacktogetherbackend.match.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MatchRequestDTO {
    private Long memberId;
    private String origin;
    private String destination;
    private short originRange;
    private short destinationRange;

    @Builder
    public MatchRequestDTO(Long memberId, String origin, String destination, short originRange, short destinationRange) {
        this.memberId = memberId;
        this.origin = origin;
        this.destination = destination;
        this.originRange = originRange;
        this.destinationRange = destinationRange;
    }
}
