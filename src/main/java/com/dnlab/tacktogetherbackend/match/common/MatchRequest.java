package com.dnlab.tacktogetherbackend.match.common;

import com.dnlab.tacktogetherbackend.match.dto.MatchRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest {
    private String id;
    private String username;
    private String origin;
    private String destination;
    private short originRange;
    private short destinationRange;

    public MatchRequest(MatchRequestDTO dto) {
        this.id = generateRequestId(dto.getUsername());
        this.username = dto.getUsername();
        this.origin = dto.getOrigin();
        this.destination = dto.getDestination();
        this.originRange = dto.getOriginRange();
        this.destinationRange = dto.getDestinationRange();
    }

    private String generateRequestId(String username) {
        return username + "-" + UUID.randomUUID();
    }
}
