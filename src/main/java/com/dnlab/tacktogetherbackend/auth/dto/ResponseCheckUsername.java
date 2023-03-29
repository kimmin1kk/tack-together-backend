package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ResponseCheckUsername {
    private boolean duplicated;
}
