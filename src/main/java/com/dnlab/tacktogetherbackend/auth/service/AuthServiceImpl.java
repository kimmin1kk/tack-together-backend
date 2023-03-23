package com.dnlab.tacktogetherbackend.auth.service;

import com.dnlab.tacktogetherbackend.auth.common.JwtTokenProvider;
import com.dnlab.tacktogetherbackend.auth.dto.MemberRegistrationResponseDTO;
import com.dnlab.tacktogetherbackend.auth.dto.RequestLoginDTO;
import com.dnlab.tacktogetherbackend.auth.dto.MemberRegistrationDTO;
import com.dnlab.tacktogetherbackend.auth.dto.MemberResponseDTO;
import com.dnlab.tacktogetherbackend.auth.common.MemberAuthority;
import com.dnlab.tacktogetherbackend.auth.domain.Authority;
import com.dnlab.tacktogetherbackend.auth.domain.Member;
import com.dnlab.tacktogetherbackend.auth.repository.AuthorityRepository;
import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO signIn(RequestLoginDTO requestLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestLoginDTO.getUsername(), requestLoginDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createToken(authentication);

        return MemberResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    @Transactional
    public MemberRegistrationResponseDTO signUp(MemberRegistrationDTO registrationDTO) {

        Member member = Member.builder()
                .username(registrationDTO.getUsername())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .enabled(true)
                .build();
        member = memberRepository.save(member);

        Authority authority = authorityRepository.save(Authority.builder()
                .member(member)
                .authorityName(MemberAuthority.ROLE_MEMBER)
                .build());

        return MemberRegistrationResponseDTO.of(member, authority);
    }
}
