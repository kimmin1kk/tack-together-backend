package com.dnlab.tacktogetherbackend.history.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
 MatchInfo
 date, origin destination

 MatchInfoMember
 paymentAmount
*/
@Data
@NoArgsConstructor
public class HistorySummaryDTO implements Serializable { //히스토리 간단히 DTO
    private long id;
    private long createTime;
    private String origin;
    private String destination;
    private String waypoints;
    private int paymentAmount;

    @Builder
    public HistorySummaryDTO(long id, long createTime, String origin, String destination, String waypoints, int paymentAmount) {
        this.id = id;
        this.createTime = createTime;
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.paymentAmount = paymentAmount;
    }
}
