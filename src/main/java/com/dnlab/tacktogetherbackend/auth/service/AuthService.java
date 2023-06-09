package com.dnlab.tacktogetherbackend.auth.service;

import com.dnlab.tacktogetherbackend.auth.dto.*;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    LoginResponseDTO signIn(LoginRequestDTO loginRequestDTO);
    RegistrationResponseDTO signUp(RegistrationRequestDTO registrationRequestDTO);
    LoginResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshTokenRequestDTO);
    CheckUsernameRequestDTO checkAvailableUsername(String username);
    boolean validAuthentication(HttpServletRequest request);

    MemberInfoResponseDTO getMemberInfoByUsername(String username);
    MemberUpdateDTO updateMemberInfo(MemberUpdateDTO memberUpdateDTO, String username);
}
