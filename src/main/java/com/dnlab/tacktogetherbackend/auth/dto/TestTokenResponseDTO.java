package com.dnlab.tacktogetherbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestTokenResponseDTO implements Serializable {
    private boolean authorized;
}
