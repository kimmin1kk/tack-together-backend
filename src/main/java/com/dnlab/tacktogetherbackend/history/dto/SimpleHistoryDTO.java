package com.dnlab.tacktogetherbackend.history.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class SimpleHistoryDTO implements Serializable { //히스토리 간단히 DTO
    private long date;
    private String origin;
    private String destination;
    private int paymentAmount;

    @Builder
    public SimpleHistoryDTO(long date, String origin, String destination, int paymentAmount) {
        this.date = date;
        this.origin = origin;
        this.destination = destination;
        this.paymentAmount = paymentAmount;
    }
}
