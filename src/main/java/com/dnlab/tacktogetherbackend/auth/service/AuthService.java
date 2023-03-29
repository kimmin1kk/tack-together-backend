package com.dnlab.tacktogetherbackend.auth.service;

import com.dnlab.tacktogetherbackend.auth.dto.*;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    ResponseLogin signIn(RequestLogin requestLogin);
    ResponseRegistration signUp(RequestRegistration requestRegistration);
    ResponseLogin refreshAccessToken(RequestRefreshToken requestRefreshToken);
    ResponseCheckUsername checkDuplicatedUsername(String username);
    boolean validAuthentication(HttpServletRequest request);
}
