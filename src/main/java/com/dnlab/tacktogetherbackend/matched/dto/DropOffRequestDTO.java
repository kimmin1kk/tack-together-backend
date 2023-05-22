package com.dnlab.tacktogetherbackend.matched.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DropOffRequestDTO {
    private String sessionId;
    private String endLocation;
}
