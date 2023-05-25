package com.dnlab.tacktogetherbackend.match.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class MatchRequestDTO {
    private String username;
    private String nickname;

    @NotNull
    private String origin;

    @NotNull
    private String destination;

    @NotNull
    private short originRange;

    @NotNull
    private short destinationRange;

    @Builder
    public MatchRequestDTO(String username, String nickname, String origin, String destination, short originRange, short destinationRange) {
        this.username = username;
        this.nickname = nickname;
        this.origin = origin;
        this.destination = destination;
        this.originRange = originRange;
        this.destinationRange = destinationRange;
    }
}
