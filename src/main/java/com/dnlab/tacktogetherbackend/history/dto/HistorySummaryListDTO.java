package com.dnlab.tacktogetherbackend.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class HistorySummaryListDTO implements Serializable {
    private List<HistorySummaryDTO> historySummaries;

}
