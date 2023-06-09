package com.dnlab.tacktogetherbackend.match.common;

import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

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
    private String nickname;

    @NotNull
    private String origin;

    @NotNull
    private String destination;

    @NotNull
    private short originRange;

    @NotNull
    private short destinationRange;

    private boolean matched = false;

    // 매칭 후
    private int distance = 0;
    private String opponentMatchRequestId = null; // 매칭된 상대 요청 아이디
    private MatchDecisionStatus matchDecisionStatus = null;

    // 매칭 정보들 아이디
    private String tempSessionId;

    public MatchRequest(MatchRequestDTO dto, String username, String nickname) {
        this.id = generateRequestId(username);
        this.username = username;
        this.nickname = nickname;
        this.origin = dto.getOrigin();
        this.destination = dto.getDestination();
        this.originRange = dto.getOriginRange();
        this.destinationRange = dto.getDestinationRange();
    }

    public void updateStatusToMatched(MatchRequest opponentMatchRequest) {
        this.matched = true;
        this.opponentMatchRequestId = opponentMatchRequest.getId();
        this.setMatchDecisionStatus(MatchDecisionStatus.WAITING);
    }

    public void resetStatus() {
        this.matched = false;
        this.matchDecisionStatus = null;
    }

    private String generateRequestId(String username) {
        return username + "-" + UUID.randomUUID();
    }
}
