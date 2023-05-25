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
    private long date;
    private String origin;
    private String destination;
    private int paymentAmount;

    @Builder
    public HistorySummaryDTO(long id, long date, String origin, String destination, int paymentAmount) {
        this.id = id;
        this.date = date;
        this.origin = origin;
        this.destination = destination;
        this.paymentAmount = paymentAmount;
    }
}
