package com.dnlab.tacktogetherbackend.history.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Duration;


@Data
@NoArgsConstructor
public class HistoryDTO { //히스토리 자세히 DTO
    private Timestamp date;
    private String origin;
    private String waypoints;
    private String destination;
    private Timestamp startTime;
    private Duration rideDuration;
    private Timestamp endTime;
    private String opponentMember;
    private String savedCost;

    @Builder
    @SuppressWarnings("squid:S107")
    public HistoryDTO(Timestamp date, String origin, String waypoints,String destination, Timestamp startTime, Duration rideDuration, Timestamp endTime, String opponentMember, String savedCost) {
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

