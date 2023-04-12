package com.dnlab.tacktogetherbackend.match.common;

import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest implements Serializable {

    // 매칭 전
    @NotNull
    private String id;

    @NotNull
    private String username;

    @NotNull
    private String origin;

    @NotNull
    private String destination;

    @NotNull
    private short originRange;

    @NotNull
    private short destinationRange;

    // 매칭 후
    private int distance = 0;
    private String matchedMatchRequestId = null; // 매칭된 상대 요청 아이디
    private MatchDecisionStatus matchDecisionStatus = null;

    public MatchRequest(MatchRequestDTO dto) {
        this.id = generateRequestId(dto.getUsername());
        this.username = dto.getUsername();
        this.origin = dto.getOrigin();
        this.destination = dto.getDestination();
        this.originRange = dto.getOriginRange();
        this.destinationRange = dto.getDestinationRange();
    }

    private String generateRequestId(String username) {
        return username + "-" + System.currentTimeMillis();
    }
}
