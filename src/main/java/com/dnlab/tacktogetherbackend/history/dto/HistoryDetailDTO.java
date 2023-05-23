package com.dnlab.tacktogetherbackend.history.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;


@Data
@NoArgsConstructor
public class HistoryDetailDTO implements Serializable { //히스토리 자세히 DTO
    private long date;
    private String origin;
    private String waypoints;
    private String destination;
    private long startTime;
    private Duration rideDuration;
    private long endTime;
    private String opponentMember;
    private String savedCost;

    @Builder
    @SuppressWarnings("squid:S107")
    public HistoryDetailDTO(long date, String origin, String waypoints, String destination, long startTime, Duration rideDuration, long endTime, String opponentMember, String savedCost) {
        this.date = date;
        this.origin = origin;
        this.waypoints = waypoints;
        this.destination = destination;
        this.startTime = startTime;
        this.rideDuration = rideDuration;
        this.endTime = endTime;
        this.opponentMember = opponentMember;
        this.savedCost = savedCost;
    }
}

