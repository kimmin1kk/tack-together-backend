package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationInfoResponseDTO {
    private String username;
    private String location;
    private boolean departureAgreed;

    @Builder
    public LocationInfoResponseDTO(String username,
                                   String location,
                                   boolean departureAgreed) {
        this.username = username;
        this.location = location;
        this.departureAgreed = departureAgreed;
    }
}
