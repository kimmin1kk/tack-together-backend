package com.dnlab.tacktogetherbackend.history.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
 MatchInfo
 date, origin destination,waypoints, createTime, dropOffTime

 MatchInfoMember
 paymentAmount,distance
*/

@Data
@NoArgsConstructor
public class HistoryDetailDTO implements Serializable { //히스토리 자세히 DTO
    private long date;
    private String origin;
    private String waypoints;
    private String destination;
    private long createTime;
    private int distance;
    private long dropOffTime;
    private String opponentMember;
    private String paymentAmount;

    @Builder
    @SuppressWarnings("squid:S107")
    public HistoryDetailDTO(long date, String origin, String waypoints, String destination, long createTime, int distance, long dropOffTime, String opponentMember, String paymentAmount) {
        this.date = date;
        this.origin = origin;
        this.waypoints = waypoints;
        this.destination = destination;
        this.createTime = createTime;
        this.distance = distance;
        this.dropOffTime = dropOffTime;
        this.opponentMember = opponentMember;
        this.paymentAmount = paymentAmount;
    }
}

