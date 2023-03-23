package com.dnlab.tacktogetherbackend.auth.service;

import com.dnlab.tacktogetherbackend.auth.dto.*;

public interface AuthService {
    ResponseLogin signIn(RequestLogin requestLogin);
    ResponseRegistration signUp(RequestRegistration requestRegistration);
    ResponseLogin refreshAccessToken(RequestRefreshToken requestRefreshToken);
}
