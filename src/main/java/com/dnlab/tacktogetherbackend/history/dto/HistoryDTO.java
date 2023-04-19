package com.dnlab.tacktogetherbackend.history.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Duration;


@Data
@NoArgsConstructor
public class HistoryDTO {
    private String origin;
    private String destination;
    private Timestamp startTime;
    private Duration rideDuration;
    private Timestamp endTime;
    private String passengerNickname;
    private String savedCost;

    @Builder
    public HistoryDTO(String origin, String destination, Timestamp startTime, Duration rideDuration, Timestamp endTime, String passengerNickname, String savedCost) {
        this.origin = origin;
        this.destination = destination;
        this.startTime = startTime;
        this.rideDuration = rideDuration;
        this.endTime = endTime;
        this.passengerNickname = passengerNickname;
        this.savedCost = savedCost;
    }
}

