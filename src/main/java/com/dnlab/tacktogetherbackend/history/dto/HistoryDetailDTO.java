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
    private long id;
    private String origin;
    private String waypoints;
    private String destination;
    private long createTime;
    private int distance;
    private long dropOffTime;
    private long matchEndTime;
    private String opponentMember;
    private int paymentAmount;

    @Builder
    @SuppressWarnings("squid:S107")
    public HistoryDetailDTO(long id, String origin, String waypoints, String destination, long createTime, int distance, long dropOffTime, long matchEndTime, String opponentMember, int paymentAmount) {
        this.id = id;
        this.origin = origin;
        this.waypoints = waypoints;
        this.destination = destination;
        this.createTime = createTime;
        this.distance = distance;
        this.dropOffTime = dropOffTime;
        this.matchEndTime = matchEndTime;
        this.opponentMember = opponentMember;
        this.paymentAmount = paymentAmount;
    }
}

