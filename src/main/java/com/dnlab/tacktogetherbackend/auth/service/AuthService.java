package com.dnlab.tacktogetherbackend.auth.service;

import com.dnlab.tacktogetherbackend.auth.dto.MemberRegistrationResponseDTO;
import com.dnlab.tacktogetherbackend.auth.dto.RequestLoginDTO;
import com.dnlab.tacktogetherbackend.auth.dto.MemberRegistrationDTO;
import com.dnlab.tacktogetherbackend.auth.dto.MemberResponseDTO;

public interface AuthService {
    MemberResponseDTO signIn(RequestLoginDTO requestLoginDTO);
    MemberRegistrationResponseDTO signUp(MemberRegistrationDTO memberRegistrationDTO);
}
