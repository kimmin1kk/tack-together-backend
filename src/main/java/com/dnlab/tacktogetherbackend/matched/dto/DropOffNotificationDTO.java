package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DropOffNotificationDTO {
    private String username;
    private String sessionId;
    private String dropOffLocation;

    @Builder
    public DropOffNotificationDTO(String username, String sessionId, String dropOffLocation) {
        this.username = username;
        this.sessionId = sessionId;
        this.dropOffLocation = dropOffLocation;
    }
}
