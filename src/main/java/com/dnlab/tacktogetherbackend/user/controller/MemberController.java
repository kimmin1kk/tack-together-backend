package com.dnlab.tacktogetherbackend.user.controller;

import com.dnlab.tacktogetherbackend.user.common.MemberRegistrationDTO;
import com.dnlab.tacktogetherbackend.user.common.MemberResponseDTO;
import com.dnlab.tacktogetherbackend.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDTO> registerMember(@RequestBody MemberRegistrationDTO registrationDTO) {
        return new ResponseEntity<>(memberService.registerMember(registrationDTO), HttpStatus.ACCEPTED);
    }

}
