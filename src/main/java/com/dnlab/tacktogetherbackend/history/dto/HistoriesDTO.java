package com.dnlab.tacktogetherbackend.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriesDTO {
    private List<HistoryDTO> histories;
}
