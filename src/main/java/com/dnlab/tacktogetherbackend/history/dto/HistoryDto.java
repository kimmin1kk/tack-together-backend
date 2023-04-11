package com.dnlab.tacktogetherbackend.history.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
public class HistoryDto {
    private String origin;
    private String destination;
    private Timestamp startTime;
    private Timestamp rideDuration;
    private Timestamp endTime;
    private String passengerNickname;
    private String savedCost;

    @Builder
    public HistoryDto(String origin, String destination, Timestamp startTime, Timestamp rideDuration, Timestamp endTime, String passengerNickname, String savedCost) {
        this.origin = origin;
        this.destination = destination;
        this.startTime = startTime;
        this.rideDuration = rideDuration;
        this.endTime = endTime;
        this.passengerNickname = passengerNickname;
        this.savedCost = savedCost;
    }
}

