package com.dnlab.tacktogetherbackend.auth.controller;

import com.dnlab.tacktogetherbackend.auth.dto.*;
import com.dnlab.tacktogetherbackend.auth.exception.DuplicateUsernameException;
import com.dnlab.tacktogetherbackend.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final String authorizationHeader;

    public AuthController(AuthService authService,
                          @Value("${jwt.header}") String authorizationHeader) {
        this.authService = authService;
        this.authorizationHeader = authorizationHeader;
    }

    /**
     * @param loginDTO username, password 가 담겨있는 dto
     * @return jwt(인증 토큰) 반환
     */
    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponseDTO> signIn(@Valid @RequestBody LoginRequestDTO loginDTO) {
        LoginResponseDTO token = authService.signIn(loginDTO);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(authorizationHeader, "Bearer " + token.getAccessToken());

        return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
    }

    /**
     * @param registrationDTO 회원 가입 정보가 담겨있는 dto
     * @return 가입된 회원 정보 반환
     */
    @PostMapping("/sign-up")
    public ResponseEntity<RegistrationResponseDTO> signUp(@Valid @RequestBody RegistrationRequestDTO registrationDTO) {
        try {
            return ResponseEntity.ok(authService.signUp(registrationDTO));
        } catch (DuplicateUsernameException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDTO> refreshAccessToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshTokenRequestDTO));
    }

    @GetMapping("/check-username")
    public ResponseEntity<CheckUsernameRequestDTO> checkDuplicatedUsername(@RequestParam String username) {
        return ResponseEntity.ok(authService.checkDuplicatedUsername(username));
    }

    // 인증 테스트 url
    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuthentication(HttpServletRequest request) {
        if (authService.validAuthentication(request)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
