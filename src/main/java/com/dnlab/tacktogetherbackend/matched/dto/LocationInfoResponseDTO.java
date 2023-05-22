package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationInfoResponseDTO {
    private String sessionId;
    private String username;
    private String location;
    private boolean departureAgreed;
    private boolean ridingStarted;

    @Builder
    public LocationInfoResponseDTO(
            String sessionId,
            String username,
            String location,
            boolean departureAgreed,
            boolean ridingStarted) {
        this.sessionId = sessionId;
        this.username = username;
        this.location = location;
        this.departureAgreed = departureAgreed;
        this.ridingStarted = ridingStarted;
    }
}
