package com.dnlab.tacktogetherbackend.match.dto;

import com.dnlab.tacktogetherbackend.match.common.MatchDecisionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchResponseDTO implements Serializable {
    private MatchDecisionStatus matchDecisionStatus;
    private String matchSessionId;

    public MatchResponseDTO(MatchDecisionStatus matchDecisionStatus) {
    }
}
