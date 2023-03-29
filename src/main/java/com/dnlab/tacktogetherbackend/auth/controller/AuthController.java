package com.dnlab.tacktogetherbackend.auth.controller;

import com.dnlab.tacktogetherbackend.auth.common.JwtFilter;
import com.dnlab.tacktogetherbackend.auth.dto.*;
import com.dnlab.tacktogetherbackend.auth.exception.DuplicateUsernameException;
import com.dnlab.tacktogetherbackend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * @param loginDTO username, password 가 담겨있는 dto
     * @return jwt(인증 토큰) 반환
     */
    @PostMapping("/signIn")
    public ResponseEntity<ResponseLogin> signIn(@Valid @RequestBody RequestLogin loginDTO) {
        ResponseLogin token = authService.signIn(loginDTO);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer" + token.getAccessToken());

        return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
    }

    /**
     * @param registrationDTO 회원 가입 정보가 담겨있는 dto
     * @return 가입된 회원 정보 반환
     */
    @PostMapping("/signUp")
    public ResponseEntity<ResponseRegistration> signUp(@Valid @RequestBody RequestRegistration registrationDTO) {
        try {
            return ResponseEntity.ok(authService.signUp(registrationDTO));
        } catch (DuplicateUsernameException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseLogin> refreshAccessToken(@Valid @RequestBody RequestRefreshToken requestRefreshToken) {
        return ResponseEntity.ok(authService.refreshAccessToken(requestRefreshToken));
    }

    @GetMapping("/checkUsername")
    public ResponseEntity<ResponseCheckUsername> checkDuplicatedUsername(@RequestParam String username) {
        return ResponseEntity.ok(authService.checkDuplicatedUsername(username));
    }

    // 인증 테스트 url
    @GetMapping("/testAuth")
    public ResponseEntity<String> testAuthentication(HttpServletRequest request) {
        if (authService.validAuthentication(request)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
