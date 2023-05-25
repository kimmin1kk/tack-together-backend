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

    public static DropOffNotificationDTO of(DropOffRequestDTO dropOffRequestDTO, String username) {
        return DropOffNotificationDTO.builder()
                .dropOffLocation(dropOffRequestDTO.getEndLocation())
                .sessionId(dropOffRequestDTO.getSessionId())
                .username(username)
                .build();
    }
}
