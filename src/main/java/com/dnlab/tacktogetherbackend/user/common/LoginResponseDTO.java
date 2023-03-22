package com.dnlab.tacktogetherbackend.user.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginResponseDTO implements Serializable {
    private String username;

}
