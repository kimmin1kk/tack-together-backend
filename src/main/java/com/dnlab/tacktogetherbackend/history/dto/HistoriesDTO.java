package com.dnlab.tacktogetherbackend.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriesDTO { //히스토리 간단히 DTO
    private Timestamp date;
    private String origin;
    private String destination;
    private int paymentAmount;

}
