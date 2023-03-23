package com.dnlab.tacktogetherbackend.auth.controller;

import com.dnlab.tacktogetherbackend.auth.common.JwtFilter;
import com.dnlab.tacktogetherbackend.auth.dto.MemberRegistrationDTO;
import com.dnlab.tacktogetherbackend.auth.dto.MemberRegistrationResponseDTO;
import com.dnlab.tacktogetherbackend.auth.dto.MemberResponseDTO;
import com.dnlab.tacktogetherbackend.auth.dto.RequestLoginDTO;
import com.dnlab.tacktogetherbackend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signIn")
    public ResponseEntity<MemberResponseDTO> signIn(@Valid @RequestBody RequestLoginDTO loginDTO) {
        MemberResponseDTO token = authService.signIn(loginDTO);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer" + token.getAccessToken());

        return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/signUp")
    public ResponseEntity<MemberRegistrationResponseDTO> signUp(@Valid @RequestBody MemberRegistrationDTO registrationDTO) {
        return ResponseEntity.ok(authService.signUp(registrationDTO));
    }
}
