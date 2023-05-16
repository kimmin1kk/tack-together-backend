package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LocationUpdateRequestDTO implements Serializable {
    private String location;
    private boolean departureAgreed;
    private String sessionId;
}
